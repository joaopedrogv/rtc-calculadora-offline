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

    @NativeQuery(value = """
            SELECT DISTINCT
                st.SITR_ID as id_cst,
                st.SITR_CD as codigo_cst,
                st.SITR_DESCRICAO as descricao_cst,
                ct.CLTR_CD as codigo_classificacao,
                ct.CLTR_DESCRICAO as descricao_classificacao
            FROM TIPO_DFE_CLASSIFICACAO tdcl
            JOIN TIPO_DFE td ON tdcl.TDCL_TPDF_ID = td.TPDF_ID
            JOIN CLASSIFICACAO_TRIBUTARIA ct ON tdcl.TDCL_CLTR_ID = ct.CLTR_ID
            JOIN SITUACAO_TRIBUTARIA st ON ct.CLTR_SITR_ID = st.SITR_ID
            JOIN TRIBUTO_SITUACAO_TRIBUTARIA tst ON tst.TRST_SITR_ID = st.SITR_ID
                AND tst.TRST_TBTO_ID != 1
                AND :data BETWEEN tst.TRST_INICIO_VIGENCIA AND COALESCE(tst.TRST_FIM_VIGENCIA, :data)
            WHERE UPPER(REPLACE(REPLACE(REPLACE(td.TPDF_SIGLA, ' ', ''), '-', ''), '_', '')) = :siglaDfe
            AND :data BETWEEN tdcl.TDCL_INICIO_VIGENCIA AND COALESCE(tdcl.TDCL_FIM_VIGENCIA, :data)
            ORDER BY st.SITR_CD ASC, ct.CLTR_CD ASC
            """)
    @Cacheable("TipoDfeClassificacaoRepository.buscarCstsComClassificacoesCbsIbs")
    List<Object[]> buscarCstsComClassificacoesCbsIbs(
            @Param("siglaDfe") String siglaDfe,
            @Param("data") LocalDate data);

    @NativeQuery(value = """
            SELECT DISTINCT
                st.SITR_ID as id_cst,
                st.SITR_CD as codigo_cst,
                st.SITR_DESCRICAO as descricao_cst,
                ct.CLTR_CD as codigo_classificacao,
                ct.CLTR_DESCRICAO as descricao_classificacao
            FROM CLASSIFICACAO_TRIBUTARIA ct
            JOIN SITUACAO_TRIBUTARIA st ON ct.CLTR_SITR_ID = st.SITR_ID
            JOIN TRIBUTO_SITUACAO_TRIBUTARIA tst ON tst.TRST_SITR_ID = st.SITR_ID
                AND tst.TRST_TBTO_ID != 1
                AND :data BETWEEN tst.TRST_INICIO_VIGENCIA AND COALESCE(tst.TRST_FIM_VIGENCIA, :data)
            ORDER BY st.SITR_CD ASC, ct.CLTR_CD ASC
            """)
    @Cacheable("TipoDfeClassificacaoRepository.buscarCstsComClassificacoesCbsIbsTodosDfes")
    List<Object[]> buscarCstsComClassificacoesCbsIbsTodosDfes(@Param("data") LocalDate data);

    @NativeQuery(value = """
            SELECT DISTINCT
                st.SITR_ID as id_cst,
                st.SITR_CD as codigo_cst,
                st.SITR_DESCRICAO as descricao_cst,
                ct.CLTR_CD as codigo_classificacao,
                ct.CLTR_DESCRICAO as descricao_classificacao
            FROM CLASSIFICACAO_TRIBUTARIA ct
            JOIN SITUACAO_TRIBUTARIA st ON ct.CLTR_SITR_ID = st.SITR_ID
            JOIN TRIBUTO_SITUACAO_TRIBUTARIA tst ON tst.TRST_SITR_ID = st.SITR_ID
                AND tst.TRST_TBTO_ID = 1
                AND :data BETWEEN tst.TRST_INICIO_VIGENCIA AND COALESCE(tst.TRST_FIM_VIGENCIA, :data)
            ORDER BY st.SITR_CD ASC, ct.CLTR_CD ASC
            """)
    @Cacheable("TipoDfeClassificacaoRepository.buscarCstsComClassificacoesIs")
    List<Object[]> buscarCstsComClassificacoesIs(@Param("data") LocalDate data);

}
