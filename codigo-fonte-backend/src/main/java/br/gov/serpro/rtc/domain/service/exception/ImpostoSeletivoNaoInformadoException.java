/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service.exception;

import java.time.LocalDate;

/**
 * Exceção lançada quando a operação exige o grupo de Imposto Seletivo, mas os
 * dados desse tributo não são informados.
 */
public class ImpostoSeletivoNaoInformadoException extends ValidacaoException {

    private static final long serialVersionUID = 529236725876113L;
    private static final String MESSAGE = "Dados do Imposto Seletivo devem ser informados para %s %s e data %s";

    public ImpostoSeletivoNaoInformadoException(String tipoNomenclatura, String codigoNomenclatura, LocalDate data) {
        super(String.format(MESSAGE, tipoNomenclatura, codigoNomenclatura, data));
    }

}
