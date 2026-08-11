/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service.exception;

import java.time.LocalDate;

/**
 * Exceção lançada para sinalizar alíquota ad rem não encontrada no domínio de
 * cálculo tributário.
 */
public class AliquotaAdRemNaoEncontradaException extends EntidadeNaoEncontradaException {

    private static final long serialVersionUID = 202504161234567890L;

    private static final String MESSAGE = "Alíquota ad rem não encontrada para NCM %s e data %s (idTributo: %s)";

    public AliquotaAdRemNaoEncontradaException(String ncm, Long idTributo, LocalDate data) {
        super(String.format(MESSAGE, ncm, data, idTributo));
    }
}
