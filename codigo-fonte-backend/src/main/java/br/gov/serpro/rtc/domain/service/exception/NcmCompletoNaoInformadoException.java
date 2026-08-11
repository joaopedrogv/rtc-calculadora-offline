/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service.exception;

/**
 * Exceção lançada quando a operação exige o NCM completo da mercadoria e esse
 * código não é informado na entrada.
 */
public class NcmCompletoNaoInformadoException extends ValidacaoException {

    private static final long serialVersionUID = 6773977945809079927L;
    private static final String MESSAGE = "NCM completo não informado";

    public NcmCompletoNaoInformadoException() {
        super(MESSAGE);
    }

}
