/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service.exception;

/**
 * Exceção lançada quando a NCM informada não possui vínculo válido com a
 * classificação tributária usada na operação.
 */
public class NcmNaoVinculadaException extends ValidacaoException {

    private static final long serialVersionUID = 7846374985007827745L;
    private static final String MESSAGE = "NCM de código %s não vinculada à Classificação Tributária de código %s (%s)";

    public NcmNaoVinculadaException(String ncm, String cClassTrib, String tributo) {
        super(String.format(MESSAGE, ncm, cClassTrib, tributo));
    }

}