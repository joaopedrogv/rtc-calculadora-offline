/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.testesintegracao.nfse;

import static org.hamcrest.Matchers.greaterThan;
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
class Teste_nfse_1_consultarIndicadoresOperacao {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void teste_controller_consultarIndicadoresOperacaoSemNbs() throws Exception {
        mockMvc.perform(get("/calculadora/nfse/indicador-operacao")
                .param("dataOcorrenciaFatoGerador", "2026-01-01")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThan(-1)))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void teste_controller_consultarIndicadoresOperacaoComNbs() throws Exception {
        mockMvc.perform(get("/calculadora/nfse/indicador-operacao")
                .param("dataOcorrenciaFatoGerador", "2026-01-01")
                .param("nbs", "115021000")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void teste_controller_consultarIndicadoresOperacaoComNbsInvalido() throws Exception {
        mockMvc.perform(get("/calculadora/nfse/indicador-operacao")
                .param("dataOcorrenciaFatoGerador", "2026-01-01")
                .param("nbs", "999999999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void teste_controller_consultarIndicadoresOperacaoDataInvalida() throws Exception {
        mockMvc.perform(get("/calculadora/nfse/indicador-operacao")
                .param("dataOcorrenciaFatoGerador", "invalid-date")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void teste_controller_consultarIndicadoresOperacaoSemDataObrigatoria() throws Exception {
        mockMvc.perform(get("/calculadora/nfse/indicador-operacao")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}