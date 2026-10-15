package br.gov.serpro.rtc.testesintegracao.dadosabertos.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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
class DadosAbertosNcmVigentesControllerIT {

    private static final String ENDPOINT = "/calculadora/dados-abertos/ncm/vigentes";
    private static final String DATA_VALIDA = "2026-01-01";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void teste_controller_consultarNcmsVigentesComSucesso() throws Exception {
        mockMvc.perform(get(ENDPOINT)
                .param("data", DATA_VALIDA)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(0)))
            .andExpect(jsonPath("$[*].ncm").value(everyItem(matchesPattern("\\d{8}"))))
            .andExpect(jsonPath("$[*].descricao").isArray());
    }

    @Test
    void teste_controller_consultarNcmsVigentesSemResultadoDeveRetornarListaVazia() throws Exception {
        mockMvc.perform(get(ENDPOINT)
                .param("data", "1900-01-01")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void teste_controller_consultarNcmsVigentesDeveRetornarCachePublicoUmaHora() throws Exception {
        mockMvc.perform(get(ENDPOINT)
                .param("data", DATA_VALIDA)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(header().string("Cache-Control", containsString("public")))
            .andExpect(header().string("Cache-Control", containsString("max-age=3600")));
    }

    @Test
    void teste_controller_consultarNcmsVigentesDeveRetornarBadRequestQuandoDataNaoInformada() throws Exception {
        mockMvc.perform(get(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.title").value("Bad Request"))
            .andExpect(jsonPath("$.detail").value(containsString("Required parameter 'data' is not present.")));
    }

    @Test
    void teste_controller_consultarNcmsVigentesDeveRetornarBadRequestQuandoDataInvalida() throws Exception {
        mockMvc.perform(get(ENDPOINT)
                .param("data", "2026-99-99")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.title").value("Tipo de argumento inválido"))
            .andExpect(jsonPath("$.detail").value(containsString("Parâmetro inválido [data: 2026-99-99]")));
    }
}
