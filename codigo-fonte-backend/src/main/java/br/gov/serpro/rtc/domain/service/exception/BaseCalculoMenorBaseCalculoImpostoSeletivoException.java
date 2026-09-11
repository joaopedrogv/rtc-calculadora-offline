/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service.exception;

import java.math.BigDecimal;

/**
 * Exceção lançada quando a base de cálculo de CBS e IBS informada é menor que a
 * base de cálculo do Imposto Seletivo.
 */
public class BaseCalculoMenorBaseCalculoImpostoSeletivoException extends ValidacaoException {

    private static final long serialVersionUID = 529236725876116L;
    private static final String MESSAGE = "A base de cálculo de CBS e IBS informada %s é menor que a base de cálculo do Imposto Seletivo %s";

    public BaseCalculoMenorBaseCalculoImpostoSeletivoException(BigDecimal baseCalculo,
            BigDecimal baseCalculoImpostoSeletivo) {
        super(String.format(MESSAGE, baseCalculo, baseCalculoImpostoSeletivo));
    }

}
