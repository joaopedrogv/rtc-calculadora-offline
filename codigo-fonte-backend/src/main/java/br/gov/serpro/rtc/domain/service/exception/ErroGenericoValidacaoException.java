/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service.exception;

/**
 * Exceção lançada para representar falhas de validação que não se enquadram em
 * categorias específicas do domínio tributário.
 */
public class ErroGenericoValidacaoException extends ValidacaoException {

    private static final long serialVersionUID = 529876772972777L;

    public ErroGenericoValidacaoException(String mensagem) {
        super(mensagem);
    }

}
