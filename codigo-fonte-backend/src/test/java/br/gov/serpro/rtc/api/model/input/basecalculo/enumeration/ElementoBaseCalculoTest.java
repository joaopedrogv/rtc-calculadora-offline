package br.gov.serpro.rtc.api.model.input.basecalculo.enumeration;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import br.gov.serpro.rtc.api.model.input.basecalculo.ComponentesBaseCalculoInput;

class ElementoBaseCalculoTest {

    private static ComponentesBaseCalculoInput criarComponentes(BigDecimal... valores) {
        ComponentesBaseCalculoInput comp = new ComponentesBaseCalculoInput();
        if (valores.length > 0 && valores[0] != null) comp.setVProd(valores[0]);
        if (valores.length > 1 && valores[1] != null) comp.setVFrete(valores[1]);
        if (valores.length > 2 && valores[2] != null) comp.setVSeg(valores[2]);
        if (valores.length > 3 && valores[3] != null) comp.setVOutro(valores[3]);
        if (valores.length > 4 && valores[4] != null) comp.setVII(valores[4]);
        if (valores.length > 5 && valores[5] != null) comp.setVIS(valores[5]);
        if (valores.length > 6 && valores[6] != null) comp.setVDesc(valores[6]);
        if (valores.length > 7 && valores[7] != null) comp.setVPIS(valores[7]);
        if (valores.length > 8 && valores[8] != null) comp.setVCOFINS(valores[8]);
        if (valores.length > 9 && valores[9] != null) comp.setVICMS(valores[9]);
        if (valores.length > 10 && valores[10] != null) comp.setVICMSUFDest(valores[10]);
        if (valores.length > 11 && valores[11] != null) comp.setVFCP(valores[11]);
        if (valores.length > 12 && valores[12] != null) comp.setVFCPUFDest(valores[12]);
        if (valores.length > 13 && valores[13] != null) comp.setVICMSMono(valores[13]);
        if (valores.length > 14 && valores[14] != null) comp.setVISSQN(valores[14]);
        return comp;
    }

    @Test
    void deveMapearTodosOs15CamposCorretamente() {
        ComponentesBaseCalculoInput comp = criarComponentes(
                new BigDecimal("1000"),  // vProd
                new BigDecimal("50"),    // vFrete
                new BigDecimal("20"),    // vSeg
                new BigDecimal("10"),    // vOutro
                new BigDecimal("150"),   // vII
                new BigDecimal("30"),    // vIS
                new BigDecimal("40"),    // vDesc
                new BigDecimal("9.25"),  // vPIS
                new BigDecimal("46.25"), // vCOFINS
                new BigDecimal("180"),   // vICMS
                new BigDecimal("20"),    // vICMSUFDest
                new BigDecimal("5"),     // vFCP
                new BigDecimal("3"),     // vFCPUFDest
                new BigDecimal("100"),   // vICMSMono
                new BigDecimal("35")     // vISSQN
        );

        assertThat(comp.getValor(ElementoBaseCalculo.V_PROD)).isEqualByComparingTo("1000");
        assertThat(comp.getValor(ElementoBaseCalculo.V_FRETE)).isEqualByComparingTo("50");
        assertThat(comp.getValor(ElementoBaseCalculo.V_SEG)).isEqualByComparingTo("20");
        assertThat(comp.getValor(ElementoBaseCalculo.V_OUTRO)).isEqualByComparingTo("10");
        assertThat(comp.getValor(ElementoBaseCalculo.V_II)).isEqualByComparingTo("150");
        assertThat(comp.getValor(ElementoBaseCalculo.V_IS)).isEqualByComparingTo("30");
        assertThat(comp.getValor(ElementoBaseCalculo.V_DESC)).isEqualByComparingTo("40");
        assertThat(comp.getValor(ElementoBaseCalculo.V_PIS)).isEqualByComparingTo("9.25");
        assertThat(comp.getValor(ElementoBaseCalculo.V_COFINS)).isEqualByComparingTo("46.25");
        assertThat(comp.getValor(ElementoBaseCalculo.V_ICMS)).isEqualByComparingTo("180");
        assertThat(comp.getValor(ElementoBaseCalculo.V_ICMS_UF_DEST)).isEqualByComparingTo("20");
        assertThat(comp.getValor(ElementoBaseCalculo.V_FCP)).isEqualByComparingTo("5");
        assertThat(comp.getValor(ElementoBaseCalculo.V_FCP_UF_DEST)).isEqualByComparingTo("3");
        assertThat(comp.getValor(ElementoBaseCalculo.V_ICMS_MONO)).isEqualByComparingTo("100");
        assertThat(comp.getValor(ElementoBaseCalculo.V_ISSQN)).isEqualByComparingTo("35");
    }

    @Test
    void deveRetornarNullQuandoCampoNaoInformado() {
        ComponentesBaseCalculoInput comp = new ComponentesBaseCalculoInput();

        assertThat(comp.getValor(ElementoBaseCalculo.V_PROD)).isNull();
        assertThat(comp.getValor(ElementoBaseCalculo.V_FRETE)).isNull();
        assertThat(comp.getValor(ElementoBaseCalculo.V_SEG)).isNull();
        assertThat(comp.getValor(ElementoBaseCalculo.V_OUTRO)).isNull();
        assertThat(comp.getValor(ElementoBaseCalculo.V_II)).isNull();
        assertThat(comp.getValor(ElementoBaseCalculo.V_IS)).isNull();
        assertThat(comp.getValor(ElementoBaseCalculo.V_DESC)).isNull();
        assertThat(comp.getValor(ElementoBaseCalculo.V_PIS)).isNull();
        assertThat(comp.getValor(ElementoBaseCalculo.V_COFINS)).isNull();
        assertThat(comp.getValor(ElementoBaseCalculo.V_ICMS)).isNull();
        assertThat(comp.getValor(ElementoBaseCalculo.V_ICMS_UF_DEST)).isNull();
        assertThat(comp.getValor(ElementoBaseCalculo.V_FCP)).isNull();
        assertThat(comp.getValor(ElementoBaseCalculo.V_FCP_UF_DEST)).isNull();
        assertThat(comp.getValor(ElementoBaseCalculo.V_ICMS_MONO)).isNull();
        assertThat(comp.getValor(ElementoBaseCalculo.V_ISSQN)).isNull();
    }
}
