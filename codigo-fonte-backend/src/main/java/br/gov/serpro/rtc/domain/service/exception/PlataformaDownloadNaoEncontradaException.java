/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service.exception;

/**
 * Exceção lançada quando a plataforma de download solicitada não está
 * presente na configuração {@code application.download.platforms}.
 */
public class PlataformaDownloadNaoEncontradaException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private static final String MESSAGE = "Plataforma de download '%s' não encontrada. Plataformas disponíveis: %s";

    public PlataformaDownloadNaoEncontradaException(String plataforma, Iterable<String> plataformasDisponiveis) {
        super(String.format(MESSAGE, plataforma, plataformasDisponiveis));
    }
}
