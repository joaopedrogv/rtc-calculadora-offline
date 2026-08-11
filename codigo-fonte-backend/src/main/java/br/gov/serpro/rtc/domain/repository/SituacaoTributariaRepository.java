/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.gov.serpro.rtc.domain.model.entity.SituacaoTributaria;

/**
 * Repositório Spring Data JPA para acesso a {@link SituacaoTributaria}, com
 * consultas de situações tributárias por tributo, validação de CST e busca por
 * código vigente.
 */
@Repository
public interface SituacaoTributariaRepository extends JpaRepository<SituacaoTributaria, Long> {

	@Query("""
			SELECT t.situacaoTributaria
			FROM TributoSituacaoTributaria t
			WHERE t.tributo.id = :idTributo
			AND t.inicioVigencia <= :data
			AND (t.fimVigencia IS NULL OR t.fimVigencia >= :data)
			""")
	List<SituacaoTributaria> consultarPorIdTributo(
			@Param("idTributo") Long idTributo,
			@Param("data") LocalDate data);

	/*
	 * Entradas recomendadas na cache: 240
	 * Memória estimada: ~28 KB
	 */
	@Query("""
	        SELECT EXISTS (
                SELECT 1
				FROM TributoSituacaoTributaria t
				WHERE t.tributo.id = :idTributo
				AND t.situacaoTributaria.codigo = :cst
				AND :data BETWEEN t.inicioVigencia AND COALESCE(t.fimVigencia, :data)
				AND :data BETWEEN t.situacaoTributaria.inicioVigencia AND COALESCE(t.situacaoTributaria.fimVigencia, :data)
			)
			""")
	@Cacheable(cacheNames = "SituacaoTributariaRepository.existeCst")
	boolean existeCst(
			@Param("cst") String cst,
			@Param("idTributo") Long idTributo,
			@Param("data") LocalDate data);

	/**
	 * Consulta uma situação tributária por código, tributo e data de vigência.
	 * Query nativa para SQLite, usando COALESCE para todas as tabelas envolvidas.
	 */
	@Query(value = """
		SELECT s.*
		FROM SITUACAO_TRIBUTARIA s
		JOIN TRIBUTO_SITUACAO_TRIBUTARIA tst ON tst.TRST_SITR_ID = s.SITR_ID
		JOIN TRIBUTO t ON t.TBTO_ID = tst.TRST_TBTO_ID
		WHERE COALESCE(s.SITR_CD, '') = COALESCE(:cst, '')
		  AND COALESCE(t.TBTO_ID, 0) = COALESCE(:idTributo, 0)
		  AND COALESCE(:data, '') BETWEEN COALESCE(tst.TRST_INICIO_VIGENCIA, '') AND COALESCE(tst.TRST_FIM_VIGENCIA, :data)
		  AND COALESCE(:data, '') BETWEEN COALESCE(s.SITR_INICIO_VIGENCIA, '') AND COALESCE(s.SITR_FIM_VIGENCIA, :data)
		  AND COALESCE(:data, '') BETWEEN COALESCE(t.TBTO_INICIO_VIGENCIA, '') AND COALESCE(t.TBTO_FIM_VIGENCIA, :data)
		LIMIT 1
	""", nativeQuery = true)
	SituacaoTributaria consultarSituacaoTributariaPorCodigo(
		@Param("cst") String cst,
		@Param("idTributo") Long idTributo,
		@Param("data") String data
	);

}
