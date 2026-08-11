/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import br.gov.serpro.rtc.domain.model.dto.ClassificacaoTributariaCalculoDTO;
import br.gov.serpro.rtc.domain.model.dto.ClassificacaoTributariaDTO;
import br.gov.serpro.rtc.domain.repository.ClassificacaoTributariaRepository;
import br.gov.serpro.rtc.domain.service.exception.ClassificacaoTributariaNaoEncontradaException;
import lombok.RequiredArgsConstructor;

/**
 * Serviço responsável por localizar classificações tributárias de CBS, IBS e
 * Imposto Seletivo e validar sua vinculação ao contexto de cálculo.
 */
@RequiredArgsConstructor
@Service
public class ClassificacaoTributariaService {

    private final ClassificacaoTributariaRepository repository;

    public ClassificacaoTributariaDTO buscarClassificacaoTributariaCbsIbs(String codigo, LocalDate data) {
        return buscarClassificacaoTributaria(codigo, 2L, data, "CBS e IBS");
    }

    public ClassificacaoTributariaDTO buscarClassificacaoTributariaImpostoSeletivo(String codigo, LocalDate data) {
        return buscarClassificacaoTributaria(codigo, 1L, data, "Imposto Seletivo");
    }
    
    private ClassificacaoTributariaDTO buscarClassificacaoTributaria(String codigo, Long idTributo, LocalDate data, String tipo) {
        final var c = repository.buscarClassificacaoTributaria(codigo, idTributo, data);
        if (c == null) {
            throw new ClassificacaoTributariaNaoEncontradaException(codigo, tipo, data);
        }
        return c;
    }
    
    public ClassificacaoTributariaCalculoDTO buscarClassificacaoTributariaCalculo(Long id) {
        final var c = repository.buscarClassificacaoTributariaCalculo(id);
        if (c == null) {
            throw new ClassificacaoTributariaNaoEncontradaException(id);
        }
        return c;
    }
    
    public List<String> listarCodigosClassificacoesServicoSemVinculoNbs(LocalDate data) {
        return repository.listarCodigosClassificacoesServicoSemVinculoNbs(data);
    }

}