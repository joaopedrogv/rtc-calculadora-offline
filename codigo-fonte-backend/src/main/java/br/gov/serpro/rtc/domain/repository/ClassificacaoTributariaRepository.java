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

import br.gov.serpro.rtc.domain.model.dto.ClassificacaoTributariaCalculoDTO;
import br.gov.serpro.rtc.domain.model.dto.ClassificacaoTributariaDTO;
import br.gov.serpro.rtc.domain.model.entity.ClassificacaoTributaria;

/**
 * Repositório Spring Data JPA para acesso a {@link ClassificacaoTributaria},
 * com consultas por código e tributo, dados resumidos para cálculo e
 * classificações de serviço sem vínculo de NBS.
 */
@Repository
public interface ClassificacaoTributariaRepository extends JpaRepository<ClassificacaoTributaria, Long> {
    
    /*
     * Entradas recomendadas na cache: 1.600
     * Memória estimada: ~384 KB
     */
    @NativeQuery(value = """
            SELECT ct.CLTR_ID as id, ct.CLTR_CD as codigo, ct.CLTR_NOMENCLATURA as nomenclatura, st.SITR_CD as cst
            FROM CLASSIFICACAO_TRIBUTARIA ct
            JOIN SITUACAO_TRIBUTARIA st ON ct.CLTR_SITR_ID = st.SITR_ID
            JOIN TRIBUTO_SITUACAO_TRIBUTARIA tst ON tst.TRST_SITR_ID = st.SITR_ID
            JOIN TRIBUTO tb ON tst.TRST_TBTO_ID = tb.TBTO_ID
            WHERE ct.CLTR_CD = :codigo
            AND tb.TBTO_ID = :idTributo
            AND :data BETWEEN ct.CLTR_INICIO_VIGENCIA AND COALESCE(ct.CLTR_FIM_VIGENCIA, :data)
            AND :data BETWEEN st.SITR_INICIO_VIGENCIA AND COALESCE(st.SITR_FIM_VIGENCIA, :data)
            AND :data BETWEEN tst.TRST_INICIO_VIGENCIA AND COALESCE(tst.TRST_FIM_VIGENCIA, :data)
            AND :data BETWEEN tb.TBTO_INICIO_VIGENCIA AND COALESCE(tb.TBTO_FIM_VIGENCIA, :data)
            """, 
            sqlResultSetMapping = "ClassificacaoTributariaDTOMapping")
    @Cacheable(cacheNames = "ClassificacaoTributariaRepository.buscarClassificacaoTributaria")
    ClassificacaoTributariaDTO buscarClassificacaoTributaria(
            @Param("codigo") String codigo,
            @Param("idTributo") long idTributo,
            @Param("data") LocalDate data);

    @Query(value = """
            SELECT new br.gov.serpro.rtc.domain.model.dto.ClassificacaoTributariaCalculoDTO(
                    ct.id, 
                    ct.codigo, 
                    ct.tipoAliquota, 
                    ct.inGrupoMonofasiaPadrao, 
                    ct.inGrupoMonofasiaReten, 
                    ct.inGrupoMonofasiaRet,
                    ct.inGrupoMonofasiaDiferimento,
                    ct.situacaoTributaria.inGrupoReducao
            )
            FROM ClassificacaoTributaria ct
            WHERE ct.id = :id
            """)
    @Cacheable(cacheNames = "ClassificacaoTributariaRepository.buscarClassificacaoTributariaCalculo")
    ClassificacaoTributariaCalculoDTO buscarClassificacaoTributariaCalculo(
            @Param("id") long id);

    @NativeQuery(value = """
            SELECT ct.CLTR_CD
            FROM CLASSIFICACAO_TRIBUTARIA ct
            WHERE (ct.CLTR_NOMENCLATURA LIKE '%NBS%')
            AND :data BETWEEN ct.CLTR_INICIO_VIGENCIA AND COALESCE(ct.CLTR_FIM_VIGENCIA, :data)
            AND ct.CLTR_ID NOT IN (
                SELECT n.NBSA_CLTR_ID
                FROM NBS_APLICAVEL n
                WHERE :data BETWEEN n.NBSA_INICIO_VIGENCIA AND COALESCE(n.NBSA_FIM_VIGENCIA, :data)
            )
    """)
    @Cacheable(cacheNames = "ClassificacaoTributariaRepository.listarCodigosClassificacoesServicoSemVinculoNbs")
    List<String> listarCodigosClassificacoesServicoSemVinculoNbs(@Param("data") LocalDate data);

