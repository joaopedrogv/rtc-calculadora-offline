package br.gov.serpro.rtc.testesintegracao.observabilidade;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
class ObservabilidadeErroGlobalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Value("classpath:entradas/calculoscorretos/observabilidade/entrada_com_erro_global.json")
    private Resource entrada1Json;

    @Value("classpath:entradas/calculoscorretos/observabilidade/entrada_com_erro_500.json")
    private Resource entrada2Json;

    @Test
    void deveRetornar4xxQuandoErroGlobalViaEndpoint() throws Exception {
        final String json = StreamUtils.copyToString(entrada1Json.getInputStream(), StandardCharsets.UTF_8);

        mockMvc.perform(post("/calculadora/observabilidade/regime-geral")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().is4xxClientError());
    }

    @Test
    void deveRetornar5xxQuandoErro500ViaEndpoint() throws Exception {
        final String json = StreamUtils.copyToString(entrada2Json.getInputStream(), StandardCharsets.UTF_8);

        mockMvc.perform(post("/calculadora/observabilidade/regime-geral")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().is5xxServerError());
    }

}
