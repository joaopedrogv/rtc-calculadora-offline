package br.gov.serpro.rtc.testesunitarios.domain;

import static java.math.BigDecimal.ZERO;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import br.gov.serpro.rtc.api.model.roc.CBSDomain;
import br.gov.serpro.rtc.api.model.roc.EstornoCreditoDomain;
import br.gov.serpro.rtc.api.model.roc.GrupoIBSCBSDomain;
import br.gov.serpro.rtc.api.model.roc.IBSCBSDomain;
import br.gov.serpro.rtc.api.model.roc.IBSMunDomain;
import br.gov.serpro.rtc.api.model.roc.IBSUFDomain;
import br.gov.serpro.rtc.api.model.roc.ImpostoSeletivoDomain;
import br.gov.serpro.rtc.api.model.roc.MonofasiaDomain;
import br.gov.serpro.rtc.api.model.roc.TributacaoRegularDomain;
import br.gov.serpro.rtc.api.model.roc.TributosDomain;

class Teste_TributosDomain {

    @Test
    void teste_inicializacaoSemImpostoSeletivo_retornaFalse() {
        var tributos = TributosDomain.builder().build();
        assertThat(tributos.possuiImpostoSeletivo()).isFalse();
        assertThat(tributos.getValorImpostoSeletivo()).isEqualByComparingTo(ZERO);
    }

    @Test
    void teste_inicializacaoComImpostoSeletivo_retornaTrue() {
        var is = ImpostoSeletivoDomain.builder().vIS(new BigDecimal("100")).build();
        var tributos = TributosDomain.builder().IS(is).build();
        assertThat(tributos.possuiImpostoSeletivo()).isTrue();
        assertThat(tributos.getValorImpostoSeletivo()).isEqualByComparingTo("100");
    }

    @Test
    void teste_inicializacaoSemIBSCBS_retornaDefaultsNullEZero() {
        var tributos = TributosDomain.builder().build();
        assertThat(tributos.possuiBaseCalculoIBSCBS()).isFalse();
        assertThat(tributos.getValorBaseCalculoIBSCBS()).isEqualByComparingTo(ZERO);
        assertThat(tributos.possuiCbs()).isFalse();
        assertThat(tributos.getGCBS()).isNull();
        assertThat(tributos.possuiCredPresCbs()).isFalse();
        assertThat(tributos.getCredPresCbs()).isNull();
        assertThat(tributos.possuiCredPresIbs()).isFalse();
        assertThat(tributos.getVCredPresIbs()).isEqualByComparingTo(ZERO);
        assertThat(tributos.possuiCredPresCondSusIbs()).isFalse();
        assertThat(tributos.getVCredPresCondSusIbs()).isEqualByComparingTo(ZERO);
        assertThat(tributos.getGrupoIBSCBS()).isNull();
        assertThat(tributos.possuiIBSUF()).isFalse();
        assertThat(tributos.possuiIBSMun()).isFalse();
        assertThat(tributos.getGIBSUF()).isNull();
        assertThat(tributos.getGIBSMun()).isNull();
        assertThat(tributos.possuiMonofasia()).isFalse();
        assertThat(tributos.getGIBSCBSMono()).isNull();
        assertThat(tributos.possuiTributacaoRegular()).isFalse();
        assertThat(tributos.getTributacaoRegular()).isNull();
        assertThat(tributos.possuiEstornoCredito()).isFalse();
        assertThat(tributos.getGEstornoCred()).isNull();
    }

    @Test
    void teste_inicializacaoComIBSCBSComGrupo_retornaDelegates() {
        var cbs = new CBSDomain();
        var ibsUf = new IBSUFDomain();
        var ibsMun = new IBSMunDomain();
        var tributacaoRegular = TributacaoRegularDomain.builder().build();
        var grupo = GrupoIBSCBSDomain.builder()
                .gCBS(cbs)
                .gIBSUF(ibsUf)
                .gIBSMun(ibsMun)
                .gTribRegular(tributacaoRegular)
                .build();
        var ibscbs = IBSCBSDomain.builder().gIBSCBS(grupo).build();
        var tributos = TributosDomain.builder().IBSCBS(ibscbs).build();

        assertThat(tributos.possuiCbs()).isTrue();
        assertThat(tributos.getGCBS()).isNotNull();
        assertThat(tributos.possuiIBSUF()).isTrue();
        assertThat(tributos.getGIBSUF()).isNotNull();
        assertThat(tributos.possuiIBSMun()).isTrue();
        assertThat(tributos.getGIBSMun()).isNotNull();
        assertThat(tributos.possuiTributacaoRegular()).isTrue();
        assertThat(tributos.getTributacaoRegular()).isNotNull();
        assertThat(tributos.getGrupoIBSCBS()).isNotNull();
    }

    @Test
    void teste_inicializacaoComMonofasia_retornaTrue() {
        var mono = MonofasiaDomain.builder().build();
        var ibscbs = IBSCBSDomain.builder().gIBSCBSMono(mono).build();
        var tributos = TributosDomain.builder().IBSCBS(ibscbs).build();
        assertThat(tributos.possuiMonofasia()).isTrue();
        assertThat(tributos.getGIBSCBSMono()).isNotNull();
    }

    @Test
    void teste_inicializacaoComEstornoCredito_retornaTrue() {
        var estorno = new EstornoCreditoDomain();
        var ibscbs = IBSCBSDomain.builder().gEstornoCred(estorno).build();
        var tributos = TributosDomain.builder().IBSCBS(ibscbs).build();
        assertThat(tributos.possuiEstornoCredito()).isTrue();
        assertThat(tributos.getGEstornoCred()).isNotNull();
    }
}
