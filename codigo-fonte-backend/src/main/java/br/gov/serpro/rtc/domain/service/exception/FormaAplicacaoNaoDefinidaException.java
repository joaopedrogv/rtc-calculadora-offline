/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service.exception;

/**
 * Exceção lançada quando a forma de aplicação da alíquota própria não está
 * definida para a tributação consultada.
 */
public class FormaAplicacaoNaoDefinidaException extends ErroInternoSistemaException {

    private static final long serialVersionUID = 1L;
    private static final String MESSAGE = "Forma de aplicação da alíquota própria não definida";

    public FormaAplicacaoNaoDefinidaException() {
        super(MESSAGE);
    }

}
