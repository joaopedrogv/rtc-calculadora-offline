/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service.exception;

import java.math.BigDecimal;

/**
 * Exceção lançada quando a base de cálculo declarada diverge da base calculada
 * a partir dos componentes informados.
 */
public class BaseCalculoComponentesInconsistenteException extends ValidacaoException {

    private static final long serialVersionUID = 529236725876115L;
    private static final String MESSAGE = "A base de cálculo declarada (%s) diverge da base calculada a partir dos componentes informados (%s)";
    public static final String CODIGO_ERRO = "REG-034";

    public BaseCalculoComponentesInconsistenteException(BigDecimal declarada, BigDecimal calculada) {
        super(String.format(MESSAGE, declarada, calculada));
    }

}