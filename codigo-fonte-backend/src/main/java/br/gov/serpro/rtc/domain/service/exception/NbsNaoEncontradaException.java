/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service.exception;

import java.time.LocalDate;

/**
 * Exceção lançada quando a NBS informada não é localizada na base de referência
 * para a vigência consultada.
 */
public class NbsNaoEncontradaException extends EntidadeNaoEncontradaException {

    private static final long serialVersionUID = 2582687430572466747L;
    private static final String MESSAGE = "NBS de código %s não encontrada para a data %s";

    public NbsNaoEncontradaException(String nbs, LocalDate data) {
        super(String.format(MESSAGE, nbs, data));
    }

}