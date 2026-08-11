/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.gov.serpro.rtc.domain.model.entity.NbsAplicavel;

/**
 * Repositório Spring Data JPA que gerencia os vínculos entre códigos NBS e
 * classificações tributárias aplicáveis. Executa consultas de existência,
 * listagem e validação de exceções para determinar se um serviço pode ser usado
 * em determinada classificação.
 */
@Repository
public interface NbsAplicavelRepository extends JpaRepository<NbsAplicavel, Long> {

    @Query("""
            SELECT EXISTS (
                SELECT 1
                FROM NbsAplicavel n
                WHERE n.nbs = SUBSTRING(:nbs, 1, LENGTH(n.nbs))
                AND n.classificacaoTributaria.id = :idClassificacaoTributaria
                AND :data BETWEEN n.inicioVigencia AND COALESCE(n.fimVigencia, :data)
            )
            """)
    // FIXME essa cache pode ser removida pois o serviço que a utiliza já faz cache
    @Cacheable(cacheNames = "NbsAplicavelRepository.temNbsAplicavel")
    boolean temNbsAplicavel(
            @Param("nbs") String nbs,
            @Param("idClassificacaoTributaria") Long idClassificacaoTributaria, 
            @Param("data") LocalDate data);

    @Query("""
            SELECT EXISTS (
                SELECT 1
                FROM ExcecaoNbsAplicavel e
                JOIN e.nbsAplicavel n
                WHERE n.nbs = SUBSTRING(:nbs, 1, LENGTH(n.nbs))
                AND e.nbs = SUBSTRING(:nbs, 1, LENGTH(e.nbs))
                AND n.classificacaoTributaria.id = :idClassificacaoTributaria
                AND :data BETWEEN e.inicioVigencia AND COALESCE(e.fimVigencia, :data)
            )
            """)
    // FIXME essa cache pode ser removida pois o serviço que a utiliza já faz cache
    @Cacheable(cacheNames = "NbsAplicavelRepository.temExcecaoNbsAplicavel")
    boolean temExcecaoNbsAplicavel(
            @Param("nbs") String nbs,
            @Param("idClassificacaoTributaria") Long idClassificacaoTributaria, 
            @Param("data") LocalDate data);

    @Query("""
            SELECT EXISTS (
                SELECT 1
                FROM NbsAplicavel n
                LEFT JOIN ExcecaoNbsAplicavel e ON e.nbsAplicavel.id = n.id
                WHERE n.nbs = SUBSTRING(:nbs, 1, LENGTH(n.nbs))
                AND n.classificacaoTributaria.id = :idClassificacaoTributaria
                AND :data BETWEEN n.inicioVigencia AND COALESCE(n.fimVigencia, :data)
                AND (e.id IS NULL OR :data NOT BETWEEN e.inicioVigencia AND COALESCE(e.fimVigencia, :data))
            )
            """)
    // FIXME essa cache pode ser removida pois o serviço que a utiliza já faz cache
    @Cacheable(cacheNames = "NbsAplicavelRepository.temNbsAplicavelSemExcecao")
    boolean temNbsAplicavelSemExcecao(
            @Param("nbs") String nbs,
            @Param("idClassificacaoTributaria") Long idClassificacaoTributaria, 
            @Param("data") LocalDate data);

    /*
     * Entradas recomendadas na cache: 400
     * Memória estimada: ~45 KB
     */
    @Query("""
            SELECT EXISTS (
                SELECT 1
                FROM NbsAplicavel n
                WHERE n.classificacaoTributaria.id = :idClassificacaoTributaria)
            """)
    // FIXME essa cache pode ser removida pois o serviço que a utiliza já faz cache
    @Cacheable(cacheNames = "NbsAplicavelRepository.tem")
    boolean tem(
        @Param("idClassificacaoTributaria") Long idClassificacaoTributaria,
        @Param("data") LocalDate data);

    @NativeQuery(value = """
        SELECT DISTINCT nbs.NBS_CD, nbs.NBS_DESCRICAO
        FROM NBS_APLICAVEL n
        JOIN NBS nbs ON nbs.NBS_CD = n.NBSA_NBS_CD
        LEFT JOIN EXCECAO_NBS_APLICAVEL e ON e.ENBS_NBSA_ID = n.NBSA_ID 
            AND :data BETWEEN e.ENBS_INICIO_VIGENCIA AND COALESCE(e.ENBS_FIM_VIGENCIA, :data)
        JOIN CLASSIFICACAO_TRIBUTARIA ct ON ct.CLTR_ID = n.NBSA_CLTR_ID
        WHERE ct.CLTR_CD = :cClassTrib
        AND :data BETWEEN n.NBSA_INICIO_VIGENCIA AND COALESCE(n.NBSA_FIM_VIGENCIA, :data)
        AND :data BETWEEN nbs.NBS_INICIO_VIGENCIA AND COALESCE(nbs.NBS_FIM_VIGENCIA, :data)
        AND :data BETWEEN ct.CLTR_INICIO_VIGENCIA AND COALESCE(ct.CLTR_FIM_VIGENCIA, :data)
        AND e.ENBS_ID IS NULL
        ORDER BY nbs.NBS_CD
    """)
    @Cacheable(cacheNames = "NbsAplicavelRepository.listarNbsAplicaveisPorClassificacao")
    List<Object[]> listarNbsAplicaveisPorClassificacao(
        @Param("cClassTrib") String cClassTrib,
        @Param("data") LocalDate data);

    @NativeQuery(value = """
        SELECT ct.CLTR_CD
          FROM NBS_APLICAVEL n
          JOIN CLASSIFICACAO_TRIBUTARIA ct ON ct.CLTR_ID = n.NBSA_CLTR_ID
          LEFT JOIN EXCECAO_NBS_APLICAVEL e ON e.ENBS_NBSA_ID = n.NBSA_ID
            AND :data BETWEEN e.ENBS_INICIO_VIGENCIA AND COALESCE(e.ENBS_FIM_VIGENCIA, :data)
         WHERE n.NBSA_NBS_CD = :nbs
           AND :data BETWEEN n.NBSA_INICIO_VIGENCIA AND COALESCE(n.NBSA_FIM_VIGENCIA, :data)
           AND :data BETWEEN ct.CLTR_INICIO_VIGENCIA AND COALESCE(ct.CLTR_FIM_VIGENCIA, :data)
           AND e.ENBS_ID IS NULL
    """)
    @Cacheable(cacheNames = "NbsAplicavelRepository.listarCodigosClassificacoesVinculadasPorNbs")
    List<String> listarCodigosClassificacoesVinculadasPorNbs(@Param("nbs") String nbs, @Param("data") LocalDate data);

    @NativeQuery(value = """
        SELECT CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END
        FROM NBS_APLICAVEL n
        JOIN CLASSIFICACAO_TRIBUTARIA ct ON ct.CLTR_ID = n.NBSA_CLTR_ID
        WHERE ct.CLTR_CD = :cClassTrib
    """)
    @Cacheable(cacheNames = "NbsAplicavelRepository.existeVinculoParaClassificacao")
    Integer existeVinculoParaClassificacao(@Param("cClassTrib") String cClassTrib);
}
