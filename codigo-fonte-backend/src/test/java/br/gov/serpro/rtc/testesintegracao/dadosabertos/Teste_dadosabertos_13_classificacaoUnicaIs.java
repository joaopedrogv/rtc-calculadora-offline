/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.testesintegracao.dadosabertos;

import static org.hamcrest.Matchers.notNullValue;
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
class Teste_dadosabertos_13_classificacaoUnicaIs {

    private static final String C_CLASS_TRIB_IS = "000001";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void teste_controller_consultarClassTribIsValidaDataValida() throws Exception {
        mockMvc.perform(get("/calculadora/dados-abertos/classificacoes-tributarias/is/class-trib/" + C_CLASS_TRIB_IS)
                .param("data", "2027-01-01")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value(C_CLASS_TRIB_IS))
                .andExpect(jsonPath("$.descricao", notNullValue()))
                .andExpect(jsonPath("$.tipoAliquota", notNullValue()));
    }

    @Test
    void teste_controller_consultarClassTribIsInvalidaDataValida() throws Exception {
        mockMvc.perform(get("/calculadora/dados-abertos/classificacoes-tributarias/is/class-trib/999999")
                .param("data", "2027-01-01")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void teste_controller_consultarClassTribIsValidaDataInvalida() throws Exception {
        mockMvc.perform(get("/calculadora/dados-abertos/classificacoes-tributarias/is/class-trib/" + C_CLASS_TRIB_IS)
                .param("data", "1980-01-01")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void teste_controller_consultarClassTribIsInvalidaDataInvalida() throws Exception {
        mockMvc.perform(get("/calculadora/dados-abertos/classificacoes-tributarias/is/class-trib/999999")
                .param("data", "1980-01-01")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void teste_controller_consultarClassificacaoUnicaIsValidaTodasAsPropriedades() throws Exception {
        mockMvc.perform(get("/calculadora/dados-abertos/classificacoes-tributarias/is/class-trib/" + C_CLASS_TRIB_IS)
                .param("data", "2027-01-01")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo", notNullValue()))
                .andExpect(jsonPath("$.descricao", notNullValue()))
                .andExpect(jsonPath("$.tipoAliquota", notNullValue()))
                .andExpect(jsonPath("$.nomenclatura", notNullValue()))
                .andExpect(jsonPath("$.descricaoTratamentoTributario", notNullValue()))
                .andExpect(jsonPath("$.incompativelComSuspensao").value(false))
                .andExpect(jsonPath("$.exigeGrupoDesoneracao").value(false))
                .andExpect(jsonPath("$.possuiPercentualReducao", notNullValue()))
                .andExpect(jsonPath("$.tiposDfeClassificacao", notNullValue()));
    }
}
