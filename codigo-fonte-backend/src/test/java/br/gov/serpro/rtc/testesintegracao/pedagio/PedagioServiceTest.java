package br.gov.serpro.rtc.testesintegracao.pedagio;

import static br.gov.serpro.rtc.util.AssertUtils.isEqualByComparingTo;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.gov.serpro.rtc.api.model.input.pedagio.PedagioInput;
import br.gov.serpro.rtc.domain.service.pedagio.PedagioService;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-testes.yml")
@ActiveProfiles("testes")
class PedagioServiceTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PedagioService pedagioService;

    @Test
    void deveCalcularTributoPedagio(
            final @Value("classpath:entradas/pedagio/entrada.json") Resource resourceFile)
            throws Exception {
        final var operacao = objectMapper.readValue(resourceFile.getInputStream(), PedagioInput.class);
        final var out = pedagioService.calcularCIBS(operacao);

        assertThat(out).isNotNull();

        // Cabeçalho
        assertThat(out.getMunicipioOrigem()).isEqualTo(1200013);
        isEqualByComparingTo(out.getBaseCalculo(), "200");
        isEqualByComparingTo(out.getExtensaoTotal(), "100");

        final var trechos = out.getTrechos();
        assertThat(trechos).isNotNull().hasSize(4);

        // Trecho 1
        var t1 = trechos.get(0);
        assertThat(t1.getNumero()).isEqualTo(1);
        assertThat(t1.getMunicipio()).isEqualTo(1200013);
        isEqualByComparingTo(t1.getBaseCalculo(), "200");
        isEqualByComparingTo(t1.getExtensaoTrecho(), "10");
        isEqualByComparingTo(t1.getCbs().getAliquota(), "0.900000");
        isEqualByComparingTo(t1.getCbs().getAliquotaEfetiva(), "0.090000");
        isEqualByComparingTo(t1.getCbs().getTributoCalculado(), "0.18000000");
        isEqualByComparingTo(t1.getIbsEstadual().getAliquota(), "0.100000");
        isEqualByComparingTo(t1.getIbsEstadual().getAliquotaEfetiva(), "0.010000");
        isEqualByComparingTo(t1.getIbsEstadual().getTributoCalculado(), "0.02000000");
        isEqualByComparingTo(t1.getIbsMunicipal().getAliquota(), "0.000000");
        isEqualByComparingTo(t1.getIbsMunicipal().getAliquotaEfetiva(), "0.000000");
        isEqualByComparingTo(t1.getIbsMunicipal().getTributoCalculado(), "0.00000000");

        // Trecho 2
        var t2 = trechos.get(1);
        assertThat(t2.getNumero()).isEqualTo(2);
        assertThat(t2.getMunicipio()).isEqualTo(1200054);
        isEqualByComparingTo(t2.getBaseCalculo(), "200");
        isEqualByComparingTo(t2.getExtensaoTrecho(), "20");
        isEqualByComparingTo(t2.getCbs().getAliquota(), "0.900000");
        isEqualByComparingTo(t2.getCbs().getAliquotaEfetiva(), "0.180000");
        isEqualByComparingTo(t2.getCbs().getTributoCalculado(), "0.36000000");
        isEqualByComparingTo(t2.getIbsEstadual().getAliquota(), "0.100000");
        isEqualByComparingTo(t2.getIbsEstadual().getAliquotaEfetiva(), "0.020000");
        isEqualByComparingTo(t2.getIbsEstadual().getTributoCalculado(), "0.04000000");
        isEqualByComparingTo(t2.getIbsMunicipal().getAliquota(), "0.000000");
        isEqualByComparingTo(t2.getIbsMunicipal().getAliquotaEfetiva(), "0.000000");
        isEqualByComparingTo(t2.getIbsMunicipal().getTributoCalculado(), "0.00000000");

        // Trecho 3
        var t3 = trechos.get(2);
        assertThat(t3.getNumero()).isEqualTo(3);
        assertThat(t3.getMunicipio()).isEqualTo(2700102);
        isEqualByComparingTo(t3.getBaseCalculo(), "200");
        isEqualByComparingTo(t3.getExtensaoTrecho(), "30");
        isEqualByComparingTo(t3.getCbs().getAliquota(), "0.900000");
        isEqualByComparingTo(t3.getCbs().getAliquotaEfetiva(), "0.270000");
        isEqualByComparingTo(t3.getCbs().getTributoCalculado(), "0.54000000");
        isEqualByComparingTo(t3.getIbsEstadual().getAliquota(), "0.100000");
        isEqualByComparingTo(t3.getIbsEstadual().getAliquotaEfetiva(), "0.030000");
        isEqualByComparingTo(t3.getIbsEstadual().getTributoCalculado(), "0.06000000");
        isEqualByComparingTo(t3.getIbsMunicipal().getAliquota(), "0.000000");
        isEqualByComparingTo(t3.getIbsMunicipal().getAliquotaEfetiva(), "0.000000");
        isEqualByComparingTo(t3.getIbsMunicipal().getTributoCalculado(), "0.00000000");

        // Trecho 4
        var t4 = trechos.get(3);
        assertThat(t4.getNumero()).isEqualTo(4);
        assertThat(t4.getMunicipio()).isEqualTo(2700201);
        isEqualByComparingTo(t4.getBaseCalculo(), "200");
        isEqualByComparingTo(t4.getExtensaoTrecho(), "40");
        isEqualByComparingTo(t4.getCbs().getAliquota(), "0.900000");
        isEqualByComparingTo(t4.getCbs().getAliquotaEfetiva(), "0.360000");
        isEqualByComparingTo(t4.getCbs().getTributoCalculado(), "0.72000000");
        isEqualByComparingTo(t4.getIbsEstadual().getAliquota(), "0.100000");
        isEqualByComparingTo(t4.getIbsEstadual().getAliquotaEfetiva(), "0.040000");
        isEqualByComparingTo(t4.getIbsEstadual().getTributoCalculado(), "0.08000000");
        isEqualByComparingTo(t4.getIbsMunicipal().getAliquota(), "0.000000");
        isEqualByComparingTo(t4.getIbsMunicipal().getAliquotaEfetiva(), "0.000000");
        isEqualByComparingTo(t4.getIbsMunicipal().getTributoCalculado(), "0.00000000");

        // Total
        final var total = out.getTotal();
        isEqualByComparingTo(total.getCbsTotal().getBaseCalculo(), "200.00");
        isEqualByComparingTo(total.getCbsTotal().getValorApurado(), "1.80");
        isEqualByComparingTo(total.getCbsTotal().getValorDevido(), "1.80");
        isEqualByComparingTo(total.getCbsTotal().getValorTributo(), "1.80");
        isEqualByComparingTo(total.getIbsEstadualTotal().getBaseCalculo(), "200.00");
        isEqualByComparingTo(total.getIbsEstadualTotal().getValorApurado(), "0.20");
        isEqualByComparingTo(total.getIbsEstadualTotal().getValorDevido(), "0.20");
        isEqualByComparingTo(total.getIbsEstadualTotal().getValorTributo(), "0.20");
        isEqualByComparingTo(total.getIbsMunicipalTotal().getBaseCalculo(), "200.00");
        isEqualByComparingTo(total.getIbsMunicipalTotal().getValorApurado(), "0.00");
        isEqualByComparingTo(total.getIbsMunicipalTotal().getValorDevido(), "0.00");
        isEqualByComparingTo(total.getIbsMunicipalTotal().getValorTributo(), "0.00");
    }
}