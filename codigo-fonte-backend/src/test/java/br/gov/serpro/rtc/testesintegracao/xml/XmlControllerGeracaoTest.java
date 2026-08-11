package br.gov.serpro.rtc.testesintegracao.xml;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
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

import br.gov.serpro.rtc.api.model.xml.enumeration.TipoDocumento;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-testes.yml")
@ActiveProfiles("testes")
class XmlControllerGeracaoTest {

    @Autowired
    private MockMvc mockMvc;

    @Value("classpath:entradas/xml/entrada.json")
    private Resource entradaJson;

    static Stream<TipoDocumento> tipos() {
        return Stream.of(
            TipoDocumento.NFE,
            TipoDocumento.NFCE,
            TipoDocumento.CTE,
            TipoDocumento.CTE_SIMPLIFICADO,
            TipoDocumento.BPE,
            TipoDocumento.BPE_TM,
            TipoDocumento.NF3E
        );
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("tipos")
    @DisplayName("Deve gerar XML com sucesso via endpoint")
    void deveGerarXmlViaEndpoint(final TipoDocumento tipo) throws Exception {
        final String json = StreamUtils.copyToString(entradaJson.getInputStream(), StandardCharsets.UTF_8);

        mockMvc.perform(post("/calculadora/xml/generate")
                .param("tipo", tipo.getMnemonico())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_XML));
    }
}