/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.gov.serpro.rtc.domain.model.entity.TipoDfe;

/**
 * Repositório Spring Data JPA para acesso a {@link TipoDfe}, com consulta do
 * tipo de documento fiscal vigente pelo seu código numérico ({@code TPDF_TIPO},
 * ex.: 55 = NF-e, 65 = NFC-e).
 */
@Repository
public interface TipoDfeRepository extends JpaRepository<TipoDfe, Long> {

    /*
     * Entradas recomendadas na cache: 50
     * O campo TPDF_TIPO é único por tipo de documento e populado para todos os
     * registros.
     */
    @Query("""
            FROM TipoDfe
            WHERE tipo = :tipo
            AND :data BETWEEN inicioVigencia AND COALESCE(fimVigencia, :data)
            """)
    @Cacheable("TipoDfeRepository.buscarPorTipo")
    Optional<TipoDfe> buscarPorTipo(
            @Param("tipo") Integer tipo,
            @Param("data") LocalDate data);

}
