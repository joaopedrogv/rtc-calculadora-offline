package br.gov.serpro.rtc.api.model.xml;

/**
 * Record que representa os metadados de um tipo de documento fiscal XML
 * suportado pela API.
 */

public record TipoDocumentoDTO(String nome, String mnemonico, String versaoNotaTecnica) {
}
