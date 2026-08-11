/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service.exception;

/**
 * Exceção lançada quando a data do fato gerador, necessária para determinar
 * vigência e regras tributárias, não é informada na operação.
 */
public class DataFatoGeradorNaoInformadaException extends CampoInvalidoException {

    private static final long serialVersionUID = 529876772972777L;

    public DataFatoGeradorNaoInformadaException() {
        super("A data do fato gerador não foi informada.");
    }

}
