/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service.exception;

/**
 * Exceção lançada, no modo de validação rígido, quando NCM ou NBS é informado
 * para um tipo de documento fiscal que não admite nomenclatura (documentos
 * fora do trio NF-e/NFC-e/NFS-e).
 */
public class NomenclaturaNaoPermitidaTipoDfeException extends ValidacaoException {

    private static final long serialVersionUID = 1L;
    private static final String MESSAGE = "NCM/NBS não permitido para a Classificação Tributária de código %s no tipo de documento %s";

    public NomenclaturaNaoPermitidaTipoDfeException(String cClassTrib, String siglaDfe) {
        super(String.format(MESSAGE, cClassTrib, siglaDfe));
    }

}
