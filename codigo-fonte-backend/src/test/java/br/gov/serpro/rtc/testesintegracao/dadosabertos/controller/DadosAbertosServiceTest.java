package br.gov.serpro.rtc.testesintegracao.dadosabertos.controller;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import br.gov.serpro.rtc.domain.service.dadosabertos.DadosAbertosService;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-testes.yml")
@ActiveProfiles("testes")
class DadosAbertosServiceTest {

    private static final LocalDate DATA = LocalDate.parse("2027-01-01");
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
    void consultarNbs() {
        assertThat(dadosAbertosService.consultarNbs(NBS, DATA)).isNotNull();
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
        assertThat(dadosAbertosService.consultarAliquota(2L, null, null, DATA)).isNotNull();
    }

    @Test
    void consultarAliquotaUf() {
        assertThat(dadosAbertosService.consultarAliquota(3L, CODIGO_UF, null, DATA)).isNotNull();
    }

    @Test
    void consultarAliquotaMunicipio() {
        assertThat(dadosAbertosService.consultarAliquota(4L, null, CODIGO_MUNICIPIO, DATA)).isNotNull();
    }

    @Test
    void consultarValidadeDfeClassificacaoTributaria() {
        assertThat(dadosAbertosService
                .consultarValidadeDfeClassificacaoTributaria(SIGLA_DFE, C_CLASS_TRIB, DATA))
            .isNotNull();
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
