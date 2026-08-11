package br.gov.serpro.rtc.testesunitarios.domain;

import static java.math.BigDecimal.ZERO;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import br.gov.serpro.rtc.api.model.roc.CBSDomain;
import br.gov.serpro.rtc.api.model.roc.GrupoIBSCBSDomain;
import br.gov.serpro.rtc.api.model.roc.IBSCBSDomain;
import br.gov.serpro.rtc.api.model.roc.ImpostoSeletivoDomain;
import br.gov.serpro.rtc.api.model.roc.ObjetoDomain;
import br.gov.serpro.rtc.api.model.roc.TributosDomain;

class Teste_ObjetoDomain {

    @Test
    void teste_inicializarObjetoDomainVazio_retornaDefaultsEFalse() {
        var obj = ObjetoDomain.builder().nObj(1).build();
        assertThat(obj.possuiImpostoSeletivo()).isFalse();
        assertThat(obj.getImpostoSeletivo()).isNull();
        assertThat(obj.getValorImpostoSeletivo()).isEqualByComparingTo(ZERO);
        assertThat(obj.possuiBaseCalculoIBSCBS()).isFalse();
        assertThat(obj.getValorBaseCalculoIBSCBS()).isEqualByComparingTo(ZERO);
        assertThat(obj.possuiCbs()).isFalse();
        assertThat(obj.getGCBS()).isNull();
        assertThat(obj.possuiCredPresCbs()).isFalse();
        assertThat(obj.getCredPresCbs()).isNull();
        assertThat(obj.possuiCredPresIbs()).isFalse();
        assertThat(obj.possuiCredPresCondSusIbs()).isFalse();
        assertThat(obj.getVCredPresIbs()).isEqualByComparingTo(ZERO);
        assertThat(obj.getVCredPresCondSusIbs()).isEqualByComparingTo(ZERO);
        assertThat(obj.getGrupoIBSCBS()).isNull();
        assertThat(obj.possuiIBSUF()).isFalse();
        assertThat(obj.getGIBSUF()).isNull();
        assertThat(obj.possuiIBSMun()).isFalse();
        assertThat(obj.getGIBSMun()).isNull();
        assertThat(obj.possuiMonofasia()).isFalse();
        assertThat(obj.getGIBSCBSMono()).isNull();
        assertThat(obj.possuiTributacaoRegular()).isFalse();
        assertThat(obj.getTributacaoRegular()).isNull();
        assertThat(obj.possuiEstornoCredito()).isFalse();
        assertThat(obj.getGEstornoCred()).isNull();
    }

    @Test
    void teste_objetoDomainComTribCalc_delegaCorretamente() {
        var is = ImpostoSeletivoDomain.builder().vIS(new BigDecimal("50")).build();
        var cbs = new CBSDomain();
        var grupo = GrupoIBSCBSDomain.builder().gCBS(cbs).build();
        var ibscbs = IBSCBSDomain.builder().gIBSCBS(grupo).build();
        var tributos = TributosDomain.builder().IS(is).IBSCBS(ibscbs).build();
        var obj = ObjetoDomain.builder().nObj(1).tribCalc(tributos).build();

        assertThat(obj.possuiImpostoSeletivo()).isTrue();
        assertThat(obj.getImpostoSeletivo()).isNotNull();
        assertThat(obj.getValorImpostoSeletivo()).isEqualByComparingTo("50");
        assertThat(obj.possuiCbs()).isTrue();
        assertThat(obj.getGCBS()).isNotNull();
    }

    @Test
    void teste_compareTo_comparaCorretamente() {
        var obj1 = ObjetoDomain.builder().nObj(1).build();
        var obj2 = ObjetoDomain.builder().nObj(2).build();
        var obj3 = ObjetoDomain.builder().nObj(1).build();
        assertThat(obj1).isLessThan(obj2);
        assertThat(obj2).isGreaterThan(obj1);
        assertThat(obj1).isEqualByComparingTo(obj3);
    }

    @Test
    void teste_compareToComNull_objNullMenor() {
        var objNull = ObjetoDomain.builder().nObj(null).build();
        var obj1 = ObjetoDomain.builder().nObj(1).build();
        assertThat(objNull).isLessThan(obj1);
        assertThat(obj1).isGreaterThan(objNull);
    }

    @Test
    void teste_equalsEHashCode_consideraApenasNumeroDeObj() {
        var obj1 = ObjetoDomain.builder().nObj(1).build();
        var obj2 = ObjetoDomain.builder().nObj(1).tribCalc(TributosDomain.builder().build()).build();
        var obj1HashCode = obj1.hashCode();
        var obj2HashCode = obj2.hashCode();
        
        assertThat(obj1).isEqualTo(obj2);
        assertThat(obj1HashCode).isEqualTo(obj2HashCode);
    }
}
