/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service.exception;

/**
 * Exceção lançada quando a nomenclatura informada (NCM ou NBS) não é aceita
 * para a classificação tributária no tipo de documento fiscal da operação —
 * por exemplo, NBS em NF-e ou NCM em NFS-e sem NCM aplicável.
 */
public class NomenclaturaIncompativelTipoDfeException extends ValidacaoException {

    private static final long serialVersionUID = 1L;
    private static final String MESSAGE = "%s não permitida para a Classificação Tributária de código %s no tipo de documento %s";

    public NomenclaturaIncompativelTipoDfeException(String nomenclatura, String cClassTrib, String siglaDfe) {
        super(String.format(MESSAGE, nomenclatura, cClassTrib, siglaDfe));
    }

}
