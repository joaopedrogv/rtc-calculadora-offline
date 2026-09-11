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

import br.gov.serpro.rtc.domain.model.entity.GrupoDfe;

/**
 * Repositório Spring Data JPA para acesso a {@link GrupoDfe}, com consulta dos
 * tipos de DFe vigentes agrupados pela sua categoria.
 */
@Repository
public interface GrupoDfeRepository extends JpaRepository<GrupoDfe, Long> {

    /**
     * Retorna os tipos de DFe vigentes na data informada, já ordenados por grupo
     * e por tipo, para montagem da estrutura agrupada exibida na seleção do tipo
     * de documento. Considera apenas grupos e tipos vigentes na data.
     *
     * <p>Cada linha traz: {@code grupo_id, grupo_titulo, tipo_sigla,
     * tipo_titulo, tipo_modelo}.
     */
    @NativeQuery(value = """
            SELECT
                g.GRDF_ID        AS grupo_id,
                g.GRDF_DESCRICAO AS grupo_titulo,
                t.TPDF_SIGLA     AS tipo_sigla,
                t.TPDF_DESCRICAO AS tipo_titulo,
                t.TPDF_TIPO      AS tipo_modelo
            FROM TIPO_DFE t
            JOIN GRUPO_DFE g ON t.TPDF_GRDF_ID = g.GRDF_ID
            WHERE :data BETWEEN t.TPDF_INICIO_VIGENCIA AND COALESCE(t.TPDF_FIM_VIGENCIA, :data)
              AND :data BETWEEN g.GRDF_INICIO_VIGENCIA AND COALESCE(g.GRDF_FIM_VIGENCIA, :data)
            ORDER BY g.GRDF_ID ASC, t.TPDF_ID ASC
            """)
    @Cacheable("GrupoDfeRepository.buscarTiposDfeAgrupados")
    List<Object[]> buscarTiposDfeAgrupados(@Param("data") LocalDate data);

}
