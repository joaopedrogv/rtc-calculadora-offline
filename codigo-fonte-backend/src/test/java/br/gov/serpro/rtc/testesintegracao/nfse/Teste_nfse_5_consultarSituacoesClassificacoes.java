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
class Teste_nfse_5_consultarSituacoesClassificacoes {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void teste_controller_consultarSituacoesClassificacoesPorNbs() throws Exception {
        mockMvc.perform(get("/calculadora/nfse/situacoes-classificacoes-tributarias")
                .param("nbs", "115021000")
                .param("data", "2026-01-01")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThan(-1))) // Aceita 0 ou mais resultados
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void teste_controller_consultarSituacoesClassificacoesSemParametros() throws Exception {
        mockMvc.perform(get("/calculadora/nfse/situacoes-classificacoes-tributarias")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void teste_controller_consultarSituacoesClassificacoesDataInvalida() throws Exception {
        mockMvc.perform(get("/calculadora/nfse/situacoes-classificacoes-tributarias")
                .param("nbs", "123456789")
                .param("data", "invalid-date")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void teste_controller_consultarSituacoesClassificacoesNbsInvalido() throws Exception {
        mockMvc.perform(get("/calculadora/nfse/situacoes-classificacoes-tributarias")
                .param("nbs", "12345") // NBS com menos de 9 dígitos
                .param("data", "2026-01-01")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void teste_controller_consultarSituacoesClassificacoes_DeveRetornar404QuandoNbsInvalido() throws Exception {
        mockMvc.perform(get("/calculadora/nfse/situacoes-classificacoes-tributarias")
                .param("nbs", "999999999")
                .param("data", "2026-01-01")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void teste_controller_consultarSituacoesClassificacoes_DeveRetornar404QuandoNbsForaDaVigencia() throws Exception {
        // NBS 126050000 starts from 2025-01-01; 2024-12-31 is before its vigência
        mockMvc.perform(get("/calculadora/nfse/situacoes-classificacoes-tributarias")
                .param("nbs", "126050000")
                .param("data", "2024-12-31")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
