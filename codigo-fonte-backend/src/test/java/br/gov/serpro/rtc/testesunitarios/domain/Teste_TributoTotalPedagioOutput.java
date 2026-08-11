package br.gov.serpro.rtc.testesunitarios.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import br.gov.serpro.rtc.api.model.output.pedagio.TrechoPedagioOutput;
import br.gov.serpro.rtc.api.model.output.pedagio.TributoPedagioOutput;
import br.gov.serpro.rtc.api.model.output.pedagio.TributoTotalPedagioOutput;

class Teste_TributoTotalPedagioOutput {

    @Test
    void teste_totalizaCbs_retornaTributoTrechos() {
        var trechos = List.of(
                criarTrecho(new BigDecimal("10"), new BigDecimal("5"), new BigDecimal("3")),
                criarTrecho(new BigDecimal("20"), new BigDecimal("15"), new BigDecimal("7")));
        var totalBC = new BigDecimal("100");

        var total = TributoTotalPedagioOutput.totalizaCbs(trechos, totalBC);

        assertThat(total.getBaseCalculo()).isEqualByComparingTo(totalBC);
        assertThat(total.getValorApurado()).isEqualByComparingTo("30");
        assertThat(total.getValorDevido()).isEqualByComparingTo("30");
        assertThat(total.getValorTributo()).isEqualByComparingTo("30");
    }

    @Test
    void teste_totalizaIbsEstadual_retornaSomaTributoTrechos() {
        var trechos = List.of(
                criarTrecho(new BigDecimal("10"), new BigDecimal("5"), new BigDecimal("3")),
                criarTrecho(new BigDecimal("20"), new BigDecimal("15"), new BigDecimal("7")));
        var totalBC = new BigDecimal("200");

        var total = TributoTotalPedagioOutput.totalizaIbsEstadual(trechos, totalBC);

        assertThat(total.getBaseCalculo()).isEqualByComparingTo(totalBC);
        assertThat(total.getValorApurado()).isEqualByComparingTo("20");
    }

    @Test
    void teste_totalizaIbsMunicipal_retornaSomaTributoTrechos() {
        var trechos = List.of(
                criarTrecho(new BigDecimal("10"), new BigDecimal("5"), new BigDecimal("3")),
                criarTrecho(new BigDecimal("20"), new BigDecimal("15"), new BigDecimal("7")));
        var totalBC = new BigDecimal("300");

        var total = TributoTotalPedagioOutput.totalizaIbsMunicipal(trechos, totalBC);

        assertThat(total.getBaseCalculo()).isEqualByComparingTo(totalBC);
        assertThat(total.getValorApurado()).isEqualByComparingTo("10");
    }

    @Test
    void teste_totalizaCbs_retornaValoresApuradoEDevido() {
        var trechos = List.of(criarTrecho(new BigDecimal("42"), new BigDecimal("21"), new BigDecimal("10")));
        var total = TributoTotalPedagioOutput.totalizaCbs(trechos, new BigDecimal("500"));

        assertThat(total.getValorApurado()).isEqualByComparingTo("42");
        assertThat(total.getValorDevido()).isEqualByComparingTo("42");
    }

    private TrechoPedagioOutput criarTrecho(BigDecimal cbsCalc, BigDecimal ibsEstCalc, BigDecimal ibsMunCalc) {
        return TrechoPedagioOutput.builder()
                .numero(1)
                .cbs(TributoPedagioOutput.builder().tributoCalculado(cbsCalc).build())
                .ibsEstadual(TributoPedagioOutput.builder().tributoCalculado(ibsEstCalc).build())
                .ibsMunicipal(TributoPedagioOutput.builder().tributoCalculado(ibsMunCalc).build())
                .build();
    }
}
