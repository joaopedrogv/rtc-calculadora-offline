package br.gov.serpro.rtc.testesintegracao.observabilidade;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.gov.serpro.rtc.api.model.input.OperacaoInput;
import br.gov.serpro.rtc.domain.service.ObservabilidadeService;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-testes.yml")
@ActiveProfiles("testes")
class ObservabilidadeServiceTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ObservabilidadeService observabilidadeService;

    @Test
    void deveProcessarOperacaoComObservabilidade(
            final @Value("classpath:entradas/calculoscorretos/observabilidade/entrada.json") Resource resourceFile)
            throws Exception {
        final var operacao = objectMapper.readValue(resourceFile.getInputStream(), OperacaoInput.class);
        final var roc = observabilidadeService.processarOperacao(operacao, "http://localhost:8080");

        assertThat(roc).isNotNull();

        final var objetos = roc.getObjetos();
        assertThat(objetos).isNotNull().hasSize(6);

        assertThat(objetos.get(0).getNObj()).isEqualTo(1);
        assertThat(objetos.get(0).getEstadoItem()).hasToString("INCONSISTENCIA_ENTRADA");

        assertThat(objetos.get(1).getNObj()).isEqualTo(2);
        assertThat(objetos.get(1).getEstadoItem()).hasToString("NAO_IMPLEMENTADO");

        assertThat(objetos.get(2).getNObj()).isEqualTo(3);
        assertThat(objetos.get(2).getEstadoItem()).hasToString("INCONSISTENCIA_ENTRADA");

        assertThat(objetos.get(3).getNObj()).isEqualTo(4);
        assertThat(objetos.get(3).getEstadoItem()).hasToString("INCONSISTENCIA_ENTRADA");

        assertThat(objetos.get(4).getNObj()).isEqualTo(5);
        assertThat(objetos.get(4).getEstadoItem()).hasToString("CALCULADO");

        assertThat(objetos.get(5).getNObj()).isEqualTo(6);
        assertThat(objetos.get(5).getEstadoItem()).hasToString("CALCULADO");
    }
}
