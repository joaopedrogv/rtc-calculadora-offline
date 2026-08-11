/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service.exception;

/**
 * Exceção lançada quando os dados de tributação regular são obrigatórios para a
 * operação, mas não foram informados na entrada.
 */
public class TributacaoRegularNaoInformadaException extends ValidacaoException {

    private static final long serialVersionUID = 523456725876113L;
    private static final String MESSAGE = "Dados da tributação regular devem ser informados para cClassTrib %s e CST %s";

    public TributacaoRegularNaoInformadaException(String cClassTrib, String cst) {
        super(String.format(MESSAGE, cClassTrib, cst));
    }

}
