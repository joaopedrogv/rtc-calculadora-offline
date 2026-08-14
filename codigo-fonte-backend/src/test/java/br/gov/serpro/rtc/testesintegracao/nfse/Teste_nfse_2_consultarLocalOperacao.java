/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.testesintegracao.nfse;

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
class Teste_nfse_2_consultarLocalOperacao {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void teste_controller_consultarLocalOperacaoComSucesso() throws Exception {
        mockMvc.perform(get("/calculadora/nfse/local-operacao")
                .param("cIndOp", "100301")
                .param("dataOcorrenciaFatoGerador", "2026-01-01")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cIndOp").value("100301"))
                .andExpect(jsonPath("$.dataOcorrenciaFatoGerador").value("2026-01-01"))
		.andExpect(jsonPath("$.codigoLocalFornecimento").exists())
                .andExpect(jsonPath("$.codigoLocalIncidencia").exists());
    }

    @Test
    void teste_controller_consultarLocalOperacaoIndicadorInexistente() throws Exception {
        mockMvc.perform(get("/calculadora/nfse/local-operacao")
                .param("cIndOp", "999999")
                .param("dataOcorrenciaFatoGerador", "2026-01-01")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void teste_controller_consultarLocalOperacaoParametroInvalido() throws Exception {
        mockMvc.perform(get("/calculadora/nfse/local-operacao")
                .param("cIndOp", "12345") // 5 dígitos em vez de 6
                .param("dataOcorrenciaFatoGerador", "2026-01-01")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void teste_controller_consultarLocalOperacaoDataInvalida() throws Exception {
        mockMvc.perform(get("/calculadora/nfse/local-operacao")
                .param("cIndOp", "100301")
                .param("dataOcorrenciaFatoGerador", "invalid-date")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void teste_controller_consultarLocalOperacaoSemParametrosObrigatorios() throws Exception {
        mockMvc.perform(get("/calculadora/nfse/local-operacao")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}