/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service.exception;

/**
 * Exceção lançada quando a alíquota padrão exigida para a apuração do tributo
 * não é encontrada na base de referência.
 */
public class AliquotaPadraoNaoEncontradaException extends EntidadeNaoEncontradaException {

    private static final long serialVersionUID = 202504161234567894L;

    private static final String DEFAULT_MESSAGE = "Alíquota padrão não encontrada";

    public AliquotaPadraoNaoEncontradaException() {
        super(DEFAULT_MESSAGE);
    }
    
}
