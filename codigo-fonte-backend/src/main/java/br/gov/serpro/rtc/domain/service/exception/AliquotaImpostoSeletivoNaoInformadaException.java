/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service.exception;

import java.time.LocalDate;

/**
 * Exceção lançada quando a NCM/NBS possui incidência do Imposto Seletivo na
 * data do fato gerador, mas a alíquota ainda não foi definida em lei e o
 * usuário não informou a alíquota nominal do Imposto Seletivo.
 */
public class AliquotaImpostoSeletivoNaoInformadaException extends ValidacaoException {

    private static final long serialVersionUID = 529236725876116L;
    private static final String MESSAGE = "A alíquota do Imposto Seletivo deve ser informada em "
            + "aliquotasNominais.impostoSeletivo para %s %s e data %s, pois ainda não foi definida em lei";

    public AliquotaImpostoSeletivoNaoInformadaException(String tipoNomenclatura, String codigoNomenclatura,
            LocalDate data) {
        super(String.format(MESSAGE, tipoNomenclatura, codigoNomenclatura, data));
    }

}