    /**
     * Retorna os códigos das classificações tributárias vigentes na data que
     * admitem a combinação de atores informada e, opcionalmente, o tipo de
     * DF-e. Classificação sem nenhum vínculo vigente em {@code ATOR_CLASSIFICACAO}
     * no papel consultado é aplicável a qualquer ator naquele papel e permanece
     * no resultado; filtros informados simultaneamente compõem um E lógico.
     * Filtros {@code null} não restringem o papel/DF-e correspondente.
     *
     * @param data       data de referência para filtragem de vigência
     * @param fornecedor ATOR_ID do ator no papel de fornecedor (opcional)
     * @param adquirente ATOR_ID do ator no papel de adquirente (opcional)
     * @param siglaDfe   sigla normalizada do tipo de DF-e (opcional), no mesmo
     *                   formato de {@code buscarCstsComClassificacoesCbsIbs}
     */
    @NativeQuery(value = """
            SELECT ct.CLTR_CD
            FROM CLASSIFICACAO_TRIBUTARIA ct
            WHERE :data BETWEEN ct.CLTR_INICIO_VIGENCIA AND COALESCE(ct.CLTR_FIM_VIGENCIA, :data)
            AND ( :fornecedor IS NULL
                  OR NOT EXISTS (SELECT 1 FROM ATOR_CLASSIFICACAO ac
                                  WHERE ac.ATCL_CLTR_ID = ct.CLTR_ID
                                    AND ac.ATCL_IN_PAPEL = 'Fornecedor'
                                    AND :data BETWEEN ac.ATCL_INICIO_VIGENCIA
                                                  AND COALESCE(ac.ATCL_FIM_VIGENCIA, :data))
                  OR EXISTS (SELECT 1 FROM ATOR_CLASSIFICACAO ac
                              WHERE ac.ATCL_CLTR_ID = ct.CLTR_ID
                                AND ac.ATCL_IN_PAPEL = 'Fornecedor'
                                AND ac.ATCL_ATOR_ID = :fornecedor
                                AND :data BETWEEN ac.ATCL_INICIO_VIGENCIA
                                              AND COALESCE(ac.ATCL_FIM_VIGENCIA, :data)) )
            AND ( :adquirente IS NULL
                  OR NOT EXISTS (SELECT 1 FROM ATOR_CLASSIFICACAO ac
                                  WHERE ac.ATCL_CLTR_ID = ct.CLTR_ID
                                    AND ac.ATCL_IN_PAPEL = 'Adquirente'
                                    AND :data BETWEEN ac.ATCL_INICIO_VIGENCIA
                                                  AND COALESCE(ac.ATCL_FIM_VIGENCIA, :data))
                  OR EXISTS (SELECT 1 FROM ATOR_CLASSIFICACAO ac
                              WHERE ac.ATCL_CLTR_ID = ct.CLTR_ID
                                AND ac.ATCL_IN_PAPEL = 'Adquirente'
                                AND ac.ATCL_ATOR_ID = :adquirente
                                AND :data BETWEEN ac.ATCL_INICIO_VIGENCIA
                                              AND COALESCE(ac.ATCL_FIM_VIGENCIA, :data)) )
            AND ( :siglaDfe IS NULL
                  OR EXISTS (SELECT 1 FROM TIPO_DFE_CLASSIFICACAO tdcl
                              JOIN TIPO_DFE td ON tdcl.TDCL_TPDF_ID = td.TPDF_ID
                              WHERE tdcl.TDCL_CLTR_ID = ct.CLTR_ID
                                AND UPPER(REPLACE(REPLACE(REPLACE(td.TPDF_SIGLA, ' ', ''), '-', ''), '_', '')) = :siglaDfe
                                AND :data BETWEEN tdcl.TDCL_INICIO_VIGENCIA
                                              AND COALESCE(tdcl.TDCL_FIM_VIGENCIA, :data)) )
            """)
    @Cacheable(cacheNames = "ClassificacaoTributariaRepository.listarCodigosClassificacoesPorAtores")
    List<String> listarCodigosClassificacoesPorAtores(
            @Param("data") LocalDate data,
            @Param("fornecedor") Long fornecedor,
            @Param("adquirente") Long adquirente,
            @Param("siglaDfe") String siglaDfe);
}
