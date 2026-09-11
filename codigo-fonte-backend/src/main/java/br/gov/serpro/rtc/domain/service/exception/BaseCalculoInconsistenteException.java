/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service.exception;

import java.math.BigDecimal;

/**
 * Exceção lançada quando a base de cálculo de CBS e IBS informada difere da
 * soma da base de cálculo do Imposto Seletivo com o Imposto Seletivo informado.
 */
public class BaseCalculoInconsistenteException extends ValidacaoException {

    private static final long serialVersionUID = 529236725876115L;
    private static final String MESSAGE = "A base de cálculo de CBS e IBS informada %s difere da soma da base de cálculo do Imposto Seletivo %s com o Imposto Seletivo informado %s";

    public BaseCalculoInconsistenteException(BigDecimal baseCalculo, BigDecimal baseCalculoImpostoSeletivo,
            BigDecimal impostoSeletivoInformado) {
        super(String.format(MESSAGE, baseCalculo, baseCalculoImpostoSeletivo, impostoSeletivoInformado));
    }

}
