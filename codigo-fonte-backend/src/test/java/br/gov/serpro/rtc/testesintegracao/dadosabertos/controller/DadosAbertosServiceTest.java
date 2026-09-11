package br.gov.serpro.rtc.testesintegracao.dadosabertos.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import br.gov.serpro.rtc.api.model.output.dadosabertos.NomenclaturaDadosAbertosOutput;
import br.gov.serpro.rtc.domain.service.dadosabertos.DadosAbertosService;
import br.gov.serpro.rtc.domain.service.exception.AliquotaNaoEncontradaException;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-testes.yml")
@ActiveProfiles("testes")
class DadosAbertosServiceTest {

    private static final LocalDate DATA = LocalDate.parse("2027-01-01");
    // Alíquotas de referência/padrão só existem em lei até 2026; a partir de
    // 2027 a consulta de alíquotas é criticada.
    private static final LocalDate DATA_COM_ALIQUOTAS_EM_LEI = LocalDate.parse("2026-01-01");
    private static final String NCM = "09024000";
    private static final String NBS = "102020000";
    private static final String SIGLA_UF = "AC";
    private static final Long CODIGO_UF = 12L;
    private static final Long CODIGO_MUNICIPIO = 1200013L;
    private static final String CST = "000";
    private static final String C_CLASS_TRIB = "000001";
    private static final Long ID_SITUACAO_TRIBUTARIA = 1L;
    private static final String SIGLA_DFE = "NFE";

    // IDs de situação tributária usados pelo controller: 2 = CBS/IBS, 1 = Imposto Seletivo
    private static final Long ID_ST_CBS_IBS = 2L;
    private static final Long ID_ST_IMPOSTO_SELETIVO = 1L;

    @Autowired
    private DadosAbertosService dadosAbertosService;

    @Test
    void consultarUfs() {
        assertThat(dadosAbertosService.consultarUfs()).isNotNull();
    }

    @Test
    void consultarMunicipiosPorSiglaUf() {
        assertThat(dadosAbertosService.consultarMunicipiosPorSiglaUf(SIGLA_UF)).isNotNull();
    }

    @Test
    void consultarSituacoesTributariasCbsIbs() {
        assertThat(dadosAbertosService.consultarSituacoesTributarias(ID_ST_CBS_IBS, DATA)).isNotNull();
    }

    @Test
    void consultarClassificacoesTributariasPorIdSituacaoTributaria() {
        assertThat(dadosAbertosService
                .consultarClassificacoesTributariasPorIdSituacaoTributaria(ID_SITUACAO_TRIBUTARIA, DATA))
            .isNotNull();
    }

    @Test
    void listarPorCstImpostoSeletivo() {
        assertThat(dadosAbertosService
                .consultarClassificacoesTributariasPorCstETributoTipo(CST, List.of("IS"), DATA))
            .isNotNull();
    }

    @Test
    void listarPorCstCbsIbs() {
        assertThat(dadosAbertosService
                .consultarClassificacoesTributariasPorCstETributoTipo(CST, List.of("CBS", "IBSUF", "IBSMun"), DATA))
            .isNotNull();
    }

    @Test
    void consultarClassificacoesTributariasCbsIbs() {
        assertThat(dadosAbertosService.consultarClassificacoesTributariasCbsIbs(DATA)).isNotNull();
    }

    @Test
    void consultarClassificacoesTributariasImpostoSeletivo() {
        assertThat(dadosAbertosService.consultarClassificacoesTributariasImpostoSeletivo(DATA)).isNotNull();
    }

    @Test
    void listarClassificacaoAplicavelPorNbs() {
        assertThat(dadosAbertosService.listarClassificacaoAplicavelPorNbs(NBS, DATA)).isNotNull();
    }

    @Test
    void consultarSituacoesTributariasImpostoSeletivo() {
        assertThat(dadosAbertosService.consultarSituacoesTributarias(ID_ST_IMPOSTO_SELETIVO, DATA)).isNotNull();
    }

    @Test
    void consultarNcm() {
        assertThat(dadosAbertosService.consultarNcm(NCM, DATA)).isNotNull();
    }

    @Test
    void consultarNcmTributadaPeloImpostoSeletivoSemValorDefinidoEmLei() {
        // A incidência do IS é determinada pela existência do registro de
        // alíquota, mesmo com o valor ainda não definido em lei (NULL): os
        // indicadores tem* são expostos e os valores numéricos são omitidos.
        var ncm = dadosAbertosService.consultarNcm("24021000", DATA);
        assertThat(ncm.isTributadoPeloImpostoSeletivo()).isTrue();
        assertThat(ncm.isTemAliquotaAdValorem()).isTrue();
        assertThat(ncm.isTemAliquotaAdRem()).isTrue();
        assertThat(ncm.getAliquotaAdValorem()).isNull();
        assertThat(ncm.getAliquotaAdRem()).isNull();
    }

    @Test
    void consultarNcmNaoTributadaPeloImpostoSeletivo() {
        var ncm = dadosAbertosService.consultarNcm(NCM, DATA);
        assertThat(ncm.isTributadoPeloImpostoSeletivo()).isFalse();
        assertThat(ncm.isTemAliquotaAdValorem()).isFalse();
        assertThat(ncm.isTemAliquotaAdRem()).isFalse();
    }

    @Test
    void consultarNbs() {
        assertThat(dadosAbertosService.consultarNbs(NBS, DATA)).isNotNull();
    }

