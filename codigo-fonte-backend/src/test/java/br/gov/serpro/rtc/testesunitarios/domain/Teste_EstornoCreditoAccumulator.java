package br.gov.serpro.rtc.testesunitarios.domain;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static java.math.BigDecimal.ZERO;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import br.gov.serpro.rtc.api.model.roc.EstornoCreditoDomain;
import br.gov.serpro.rtc.api.model.roc.reduce.EstornoCreditoAccumulator;

class Teste_EstornoCreditoAccumulator {

    @Test
    void teste_construtorPadrao_inicializaComZeros() {
        var acc = new EstornoCreditoAccumulator();
        EstornoCreditoDomain total = acc.toEstornoCreditoDomainTotal();
        assertThat(total.getVIBSEstCred()).isEqualByComparingTo(ZERO);
        assertThat(total.getVCBSEstCred()).isEqualByComparingTo(ZERO);
    }

    @Test
    void teste_construtorComValores_preservaValores() {
        var acc = new EstornoCreditoAccumulator(TEN, ONE);
        EstornoCreditoDomain total = acc.toEstornoCreditoDomainTotal();
        assertThat(total.getVIBSEstCred()).isEqualByComparingTo(TEN);
        assertThat(total.getVCBSEstCred()).isEqualByComparingTo(ONE);
    }

    @Test
    void teste_construtorComNulls_substituiPorZeros() {
        var acc = new EstornoCreditoAccumulator(null, null);
        EstornoCreditoDomain total = acc.toEstornoCreditoDomainTotal();
        assertThat(total.getVIBSEstCred()).isEqualByComparingTo(ZERO);
        assertThat(total.getVCBSEstCred()).isEqualByComparingTo(ZERO);
    }

    @Test
    void teste_fromNull_inicializaComZeros() {
        var acc = EstornoCreditoAccumulator.from(null);
        EstornoCreditoDomain total = acc.toEstornoCreditoDomainTotal();
        assertThat(total.getVIBSEstCred()).isEqualByComparingTo(ZERO);
        assertThat(total.getVCBSEstCred()).isEqualByComparingTo(ZERO);
    }

    @Test
    void teste_fromDomain_extraiValores() {
        var domain = new EstornoCreditoDomain();
        domain.setVIBSEstCred(new BigDecimal("5.50"));
        domain.setVCBSEstCred(new BigDecimal("3.25"));
        var acc = EstornoCreditoAccumulator.from(domain);
        EstornoCreditoDomain total = acc.toEstornoCreditoDomainTotal();
        assertThat(total.getVIBSEstCred()).isEqualByComparingTo("5.50");
        assertThat(total.getVCBSEstCred()).isEqualByComparingTo("3.25");
    }

    @Test
    void teste_fromDomainComCamposNulos_inicializaComZeros() {
        var domain = new EstornoCreditoDomain();
        var acc = EstornoCreditoAccumulator.from(domain);
        EstornoCreditoDomain total = acc.toEstornoCreditoDomainTotal();
        assertThat(total.getVIBSEstCred()).isEqualByComparingTo(ZERO);
        assertThat(total.getVCBSEstCred()).isEqualByComparingTo(ZERO);
    }

    @Test
    void teste_addNull_adicionaZero() {
        var acc = new EstornoCreditoAccumulator(TEN, ONE);
        var result = acc.add(null);
        EstornoCreditoDomain total = result.toEstornoCreditoDomainTotal();
        assertThat(total.getVIBSEstCred()).isEqualByComparingTo(TEN);
        assertThat(total.getVCBSEstCred()).isEqualByComparingTo(ONE);
    }

    @Test
    void teste_addValor_somaCorretamente() {
        var acc1 = new EstornoCreditoAccumulator(TEN, ONE);
        var acc2 = new EstornoCreditoAccumulator(new BigDecimal("5"), new BigDecimal("2"));
        var result = acc1.add(acc2);
        EstornoCreditoDomain total = result.toEstornoCreditoDomainTotal();
        assertThat(total.getVIBSEstCred()).isEqualByComparingTo("15");
        assertThat(total.getVCBSEstCred()).isEqualByComparingTo("3");
    }
}
