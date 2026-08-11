package br.gov.serpro.rtc.testesintegracao.xml;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.gov.serpro.rtc.api.model.roc.ROCDomain;
import br.gov.serpro.rtc.api.model.xml.enumeration.TipoDocumento;
import br.gov.serpro.rtc.domain.service.xml.XmlService;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-testes.yml")
@ActiveProfiles("testes")
class XmlServiceGeracaoTest {

    @Autowired
    private XmlService xmlService;

    @Autowired
    private ObjectMapper objectMapper;

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
    @DisplayName("Deve gerar XML com sucesso via service")
    void deveGerarXml(final TipoDocumento tipo) throws Exception {
        final var roc = objectMapper.readValue(entradaJson.getInputStream(), ROCDomain.class);

        final String xml = xmlService.gerarXml(roc, tipo);

        assertThat(xml)
            .as("XML gerado vazio para %s", tipo)
            .isNotBlank();
    }
}