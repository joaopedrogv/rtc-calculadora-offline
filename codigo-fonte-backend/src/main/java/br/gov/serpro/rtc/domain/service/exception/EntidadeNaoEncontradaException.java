/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service.exception;

/**
 * Exceção base para ausência de entidades de referência necessárias ao cálculo
 * tributário.
 */
public abstract class EntidadeNaoEncontradaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    protected EntidadeNaoEncontradaException(String mensagem) {
        super(mensagem);
    }

}
