/*
* Versão de Homologação/Testes
*/
package br.gov.serpro.rtc.testesintegracao.tpdoc;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.gov.serpro.rtc.api.model.input.OperacaoInput;
import br.gov.serpro.rtc.domain.service.CalculadoraService;
import br.gov.serpro.rtc.domain.service.exception.ImpostoSeletivoNaoAdmitidoTipoDfeException;

/**
 * Caso de aceitação 8 — Imposto Seletivo com tpDoc:
 * - tpDoc 55/65 (whitelist): IS aceito como hoje;
 * - tpDoc fora da whitelist com NCM que exige IS: erro de item;
 * - grupo IS supérfluo (NCM não exige IS): segue ignorado;
 * - sem tpDoc: comportamento atual (coberto pela suíte existente).
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-testes.yml")
@ActiveProfiles("testes")
class Teste_tpdoc_8_ImpostoSeletivo {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CalculadoraService calculadoraService;

    @Value("classpath:entradas/tpdoc/tpdoc_imposto_seletivo_2027.json")
    private Resource entradaImpostoSeletivo2027;

    @Value("classpath:entradas/tpdoc/tpdoc_imposto_seletivo_superfluo_2026.json")
    private Resource entradaImpostoSeletivoSuperfluo2026;

    private OperacaoInput ler(Resource resource) throws IOException {
        return objectMapper.readValue(resource.getInputStream(), OperacaoInput.class);
    }

    @Test
    @DisplayName("tpDoc 55 (whitelist): NCM tributado pelo IS com grupo informado passa")
    void teste_tpDoc55_impostoSeletivoAceito() throws IOException {
        OperacaoInput operacao = ler(entradaImpostoSeletivo2027);
        assertThatCode(() -> calculadoraService.calcularTributos(operacao))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("tpDoc 65 (whitelist): NCM tributado pelo IS com grupo informado passa")
    void teste_tpDoc65_impostoSeletivoAceito() throws IOException {
        OperacaoInput operacao = ler(entradaImpostoSeletivo2027);
        operacao.setTpDoc(65);
        assertThatCode(() -> calculadoraService.calcularTributos(operacao))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("tpDoc 62 (fora da whitelist): NCM que exige IS gera erro de item")
    void teste_tpDocForaWhitelist_ncmExigeIsErro() throws IOException {
        OperacaoInput operacao = ler(entradaImpostoSeletivo2027);
        operacao.setTpDoc(62);
        assertThatThrownBy(() -> calculadoraService.calcularTributos(operacao))
                .isExactlyInstanceOf(ImpostoSeletivoNaoAdmitidoTipoDfeException.class);
    }

    @Test
    @DisplayName("tpDoc 62 (fora da whitelist): grupo IS supérfluo é ignorado")
    void teste_tpDocForaWhitelist_grupoIsSuperfluoIgnorado() throws IOException {
        // Em 2026 o NCM 24021000 não é tributado pelo IS; o grupo informado
        // desnecessariamente continua sendo ignorado (sem erro)
        OperacaoInput operacao = ler(entradaImpostoSeletivoSuperfluo2026);
        assertThatCode(() -> calculadoraService.calcularTributos(operacao))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Sem tpDoc: IS aceito como hoje (whitelist não se aplica)")
    void teste_semTpDoc_comportamentoAtual() throws IOException {
        OperacaoInput operacao = ler(entradaImpostoSeletivo2027);
        operacao.setTpDoc(null);
        assertThatCode(() -> calculadoraService.calcularTributos(operacao))
                .doesNotThrowAnyException();
    }

}
