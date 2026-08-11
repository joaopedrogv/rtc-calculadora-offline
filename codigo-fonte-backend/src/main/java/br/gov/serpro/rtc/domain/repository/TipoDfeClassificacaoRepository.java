/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.gov.serpro.rtc.domain.model.entity.TipoDfeClassificacao;

/**
 * Repositório Spring Data JPA para acesso a {@link TipoDfeClassificacao}, com
 * consulta dos vínculos vigentes entre tipo de DFe e classificação tributária.
 */
@Repository
public interface TipoDfeClassificacaoRepository extends JpaRepository<TipoDfeClassificacao, Long> {

    @Query("""
            FROM TipoDfeClassificacao
            WHERE classificacaoTributaria.id = :idClassificacaoTributaria
            AND (inicioVigencia <= :data AND (fimVigencia IS NULL OR fimVigencia >= :data))
            """)
    @Cacheable("TipoDfeClassificacaoRepository.buscar")
    List<TipoDfeClassificacao> buscar(Long idClassificacaoTributaria, LocalDate data);

}
