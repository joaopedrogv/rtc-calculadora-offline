/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.api.model.input.basecalculo.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Regime de apuração IBS/CBS no Simples Nacional.
 */
@Schema(description = "Regime de apuração IBS/CBS no Simples Nacional", enumAsRef = true)
public enum RegimeApuracaoIbsCbsSN {

    IBS_CBS_PELO_SN(1, "IBS+CBS pelo SN"),
    CBS_PELO_SN_IBS_REGULAR(2, "CBS pelo SN + IBS regular"),
    AMBOS_REGULARES(3, "Ambos regulares");

    private final int codigo;
    private final String descricao;

    RegimeApuracaoIbsCbsSN(int codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    @JsonValue
    public int getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isCalculaBaseSN() {
        return this != AMBOS_REGULARES;
    }

    @JsonCreator
    public static RegimeApuracaoIbsCbsSN fromCodigo(int codigo) {
        for (RegimeApuracaoIbsCbsSN value : values()) {
            if (value.codigo == codigo) {
                return value;
            }
        }
        throw new IllegalArgumentException("Código inválido para RegimeApuracaoIbsCbsSN: " + codigo);
    }
}
