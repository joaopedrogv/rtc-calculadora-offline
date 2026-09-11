/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import br.gov.serpro.rtc.domain.model.entity.TipoDfe;
import br.gov.serpro.rtc.domain.model.entity.TipoDfeClassificacao;
import br.gov.serpro.rtc.domain.repository.TipoDfeClassificacaoRepository;
import br.gov.serpro.rtc.domain.service.exception.ClassificacaoTributariaNaoVinculadaTipoDfeException;
import lombok.RequiredArgsConstructor;

/**
 * Serviço responsável por consultar os tipos de DF-e vinculados às
 * classificações tributárias disponíveis.
 */
@RequiredArgsConstructor
@Service
public class TipoDfeClassificacaoService {

    private final TipoDfeClassificacaoRepository tipoDfeClassificacaoRepository;

    @Cacheable(cacheNames = "TipoDfeClassificacaoService.buscar")
    public List<TipoDfeClassificacao> buscar(Long idClassificacaoTributaria, LocalDate data) {
        return tipoDfeClassificacaoRepository.buscar(idClassificacaoTributaria, data);
    }

    /**
     * Valida que a classificação tributária possui vínculo vigente com o tipo
     * de documento fiscal informado (tabela {@code TIPO_DFE_CLASSIFICACAO}).
     *
     * @throws ClassificacaoTributariaNaoVinculadaTipoDfeException se não houver
     *         vínculo vigente na data informada
     */
    public void validarVinculo(TipoDfe tipoDfe, Long idClassificacaoTributaria, String cClassTrib,
            LocalDate data, String tributos) {
        // buscar já é cacheado; a resolução do proxy Spring garante o uso da cache
        boolean vinculado = tipoDfeClassificacaoRepository.buscar(idClassificacaoTributaria, data).stream()
                .anyMatch(t -> t.getTipoDfe().getId().equals(tipoDfe.getId()));
        if (!vinculado) {
            throw new ClassificacaoTributariaNaoVinculadaTipoDfeException(cClassTrib, tipoDfe.getTipo(), tributos);
        }
    }

}