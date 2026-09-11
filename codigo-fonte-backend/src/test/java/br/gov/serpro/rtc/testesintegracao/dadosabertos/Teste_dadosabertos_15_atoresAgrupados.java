/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.testesintegracao.dadosabertos;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
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

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-testes.yml")
@ActiveProfiles("testes")
@DisplayName("Atores agrupados por Grupo de Ator - Integration Tests")
class Teste_dadosabertos_15_atoresAgrupados {

    private static final String URI = "/calculadora/dados-abertos/ator/grupos";
    private static final String DATA_VIGENTE = "2027-01-01";

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Retorna 200 com lista de grupos não-vazia para data vigente (sem papel)")
    void teste_atoresAgrupados_dataVigente_retornaGrupos() throws Exception {
        mockMvc.perform(get(URI)
                .param("data", DATA_VIGENTE)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.length()", greaterThan(0)));
    }

    @Test
    @DisplayName("Retorna 200 com lista filtrada para papel FORNECEDOR")
    void teste_atoresAgrupados_papelFornecedor() throws Exception {
        mockMvc.perform(get(URI)
                .param("data", DATA_VIGENTE)
                .param("papel", "FORNECEDOR")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()));
    }

    @Test
    @DisplayName("Retorna 200 com lista filtrada para papel ADQUIRENTE")
    void teste_atoresAgrupados_papelAdquirente() throws Exception {
        mockMvc.perform(get(URI)
                .param("data", DATA_VIGENTE)
                .param("papel", "ADQUIRENTE")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()));
    }

    @Test
    @DisplayName("Estrutura do grupo e dos atores aninhados conforme contrato")
    void teste_atoresAgrupados_estruturaAninhada() throws Exception {
        mockMvc.perform(get(URI)
                .param("data", DATA_VIGENTE)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", notNullValue()))
                .andExpect(jsonPath("$[0].descricao", notNullValue()))
                .andExpect(jsonPath("$[0].ordem", notNullValue()))
                .andExpect(jsonPath("$[0].atores", notNullValue()))
                .andExpect(jsonPath("$[0].atores.length()", greaterThan(0)))
                .andExpect(jsonPath("$[0].atores[0].id", notNullValue()))
                .andExpect(jsonPath("$[0].atores[0].descricao", notNullValue()))
                .andExpect(jsonPath("$[0].atores[0].ordem", notNullValue()));
    }

    @Test
    @DisplayName("Grupos vêm ordenados por ordem crescente")
    void teste_atoresAgrupados_gruposOrdenados() throws Exception {
        mockMvc.perform(get(URI)
                .param("data", DATA_VIGENTE)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ordem", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$..ordem", everyItem(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("HTTP 400 quando parâmetro data ausente")
    void teste_atoresAgrupados_semData_retorna400() throws Exception {
        mockMvc.perform(get(URI)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("HTTP 400 quando papel tem valor inválido")
    void teste_atoresAgrupados_papelInvalido_retorna400() throws Exception {
        mockMvc.perform(get(URI)
                .param("data", DATA_VIGENTE)
                .param("papel", "INVALIDO")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
