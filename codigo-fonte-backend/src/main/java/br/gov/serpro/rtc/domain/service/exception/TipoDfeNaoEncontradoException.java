/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service.exception;

import java.time.LocalDate;

/**
 * Exceção lançada quando o tipo de documento fiscal (tpDoc) informado na
 * operação não existe ou está fora de vigência na data do fato gerador.
 */
public class TipoDfeNaoEncontradoException extends ValidacaoException {

    private static final long serialVersionUID = 1L;
    private static final String MESSAGE = "Tipo de documento fiscal (tpDoc) de código %d não encontrado ou fora de vigência na data %s";

    public TipoDfeNaoEncontradoException(Integer tpDoc, LocalDate data) {
        super(String.format(MESSAGE, tpDoc, data));
    }

}
