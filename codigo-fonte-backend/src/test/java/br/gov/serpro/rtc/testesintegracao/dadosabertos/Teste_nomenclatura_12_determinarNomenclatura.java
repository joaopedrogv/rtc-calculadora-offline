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

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-testes.yml")
@ActiveProfiles("testes")
@DisplayName("Nomenclatura - Integration Tests")
class Teste_nomenclatura_12_determinarNomenclatura {

    @Autowired
    private MockMvc mockMvc;

    private static final String ENDPOINT = "/calculadora/dados-abertos/nomenclatura/{siglaDfe}/{cClassTrib}";
    private static final LocalDate DATA_VIGENCIA = LocalDate.of(2027, 1, 1);
    private static final String DATA_PARAM = DATA_VIGENCIA.format(DateTimeFormatter.ISO_LOCAL_DATE);

    @Test
    @DisplayName("Nomenclatura: Retorna SEM para classificação 200001 com NFe")
    void teste_SEM_200001_NFe() throws Exception {
        mockMvc.perform(get(ENDPOINT, "NFe", "200001")
                .param("data", DATA_PARAM)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.nomenclatura", is("SEM")));
    }

    @Test
    @DisplayName("Nomenclatura: Retorna NCM para classificação 200003 com NFe")
    void teste_NCM_200003_NFe() throws Exception {
        mockMvc.perform(get(ENDPOINT, "NFe", "200003")
                .param("data", DATA_PARAM)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.nomenclatura", is("NCM")));
    }

    @Test
    @DisplayName("Nomenclatura: Retorna NBS para classificação 200028 com NFSe")
    void teste_NBS_200028_NFSe() throws Exception {
        mockMvc.perform(get(ENDPOINT, "NFSe", "200028")
                .param("data", DATA_PARAM)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.nomenclatura", is("NBS")));
    }

    @Test
    @DisplayName("Nomenclatura: Retorna MISTO para classificação 200044 com NFSe")
    void teste_MISTO_200044_NFSe() throws Exception {
        mockMvc.perform(get(ENDPOINT, "NFSe", "200044")
                .param("data", DATA_PARAM)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.nomenclatura", is("MISTO")));
    }

    @Test
    @DisplayName("Nomenclatura: Retorna EXCECAO_NCM para classificação 200038 com NFe")
    void teste_EXCECAO_NCM_200038_NFe() throws Exception {
        mockMvc.perform(get(ENDPOINT, "NFe", "200038")
                .param("data", DATA_PARAM)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.nomenclatura", is("EXCECAO_NCM")));
    }

    @Test
    @DisplayName("Nomenclatura: Retorna EXCECAO_NCM para classificação 200038 com NFCe")
    void teste_EXCECAO_NCM_200038_NFCe() throws Exception {
        mockMvc.perform(get(ENDPOINT, "NFCe", "200038")
                .param("data", DATA_PARAM)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.nomenclatura", is("EXCECAO_NCM")));
    }

    @Test
    @DisplayName("Nomenclatura: Retorna EXCECAO_NBS para classificação 200038 com NFSe")
    void teste_EXCECAO_NBS_200038_NFSe() throws Exception {
        mockMvc.perform(get(ENDPOINT, "NFSe", "200038")
                .param("data", DATA_PARAM)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.nomenclatura", is("EXCECAO_NBS")));
    }

    @Test
    @DisplayName("Nomenclatura: Retorna SEM para classificação 200038 com BPe")
    void teste_SEM_200038_BPe() throws Exception {
        mockMvc.perform(get(ENDPOINT, "BPe", "200038")
                .param("data", DATA_PARAM)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.nomenclatura", is("SEM")));
    }

    @Test
    @DisplayName("Nomenclatura: NFSe retorna NCM quando apenas NCM é aplicável")
    void teste_NCM_200003_NFSe_OnlyNCM() throws Exception {
        mockMvc.perform(get(ENDPOINT, "NFSe", "200004")
                .param("data", DATA_PARAM)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.nomenclatura", is("NCM")));
    }

    @Test
    @DisplayName("Nomenclatura: NFe ignora NBS e retorna SEM quando apenas NBS é aplicável")
    void teste_SEM_200028_NFe_OnlyNBS() throws Exception {
        mockMvc.perform(get(ENDPOINT, "NFe", "200028")
                .param("data", DATA_PARAM)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.nomenclatura", is("SEM")));
    }

    @Test
    @DisplayName("Nomenclatura: BPe sempre retorna SEM")
    void teste_SEM_200003_BPe() throws Exception {
        mockMvc.perform(get(ENDPOINT, "BPe", "200003")
                .param("data", DATA_PARAM)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.nomenclatura", is("SEM")));
    }

    @Test
    @DisplayName("Nomenclatura: CTe sempre retorna SEM")
    void teste_SEM_200003_CTe() throws Exception {
        mockMvc.perform(get(ENDPOINT, "CTe", "200003")
                .param("data", DATA_PARAM)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.nomenclatura", is("SEM")));
    }
}
