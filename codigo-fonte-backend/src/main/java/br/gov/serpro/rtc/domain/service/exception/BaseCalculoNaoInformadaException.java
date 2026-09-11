/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service.exception;

/**
 * Exceção lançada quando a base de cálculo de CBS e IBS não é informada em um
 * item sem o grupo de Imposto Seletivo, situação em que ela é obrigatória.
 */
public class BaseCalculoNaoInformadaException extends ValidacaoException {

    private static final long serialVersionUID = 529236725876114L;
    private static final String MESSAGE = "A base de cálculo de CBS e IBS deve ser informada quando o grupo do Imposto Seletivo não é informado";

    public BaseCalculoNaoInformadaException() {
        super(MESSAGE);
    }

}
