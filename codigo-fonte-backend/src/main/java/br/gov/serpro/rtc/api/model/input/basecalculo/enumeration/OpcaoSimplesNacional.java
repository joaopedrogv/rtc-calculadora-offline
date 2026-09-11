/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.api.model.input.basecalculo.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Opção pelo Simples Nacional do contribuinte.
 */
@Schema(description = "Opção pelo Simples Nacional", enumAsRef = true)
public enum OpcaoSimplesNacional {

    NAO_OPTANTE(1, "Não Optante"),
    MEI(2, "MEI"),
    OPTANTE_ME_EPP(3, "Optante ME/EPP"),
    OPTANTE_PENDENTE(4, "Optante Pendente");

    private final int codigo;
    private final String descricao;

    OpcaoSimplesNacional(int codigo, String descricao) {
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

    public boolean isOptante() {
        return this == OPTANTE_ME_EPP || this == OPTANTE_PENDENTE;
    }

    public boolean isMei() {
        return this == MEI;
    }

    /**
     * Indica se o regime de apuração (regApIBSCBSSN) é obrigatório para esta opção.
     */
    public boolean exigeRegimeApuracao() {
        return isOptante();
    }

    /**
     * Indica se o regime de apuração (regApIBSCBSSN) é proibido para esta opção.
     */
    public boolean proibeRegimeApuracao() {
        return this == NAO_OPTANTE;
    }

    public boolean isPendente() {
        return this == OPTANTE_PENDENTE;
    }

    /**
     * Indica se a locação de imóveis (99.03) é incompatível com esta opção.
     * Vedado para MEI e optante ME/EPP (conforme NT 009).
     */
    public boolean vedaLocacao() {
        return this == MEI || this == OPTANTE_ME_EPP;
    }

    /**
     * Indica se baseSN é inaplicável para esta opção (não optante ou MEI).
     */
    public boolean baseSNInaplicavel() {
        return this == NAO_OPTANTE || this == MEI;
    }

    @JsonCreator
    public static OpcaoSimplesNacional fromCodigo(int codigo) {
        for (OpcaoSimplesNacional value : values()) {
            if (value.codigo == codigo) {
                return value;
            }
        }
        throw new IllegalArgumentException("Código inválido para OpcaoSimplesNacional: " + codigo);
    }
}
