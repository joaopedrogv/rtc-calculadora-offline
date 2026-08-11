package br.gov.serpro.rtc.domain.model.enumeration;

/**
 * Enumera os estados possíveis de um item processado pela calculadora.
 *
 * Valores: {@code CALCULADO}, {@code NAO_IMPLEMENTADO}, {@code
 * INCONSISTENCIA_ENTRADA} e {@code FALHA_TECNICA}.
 */
public enum EstadoItemEnum {
    CALCULADO,
    NAO_IMPLEMENTADO,
    INCONSISTENCIA_ENTRADA,
    FALHA_TECNICA
}
