package br.gov.serpro.rtc.domain.service.collector;

import java.util.List;

import br.gov.serpro.rtc.api.exceptionhandler.dto.ErroDetalhe;

/**
 * Exceção que encapsula a lista de erros globais identificados durante o
 * processamento em modo de observabilidade.
 */
public class ErrosCalculoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final List<ErroDetalhe> erros;

    public ErrosCalculoException(List<ErroDetalhe> erros) {
        super("Erros de cálculo encontrados");
        this.erros = List.copyOf(erros);
    }

    public List<ErroDetalhe> getErros() {
        return erros;
    }
}
