/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service.exception;

/**
 * Exceção base para inconsistências relacionadas às nomenclaturas fiscais
 * informadas na operação.
 */
public class NomenclaturaException extends ValidacaoException {

    private static final long serialVersionUID = 5292642729761124983L;

    public NomenclaturaException(String mensagem) {
        super(String.format(mensagem));
    }

}
