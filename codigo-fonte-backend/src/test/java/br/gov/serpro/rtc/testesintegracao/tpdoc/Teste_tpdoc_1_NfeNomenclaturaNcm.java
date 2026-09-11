/*
* Versão de Homologação/Testes
*/
package br.gov.serpro.rtc.testesintegracao.tpdoc;

import static org.assertj.core.api.Assertions.assertThatCode;
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
import br.gov.serpro.rtc.domain.service.exception.NcmCompletoNaoInformadoException;
import br.gov.serpro.rtc.domain.service.exception.NcmNaoVinculadaException;
import br.gov.serpro.rtc.domain.service.exception.NomenclaturaIncompativelTipoDfeException;

/**
 * Caso de aceitação 1: cClassTrib 200043 (anexo com NCM e NBS) com tpDoc 55
 * (NF-e) — nomenclatura NCM: exige NCM completo criticado contra o anexo; NBS
 * informada gera erro.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-testes.yml")
@ActiveProfiles("testes")
class Teste_tpdoc_1_NfeNomenclaturaNcm {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CalculadoraService calculadoraService;

    private OperacaoInput operacao;

    @BeforeEach
    void beforeEach(final @Value("classpath:entradas/tpdoc/tpdoc_nfe_200043.json") Resource resourceFile)
            throws IOException {
        operacao = objectMapper.readValue(resourceFile.getInputStream(), OperacaoInput.class);
    }

    @Test
    @DisplayName("NF-e + 200043: NCM válido do anexo passa")
    void teste_ncmValidoDoAnexoPassa() {
        assertThatCode(() -> calculadoraService.calcularTributos(operacao))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("NF-e + 200043: NCM fora do anexo gera erro")
    void teste_ncmForaDoAnexoErro() {
        operacao.getItens().get(0).setNcm("24021000");
        assertThatThrownBy(() -> calculadoraService.calcularTributos(operacao))
                .isExactlyInstanceOf(NcmNaoVinculadaException.class);
    }

    @Test
    @DisplayName("NF-e + 200043: NBS informada gera erro de nomenclatura incompatível")
    void teste_nbsInformadaErro() {
        operacao.getItens().get(0).setNcm(null);
        operacao.getItens().get(0).setNbs("115012000");
        assertThatThrownBy(() -> calculadoraService.calcularTributos(operacao))
                .isExactlyInstanceOf(NomenclaturaIncompativelTipoDfeException.class);
    }

    @Test
    @DisplayName("NF-e + 200043: NCM ausente gera erro de NCM completo não informado")
    void teste_ncmAusenteErro() {
        operacao.getItens().get(0).setNcm(null);
        assertThatThrownBy(() -> calculadoraService.calcularTributos(operacao))
                .isExactlyInstanceOf(NcmCompletoNaoInformadoException.class);
    }

    @Test
    @DisplayName("Controller: NBS em NF-e retorna 4xx com título de nomenclatura incompatível")
    void teste_controller_nbsIncompativel() throws Exception {
        operacao.getItens().get(0).setNcm(null);
        operacao.getItens().get(0).setNbs("115012000");
        final String jsonContent = objectMapper.writeValueAsString(operacao);
        mockMvc.perform(post("/calculadora/regime-geral")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.title").value("Nomenclatura incompatível com o tipo de documento"));
    }

}
