package br.gov.serpro.rtc.testesintegracao.observabilidade;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;

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
import org.springframework.util.StreamUtils;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-testes.yml")
@ActiveProfiles("testes")
class ObservabilidadeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Value("classpath:entradas/calculoscorretos/observabilidade/entrada.json")
    private Resource entradaJson;

    @Test
    void deveProcessarOperacaoComObservabilidadeViaEndpoint() throws Exception {
        final String json = StreamUtils.copyToString(entradaJson.getInputStream(), StandardCharsets.UTF_8);

        mockMvc.perform(post("/calculadora/observabilidade/regime-geral")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isOk())

            .andExpect(jsonPath("$.objetos[0].nObj").value(1))
            .andExpect(jsonPath("$.objetos[0].estadoItem").value("INCONSISTENCIA_ENTRADA"))

            .andExpect(jsonPath("$.objetos[1].nObj").value(2))
            .andExpect(jsonPath("$.objetos[1].estadoItem").value("NAO_IMPLEMENTADO"))

            .andExpect(jsonPath("$.objetos[2].nObj").value(3))
            .andExpect(jsonPath("$.objetos[2].estadoItem").value("INCONSISTENCIA_ENTRADA"))

            .andExpect(jsonPath("$.objetos[3].nObj").value(4))
            .andExpect(jsonPath("$.objetos[3].estadoItem").value("INCONSISTENCIA_ENTRADA"))

            .andExpect(jsonPath("$.objetos[4].nObj").value(5))
            .andExpect(jsonPath("$.objetos[4].estadoItem").value("CALCULADO"))

            .andExpect(jsonPath("$.objetos[5].nObj").value(6))
            .andExpect(jsonPath("$.objetos[5].estadoItem").value("CALCULADO"));
    }
}
