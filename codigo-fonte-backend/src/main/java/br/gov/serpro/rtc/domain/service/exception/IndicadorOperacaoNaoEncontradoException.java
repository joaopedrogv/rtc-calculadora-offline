/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service.exception;

import java.time.LocalDate;

/**
 * Exceção lançada quando o indicador de operação necessário para classificar a
 * operação fiscal não é encontrado na base de referência.
 */
public class IndicadorOperacaoNaoEncontradoException extends EntidadeNaoEncontradaException {

    private static final long serialVersionUID = 1L;
    private static final String MESSAGE = "Indicador de operação '%s' não encontrado para a data %s";

    public IndicadorOperacaoNaoEncontradoException(String cIndOp, LocalDate data) {
        super(String.format(MESSAGE, cIndOp, data));
    }

}
