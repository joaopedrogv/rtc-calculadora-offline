/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.api.model.input.basecalculo.enumeration;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Enum para situação da operação NFS-e
 */
@Schema(description = "Situação da operação NFS-e", enumAsRef = true)
public enum SituacaoOperacaoNfse {

    @JsonProperty("comum")
    COMUM,

    @JsonProperty("salaoParceiro")
    SALAO_PARCEIRO,

    @JsonProperty("locacao9903")
    LOCACAO_9903;

    public boolean isLocacao() {
        return this == LOCACAO_9903;
    }

    public boolean isSalaoParceiro() {
        return this == SALAO_PARCEIRO;
    }

    /**
     * Em contexto de locação, documentos de ajuste são ignorados para fins de agregação.
     */
    public boolean ignoraDocumentos() {
        return this == LOCACAO_9903;
    }
}
