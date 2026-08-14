package br.gov.serpro.rtc.testesintegracao.calculoscorretos.comprasgovernamentais;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StreamUtils;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-testes.yml")
@ActiveProfiles("testes")
class CompraGovControllerComObservabilidadeArquivoTest {

	private static final String ENDPOINT = "/calculadora/observabilidade/regime-geral";
	private static final String TESTS_PATH = "entradas/calculoscorretos/compras-governamentais/testes/";

    @Autowired
    private MockMvc mockMvc;
    
    @ParameterizedTest(name = "Input = {0}, Output = {1}")
	@MethodSource("cenariosSucesso")
    void deveCalcularTributosCompraGovViaEndpoint(String input, String output) throws Exception {
        final var jsonRequest = getContentFrom(input);
        final var jsonResponse = getContentFrom(output);

        mockMvc.perform(post(ENDPOINT)
                .contentType(APPLICATION_JSON)
                .content(jsonRequest))
            .andExpect(status().isOk())
            .andExpect(content().json(jsonResponse));
    }
    
    @ParameterizedTest(name = "Input = {0}")
	@MethodSource("cenariosFalha")
    void deveFalharComValoresInvalidos(String input) throws Exception {
        final var jsonRequest = getContentFrom(input);

        mockMvc.perform(post(ENDPOINT)
                .contentType(APPLICATION_JSON)
                .content(jsonRequest))
            .andExpect(status().is4xxClientError());
    }
    
	private static Stream<Arguments> cenariosSucesso() {
		return Stream.of(
				Arguments.of("cenario_01_uniao_input.json", "cenario_01_uniao_output_observabilidade.json"),
				Arguments.of("cenario_02_estado_input.json", "cenario_02_estado_output_observabilidade.json"),
				Arguments.of("cenario_03_distrito_federal_input.json", "cenario_03_distrito_federal_output_observabilidade.json"),
				Arguments.of("cenario_04_municipio_input.json", "cenario_04_municipio_output_observabilidade.json"),
				Arguments.of("cenario_05_consorcio_input.json", "cenario_05_consorcio_output_observabilidade.json"),
				Arguments.of("cenario_06_comite_gestor_input.json", "cenario_06_comite_gestor_output_observabilidade.json"));
	}
	
	private static Stream<Arguments> cenariosFalha() {
		return Stream.of(
				Arguments.of("cenario_00_tipo_ente_invalido_input.json"), 
				Arguments.of("cenario_00_tipo_operacao_invalido_input.json"));
	}

	private static String getContentFrom(String filename) throws Exception {
		var resource = new ClassPathResource(TESTS_PATH + filename);
		return StreamUtils.copyToString(resource.getInputStream(), UTF_8);
	}
}