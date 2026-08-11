/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service.exception;

/**
 * Exceção lançada quando a aplicação exige validação humana por captcha antes
 * de permitir o prosseguimento do processamento tributário.
 */
public class CaptchaException extends ValidacaoException {

    private static final long serialVersionUID = 529876772972113L;

    public CaptchaException(String mensagem) {
        super(mensagem);
    }

}
