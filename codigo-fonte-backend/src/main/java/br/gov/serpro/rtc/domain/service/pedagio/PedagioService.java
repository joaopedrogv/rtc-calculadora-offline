/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service.pedagio;

import static br.gov.serpro.rtc.api.model.output.pedagio.TotalPedagioOutput.getTotal;
import static br.gov.serpro.rtc.core.util.ArredondamentoUtils.dividir;
import static br.gov.serpro.rtc.core.util.ArredondamentoUtils.dividirPorCem;
import static br.gov.serpro.rtc.core.util.ArredondamentoUtils.multiplicar;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import br.gov.serpro.rtc.api.model.input.pedagio.PedagioInput;
import br.gov.serpro.rtc.api.model.output.pedagio.PedagioOutput;
import br.gov.serpro.rtc.api.model.output.pedagio.TrechoPedagioOutput;
import br.gov.serpro.rtc.api.model.output.pedagio.TributoPedagioOutput;
import br.gov.serpro.rtc.domain.model.enumeration.TipoWarningDadosSimulados;
import br.gov.serpro.rtc.domain.model.enumeration.TributoEnum;
import br.gov.serpro.rtc.domain.service.AliquotaPadraoService;
import br.gov.serpro.rtc.domain.service.MunicipioService;
import br.gov.serpro.rtc.domain.service.UfService;
import br.gov.serpro.rtc.domain.service.exception.CampoInvalidoException;
import lombok.RequiredArgsConstructor;

/**
 * Serviço responsável por calcular CBS e IBS em operações de pedágio,
 * distribuindo alíquotas por trecho e identificando avisos de dados simulados.
 */
@RequiredArgsConstructor
@Service
public class PedagioService {

    private final AliquotaPadraoService aliquotaPadraoService;

    private final UfService ufService;

    private final MunicipioService municipioService;

    public PedagioOutput calcularCIBS(PedagioInput operacao) {

        validarNumerosUnicos(operacao);
        municipioService.validarMunicipio(operacao.getCodigoMunicipioOrigem(), operacao.getUfMunicipioOrigem());
        operacao.getTrechos().forEach(t -> municipioService.validarMunicipio(t.getMunicipio(), t.getUf()));

        final List<TrechoPedagioOutput> trechosPedagio = getTrechosPedagio(operacao);
        return PedagioOutput.builder()
                .cst(operacao.getCst())
                .cClassTrib(operacao.getCClassTrib())
                .extensaoTotal(operacao.getExtensaoTotal())
                .dataHoraEmissao(operacao.getDataHoraEmissao())
                .municipioOrigem(operacao.getCodigoMunicipioOrigem())
                .baseCalculo(operacao.getBaseCalculo())
                .ufMunicipioOrigem(operacao.getUfMunicipioOrigem())
                .trechos(trechosPedagio)
                .total(getTotal(trechosPedagio))
                .build();
    }

    private List<TrechoPedagioOutput> getTrechosPedagio(PedagioInput operacao) {

        final BigDecimal x = operacao.getExtensaoTotal();

        return operacao.getTrechos().parallelStream().map(trecho -> {

            final BigDecimal baseCalculoCIBS = operacao.getBaseCalculo();

            final LocalDate dataFatoGerador = operacao.getDataHoraEmissao().toLocalDate();

            Long codigoUf = ufService.buscar(trecho.getUf());

            BigDecimal valorAliquotaCbs = buscarAliquotaPadrao(TributoEnum.CBS, dataFatoGerador, null, null);
            BigDecimal valorAliquotaIbsEstadual = buscarAliquotaPadrao(TributoEnum.IBS_ESTADUAL, dataFatoGerador, codigoUf, null);
            BigDecimal valorAliquotaIbsMunicipal = buscarAliquotaPadrao(TributoEnum.IBS_MUNICIPAL, dataFatoGerador, null, trecho.getMunicipio());

            if (valorAliquotaCbs == null || valorAliquotaIbsEstadual == null || valorAliquotaIbsMunicipal == null) {
                throw new CampoInvalidoException("Erro ao obter alíquotas");
            }

            BigDecimal valorAliquotaEfetivaCbs = dividir(
                    valorAliquotaCbs.multiply(trecho.getExtensao()), x);
            BigDecimal valorAliquotaEfetivaIbsEstadual = dividir(
                    valorAliquotaIbsEstadual.multiply(trecho.getExtensao()), x);
            BigDecimal valorAliquotaEfetivaIbsMunicipal = dividir(
                    valorAliquotaIbsMunicipal.multiply(trecho.getExtensao()), x);

            final TributoPedagioOutput cbs = obterCIBS(valorAliquotaCbs, valorAliquotaEfetivaCbs, baseCalculoCIBS);
            final TributoPedagioOutput ibsEstado = obterCIBS(valorAliquotaIbsEstadual, valorAliquotaEfetivaIbsEstadual,
                    baseCalculoCIBS);
            final TributoPedagioOutput ibsMunicipio = obterCIBS(valorAliquotaIbsMunicipal,
                    valorAliquotaEfetivaIbsMunicipal, baseCalculoCIBS);

            return TrechoPedagioOutput
                    .builder()
                    .numero(trecho.getNumero())
                    .uf(trecho.getUf())
                    .municipio(trecho.getMunicipio())
                    .baseCalculo(baseCalculoCIBS)
                    .cbs(cbs)
                    .ibsEstadual(ibsEstado)
                    .ibsMunicipal(ibsMunicipio)
                    .extensaoTrecho(trecho.getExtensao())
                    .build();
        }).sorted().toList();
    }

    private static TributoPedagioOutput obterCIBS(
            BigDecimal aliquota,
            BigDecimal aliquotaEfetiva,
            BigDecimal baseCalculo) {
        return TributoPedagioOutput
                .builder()
                .aliquota(aliquota != null ? aliquota.movePointRight(2) : null)
                .aliquotaEfetiva(aliquotaEfetiva != null ? aliquotaEfetiva.movePointRight(2) : null)
                .tributoCalculado(calcularTributo(baseCalculo, aliquotaEfetiva))
                .build();
    }

    private static BigDecimal calcularTributo(BigDecimal baseCalculo, BigDecimal aliquota) {
        return multiplicar(baseCalculo, aliquota);
    }

    public BigDecimal buscarAliquotaPadrao(TributoEnum tributo, LocalDate data, Long codigoUf, Long codigoMunicipio) {
        final var aliquota = aliquotaPadraoService.buscarAliquota(tributo.getCodigo(), codigoUf, codigoMunicipio, data);
        return dividirPorCem(aliquota.valorAplicavel());
    }

    private static void validarNumerosUnicos(PedagioInput operacao) {
        Set<Integer> numerosVistos = new HashSet<>();
        for (var trecho : operacao.getTrechos()) {
            if (!numerosVistos.add(trecho.getNumero())) {
                throw new CampoInvalidoException(
                        "Número de trecho duplicado: " + trecho.getNumero());
            }
        }
    }

    public TipoWarningDadosSimulados getWarningDadosSimulados(PedagioInput operacao) {
        LocalDate dataOperacao = operacao.getDataHoraEmissao().toLocalDate();
        LocalDate dataLimite = LocalDate.of(2027, 1, 1);
        
        if (dataOperacao.isBefore(dataLimite)) {
            return null;
        }
        
        return TipoWarningDadosSimulados.CASO_GERAL;
    }

}
