package br.gov.serpro.rtc.domain.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import br.gov.serpro.rtc.api.exceptionhandler.enumeration.CodigoValidacao;
import br.gov.serpro.rtc.api.exceptionhandler.dto.ErroDescricao;
import br.gov.serpro.rtc.api.exceptionhandler.enumeration.ProblemType;

/**
 * Serviço responsável por resolver códigos de erro e validação em descrições
 * padronizadas usadas pela API.
 */
@Service
public class ErroService {

    public Optional<ErroDescricao> buscarPorCodigo(String codigo) {
        if (codigo == null) {
            return Optional.empty();
        }

        Optional<ProblemType> pt = ProblemType.fromCodigo(codigo);
        if (pt.isPresent()) {
            return Optional.of(new ErroDescricao(
                    codigo, pt.get().getTitulo(), pt.get().getDescricao()));
        }

        return CodigoValidacao.fromCodigo(codigo)
                .map(cv -> new ErroDescricao(codigo, cv.getTitulo(), cv.getDescricao()));
    }
}
