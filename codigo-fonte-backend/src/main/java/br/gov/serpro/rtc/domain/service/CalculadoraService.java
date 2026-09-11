/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service;

import java.time.LocalDate;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import br.gov.serpro.rtc.api.model.input.OperacaoInput;
import br.gov.serpro.rtc.api.model.roc.CompraGovernamentalDomain;
import br.gov.serpro.rtc.api.model.roc.ObjetoDomain;
import br.gov.serpro.rtc.api.model.roc.OperacaoConsumoDomain;
import br.gov.serpro.rtc.api.model.roc.ROCDomain;
import br.gov.serpro.rtc.api.model.roc.ValoresTotaisDomain;
import br.gov.serpro.rtc.domain.model.entity.TipoDfe;
import lombok.RequiredArgsConstructor;

/**
 * Serviço responsável por orquestrar o cálculo de tributos da operação,
 * validando UF e município, aplicando redutores de compra governamental e
 * delegando o processamento de cada item.
 */
@RequiredArgsConstructor
@Service
public class CalculadoraService {

    private final UfService ufService;
    private final MunicipioService municipioService;
    private final RedutorCompraGovernamentalService redutorCompraGovService;
    private final TipoDfeService tipoDfeService;

    private final ProcessamentoItemService processamentoItemService;

    public ROCDomain calcularTributos(OperacaoInput operacao) {

        // Validar UF e Município
        if (operacao.getUf() == null) {
            operacao.setUf(municipioService.buscarUfPorMunicipio(operacao.getMunicipio()));
        }
        ufService.validarUf(operacao.getUf());
        municipioService.validarMunicipio(operacao.getMunicipio(), operacao.getUf());

        // Resolver o tpDoc da operação (erro de operação se inexistente ou fora de vigência)
        final TipoDfe tipoDfe = tipoDfeService.buscarPorTipo(operacao.getTpDoc(), operacao.getFatoGeradorAplicavel());

        final List<ObjetoDomain> detalhes = getDetalhesImposto(operacao, tipoDfe);
        
        return ROCDomain.builder()
            .oper(getOperacaoOutput(operacao))
            .objetos(detalhes)
            .total(ValoresTotaisDomain.create(detalhes))
        .build();
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
    
    private List<ObjetoDomain> getDetalhesImposto(OperacaoInput operacao, TipoDfe tipoDfe) {
        final LocalDate data = operacao.getFatoGeradorAplicavel();

        return operacao.getItens()
                .parallelStream()
                .map(item -> ObjetoDomain.builder()
                        .nObj(item.getNumero())
                        .tribCalc(processamentoItemService.processarItem(operacao, item, data, tipoDfe))
                        .calculoSimulado(item.getAliquotasNominais() != null)
                        .build())
                .sorted()
                .toList();
    }
    
}
