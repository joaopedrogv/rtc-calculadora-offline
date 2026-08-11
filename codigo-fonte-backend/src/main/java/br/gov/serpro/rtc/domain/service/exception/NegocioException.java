/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service.exception;

/**
 * Exceção base para erros de negócio identificados durante o processamento
 * tributário. Serve como superclasse das exceções de domínio ligadas às regras
 * da calculadora.
 */
public abstract class NegocioException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    protected NegocioException(String mensagem) {
        super(mensagem);
    }

}
