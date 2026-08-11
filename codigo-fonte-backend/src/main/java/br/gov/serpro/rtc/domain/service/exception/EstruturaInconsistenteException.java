/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service.exception;

/**
 * Exceção base para inconsistências estruturais em dados ou configurações
 * tributárias que impedem o cálculo correto.
 */
public abstract class EstruturaInconsistenteException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    protected EstruturaInconsistenteException(String mensagem) {
        super(mensagem);
    }

}
