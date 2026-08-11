package br.gov.serpro.rtc.domain.model.dto;

/**
 * DTO interno que representa a classificação tributária consultada para
 * cálculo, contendo identificador, código, nomenclatura e CST.
 */
public record ClassificacaoTributariaDTO(Long id, String codigo, String nomenclatura, String cst) {
}
