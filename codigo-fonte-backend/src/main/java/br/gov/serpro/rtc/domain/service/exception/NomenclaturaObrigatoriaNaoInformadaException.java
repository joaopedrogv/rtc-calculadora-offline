/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service.exception;

/**
 * Exceção lançada quando a classificação tributária possui anexo com NCM e NBS
 * aplicáveis (nomenclatura MISTO) e nenhuma das duas nomenclaturas foi
 * informada completa no item.
 */
public class NomenclaturaObrigatoriaNaoInformadaException extends ValidacaoException {

    private static final long serialVersionUID = 1L;
    private static final String MESSAGE = "A Classificação Tributária de código %s exige NCM ou NBS completo para o tipo de documento %s";

    public NomenclaturaObrigatoriaNaoInformadaException(String cClassTrib, String siglaDfe) {
        super(String.format(MESSAGE, cClassTrib, siglaDfe));
    }

}
