/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.testesintegracao.dadosabertos;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.jayway.jsonpath.JsonPath;

import net.minidev.json.JSONArray;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-testes.yml")
@ActiveProfiles("testes")
@DisplayName("Classificações Tributárias CBS/IBS por Atores - Integration Tests")
class Teste_dadosabertos_16_classificacoesPorAtores {

    private static final String URI = "/calculadora/dados-abertos/classificacoes-tributarias/cbs-ibs/por-atores";
    private static final String URI_SEM_FILTRO = "/calculadora/dados-abertos/classificacoes-tributarias/cbs-ibs";
    private static final String DATA_VIGENTE = "2027-01-01";

    // Classificação com vínculo de fornecedor 27 e adquirente 28
    private static final String CLASSIFICACAO_FORNECEDOR_27_ADQUIRENTE_28 = "200054";
    // Classificação com restrição apenas de adquirente (ator 70)
    private static final String CLASSIFICACAO_APENAS_ADQUIRENTE_70 = "550017";
    // Classificação sem nenhum vínculo de ator (aplicável a qualquer ator)
    private static final String CLASSIFICACAO_SEM_RESTRICAO = "000001";

    @Autowired
    private MockMvc mockMvc;

    private int consultarQuantidade(String uri, String... params) throws Exception {
        var request = get(uri).param("data", DATA_VIGENTE).contentType(MediaType.APPLICATION_JSON);
        for (int i = 0; i < params.length; i += 2) {
            request = request.param(params[i], params[i + 1]);
        }
        String json = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.<JSONArray>read(json, "$").size();
    }

    @Test
    @DisplayName("Sem filtros retorna a mesma quantidade do endpoint de classificações CBS/IBS")
    void teste_semFiltros_mesmaQuantidadeDoEndpointCbsIbs() throws Exception {
        int quantidadeSemFiltro = consultarQuantidade(URI_SEM_FILTRO);
        int quantidadePorAtores = consultarQuantidade(URI);

        assertTrue(quantidadeSemFiltro > 0);
        assertEquals(quantidadeSemFiltro, quantidadePorAtores);
    }

    @Test
    @DisplayName("Fornecedor 27 e adquirente 28 retorna 200054 e também as classificações sem restrição")
    void teste_fornecedor27Adquirente28_contem200054ESemRestricao() throws Exception {
        mockMvc.perform(get(URI)
                .param("data", DATA_VIGENTE)
                .param("fornecedor", "27")
                .param("adquirente", "28")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..codigo", hasItem(CLASSIFICACAO_FORNECEDOR_27_ADQUIRENTE_28)))
                .andExpect(jsonPath("$..codigo", hasItem(CLASSIFICACAO_SEM_RESTRICAO)));
    }

    @Test
    @DisplayName("Fornecedor 27 e adquirente 29 não retorna 200054, mas mantém as sem restrição")
    void teste_fornecedor27Adquirente29_naoContem200054() throws Exception {
        mockMvc.perform(get(URI)
                .param("data", DATA_VIGENTE)
                .param("fornecedor", "27")
                .param("adquirente", "29")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..codigo", not(hasItem(CLASSIFICACAO_FORNECEDOR_27_ADQUIRENTE_28))))
                .andExpect(jsonPath("$..codigo", hasItem(CLASSIFICACAO_SEM_RESTRICAO)));
    }

    @Test
    @DisplayName("Classificação restrita a adquirente 70 aparece com adquirente=70")
    void teste_restricaoApenasAdquirente_adquirenteVinculado_aparece() throws Exception {
        mockMvc.perform(get(URI)
                .param("data", DATA_VIGENTE)
                .param("adquirente", "70")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..codigo", hasItem(CLASSIFICACAO_APENAS_ADQUIRENTE_70)));
    }

    @Test
    @DisplayName("Classificação restrita a adquirente 70 não aparece com adquirente=22")
    void teste_restricaoApenasAdquirente_adquirenteNaoVinculado_naoAparece() throws Exception {
        mockMvc.perform(get(URI)
                .param("data", DATA_VIGENTE)
                .param("adquirente", "22")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..codigo", not(hasItem(CLASSIFICACAO_APENAS_ADQUIRENTE_70))));
    }

    @Test
    @DisplayName("Classificação restrita a adquirente aparece quando apenas fornecedor é filtrado")
    void teste_restricaoApenasAdquirente_papelAdquirenteNaoFiltrado_aparece() throws Exception {
        mockMvc.perform(get(URI)
                .param("data", DATA_VIGENTE)
                .param("fornecedor", "22")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..codigo", hasItem(CLASSIFICACAO_APENAS_ADQUIRENTE_70)));
    }

    @Test
    @DisplayName("siglaDfe válida reduz ou mantém a quantidade da lista")
    void teste_siglaDfe_reduzOuMantemLista() throws Exception {
        int quantidadeSemFiltro = consultarQuantidade(URI);
        int quantidadeComSigla = consultarQuantidade(URI, "siglaDfe", "NFSe");

        assertTrue(quantidadeComSigla > 0);
        assertThat(quantidadeComSigla, lessThanOrEqualTo(quantidadeSemFiltro));
    }

    @Test
    @DisplayName("siglaDfe combinada com atores aplica E lógico")
    void teste_siglaDfeCombinadaComAtores_aplicaELogico() throws Exception {
        // 200054 é vinculada à NFS-e: aparece para o par (27, 28) com siglaDfe=NFSe
        mockMvc.perform(get(URI)
                .param("data", DATA_VIGENTE)
                .param("fornecedor", "27")
                .param("adquirente", "28")
                .param("siglaDfe", "NFSe")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..codigo", hasItem(CLASSIFICACAO_FORNECEDOR_27_ADQUIRENTE_28)));

        // 200054 não é vinculada à CT-e: mesmo com o par (27, 28), sai da lista
        mockMvc.perform(get(URI)
                .param("data", DATA_VIGENTE)
                .param("fornecedor", "27")
                .param("adquirente", "28")
                .param("siglaDfe", "CTe")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..codigo", not(hasItem(CLASSIFICACAO_FORNECEDOR_27_ADQUIRENTE_28))))
                .andExpect(jsonPath("$..codigo", hasItem(CLASSIFICACAO_SEM_RESTRICAO)));
    }

    @Test
    @DisplayName("HTTP 400 quando parâmetro data ausente")
    void teste_semData_retorna400() throws Exception {
        mockMvc.perform(get(URI)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Fornecedor inexistente retorna apenas as classificações sem restrição de fornecedor")
    void teste_fornecedorInexistente_retornaApenasSemRestricaoDeFornecedor() throws Exception {
        mockMvc.perform(get(URI)
                .param("data", DATA_VIGENTE)
                .param("fornecedor", "9999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.length()", greaterThan(0)))
                .andExpect(jsonPath("$..codigo", not(hasItem(CLASSIFICACAO_FORNECEDOR_27_ADQUIRENTE_28))))
                .andExpect(jsonPath("$..codigo", hasItem(CLASSIFICACAO_SEM_RESTRICAO)));
    }
}
