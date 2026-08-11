/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service.exception;

/**
 * Exceção lançada quando a forma de aplicação da alíquota configurada para uma
 * classificação não possui tratamento implementado na lógica de cálculo.
 */
public class FormaAplicacaoNaoTratadaException extends ErroInternoSistemaException {

    private static final long serialVersionUID = 1L;
    private static final String MESSAGE = "Forma de aplicação da alíquota própria não tratada";

    public FormaAplicacaoNaoTratadaException() {
        super(MESSAGE);
    }

}
