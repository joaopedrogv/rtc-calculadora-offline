/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service.exception;

/**
 * Exceção base para falhas de validação de entrada e consistência de dados
 * processados pela calculadora. Centraliza erros de formato, obrigatoriedade e
 * integridade das informações recebidas.
 */
public abstract class ValidacaoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    protected ValidacaoException(String mensagem) {
        super(mensagem);
    }

}
