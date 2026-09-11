/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.testesintegracao.dadosabertos;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

/**
 * Caso de aceitação 10: o endpoint de nomenclatura devolve, além do campo
 * nomenclatura (inalterado), os sinais de obrigatoriedade que permitem ao
 * frontend remover o contorno provisório: obrigatorio (true para NCM, NBS,
 * MISTO, EXCECAO_NCM e EXCECAO_NBS; false para SEM) e a desambiguação do SEM
 * via ncmOpcional (NF-e/NFC-e) e nbsOpcional (NFS-e).
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-testes.yml")
@ActiveProfiles("testes")
@DisplayName("Nomenclatura - obrigatoriedade e campos opcionais")
class Teste_nomenclatura_13_obrigatoriedade {

    @Autowired
    private MockMvc mockMvc;

    private static final String ENDPOINT = "/calculadora/dados-abertos/nomenclatura/{siglaDfe}/{cClassTrib}";
    private static final String DATA_PARAM = LocalDate.of(2027, 1, 1).format(DateTimeFormatter.ISO_LOCAL_DATE);

    private ResultActions consultar(String siglaDfe, String cClassTrib) throws Exception {
        return mockMvc.perform(get(ENDPOINT, siglaDfe, cClassTrib)
                .param("data", DATA_PARAM)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("NCM (NFe + 200003): obrigatorio true, opcionais false")
    void teste_ncmObrigatorio() throws Exception {
        consultar("NFe", "200003")
                .andExpect(jsonPath("$.nomenclatura", is("NCM")))
                .andExpect(jsonPath("$.obrigatorio", is(true)))
                .andExpect(jsonPath("$.ncmOpcional", is(false)))
                .andExpect(jsonPath("$.nbsOpcional", is(false)));
    }

    @Test
    @DisplayName("NBS (NFSe + 200028): obrigatorio true, opcionais false")
    void teste_nbsObrigatoria() throws Exception {
        consultar("NFSe", "200028")
                .andExpect(jsonPath("$.nomenclatura", is("NBS")))
                .andExpect(jsonPath("$.obrigatorio", is(true)))
                .andExpect(jsonPath("$.ncmOpcional", is(false)))
                .andExpect(jsonPath("$.nbsOpcional", is(false)));
    }

    @Test
    @DisplayName("MISTO (NFSe + 200044): obrigatorio true, opcionais false")
    void teste_mistoObrigatorio() throws Exception {
        consultar("NFSe", "200044")
                .andExpect(jsonPath("$.nomenclatura", is("MISTO")))
                .andExpect(jsonPath("$.obrigatorio", is(true)))
                .andExpect(jsonPath("$.ncmOpcional", is(false)))
                .andExpect(jsonPath("$.nbsOpcional", is(false)));
    }

    @Test
    @DisplayName("SEM em NFe (200001): ncmOpcional true, nbsOpcional false")
    void teste_semNfeNcmOpcional() throws Exception {
        consultar("NFe", "200001")
                .andExpect(jsonPath("$.nomenclatura", is("SEM")))
                .andExpect(jsonPath("$.obrigatorio", is(false)))
                .andExpect(jsonPath("$.ncmOpcional", is(true)))
                .andExpect(jsonPath("$.nbsOpcional", is(false)));
    }

    @Test
    @DisplayName("SEM em NFCe (200001): ncmOpcional true, nbsOpcional false")
    void teste_semNfceNcmOpcional() throws Exception {
        consultar("NFCe", "200001")
                .andExpect(jsonPath("$.nomenclatura", is("SEM")))
                .andExpect(jsonPath("$.obrigatorio", is(false)))
                .andExpect(jsonPath("$.ncmOpcional", is(true)))
                .andExpect(jsonPath("$.nbsOpcional", is(false)));
    }

    @Test
    @DisplayName("SEM em NFSe (200001): nbsOpcional true, ncmOpcional false")
    void teste_semNfseNbsOpcional() throws Exception {
        consultar("NFSe", "200001")
                .andExpect(jsonPath("$.nomenclatura", is("SEM")))
                .andExpect(jsonPath("$.obrigatorio", is(false)))
                .andExpect(jsonPath("$.ncmOpcional", is(false)))
                .andExpect(jsonPath("$.nbsOpcional", is(true)));
    }

    @Test
    @DisplayName("SEM fora do trio (BPe + 200003): ambos opcionais false")
    void teste_semForaDoTrio() throws Exception {
        consultar("BPe", "200003")
                .andExpect(jsonPath("$.nomenclatura", is("SEM")))
                .andExpect(jsonPath("$.obrigatorio", is(false)))
                .andExpect(jsonPath("$.ncmOpcional", is(false)))
                .andExpect(jsonPath("$.nbsOpcional", is(false)));
    }

    @Test
    @DisplayName("SEM fora do trio (CTe + 200003): ambos opcionais false")
    void teste_semForaDoTrioCte() throws Exception {
        consultar("CTe", "200003")
                .andExpect(jsonPath("$.nomenclatura", is("SEM")))
                .andExpect(jsonPath("$.obrigatorio", is(false)))
                .andExpect(jsonPath("$.ncmOpcional", is(false)))
                .andExpect(jsonPath("$.nbsOpcional", is(false)));
    }

    @Test
    @DisplayName("Caso 200038 inalterado: EXCECAO_NCM em NFe, obrigatorio true")
    void teste_excecaoNcm200038() throws Exception {
        consultar("NFe", "200038")
                .andExpect(jsonPath("$.nomenclatura", is("EXCECAO_NCM")))
                .andExpect(jsonPath("$.obrigatorio", is(true)))
                .andExpect(jsonPath("$.ncmOpcional", is(false)))
                .andExpect(jsonPath("$.nbsOpcional", is(false)));
    }

    @Test
    @DisplayName("Caso 200038 inalterado: EXCECAO_NBS em NFSe, obrigatorio true")
    void teste_excecaoNbs200038() throws Exception {
        consultar("NFSe", "200038")
                .andExpect(jsonPath("$.nomenclatura", is("EXCECAO_NBS")))
                .andExpect(jsonPath("$.obrigatorio", is(true)))
                .andExpect(jsonPath("$.ncmOpcional", is(false)))
                .andExpect(jsonPath("$.nbsOpcional", is(false)));
    }

    @Test
    @DisplayName("Caso 200038 inalterado: SEM em BPe, obrigatorio false")
    void teste_200038ForaDoTrio() throws Exception {
        consultar("BPe", "200038")
                .andExpect(jsonPath("$.nomenclatura", is("SEM")))
                .andExpect(jsonPath("$.obrigatorio", is(false)))
                .andExpect(jsonPath("$.ncmOpcional", is(false)))
                .andExpect(jsonPath("$.nbsOpcional", is(false)));
    }

}
