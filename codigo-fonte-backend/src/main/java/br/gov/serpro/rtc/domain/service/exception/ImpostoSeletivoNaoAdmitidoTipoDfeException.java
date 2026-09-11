/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service.exception;

/**
 * Exceção lançada quando o NCM/NBS do item é tributado pelo Imposto Seletivo,
 * mas o tipo de documento fiscal (tpDoc) da operação não admite Imposto
 * Seletivo.
 */
public class ImpostoSeletivoNaoAdmitidoTipoDfeException extends ValidacaoException {

    private static final long serialVersionUID = 1L;
    private static final String MESSAGE = "O tipo de documento fiscal (tpDoc) %d não admite Imposto Seletivo, exigido para %s de código %s";

    public ImpostoSeletivoNaoAdmitidoTipoDfeException(Integer tpDoc, String nomenclatura, String codigo) {
        super(String.format(MESSAGE, tpDoc, nomenclatura, codigo));
    }

}
