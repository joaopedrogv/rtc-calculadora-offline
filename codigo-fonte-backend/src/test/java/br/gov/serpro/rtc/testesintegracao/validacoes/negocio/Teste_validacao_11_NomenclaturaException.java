/*
* Versão de Homologação/Testes
*/
package br.gov.serpro.rtc.testesintegracao.validacoes.negocio;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.gov.serpro.rtc.api.model.input.OperacaoInput;
import br.gov.serpro.rtc.domain.service.CalculadoraService;

/**
 * Regressão da abolição da coluna CLTR_NOMENCLATURA nas validações
 * (Requisito 4): a entrada que antes disparava NomenclaturaException (NCM
 * informado para classificação com CLTR_NOMENCLATURA restritiva) passa a ser
 * aceita — a exigência/permissão de NCM/NBS é dirigida pelos anexos
 * (NCM_APLICAVEL/NBS_APLICAVEL) e, com tpDoc, pela NomenclaturaService.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-testes.yml")
@ActiveProfiles("testes")
class Teste_validacao_11_NomenclaturaException {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CalculadoraService calculadoraService;

    private OperacaoInput operacao;

    @BeforeEach
    void beforeEach(
            final @Value("classpath:entradas/validacoes/Teste_validacao_11_NomenclaturaException.json") Resource resourceFile)
            throws IOException {
        operacao = objectMapper.readValue(resourceFile.getInputStream(), OperacaoInput.class);
    }

    @Test
    @DisplayName("CLTR_NOMENCLATURA não bloqueia mais: NCM aceito para classificação sem anexo")
    void teste_service_CalcularTributos() {
        assertThatCode(() -> calculadoraService.calcularTributos(operacao))
                .doesNotThrowAnyException();
    }

    @Test
    void teste_controller_CalcularTributos() throws Exception {
        final String jsonContent = objectMapper.writeValueAsString(operacao);
        mockMvc.perform(post("/calculadora/regime-geral")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(status().is2xxSuccessful());
    }

}
