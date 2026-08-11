/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.testesintegracao.nfse;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import com.fasterxml.jackson.databind.ObjectMapper;

import br.gov.serpro.rtc.api.model.input.nfse.NfseValidacaoIndicadorOperacaoInput;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-testes.yml")
@ActiveProfiles("testes")
class Teste_nfse_4_validarIndicadorOperacao {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void teste_controller_validarIndicadorOperacaoComCTribNac() throws Exception {
        NfseValidacaoIndicadorOperacaoInput input = new NfseValidacaoIndicadorOperacaoInput();
        input.setNbs("123456789");
        input.setCClassTrib("620001");
        input.setCIndOp("100301");
        input.setCTribNac("0101");
        input.setDataOcorrenciaFatoGerador(java.time.LocalDate.of(2026, 1, 1));

        mockMvc.perform(post("/calculadora/nfse/indicador-operacao/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cIndOp").exists())
                .andExpect(jsonPath("$.cClassTrib").exists())
                .andExpect(jsonPath("$.nbs").exists())
                .andExpect(jsonPath("$.dataOcorrenciaFatoGerador").exists())
                .andExpect(jsonPath("$.valido").exists());
    }

    @Test
    void teste_controller_validarIndicadorOperacaoSemCTribNac() throws Exception {
        NfseValidacaoIndicadorOperacaoInput input = new NfseValidacaoIndicadorOperacaoInput();
        input.setNbs("123456789");
        input.setCClassTrib("620001");
        input.setCIndOp("100301");
        input.setDataOcorrenciaFatoGerador(java.time.LocalDate.of(2026, 1, 1));

        mockMvc.perform(post("/calculadora/nfse/indicador-operacao/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cIndOp").exists())
                .andExpect(jsonPath("$.cClassTrib").exists())
                .andExpect(jsonPath("$.nbs").exists())
                .andExpect(jsonPath("$.dataOcorrenciaFatoGerador").exists())
                .andExpect(jsonPath("$.valido").exists());
    }

    @Test
    void teste_controller_validarIndicadorOperacaoCombinacaoInvalida() throws Exception {
        NfseValidacaoIndicadorOperacaoInput input = new NfseValidacaoIndicadorOperacaoInput();
        input.setNbs("999999999");
        input.setCClassTrib("999999"); // Must be 6 digits
        input.setCIndOp("999999");
        input.setCTribNac("9999");
        input.setDataOcorrenciaFatoGerador(java.time.LocalDate.of(2026, 1, 1));

        mockMvc.perform(post("/calculadora/nfse/indicador-operacao/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valido").exists());
    }

    @Test
    void teste_controller_validarIndicadorOperacaoDadosObrigatoriosNulos() throws Exception {
        NfseValidacaoIndicadorOperacaoInput input = new NfseValidacaoIndicadorOperacaoInput();

        mockMvc.perform(post("/calculadora/nfse/indicador-operacao/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());
    }
}