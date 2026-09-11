/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service.exception;

/**
 * Exceção lançada quando a classificação tributária informada não possui
 * vínculo vigente com o tipo de documento fiscal (tpDoc) da operação.
 */
public class ClassificacaoTributariaNaoVinculadaTipoDfeException extends ValidacaoException {

    private static final long serialVersionUID = 1L;
    private static final String MESSAGE = "Classificação Tributária de código %s não vinculada ao tipo de documento fiscal (tpDoc) %d (%s)";

    public ClassificacaoTributariaNaoVinculadaTipoDfeException(String cClassTrib, Integer tpDoc, String tributos) {
        super(String.format(MESSAGE, cClassTrib, tpDoc, tributos));
    }

}
