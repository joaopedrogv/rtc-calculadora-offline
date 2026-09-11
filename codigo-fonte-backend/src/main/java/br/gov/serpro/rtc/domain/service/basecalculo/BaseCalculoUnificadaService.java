package br.gov.serpro.rtc.domain.service.basecalculo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import br.gov.serpro.rtc.api.model.input.basecalculo.BaseCalculoUnificadaInput;
import br.gov.serpro.rtc.api.model.input.basecalculo.ComponentesBaseCalculoInput;
import br.gov.serpro.rtc.api.model.input.basecalculo.enumeration.BaseCalculoBase;
import br.gov.serpro.rtc.api.model.input.basecalculo.enumeration.BaseCalculoNatureza;
import br.gov.serpro.rtc.api.model.input.basecalculo.enumeration.BaseCalculoOperacao;
import br.gov.serpro.rtc.api.model.input.basecalculo.enumeration.ElementoBaseCalculo;
import br.gov.serpro.rtc.api.model.output.basecalculo.BaseCalculoUnificadaOutput;
import br.gov.serpro.rtc.core.util.ArredondamentoUtils;
import br.gov.serpro.rtc.domain.service.exception.CampoInvalidoException;

/**
 * Serviço responsável pelo cálculo unificado das três bases: RG/IBS-CBS, IS, SN.
 * Ponto único de cálculo com múltiplos pontos de entrada para diferentes contextos.
 */
@Service
public class BaseCalculoUnificadaService {

    /**
     * Calcula a partir do DTO do endpoint dedicado.
     */
    public BaseCalculoUnificadaOutput calcular(BaseCalculoUnificadaInput input) {
        return calcular(input.getComponentes(), input.getAnoFatoGerador(), input.getNatureza(), input.getTipoDocumento());
    }

    /**
     * Calcula a partir dos componentes + contexto (e qualquer outro caller que já possua os 
     * componentes separados do input do endpoint).
     *
     * @param componentes os componentes da base de cálculo
     * @param ano o ano do fato gerador
     * @param natureza a natureza da operação (BEM, SERVICO ou null)
     * @param tpDoc o tipo de documento fiscal (55 ou 65)
     * @return resultado com as 3 bases calculadas e lista de avisos
     */
    public BaseCalculoUnificadaOutput calcular(ComponentesBaseCalculoInput componentes, int ano, BaseCalculoNatureza natureza, Integer tpDoc) {
        if (tpDoc == null || (tpDoc != 55 && tpDoc != 65)) {
            throw new CampoInvalidoException("Tipo de documento deve ser 55 (NF-e) ou 65 (NFC-e)");
        }

        BigDecimal rgSoma = BigDecimal.ZERO;
        BigDecimal rgSubtrai = BigDecimal.ZERO;
        BigDecimal isSoma = BigDecimal.ZERO;
        BigDecimal isSubtrai = BigDecimal.ZERO;
        BigDecimal snSoma = BigDecimal.ZERO;
        BigDecimal snSubtrai = BigDecimal.ZERO;

        List<String> avisos = new ArrayList<>();

        for (ElementoBaseCalculo elemento : ElementoBaseCalculo.todos()) {
            if (!elemento.campoCompoeBase(ano, natureza, tpDoc)) {
                BigDecimal valor = componentes.getValor(elemento);
                if (valor != null && valor.compareTo(BigDecimal.ZERO) != 0) {
                    elemento.motivoNaoComposicao(ano, natureza, tpDoc).ifPresent(avisos::add);
                }
                continue;
            }

            BigDecimal valor = componentes.getValor(elemento);
            if (valor == null || valor.compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }

            if (elemento.getOperacao() == BaseCalculoOperacao.SOMA) {
                if (elemento.aplicaBase(BaseCalculoBase.RG)) rgSoma = rgSoma.add(valor);
                if (elemento.aplicaBase(BaseCalculoBase.IS)) isSoma = isSoma.add(valor);
                if (elemento.aplicaBase(BaseCalculoBase.SN)) snSoma = snSoma.add(valor);
            }

            if (elemento.getOperacao() == BaseCalculoOperacao.SUBTRAI) {
                if (elemento.aplicaBase(BaseCalculoBase.RG)) rgSubtrai = rgSubtrai.add(valor);
                if (elemento.aplicaBase(BaseCalculoBase.IS)) isSubtrai = isSubtrai.add(valor);
                if (elemento.aplicaBase(BaseCalculoBase.SN)) snSubtrai = snSubtrai.add(valor);
            }
        }

        BigDecimal baseRG = ArredondamentoUtils.subtrair(rgSoma, rgSubtrai);
        BigDecimal baseIS = ArredondamentoUtils.subtrair(isSoma, isSubtrai);
        BigDecimal baseSN = ArredondamentoUtils.subtrair(snSoma, snSubtrai);

        return new BaseCalculoUnificadaOutput(baseRG, baseIS, baseSN, avisos);
    }
}
