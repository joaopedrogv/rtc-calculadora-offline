/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.testesintegracao.dadosabertos;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-testes.yml")
@ActiveProfiles("testes")
class Teste_dadosabertos_11_situacaoClassificacaoAninhada {

    private static final String URI_CBS_IBS = "/calculadora/dados-abertos/situacoes-tributarias/cbs-ibs";
    private static final String URI_IS = "/calculadora/dados-abertos/situacoes-tributarias/is";

    @Autowired
    private MockMvc mockMvc;

    // --- CBS/IBS ---

    @Test
    void teste_controller_cbsIbs_dfeExistente_dataValida() throws Exception {
        mockMvc.perform(get(URI_CBS_IBS)
                .param("siglaDfe", "NFE")
                .param("data", "2027-01-01")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$[0].id", notNullValue()))
                .andExpect(jsonPath("$[0].codigo", notNullValue()))
                .andExpect(jsonPath("$[0].descricao", notNullValue()))
                .andExpect(jsonPath("$[0].classificacoesTributarias", notNullValue()))
                .andExpect(jsonPath("$[0].classificacoesTributarias[0].codigo", notNullValue()))
                .andExpect(jsonPath("$[0].classificacoesTributarias[0].descricao", notNullValue()));
    }

    @Test
    void teste_controller_cbsIbs_semSiglaDfe_dataValida() throws Exception {
        mockMvc.perform(get(URI_CBS_IBS)
                .param("data", "2027-01-01")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.length()", greaterThan(0)))
                .andExpect(jsonPath("$[0].id", notNullValue()))
                .andExpect(jsonPath("$[0].codigo", notNullValue()))
                .andExpect(jsonPath("$[0].descricao", notNullValue()))
                .andExpect(jsonPath("$[0].classificacoesTributarias", notNullValue()))
                .andExpect(jsonPath("$[0].classificacoesTributarias.length()", greaterThan(0)));
    }

    @Test
    void teste_controller_cbsIbs_dfeInexistente_dataValida() throws Exception {
        mockMvc.perform(get(URI_CBS_IBS)
                .param("siglaDfe", "XXXX")
                .param("data", "2027-01-01")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void teste_controller_cbsIbs_dfeExistente_dataAnterior() throws Exception {
        mockMvc.perform(get(URI_CBS_IBS)
                .param("siglaDfe", "NFE")
                .param("data", "1980-01-01")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.length()", is(0)));
    }

    @Test
    void teste_controller_cbsIbs_semSiglaDfe_dataAnterior() throws Exception {
        mockMvc.perform(get(URI_CBS_IBS)
                .param("data", "1980-01-01")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.length()", is(0)));
    }

    @Test
    void teste_controller_cbsIbs_normalizaSiglaDfe() throws Exception {
        mockMvc.perform(get(URI_CBS_IBS)
                .param("siglaDfe", "nf-e ")
                .param("data", "2027-01-01")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.length()", greaterThan(0)));
    }

    @Test
    void teste_controller_cbsIbs_retornaListaOrdenada() throws Exception {
        mockMvc.perform(get(URI_CBS_IBS)
                .param("siglaDfe", "NFE")
                .param("data", "2027-01-01")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$[0].codigo", notNullValue()))
                .andExpect(jsonPath("$[0].classificacoesTributarias", notNullValue()))
                .andExpect(jsonPath("$[0].classificacoesTributarias.length()", greaterThan(0)));
    }

    // --- IS ---

    @Test
    void teste_controller_is_dfeExistente_dataValida() throws Exception {
        mockMvc.perform(get(URI_IS)
                .param("siglaDfe", "NFE")
                .param("data", "2027-01-01")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()));
    }

    @Test
    void teste_controller_is_semSiglaDfe_dataValida() throws Exception {
        mockMvc.perform(get(URI_IS)
                .param("data", "2027-01-01")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.length()", greaterThan(0)))
                .andExpect(jsonPath("$[0].id", notNullValue()))
                .andExpect(jsonPath("$[0].codigo", notNullValue()))
                .andExpect(jsonPath("$[0].classificacoesTributarias", notNullValue()));
    }

    @Test
    void teste_controller_is_dfeInexistente_dataValida() throws Exception {
        mockMvc.perform(get(URI_IS)
                .param("siglaDfe", "XXXX")
                .param("data", "2027-01-01")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void teste_controller_is_dfeExistente_dataAnterior() throws Exception {
        mockMvc.perform(get(URI_IS)
                .param("siglaDfe", "NFE")
                .param("data", "1980-01-01")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.length()", is(0)));
    }

    @Test
    void teste_controller_is_normalizaSiglaDfe() throws Exception {
        mockMvc.perform(get(URI_IS)
                .param("siglaDfe", "nf-e ")
                .param("data", "2027-01-01")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()));
    }
}
