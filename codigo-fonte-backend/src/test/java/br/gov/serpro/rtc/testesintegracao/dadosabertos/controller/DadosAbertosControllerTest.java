package br.gov.serpro.rtc.testesintegracao.dadosabertos.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-testes.yml")
@ActiveProfiles("testes")
class DadosAbertosControllerTest {

    private static final String DATA = "2027-01-01";
    private static final String NCM = "09024000";
    private static final String NBS = "102020000";
    private static final String SIGLA_UF = "AC";
    private static final String CODIGO_UF = "12";
    private static final String CODIGO_MUNICIPIO = "1200013";
    private static final String CST = "000";
    private static final String C_CLASS_TRIB = "000001";
    private static final String ID_SITUACAO_TRIBUTARIA = "1";
    private static final String SIGLA_DFE = "NFE";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void consultarUfs() throws Exception {
        mockMvc.perform(get("/calculadora/dados-abertos/ufs"))
            .andExpect(status().isOk());
    }

    @Test
    void consultarMunicipiosPorSiglaUf() throws Exception {
        mockMvc.perform(get("/calculadora/dados-abertos/ufs/municipios")
                .param("siglaUf", SIGLA_UF))
            .andExpect(status().isOk());
    }

    @Test
    void consultarSituacoesTributariasCbsIbs() throws Exception {
        mockMvc.perform(get("/calculadora/dados-abertos/situacoes-tributarias/cbs-ibs")
                .param("data", DATA))
            .andExpect(status().isOk());
    }

    @Test
    void consultarClassificacoesTributariasPorIdSituacaoTributaria() throws Exception {
        mockMvc.perform(get("/calculadora/dados-abertos/classificacoes-tributarias/" + ID_SITUACAO_TRIBUTARIA)
                .param("data", DATA))
            .andExpect(status().isOk());
    }

    @Test
    void listarPorCstImpostoSeletivo() throws Exception {
        mockMvc.perform(get("/calculadora/dados-abertos/classificacoes-tributarias/imposto-seletivo/" + CST)
                .param("data", DATA))
            .andExpect(status().isOk());
    }

    @Test
    void listarPorCstCbsIbs() throws Exception {
        mockMvc.perform(get("/calculadora/dados-abertos/classificacoes-tributarias/cbs-ibs/" + CST)
                .param("data", DATA))
            .andExpect(status().isOk());
    }

    @Test
    void consultarClassificacoesTributariasCbsIbs() throws Exception {
        mockMvc.perform(get("/calculadora/dados-abertos/classificacoes-tributarias/cbs-ibs")
                .param("data", DATA))
            .andExpect(status().isOk());
    }

    @Test
    void consultarClassificacoesTributariasImpostoSeletivo() throws Exception {
        mockMvc.perform(get("/calculadora/dados-abertos/classificacoes-tributarias/imposto-seletivo")
                .param("data", DATA))
            .andExpect(status().isOk());
    }

    @Test
    void listarClassificacoesTributariasPorNbs() throws Exception {
        mockMvc.perform(get("/calculadora/dados-abertos/classificacoes-tributarias/nbs")
                .param("nbs", NBS)
                .param("data", DATA))
            .andExpect(status().isOk());
    }

    @Test
    void consultarSituacoesTributariasImpostoSeletivo() throws Exception {
        mockMvc.perform(get("/calculadora/dados-abertos/situacoes-tributarias/imposto-seletivo")
                .param("data", DATA))
            .andExpect(status().isOk());
    }

    @Test
    void consultarNcm() throws Exception {
        mockMvc.perform(get("/calculadora/dados-abertos/ncm")
                .param("ncm", NCM)
                .param("data", DATA))
            .andExpect(status().isOk());
    }

    @Test
    void consultarNbs() throws Exception {
        mockMvc.perform(get("/calculadora/dados-abertos/nbs")
                .param("nbs", NBS)
                .param("data", DATA))
            .andExpect(status().isOk());
    }

    @Test
    void listarNbs() throws Exception {
        mockMvc.perform(get("/calculadora/dados-abertos/nbs/lista")
                .param("data", DATA))
            .andExpect(status().isOk());
    }

    @Test
    void listarNbsAplicaveisPorClassificacao() throws Exception {
        mockMvc.perform(get("/calculadora/dados-abertos/nbs-aplicaveis")
                .param("cClassTrib", C_CLASS_TRIB)
                .param("data", DATA))
            .andExpect(status().isOk());
    }

    @Test
    void consultarFundamentacoesLegais() throws Exception {
        mockMvc.perform(get("/calculadora/dados-abertos/fundamentacoes-legais")
                .param("data", DATA))
            .andExpect(status().isOk());
    }

    @Test
    void consultarAliquotaUniao() throws Exception {
        mockMvc.perform(get("/calculadora/dados-abertos/aliquota-uniao")
                .param("data", DATA))
            .andExpect(status().isOk());
    }

    @Test
    void consultarAliquotaUf() throws Exception {
        mockMvc.perform(get("/calculadora/dados-abertos/aliquota-uf")
                .param("codigoUf", CODIGO_UF)
                .param("data", DATA))
            .andExpect(status().isOk());
    }

    @Test
    void consultarAliquotaMunicipio() throws Exception {
        mockMvc.perform(get("/calculadora/dados-abertos/aliquota-municipio")
                .param("codigoMunicipio", CODIGO_MUNICIPIO)
                .param("data", DATA))
            .andExpect(status().isOk());
    }

    @Test
    void consultarValidadeDfeClassificacaoTributaria() throws Exception {
        mockMvc.perform(get("/calculadora/dados-abertos/classificacoes-tributarias/cbs-ibs/"
                + SIGLA_DFE + "/" + C_CLASS_TRIB)
                .param("data", DATA))
            .andExpect(status().isOk());
    }

    @Test
    void consultarVersao() throws Exception {
        mockMvc.perform(get("/calculadora/dados-abertos/versao"))
            .andExpect(status().isOk());
    }

    @Test
    void consultarRedutoresCompraGovernamental() throws Exception {
        mockMvc.perform(get("/calculadora/dados-abertos/redutores-compra-governamental"))
            .andExpect(status().isOk());
    }

    @Test
    void consultarTransferenciasCBS() throws Exception {
        mockMvc.perform(get("/calculadora/dados-abertos/transferencias-cbs"))
            .andExpect(status().isOk());
    }

    @Test
    void consultarTransferenciasIBS() throws Exception {
        mockMvc.perform(get("/calculadora/dados-abertos/transferencias-ibs"))
            .andExpect(status().isOk());
    }
}
