/*
* Versão de Homologação/Testes
*/
package br.gov.serpro.rtc.testesintegracao.tpdoc;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.time.OffsetDateTime;

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
import br.gov.serpro.rtc.domain.service.ObservabilidadeService;
import br.gov.serpro.rtc.domain.service.collector.ErrosCalculoException;
import br.gov.serpro.rtc.domain.service.exception.TipoDfeNaoEncontradoException;

/**
 * Caso de aceitação 5: tpDoc inexistente ou fora de vigência é erro de
 * operação nas duas APIs; na observabilidade interrompe todo o processamento
 * (erro global). Também garante que o código REG-028 é consultável no
 * endpoint de erros.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-testes.yml")
@ActiveProfiles("testes")
class Teste_tpdoc_5_TpDocInvalido {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CalculadoraService calculadoraService;

    @Autowired
    private ObservabilidadeService observabilidadeService;

    private OperacaoInput operacao;

    @BeforeEach
    void beforeEach(final @Value("classpath:entradas/tpdoc/tpdoc_sem_anexo_000001.json") Resource resourceFile)
            throws IOException {
        operacao = objectMapper.readValue(resourceFile.getInputStream(), OperacaoInput.class);
    }

    @Test
    @DisplayName("tpDoc inexistente: erro de operação na API sem observabilidade")
    void teste_service_tpDocInexistente() {
        operacao.setTpDoc(99);
        assertThatThrownBy(() -> calculadoraService.calcularTributos(operacao))
                .isExactlyInstanceOf(TipoDfeNaoEncontradoException.class);
    }

    @Test
    @DisplayName("tpDoc fora de vigência na data do fato gerador: erro de operação")
    void teste_service_tpDocForaDeVigencia() {
        // TIPO_DFE vigente a partir de 2025-01-01; fato gerador anterior
        operacao.setDhFatoGerador(OffsetDateTime.parse("2024-06-01T09:50:05-03:00"));
        assertThatThrownBy(() -> calculadoraService.calcularTributos(operacao))
                .isExactlyInstanceOf(TipoDfeNaoEncontradoException.class);
    }

    @Test
    @DisplayName("Controller sem observabilidade: tpDoc inexistente retorna 4xx")
    void teste_controller_tpDocInexistente() throws Exception {
        operacao.setTpDoc(99);
        final String jsonContent = objectMapper.writeValueAsString(operacao);
        mockMvc.perform(post("/calculadora/regime-geral")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.title").value("Tipo de documento fiscal não encontrado"));
    }

    @Test
    @DisplayName("Observabilidade: tpDoc inexistente é erro global que interrompe o processamento")
    void teste_observabilidade_tpDocInexistente() {
        operacao.setTpDoc(99);
        assertThatThrownBy(() -> observabilidadeService.processarOperacao(operacao, "http://localhost:8080"))
                .isExactlyInstanceOf(ErrosCalculoException.class);
    }

    @Test
    @DisplayName("Controller com observabilidade: tpDoc inexistente retorna 4xx com REG-028")
    void teste_controllerObservabilidade_tpDocInexistente() throws Exception {
        operacao.setTpDoc(99);
        final String jsonContent = objectMapper.writeValueAsString(operacao);
        mockMvc.perform(post("/calculadora/observabilidade/regime-geral")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.errors[0].code").value("REG-028"));
    }

    @Test
    @DisplayName("Catálogo de erros: REG-028 consultável em /calculadora/observabilidade/erros")
    void teste_catalogoErros_REG028() throws Exception {
        mockMvc.perform(get("/calculadora/observabilidade/erros").param("codigo", "REG-028"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.titulo").value("Tipo de documento fiscal não encontrado"));
    }

    @Test
    @DisplayName("Catálogo de erros: REG-029 a REG-033 consultáveis")
    void teste_catalogoErros_novosCodigos() throws Exception {
        for (String codigo : new String[] {"REG-029", "REG-030", "REG-031", "REG-032", "REG-033"}) {
            mockMvc.perform(get("/calculadora/observabilidade/erros").param("codigo", codigo))
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(jsonPath("$.codigo").value(codigo));
        }
    }

}
