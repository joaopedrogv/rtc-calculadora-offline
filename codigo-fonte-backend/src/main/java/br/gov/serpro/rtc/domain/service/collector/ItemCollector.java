package br.gov.serpro.rtc.domain.service.collector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.gov.serpro.rtc.api.exceptionhandler.dto.CodigoErro;
import br.gov.serpro.rtc.api.exceptionhandler.enumeration.CodigoValidacao;
import br.gov.serpro.rtc.api.exceptionhandler.enumeration.ProblemType;
import br.gov.serpro.rtc.api.exceptionhandler.dto.ErroDetalhe;
import br.gov.serpro.rtc.domain.model.enumeration.EstadoItemEnum;

/**
 * Classe responsável por registrar o estado de processamento de um item e
 * converter falhas em detalhes padronizados de erro para observabilidade.
 */
public class ItemCollector {

    private final int index;
    private final String baseURL;
    private final List<ErroDetalhe> erros = new ArrayList<>();
    private EstadoItemEnum estado = EstadoItemEnum.CALCULADO;

    public ItemCollector(int index, String baseURL) {
        this.index = index;
        this.baseURL = baseURL;
    }

    public void addErroValidacao(String code, String campo, String detail) {
        if (estado == EstadoItemEnum.CALCULADO) {
            estado = EstadoItemEnum.INCONSISTENCIA_ENTRADA;
        }
        String pointer = buildPointer(campo);
        String type = buildTypeURI(code);
        String title = CodigoValidacao.fromCodigo(code)
                .map(CodigoValidacao::getTitulo)
                .orElse("Erro de validação");
        erros.add(new ErroDetalhe(type, title, code, pointer, detail));
    }

    public void addErro(Exception ex) {
        ProblemType problemType = ProblemType.from(ex);
        CodigoErro codigoErro = problemType.getCodigoErro();

        if (codigoErro != null) {
            estado = codigoErro.estado();
            String pointer = buildPointer(codigoErro.campoDefault());
            String type = buildTypeURI(codigoErro.codigo());
            erros.add(new ErroDetalhe(type, problemType.getTitulo(), codigoErro.codigo(), pointer, ex.getMessage()));
        } else {
            estado = EstadoItemEnum.FALHA_TECNICA;
            String pointer = "#/itens/" + index;
            String type = buildTypeURI("CAL-999");
            erros.add(new ErroDetalhe(type, problemType.getTitulo(), "CAL-999", pointer, ex.getMessage()));
        }
    }

    private String buildTypeURI(String codigo) {
        return String.format("%s/api/calculadora/observabilidade/erros?codigo=%s", baseURL, codigo);
    }

    private String buildPointer(String campo) {
        if (campo == null) {
            return "#/itens/" + index;
        }
        return "#/itens/" + index + "/" + campo;
    }

    public EstadoItemEnum getEstado() {
        return estado;
    }

    public List<ErroDetalhe> getErros() {
        return Collections.unmodifiableList(erros);
    }

    public boolean hasErros() {
        return !erros.isEmpty();
    }
}
