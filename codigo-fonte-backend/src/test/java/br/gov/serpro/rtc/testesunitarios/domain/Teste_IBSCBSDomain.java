package br.gov.serpro.rtc.testesunitarios.domain;

import static java.math.BigDecimal.ZERO;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import br.gov.serpro.rtc.api.model.roc.CBSDomain;
import br.gov.serpro.rtc.api.model.roc.CreditoPresumidoOperacaoDomain;
import br.gov.serpro.rtc.api.model.roc.EstornoCreditoDomain;
import br.gov.serpro.rtc.api.model.roc.GrupoIBSCBSDomain;
import br.gov.serpro.rtc.api.model.roc.IBSCBSCreditoPresumidoDomain;
import br.gov.serpro.rtc.api.model.roc.IBSCBSDomain;
import br.gov.serpro.rtc.api.model.roc.IBSMunDomain;
import br.gov.serpro.rtc.api.model.roc.IBSUFDomain;
import br.gov.serpro.rtc.api.model.roc.MonofasiaDomain;
import br.gov.serpro.rtc.api.model.roc.TributacaoRegularDomain;

class Teste_IBSCBSDomain {

    @Test
    void teste_IBSCBSDomainVazio_retornaFalseENull() {
        var domain = IBSCBSDomain.builder().build();
        assertThat(domain.possuiBaseCalculoIBSCBS()).isFalse();
        assertThat(domain.getValorBaseCalculoIBSCBS()).isEqualByComparingTo(ZERO);
        assertThat(domain.possuiCbs()).isFalse();
        assertThat(domain.getGCBS()).isNull();
        assertThat(domain.getGrupoIBSCBS()).isNull();
        assertThat(domain.possuiIBSUF()).isFalse();
        assertThat(domain.possuiIBSMun()).isFalse();
        assertThat(domain.getGIBSUF()).isNull();
        assertThat(domain.getGIBSMun()).isNull();
        assertThat(domain.possuiTributacaoRegular()).isFalse();
        assertThat(domain.getTributacaoRegular()).isNull();
    }

    @Test
    void teste_IBSCBSDomainComGrupoIBSCBS_retornaDelegates() {
        var cbs = new CBSDomain();
        var ibsUf = new IBSUFDomain();
        var ibsMun = new IBSMunDomain();
        var tribReg = TributacaoRegularDomain.builder().build();
        var grupo = GrupoIBSCBSDomain.builder()
                .gCBS(cbs).gIBSUF(ibsUf).gIBSMun(ibsMun).gTribRegular(tribReg)
                .build();
        var domain = IBSCBSDomain.builder().gIBSCBS(grupo).build();

        assertThat(domain.possuiCbs()).isTrue();
        assertThat(domain.getGCBS()).isNotNull();
        assertThat(domain.possuiIBSUF()).isTrue();
        assertThat(domain.possuiIBSMun()).isTrue();
        assertThat(domain.getGIBSUF()).isNotNull();
        assertThat(domain.getGIBSMun()).isNotNull();
        assertThat(domain.possuiTributacaoRegular()).isTrue();
        assertThat(domain.getTributacaoRegular()).isNotNull();
        assertThat(domain.getGrupoIBSCBS()).isNotNull();
    }

    @Test
    void teste_possuiMonofasiaComGIBSCBSMono_retornaTrue() {
        var mono = MonofasiaDomain.builder().build();
        var domain = IBSCBSDomain.builder().gIBSCBSMono(mono).build();
        assertThat(domain.possuiMonofasia()).isTrue();
    }

    @Test
    void teste_possuiMonofasiaSemGIBSCBSMono_retornaFalse() {
        var domain = IBSCBSDomain.builder().build();
        assertThat(domain.possuiMonofasia()).isFalse();
    }

    @Test
    void teste_possuiEstornoCreditoComEstorno_retornaTrue() {
        var domain = IBSCBSDomain.builder().gEstornoCred(new EstornoCreditoDomain()).build();
        assertThat(domain.possuiEstornoCredito()).isTrue();
    }

    @Test
    void teste_possuiEstornoCreditoSemEstorno_retornaFalse() {
        var domain = IBSCBSDomain.builder().build();
        assertThat(domain.possuiEstornoCredito()).isFalse();
    }

    @Test
    void teste_IBSCBSDomainSemCredPresOper_retornaFalse() {
        var domain = IBSCBSDomain.builder().build();
        assertThat(domain.possuiCredPresCbs()).isFalse();
        assertThat(domain.getCredPresCbs()).isNull();
        assertThat(domain.possuiCredPresIbs()).isFalse();
        assertThat(domain.getVCredPresIbs()).isEqualByComparingTo(ZERO);
        assertThat(domain.possuiCredPresCondSusIbs()).isFalse();
        assertThat(domain.getVCredPresCondSusIbs()).isEqualByComparingTo(ZERO);
    }

    @Test
    void teste_IBSCBSDomainComCredPresOper_retornaTrue() {
        var cbsCredPres = new IBSCBSCreditoPresumidoDomain();
        var credPres = new CreditoPresumidoOperacaoDomain();
        credPres.setGCBSCredPres(cbsCredPres);
        var domain = IBSCBSDomain.builder().gCredPresOper(credPres).build();
        assertThat(domain.possuiCredPresCbs()).isTrue();
        assertThat(domain.getCredPresCbs()).isNotNull();
    }

    @Test
    void teste_IBSCBSDomainComCredPresOperEValor_retornaTrueEValor() {
        var ibsCredPres = new IBSCBSCreditoPresumidoDomain();
        ibsCredPres.setVCredPres(new BigDecimal("25.00"));
        var credPres = new CreditoPresumidoOperacaoDomain();
        credPres.setGIBSCredPres(ibsCredPres);
        var domain = IBSCBSDomain.builder().gCredPresOper(credPres).build();
        assertThat(domain.possuiCredPresIbs()).isTrue();
        assertThat(domain.getVCredPresIbs()).isEqualByComparingTo("25.00");
    }
}
