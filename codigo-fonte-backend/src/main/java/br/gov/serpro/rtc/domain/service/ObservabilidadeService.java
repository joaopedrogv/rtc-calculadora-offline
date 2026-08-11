package br.gov.serpro.rtc.domain.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import br.gov.serpro.rtc.api.exceptionhandler.dto.ErroDetalhe;
import br.gov.serpro.rtc.api.exceptionhandler.enumeration.CodigoValidacao;
import br.gov.serpro.rtc.api.model.input.ItemOperacaoInput;
import br.gov.serpro.rtc.api.model.input.OperacaoInput;
import br.gov.serpro.rtc.api.model.roc.CompraGovernamentalDomain;
import br.gov.serpro.rtc.api.model.roc.ObjetoDomain;
import br.gov.serpro.rtc.api.model.roc.ObservabilidadeItemDomain;
import br.gov.serpro.rtc.api.model.roc.ObservabilidadeROCDomain;
import br.gov.serpro.rtc.api.model.roc.OperacaoConsumoDomain;
import br.gov.serpro.rtc.api.model.roc.TributosDomain;
import br.gov.serpro.rtc.api.model.roc.ValoresTotaisDomain;
import br.gov.serpro.rtc.domain.model.enumeration.EstadoItemEnum;
import br.gov.serpro.rtc.domain.service.collector.ErrosCalculoException;
import br.gov.serpro.rtc.domain.service.collector.ItemCollector;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Serviço responsável por processar operações em modo de observabilidade,
 * validando entrada, calculando itens em paralelo e consolidando erros e
 * resultados.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ObservabilidadeService {

    private static final Pattern ARRAY_INDEX = Pattern.compile("\\[(\\d+)]");

    private final ProcessamentoItemService processamentoItemService;
    private final UfService ufService;
    private final MunicipioService municipioService;
    private final Validator validator;
    private final RedutorCompraGovernamentalService redutorCompraGovService;

    public ObservabilidadeROCDomain processarOperacao(OperacaoInput operacao, String baseURL) {
        // Validar campos globais estruturais via Bean Validation (id, versao, municipio, itens)
        List<ErroDetalhe> errosGlobaisVal = validarCamposGlobais(operacao, baseURL);
        if (!errosGlobaisVal.isEmpty()) {
            throw new ErrosCalculoException(errosGlobaisVal);
        }

        // Validar globais de negócio — se falhar, lança exceção normalmente (422)
        if (operacao.getUf() == null) {
            operacao.setUf(municipioService.buscarUfPorMunicipio(operacao.getMunicipio()));
        }
        ufService.validarUf(operacao.getUf());
        municipioService.validarMunicipio(operacao.getMunicipio(), operacao.getUf());

        final LocalDate data = operacao.getFatoGeradorAplicavel();

        List<ItemOperacaoInput> itens = operacao.getItens();

        // Processar itens com virtual threads
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<CompletableFuture<ObservabilidadeItemDomain>> futures = new ArrayList<>();

            for (int i = 0; i < itens.size(); i++) {
                final int index = i;
                final ItemOperacaoInput item = itens.get(index);

                CompletableFuture<ObservabilidadeItemDomain> future = CompletableFuture.supplyAsync(() ->
                    processarItemComValidacao(operacao, item, index, data, baseURL), executor);

                futures.add(future);
            }

            List<ObservabilidadeItemDomain> resultados = futures.stream()
                    .map(CompletableFuture::join)
                    .sorted(Comparator.comparing(ObservabilidadeItemDomain::getNObj))
                    .toList();

            // Totalizar apenas itens CALCULADO
            List<ObjetoDomain> itensCalculados = resultados.stream()
                    .filter(r -> r.getEstadoItem() == EstadoItemEnum.CALCULADO)
                    .map(r -> ObjetoDomain.builder()
                            .nObj(r.getNObj())
                            .tribCalc(r.getTribCalc())
                            .build())
                    .toList();

            ValoresTotaisDomain total = itensCalculados.isEmpty() ? null : ValoresTotaisDomain.create(itensCalculados);

            return ObservabilidadeROCDomain.builder()
            		.oper(getOperacaoOutput(operacao))
                    .objetos(resultados)
                    .total(total)
                    .build();
        }
    }
    
    private OperacaoConsumoDomain getOperacaoOutput(OperacaoInput op) {
        final var gCompraGovIn = op.getGCompraGov();
        if (gCompraGovIn == null) {
            return null;
        }
        
        final var gCompraGovOut = new ModelMapper().map(gCompraGovIn, CompraGovernamentalDomain.class);
        gCompraGovOut.setPRedutor(redutorCompraGovService.buscarValorRedutor(op.getTpEnteGov(), op.getFatoGeradorAplicavel()));
        return OperacaoConsumoDomain
                .builder()
                .gCompraGov(gCompraGovOut)
                .build();
    }

    private ObservabilidadeItemDomain processarItemComValidacao(
            OperacaoInput operacao, ItemOperacaoInput item, int index, LocalDate data, String baseURL) {

        ItemCollector collector = new ItemCollector(index, baseURL);

        // 1. Validar Bean Validation no item
        Set<ConstraintViolation<ItemOperacaoInput>> violations = validator.validate(item);
        if (!violations.isEmpty()) {
            for (ConstraintViolation<ItemOperacaoInput> v : violations) {
                String annotationName = v.getConstraintDescriptor()
                        .getAnnotation().annotationType().getSimpleName();
                String code = CodigoValidacao.codeFrom(annotationName);
                String campo = extractFieldName(v.getPropertyPath());
                String detail = buildValidationDetail(campo, v.getMessage(), v.getInvalidValue());
                collector.addErroValidacao(code, campo, detail);
            }
        }

        // Se houver erros de validação, não tenta calcular
        if (collector.hasErros()) {
            return ObservabilidadeItemDomain.builder()
                    .nObj(item.getNumero() != null ? item.getNumero() : index + 1)
                    .estadoItem(collector.getEstado())
                    .erros(collector.getErros())
                    .build();
        }

        // 2. Tentar calcular
        try {
            TributosDomain tributos = processamentoItemService.processarItem(operacao, item, data);
            return ObservabilidadeItemDomain.builder()
                    .nObj(item.getNumero() != null ? item.getNumero() : index + 1)
                    .estadoItem(EstadoItemEnum.CALCULADO)
                    .tribCalc(tributos)
                    .build();
        } catch (Exception ex) {
            log.debug("Erro ao processar item {} (índice {}): {}", item.getNumero() != null ? item.getNumero() : index + 1, index, ex.getMessage());
            collector.addErro(ex);
            return ObservabilidadeItemDomain.builder()
                    .nObj(item.getNumero() != null ? item.getNumero() : index + 1)
                    .estadoItem(collector.getEstado())
                    .erros(collector.getErros())
                    .build();
        }
    }

    private List<ErroDetalhe> validarCamposGlobais(OperacaoInput operacao, String baseURL) {
        List<ErroDetalhe> erros = new ArrayList<>();
        Set<ConstraintViolation<OperacaoInput>> violations = validator.validate(operacao);
        for (ConstraintViolation<OperacaoInput> v : violations) {
            String field = extractFieldName(v.getPropertyPath());
            // Ignorar erros de itens (serão validados individualmente)
            if (field.startsWith("itens[") || field.startsWith("itens.")) {
                continue;
            }
            // itens nível raiz (NotEmpty)
            String annotationName = v.getConstraintDescriptor()
                    .getAnnotation().annotationType().getSimpleName();
            String code = CodigoValidacao.codeFrom(annotationName);
            String pointer = toJsonPointer(field);
            String detail = buildValidationDetail(field, v.getMessage(), v.getInvalidValue());
            String type = String.format("%s/calculadora/observabilidade/erros?codigo=%s", baseURL, code);
            String title = CodigoValidacao.fromCodigo(code)
                    .map(CodigoValidacao::getTitulo)
                    .orElse("Erro de validação");
            erros.add(new ErroDetalhe(type, title, code, pointer, detail));
        }
        return erros;
    }

    private static String extractFieldName(Path propertyPath) {
        return propertyPath.toString();
    }

    private static String buildValidationDetail(String field, String message, Object rejectedValue) {
        if (rejectedValue != null) {
            return String.format("Campo '%s' %s, recebido: '%s'", field, message, rejectedValue);
        }
        return String.format("Campo '%s' %s", field, message);
    }

    private static String toJsonPointer(String field) {
        String converted = ARRAY_INDEX.matcher(field).replaceAll("/$1");
        converted = converted.replace('.', '/');
        return "#/" + converted;
    }
}
