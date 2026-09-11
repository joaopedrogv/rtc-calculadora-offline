/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.testesintegracao.calculoscorretos.cbsibs.cclasstrib_000001;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

/**
 * A partir de 01/01/2027 as alíquotas de referência não existem em lei e o
 * usuário é obrigado a informar as alíquotas nominais quando o CST exige o
 * grupo IBS/CBS. Este teste cobre o novo contrato para o cClassTrib 000001:
 * com as alíquotas informadas o cálculo é aceito (simulação); sem elas (ou com
 * preenchimento parcial), a operação é criticada.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-testes.yml")
@ActiveProfiles("testes")
class Teste_000001_EquivalenciaAliquotasNominais {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveCalcularComAliquotasNominaisInformadasEm2027(
            final @Value("classpath:entradas/calculoscorretos/Teste_000001_2.json") Resource resourceFile)
            throws Exception {
        OperacaoInput operacao = lerOperacao(resourceFile);

        mockMvc.perform(post("/calculadora/regime-geral")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(operacao)))
                .andExpect(status().isOk());
    }

    @Test
    void deveCriticarAusenciaDeAliquotasNominaisEm2027(
            final @Value("classpath:entradas/calculoscorretos/Teste_000001_2.json") Resource resourceFile)
            throws Exception {
        OperacaoInput operacao = lerOperacao(resourceFile);
        operacao.getItens().get(0).setAliquotasNominais(null);

        mockMvc.perform(post("/calculadora/regime-geral")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(operacao)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").value("Alíquotas nominais não informadas"));
    }

    @Test
    void deveCriticarPreenchimentoParcialDeAliquotasNominaisEm2027(
            final @Value("classpath:entradas/calculoscorretos/Teste_000001_2.json") Resource resourceFile)
            throws Exception {
        OperacaoInput operacao = lerOperacao(resourceFile);
        operacao.getItens().get(0).getAliquotasNominais().setIbsEstadual(null);
        operacao.getItens().get(0).getAliquotasNominais().setIbsMunicipal(null);

        mockMvc.perform(post("/calculadora/regime-geral")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(operacao)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").value("Alíquotas nominais não informadas"));
    }

    private OperacaoInput lerOperacao(Resource resourceFile) throws Exception {
        return objectMapper.readValue(resourceFile.getInputStream(), OperacaoInput.class);
    }
}
