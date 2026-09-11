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
class Teste_dadosabertos_10_classificacaoUnicaCbsIbs {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void teste_controller_consultarClassTribValidaDataValida() throws Exception {
        mockMvc.perform(get("/calculadora/dados-abertos/classificacoes-tributarias/cbs-ibs/class-trib/000001")
                .param("data", "2027-01-01")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("000001"))
                .andExpect(jsonPath("$.descricao", notNullValue()))
                .andExpect(jsonPath("$.tipoAliquota", notNullValue()));
    }

    @Test
    void teste_controller_consultarClassTribInvalidaDataValida() throws Exception {
        mockMvc.perform(get("/calculadora/dados-abertos/classificacoes-tributarias/cbs-ibs/class-trib/999999")
                .param("data", "2027-01-01")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void teste_controller_consultarClassTribValidaDataInvalida() throws Exception {
        mockMvc.perform(get("/calculadora/dados-abertos/classificacoes-tributarias/cbs-ibs/class-trib/000001")
                .param("data", "1980-01-01")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void teste_controller_consultarClassTribInvalidaDataInvalida() throws Exception {
        mockMvc.perform(get("/calculadora/dados-abertos/classificacoes-tributarias/cbs-ibs/class-trib/999999")
                .param("data", "1980-01-01")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void teste_controller_consultarClassificacaoUnicaCbsIbsValidaTodasAsPropriedades() throws Exception {
        mockMvc.perform(get("/calculadora/dados-abertos/classificacoes-tributarias/cbs-ibs/class-trib/000001")
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
                .andExpect(jsonPath("$.indicaApropriacaoCreditoAdquirenteCbs", notNullValue()))
                .andExpect(jsonPath("$.indicaApropriacaoCreditoAdquirenteIbs", notNullValue()))
                .andExpect(jsonPath("$.indicaCreditoPresumidoFornecedor", notNullValue()))
                .andExpect(jsonPath("$.indicaCreditoPresumidoAdquirente", notNullValue()))
                .andExpect(jsonPath("$.tipoReceitaBrutaSimplesNacional").value(1))
                .andExpect(jsonPath("$.tiposDfeClassificacao", notNullValue()))
                .andExpect(jsonPath("$.dataAtualizacao", notNullValue()));
    }

    @Test
    void teste_controller_consultarClassTribRetornaTipoReceitaBrutaSimplesNacional() throws Exception {
        mockMvc.perform(get("/calculadora/dados-abertos/classificacoes-tributarias/cbs-ibs/class-trib/410002")
                .param("data", "2026-08-07")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("410002"))
                .andExpect(jsonPath("$.tipoReceitaBrutaSimplesNacional").value(0));
    }
}
