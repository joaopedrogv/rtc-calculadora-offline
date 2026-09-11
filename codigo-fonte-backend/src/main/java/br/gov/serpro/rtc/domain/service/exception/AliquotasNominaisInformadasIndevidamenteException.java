/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service.exception;

/**
 * Exceção lançada quando o grupo de alíquotas nominais é informado para
 * operações com fato gerador anterior a 01/01/2027, período em que as
 * alíquotas vigentes são resolvidas pela calculadora.
 */
public class AliquotasNominaisInformadasIndevidamenteException extends ValidacaoException {

    private static final long serialVersionUID = 529236725876114L;
    private static final String MESSAGE = "As alíquotas nominais não podem ser informadas para operações com fato "
            + "gerador anterior a 01/01/2027, pois a calculadora resolve as alíquotas vigentes";

    public AliquotasNominaisInformadasIndevidamenteException() {
        super(MESSAGE);
    }

}
