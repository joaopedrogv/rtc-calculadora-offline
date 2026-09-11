/*
* Versão de Homologação/Testes
*/
package br.gov.serpro.rtc.testesintegracao.tpdoc;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
import br.gov.serpro.rtc.domain.service.exception.ClassificacaoTributariaNaoVinculadaTipoDfeException;

/**
 * Caso de aceitação 6: cClassTrib sem vínculo com o tpDoc informado é erro de
 * item (200044 não é vinculada à NF-e). Na observabilidade, apenas o item
 * afetado falha; os demais itens seguem sendo calculados.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-testes.yml")
@ActiveProfiles("testes")
class Teste_tpdoc_6_ClassificacaoSemVinculoTipoDfe {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CalculadoraService calculadoraService;

    private OperacaoInput operacao;

    @BeforeEach
    void beforeEach(final @Value("classpath:entradas/tpdoc/tpdoc_observabilidade_vinculo.json") Resource resourceFile)
            throws IOException {
        operacao = objectMapper.readValue(resourceFile.getInputStream(), OperacaoInput.class);
    }

    @Test
    @DisplayName("Sem observabilidade: item com classificação não vinculada ao tpDoc aborta a requisição")
    void teste_service_classificacaoSemVinculoErro() {
        assertThatThrownBy(() -> calculadoraService.calcularTributos(operacao))
                .isExactlyInstanceOf(ClassificacaoTributariaNaoVinculadaTipoDfeException.class);
    }

    @Test
    @DisplayName("Observabilidade: apenas o item sem vínculo falha (REG-029); o outro é calculado")
    void teste_observabilidade_apenasItemAfetadoFalha() throws Exception {
        final String jsonContent = objectMapper.writeValueAsString(operacao);
        mockMvc.perform(post("/calculadora/observabilidade/regime-geral")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.objetos[0].nObj").value(1))
                .andExpect(jsonPath("$.objetos[0].estadoItem").value("CALCULADO"))
                .andExpect(jsonPath("$.objetos[1].nObj").value(2))
                .andExpect(jsonPath("$.objetos[1].estadoItem").value("INCONSISTENCIA_ENTRADA"))
                .andExpect(jsonPath("$.objetos[1].erros[0].code").value("REG-029"));
    }

}
