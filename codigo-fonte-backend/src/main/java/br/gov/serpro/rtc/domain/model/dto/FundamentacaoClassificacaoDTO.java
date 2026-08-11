package br.gov.serpro.rtc.domain.model.dto;

/**
 * DTO interno que reúne o texto curto da fundamentação legal e a memória de
 * cálculo associada à classificação tributária.
 */
public record FundamentacaoClassificacaoDTO(String textoCurto, String memoriaCalculo) {
}
