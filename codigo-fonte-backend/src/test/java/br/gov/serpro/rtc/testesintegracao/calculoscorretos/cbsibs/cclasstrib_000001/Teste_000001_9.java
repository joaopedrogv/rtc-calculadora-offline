/*
* Versão de Homologação/Testes
*/
package br.gov.serpro.rtc.testesintegracao.calculoscorretos.cbsibs.cclasstrib_000001;

import static br.gov.serpro.rtc.util.AssertUtils.isEqualByComparingTo;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.gov.serpro.rtc.api.model.input.ItemOperacaoInput;
import br.gov.serpro.rtc.api.model.input.OperacaoInput;
import br.gov.serpro.rtc.domain.service.CalculadoraService;

/**
 * Cenários de consistência e derivação da base de cálculo de CBS e IBS a
 * partir da base de cálculo do Imposto Seletivo (BC CBS = BC IS + IS
 * informado).
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-testes.yml")
@ActiveProfiles("testes")
class Teste_000001_9 {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CalculadoraService calculadoraService;

    private OperacaoInput operacao;

    @BeforeEach
    void beforeEach(
            final @Value("classpath:entradas/calculoscorretos/Teste_000001_2.json") Resource resourceFile)
            throws Exception {
        operacao = objectMapper.readValue(resourceFile.getInputStream(), OperacaoInput.class);
    }

    private ItemOperacaoInput item() {
        return operacao.getItens().get(0);
    }

    private void configurar(String baseCalculo, String baseCalculoIs, String impostoInformado) {
        final var item = item();
        item.setBaseCalculo(baseCalculo != null ? new BigDecimal(baseCalculo) : null);
        item.getImpostoSeletivo().setBaseCalculo(baseCalculoIs != null ? new BigDecimal(baseCalculoIs) : null);
        item.getImpostoSeletivo().setImpostoInformado(impostoInformado != null ? new BigDecimal(impostoInformado) : null);
    }

    private BigDecimal calcularBaseCalculoIbsCbs() throws Exception {
        final var resultado = calculadoraService.calcularTributos(operacao);
        assertThat(resultado).isNotNull();
        final var objetos = resultado.getObjetos();
        assertThat(objetos).isNotNull().isNotEmpty();
        final var objeto = objetos.get(0);
        assertThat(objeto).isNotNull();
        return objeto.getValorBaseCalculoIBSCBS();
    }

    @Test
    void testEntradaConsistente() throws Exception {
        configurar("200", "188", "12");
        // BC IS 188 → vIS = 188 x 13% + 21.30 = 45.74; BC CBS/IBS = 188 + 45.74
        isEqualByComparingTo(calcularBaseCalculoIbsCbs(), "233.74");
    }

    @Test
    void testBaseCalculoDerivadaComImpostoInformado() throws Exception {
        configurar(null, "188", "12");
        isEqualByComparingTo(calcularBaseCalculoIbsCbs(), "233.74");
    }

    @Test
    void testBaseCalculoDerivadaSemImpostoInformado() throws Exception {
        configurar(null, "188", null);
        isEqualByComparingTo(calcularBaseCalculoIbsCbs(), "233.74");
    }

    @Test
    void testBaseCalculoMaiorSemImpostoInformado() throws Exception {
        configurar("200", "188", null);
        // BC CBS/IBS informada (200) prevalece: 200 + 45.74
        isEqualByComparingTo(calcularBaseCalculoIbsCbs(), "245.74");
    }

    @Test
    void testBaseCalculoIgualBaseCalculoIsSemImpostoInformado() throws Exception {
        configurar("188", "188", null);
        isEqualByComparingTo(calcularBaseCalculoIbsCbs(), "233.74");
    }

    @Test
    void testImpostoInformadoZero() throws Exception {
        configurar("188", "188", "0");
        isEqualByComparingTo(calcularBaseCalculoIbsCbs(), "233.74");
    }

    @Test
    void testValoresComCasasDecimais() throws Exception {
        configurar("200.50", "188.25", "12.25");
        // vIS = 188.25 x 13% + 21.30 = 45.7725; BC CBS/IBS = 188.25 + 45.7725
        isEqualByComparingTo(calcularBaseCalculoIbsCbs(), "234.0225");
    }

    @Test
    void testItemSemImpostoSeletivoComBaseCalculo(
            final @Value("classpath:entradas/calculoscorretos/Teste_000001_1.json") Resource resourceFile)
            throws Exception {
        operacao = objectMapper.readValue(resourceFile.getInputStream(), OperacaoInput.class);
        isEqualByComparingTo(calcularBaseCalculoIbsCbs(), "200.00");
    }

}
