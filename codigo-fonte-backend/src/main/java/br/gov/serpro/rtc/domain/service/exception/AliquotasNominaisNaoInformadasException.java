/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service.exception;

/**
 * Exceção lançada quando as alíquotas nominais de CBS e IBS são obrigatórias
 * para a operação (fato gerador a partir de 01/01/2027), mas não foram
 * informadas ou foram informadas parcialmente.
 */
public class AliquotasNominaisNaoInformadasException extends ValidacaoException {

    private static final long serialVersionUID = 529236725876115L;
    private static final String MESSAGE = "As alíquotas nominais (os três campos cbs, ibsEstadual e ibsMunicipal) "
            + "devem ser informadas para operações com fato gerador a partir de 01/01/2027";

    public AliquotasNominaisNaoInformadasException() {
        super(MESSAGE);
    }

}
