/*
* Versão de Homologação/Testes
*/
package br.gov.serpro.rtc.testesintegracao.dadosabertos;

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
class Teste_dadosabertos_6_consultarNcm {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void teste_controller_consultarNcm() throws Exception {
        mockMvc.perform(get("/calculadora/dados-abertos/ncm")
                .param("data", "2027-01-01")
                .param("ncm", "24021000")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.tributadoPeloImpostoSeletivo").value(true));
    }

    @Test
    void teste_controller_consultarNcm_inexistente() throws Exception {
        mockMvc.perform(get("/calculadora/dados-abertos/ncm")
                .param("data", "2027-01-01")
                .param("ncm", "24020009")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("NCM de código 24020009 não encontrada para a data 2027-01-01"));
    }

}