package br.gov.serpro.rtc.api.exceptionhandler.dto;

/**
 * DTO de resposta do endpoint {@code GET /calculadora/observabilidade/erros?codigo=}.
 * Retorna o código, título e descrição em PT-BR de um erro catalogado (REG, CAL ou VAL).
 */
public record ErroDescricao(String codigo, String titulo, String descricao) {
}
