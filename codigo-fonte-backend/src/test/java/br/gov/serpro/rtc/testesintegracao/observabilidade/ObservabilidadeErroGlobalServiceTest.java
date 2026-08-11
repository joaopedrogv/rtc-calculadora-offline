package br.gov.serpro.rtc.testesintegracao.observabilidade;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
class ObservabilidadeErroGlobalServiceTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ObservabilidadeService observabilidadeService;

    @Test
    void deveLancarExcecaoQuandoErroGlobal4xx(
            final @Value("classpath:entradas/calculoscorretos/observabilidade/entrada_com_erro_global.json") Resource resourceFile)
            throws Exception {
        final var operacao = objectMapper.readValue(resourceFile.getInputStream(), OperacaoInput.class);

        // O erro global (município não pertencente à UF) impede o processamento de todos os itens,
        // resultando em uma exceção (que o handler converte em HTTP 422).
        assertThatThrownBy(() -> observabilidadeService.processarOperacao(operacao, "http://localhost:8080"))
            .isInstanceOf(Exception.class);
    }

    @Test
    void deveLancarExcecaoQuandoErroGlobal5xx(
            final @Value("classpath:entradas/calculoscorretos/observabilidade/entrada_com_erro_500.json") Resource resourceFile)
            throws Exception {
        final var operacao = objectMapper.readValue(resourceFile.getInputStream(), OperacaoInput.class);

        assertThatThrownBy(() -> observabilidadeService.processarOperacao(operacao, "http://localhost:8080"))
            .isInstanceOf(Exception.class);
    }

}
