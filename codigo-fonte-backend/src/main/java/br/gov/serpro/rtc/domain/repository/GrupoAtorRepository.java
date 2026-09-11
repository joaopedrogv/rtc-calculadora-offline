/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.gov.serpro.rtc.domain.model.entity.GrupoAtor;

/**
 * Repositório Spring Data JPA para acesso a {@link GrupoAtor}, com consulta
 * dos atores vigentes agrupados por grupo de ator, opcionalmente filtrados
 * por papel (Fornecedor/Adquirente).
 */
@Repository
public interface GrupoAtorRepository extends JpaRepository<GrupoAtor, Long> {

    /**
     * Retorna os atores vigentes na data informada, agrupados por grupo de ator,
     * opcionalmente filtrados pelo papel na classificação. Quando {@code papel}
     * é {@code null}, retorna todos os atores vigentes.
     *
     * <p>Cada linha traz: {@code grupo_id, grupo_descricao, grupo_ordem,
     * ator_id, ator_descricao, ator_ordem}.
     */
    @NativeQuery(value = """
            SELECT
                g.GRAT_ID        AS grupo_id,
                g.GRAT_DESCRICAO AS grupo_descricao,
                g.GRAT_ORDEM     AS grupo_ordem,
                a.ATOR_ID        AS ator_id,
                a.ATOR_DESCRICAO AS ator_descricao,
                a.ATOR_ORDEM     AS ator_ordem
            FROM ATOR a
            JOIN GRUPO_ATOR g ON a.ATOR_GRAT_ID = g.GRAT_ID
            WHERE a.ATOR_INICIO_VIGENCIA <= :data
              AND (a.ATOR_FIM_VIGENCIA IS NULL OR a.ATOR_FIM_VIGENCIA >= :data)
              AND g.GRAT_INICIO_VIGENCIA <= :data
              AND (g.GRAT_FIM_VIGENCIA IS NULL OR g.GRAT_FIM_VIGENCIA >= :data)
              AND (
                  :papel IS NULL
                  OR EXISTS (
                      SELECT 1 FROM ATOR_CLASSIFICACAO ac
                      WHERE ac.ATCL_ATOR_ID = a.ATOR_ID
                        AND ac.ATCL_IN_PAPEL = :papel
                        AND ac.ATCL_INICIO_VIGENCIA <= :data
                        AND (ac.ATCL_FIM_VIGENCIA IS NULL OR ac.ATCL_FIM_VIGENCIA >= :data)
                  )
              )
            ORDER BY g.GRAT_ORDEM ASC, a.ATOR_ORDEM ASC
            """)
    @Cacheable("GrupoAtorRepository.buscarAtoresAgrupados")
    List<Object[]> buscarAtoresAgrupados(@Param("data") LocalDate data,
                                         @Param("papel") String papel);
}
