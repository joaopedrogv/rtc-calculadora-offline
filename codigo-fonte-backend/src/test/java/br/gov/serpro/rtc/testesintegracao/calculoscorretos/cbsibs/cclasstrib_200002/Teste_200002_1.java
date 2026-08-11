/*
* Versão de Homologação/Testes
*/
package br.gov.serpro.rtc.testesintegracao.calculoscorretos.cbsibs.cclasstrib_200002;

import static br.gov.serpro.rtc.util.AssertUtils.isEqualByComparingTo;
import static java.math.BigDecimal.ZERO;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.gov.serpro.rtc.api.model.input.OperacaoInput;
import br.gov.serpro.rtc.api.model.roc.CBSDomain;
import br.gov.serpro.rtc.api.model.roc.IBSMunDomain;
import br.gov.serpro.rtc.api.model.roc.IBSUFDomain;
import br.gov.serpro.rtc.domain.service.CalculadoraService;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-testes.yml")
@ActiveProfiles("testes")
class Teste_200002_1 {
    
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CalculadoraService calculadoraService;

    @Test
    void testCalcularTributos(
            final @Value("classpath:entradas/calculoscorretos/Teste_200002_1.json") Resource resourceFile)
            throws Exception {
        final var operacao = objectMapper.readValue(resourceFile.getInputStream(), OperacaoInput.class);
        final var resultado = calculadoraService.calcularTributos(operacao);

        assertThat(resultado).isNotNull();
        final var objetos = resultado.getObjetos();
        assertThat(objetos).isNotNull().isNotEmpty();

        var item = objetos.get(0);
        assertThat(item).isNotNull();

        isEqualByComparingTo(item.getValorBaseCalculoIBSCBS(), "200.00");
        
        assertCbs(item.getGCBS());
        assertIbsEstadual(item.getGIBSUF());
        assertIbsMunicipal(item.getGIBSMun());
        assertThat(item.getImpostoSeletivo()).isNull();
        
        assertThat(item.getTributacaoRegular()).isNull();
    }

    private void assertCbs(final CBSDomain cbs) {
        assertThat(cbs).isNotNull();
        isEqualByComparingTo(cbs.getPCBS(), new BigDecimal("0.90"));
        isEqualByComparingTo(cbs.getVCBS(), ZERO);
    }

    private void assertIbsEstadual(final IBSUFDomain ibsEstadual) {
        assertThat(ibsEstadual).isNotNull();
        isEqualByComparingTo(ibsEstadual.getPIBSUF(), new BigDecimal("0.10"));
        isEqualByComparingTo(ibsEstadual.getVIBSUF(), ZERO);
    }

    private void assertIbsMunicipal(final IBSMunDomain ibsMunicipal) {
        assertThat(ibsMunicipal).isNotNull();
        isEqualByComparingTo(ibsMunicipal.getPIBSMun(), ZERO);
        isEqualByComparingTo(ibsMunicipal.getVIBSMun(), ZERO);
    }
}