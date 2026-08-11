package br.gov.serpro.rtc.api.exceptionhandler.dto;

/**
 * Detalhe de erro individual retornado na resposta de observabilidade, conforme RFC 9457.
 * Contém o URI do tipo de erro, título descritivo, código, ponteiro JSON e mensagem de detalhe.
 * O campo `pointer` segue a especificação RFC 6901 para JSON Pointer.
 */
public record ErroDetalhe(String type, String title, String code, String pointer, String detail) {
}