    @Test
    void consultarNbsNaoTributadaPeloImpostoSeletivo() {
        var nbs = dadosAbertosService.consultarNbs(NBS, DATA);
        assertThat(nbs.isTributadoPeloImpostoSeletivo()).isFalse();
        assertThat(nbs.isTemAliquotaAdValorem()).isFalse();
    }

    @Test
    void listarNbs() {
        assertThat(dadosAbertosService.listarNbs(DATA)).isNotNull();
    }

    @Test
    void listarNbsAplicaveisPorClassificacao() {
        assertThat(dadosAbertosService.listarNbsAplicaveisPorClassificacao(C_CLASS_TRIB, DATA)).isNotNull();
    }

    @Test
    void consultarFundamentacoesLegais() {
        assertThat(dadosAbertosService.consultarFundamentacoesLegais(DATA)).isNotNull();
    }

    @Test
    void consultarAliquotaUniao() {
        assertThat(dadosAbertosService.consultarAliquota(2L, null, null, DATA_COM_ALIQUOTAS_EM_LEI)).isNotNull();
    }

    @Test
    void consultarAliquotaUf() {
        assertThat(dadosAbertosService.consultarAliquota(3L, CODIGO_UF, null, DATA_COM_ALIQUOTAS_EM_LEI)).isNotNull();
    }

    @Test
    void consultarAliquotaMunicipio() {
        assertThat(dadosAbertosService.consultarAliquota(4L, null, CODIGO_MUNICIPIO, DATA_COM_ALIQUOTAS_EM_LEI)).isNotNull();
    }

    @Test
    void consultarAliquotaUniaoSemAliquotaDefinidaEmLei() {
        // A partir de 2027 as alíquotas de referência não existem em lei e não
        // são mais divulgadas pela calculadora.
        assertThatThrownBy(() -> dadosAbertosService.consultarAliquota(2L, null, null, DATA))
                .isInstanceOf(AliquotaNaoEncontradaException.class);
    }

    @Test
    void consultarAliquotaUfSemAliquotaDefinidaEmLei() {
        assertThatThrownBy(() -> dadosAbertosService.consultarAliquota(3L, CODIGO_UF, null, DATA))
                .isInstanceOf(AliquotaNaoEncontradaException.class);
    }

    @Test
    void consultarAliquotaMunicipioSemAliquotaDefinidaEmLei() {
        assertThatThrownBy(() -> dadosAbertosService.consultarAliquota(4L, null, CODIGO_MUNICIPIO, DATA))
                .isInstanceOf(AliquotaNaoEncontradaException.class);
    }

    @Test
    void consultarNomenclatura() {
        var result = dadosAbertosService
                .consultarNomenclatura(SIGLA_DFE, C_CLASS_TRIB, DATA);
        assertThat(result.getSiglaDfe()).isEqualTo(SIGLA_DFE);
        assertThat(result.getCClassTrib()).isEqualTo(C_CLASS_TRIB);
        assertThat(result.getData()).isEqualTo(DATA);
        assertThat(result.getNomenclatura()).isEqualTo("SEM");
    }

    @Test
    void consultarClassificacaoTributariaIsPorCodigo() {
        assertThat(dadosAbertosService.consultarClassificacaoTributariaIsPorCodigo(C_CLASS_TRIB, DATA))
            .isNotNull();
    }

    @Test
    void consultarCstsComClassificacoesCbsIbsPorSiglaDfe() {
        assertThat(dadosAbertosService.consultarCstsComClassificacoesCbsIbs(SIGLA_DFE, DATA))
            .isNotNull()
            .isNotEmpty();
    }

    @Test
    void consultarCstsComClassificacoesCbsIbsSemSiglaDfe() {
        assertThat(dadosAbertosService.consultarCstsComClassificacoesCbsIbs(null, DATA))
            .isNotNull()
            .isNotEmpty();
    }

    @Test
    void consultarCstsComClassificacoesIsPorSiglaDfe() {
        assertThat(dadosAbertosService.consultarCstsComClassificacoesIs(SIGLA_DFE, DATA))
            .isNotNull();
    }

    @Test
    void consultarCstsComClassificacoesIsSemSiglaDfe() {
        assertThat(dadosAbertosService.consultarCstsComClassificacoesIs(null, DATA))
            .isNotNull();
    }

    @Test
    void consultarTiposDfeAgrupados() {
        var grupos = dadosAbertosService.consultarTiposDfeAgrupados(DATA);
        assertThat(grupos).isNotNull().isNotEmpty();
        // Nenhum grupo sem tipos e ordem sequencial iniciando em 1
        assertThat(grupos).allSatisfy(grupo -> {
            assertThat(grupo.getTipos()).isNotEmpty();
            assertThat(grupo.getOrdem()).isPositive();
        });
        assertThat(grupos.get(0).getOrdem()).isEqualTo(1);
        // codigo normalizado casa com o siglaDfe consumido pelos demais endpoints
        assertThat(grupos.get(0).getTipos().get(0).getCodigo()).isEqualTo(SIGLA_DFE);
    }

    @Test
    void consultarTiposDfeAgrupados_semData_usaDataAtual() {
        assertThat(dadosAbertosService.consultarTiposDfeAgrupados(null))
            .isNotNull()
            .isNotEmpty();
    }

    @Test
    void consultarRedutoresCompraGovernamental() {
        assertThat(dadosAbertosService.consultarRedutoresCompraGovernamental()).isNotNull();
    }

    @Test
    void consultarTransferenciasCBS() {
        assertThat(dadosAbertosService.consultarTransferenciasCBS()).isNotNull();
    }

    @Test
    void consultarTransferenciasIBS() {
        assertThat(dadosAbertosService.consultarTransferenciasIBS()).isNotNull();
    }
}
