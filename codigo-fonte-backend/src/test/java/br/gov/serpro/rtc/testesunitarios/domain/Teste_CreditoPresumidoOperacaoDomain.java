package br.gov.serpro.rtc.testesunitarios.domain;

import static java.math.BigDecimal.ZERO;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import br.gov.serpro.rtc.api.model.roc.CreditoPresumidoOperacaoDomain;
import br.gov.serpro.rtc.api.model.roc.IBSCBSCreditoPresumidoDomain;

class Teste_CreditoPresumidoOperacaoDomain {

    @Test
    void teste_possuiCredPresCbsSemDados_retornaFalse() {
        var domain = new CreditoPresumidoOperacaoDomain();
        assertThat(domain.possuiCredPresCbs()).isFalse();
    }

    @Test
    void teste_possuiCredPresCbsComDados_retornaTrue() {
        var domain = new CreditoPresumidoOperacaoDomain();
        domain.setGCBSCredPres(new IBSCBSCreditoPresumidoDomain());
        assertThat(domain.possuiCredPresCbs()).isTrue();
    }

    @Test
    void teste_possuiCredPresIbsSemDados_retornaFalse() {
        var domain = new CreditoPresumidoOperacaoDomain();
        assertThat(domain.possuiCredPresIbs()).isFalse();
    }

    @Test
    void teste_possuiCredPresIbsComDadosVazios_retornaFalse() {
        var domain = new CreditoPresumidoOperacaoDomain();
        domain.setGIBSCredPres(new IBSCBSCreditoPresumidoDomain());
        assertThat(domain.possuiCredPresIbs()).isFalse();
    }

    @Test
    void teste_possuiCredPresIbsComValor_retornaTrue() {
        var domain = new CreditoPresumidoOperacaoDomain();
        var ibs = new IBSCBSCreditoPresumidoDomain();
        ibs.setVCredPres(new BigDecimal("10.00"));
        domain.setGIBSCredPres(ibs);
        assertThat(domain.possuiCredPresIbs()).isTrue();
    }

    @Test
    void teste_getVCredPresIbsSemDados_retornaZero() {
        var domain = new CreditoPresumidoOperacaoDomain();
        assertThat(domain.getVCredPresIbs()).isEqualByComparingTo(ZERO);
    }

    @Test
    void teste_getVCredPresIbsComDados_retornaValor() {
        var domain = new CreditoPresumidoOperacaoDomain();
        var ibs = new IBSCBSCreditoPresumidoDomain();
        ibs.setVCredPres(new BigDecimal("42.50"));
        domain.setGIBSCredPres(ibs);
        assertThat(domain.getVCredPresIbs()).isEqualByComparingTo("42.50");
    }

    @Test
    void teste_possuiCredPresCondSusIbsSemDados_retornaFalse() {
        var domain = new CreditoPresumidoOperacaoDomain();
        assertThat(domain.possuiCredPresCondSusIbs()).isFalse();
    }

    @Test
    void teste_possuiCredPresCondSusIbsComValor_retornaTrue() {
        var domain = new CreditoPresumidoOperacaoDomain();
        var ibs = new IBSCBSCreditoPresumidoDomain();
        ibs.setVCredPresCondSus(new BigDecimal("5.00"));
        domain.setGIBSCredPres(ibs);
        assertThat(domain.possuiCredPresCondSusIbs()).isTrue();
    }

    @Test
    void teste_getVCredPresCondSusIbsSemDados_retornaZero() {
        var domain = new CreditoPresumidoOperacaoDomain();
        assertThat(domain.getVCredPresCondSusIbs()).isEqualByComparingTo(ZERO);
    }

    @Test
    void teste_getVCredPresCondSusIbsComDados_retornaValor() {
        var domain = new CreditoPresumidoOperacaoDomain();
        var ibs = new IBSCBSCreditoPresumidoDomain();
        ibs.setVCredPresCondSus(new BigDecimal("7.75"));
        domain.setGIBSCredPres(ibs);
        assertThat(domain.getVCredPresCondSusIbs()).isEqualByComparingTo("7.75");
    }
}
