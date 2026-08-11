package br.gov.serpro.rtc.testesintegracao.xml;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StreamUtils;

import br.gov.serpro.rtc.api.model.xml.enumeration.TipoDocumento;
import br.gov.serpro.rtc.api.model.xml.enumeration.TipoXml;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-testes.yml")
@ActiveProfiles("testes")
class XmlControllerValidacaoTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ResourceLoader resourceLoader;

    static Stream<Arguments> documentos() {
        return Stream.of(
            Arguments.of("classpath:entradas/xml/nfe.xml",              TipoDocumento.NFE,              TipoXml.GRUPO),
            Arguments.of("classpath:entradas/xml/nfce.xml",             TipoDocumento.NFCE,             TipoXml.GRUPO),
            Arguments.of("classpath:entradas/xml/cte.xml",              TipoDocumento.CTE,              TipoXml.GRUPO),
            Arguments.of("classpath:entradas/xml/cte-simplificado.xml", TipoDocumento.CTE_SIMPLIFICADO, TipoXml.GRUPO),
            Arguments.of("classpath:entradas/xml/bpe.xml",              TipoDocumento.BPE,              TipoXml.GRUPO),
            Arguments.of("classpath:entradas/xml/bpe-tm.xml",           TipoDocumento.BPE_TM,           TipoXml.GRUPO),
            Arguments.of("classpath:entradas/xml/nf3e.xml",             TipoDocumento.NF3E,             TipoXml.GRUPO)
        );
    }

    @ParameterizedTest(name = "[{index}] {1} - {2}")
    @MethodSource("documentos")
    @DisplayName("Deve validar XML com sucesso via endpoint")
    void deveValidarXmlViaEndpoint(final String caminho, final TipoDocumento tipo, final TipoXml subtipo)
            throws Exception {
        final Resource resource = resourceLoader.getResource(caminho);
        final String xml = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);

        mockMvc.perform(post("/calculadora/xml/validate")
                .param("tipo", tipo.getMnemonico())
                .param("subtipo", subtipo.toJson())
                .contentType(MediaType.APPLICATION_XML)
                .content(xml))
            .andExpect(status().isOk())
            .andExpect(content().string("true"));
    }
}