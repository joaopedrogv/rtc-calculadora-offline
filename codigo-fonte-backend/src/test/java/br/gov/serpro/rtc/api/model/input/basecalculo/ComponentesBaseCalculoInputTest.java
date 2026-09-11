package br.gov.serpro.rtc.api.model.input.basecalculo;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import br.gov.serpro.rtc.api.model.input.basecalculo.enumeration.ElementoBaseCalculo;

class ComponentesBaseCalculoInputTest {

    @Test
    void getValor_deveRetornarVProd() {
        ComponentesBaseCalculoInput comp = new ComponentesBaseCalculoInput();
        comp.setVProd(new BigDecimal("1000.00"));
        assertThat(comp.getValor(ElementoBaseCalculo.V_PROD)).isEqualByComparingTo("1000.00");
    }

    @Test
    void getValor_deveRetornarVFrete() {
        ComponentesBaseCalculoInput comp = new ComponentesBaseCalculoInput();
        comp.setVFrete(new BigDecimal("50.00"));
        assertThat(comp.getValor(ElementoBaseCalculo.V_FRETE)).isEqualByComparingTo("50.00");
    }

    @Test
    void getValor_deveRetornarVSeg() {
        ComponentesBaseCalculoInput comp = new ComponentesBaseCalculoInput();
        comp.setVSeg(new BigDecimal("20.00"));
        assertThat(comp.getValor(ElementoBaseCalculo.V_SEG)).isEqualByComparingTo("20.00");
    }

    @Test
    void getValor_deveRetornarVOutro() {
        ComponentesBaseCalculoInput comp = new ComponentesBaseCalculoInput();
        comp.setVOutro(new BigDecimal("10.00"));
        assertThat(comp.getValor(ElementoBaseCalculo.V_OUTRO)).isEqualByComparingTo("10.00");
    }

    @Test
    void getValor_deveRetornarVII() {
        ComponentesBaseCalculoInput comp = new ComponentesBaseCalculoInput();
        comp.setVII(new BigDecimal("150.00"));
        assertThat(comp.getValor(ElementoBaseCalculo.V_II)).isEqualByComparingTo("150.00");
    }

    @Test
    void getValor_deveRetornarVIS() {
        ComponentesBaseCalculoInput comp = new ComponentesBaseCalculoInput();
        comp.setVIS(new BigDecimal("30.00"));
        assertThat(comp.getValor(ElementoBaseCalculo.V_IS)).isEqualByComparingTo("30.00");
    }

    @Test
    void getValor_deveRetornarVDesc() {
        ComponentesBaseCalculoInput comp = new ComponentesBaseCalculoInput();
        comp.setVDesc(new BigDecimal("50.00"));
        assertThat(comp.getValor(ElementoBaseCalculo.V_DESC)).isEqualByComparingTo("50.00");
    }

    @Test
    void getValor_deveRetornarVPIS() {
        ComponentesBaseCalculoInput comp = new ComponentesBaseCalculoInput();
        comp.setVPIS(new BigDecimal("9.25"));
        assertThat(comp.getValor(ElementoBaseCalculo.V_PIS)).isEqualByComparingTo("9.25");
    }

    @Test
    void getValor_deveRetornarVCOFINS() {
        ComponentesBaseCalculoInput comp = new ComponentesBaseCalculoInput();
        comp.setVCOFINS(new BigDecimal("46.25"));
        assertThat(comp.getValor(ElementoBaseCalculo.V_COFINS)).isEqualByComparingTo("46.25");
    }

    @Test
    void getValor_deveRetornarVICMS() {
        ComponentesBaseCalculoInput comp = new ComponentesBaseCalculoInput();
        comp.setVICMS(new BigDecimal("180.00"));
        assertThat(comp.getValor(ElementoBaseCalculo.V_ICMS)).isEqualByComparingTo("180.00");
    }

    @Test
    void getValor_deveRetornarVICMSUFDest() {
        ComponentesBaseCalculoInput comp = new ComponentesBaseCalculoInput();
        comp.setVICMSUFDest(new BigDecimal("20.00"));
        assertThat(comp.getValor(ElementoBaseCalculo.V_ICMS_UF_DEST)).isEqualByComparingTo("20.00");
    }

    @Test
    void getValor_deveRetornarVFCP() {
        ComponentesBaseCalculoInput comp = new ComponentesBaseCalculoInput();
        comp.setVFCP(new BigDecimal("10.00"));
        assertThat(comp.getValor(ElementoBaseCalculo.V_FCP)).isEqualByComparingTo("10.00");
    }

    @Test
    void getValor_deveRetornarVFCPUFDest() {
        ComponentesBaseCalculoInput comp = new ComponentesBaseCalculoInput();
        comp.setVFCPUFDest(new BigDecimal("5.00"));
        assertThat(comp.getValor(ElementoBaseCalculo.V_FCP_UF_DEST)).isEqualByComparingTo("5.00");
    }

    @Test
    void getValor_deveRetornarVICMSMono() {
        ComponentesBaseCalculoInput comp = new ComponentesBaseCalculoInput();
        comp.setVICMSMono(new BigDecimal("100.00"));
        assertThat(comp.getValor(ElementoBaseCalculo.V_ICMS_MONO)).isEqualByComparingTo("100.00");
    }

    @Test
    void getValor_deveRetornarVISSQN() {
        ComponentesBaseCalculoInput comp = new ComponentesBaseCalculoInput();
        comp.setVISSQN(new BigDecimal("30.00"));
        assertThat(comp.getValor(ElementoBaseCalculo.V_ISSQN)).isEqualByComparingTo("30.00");
    }

    @ParameterizedTest
    @EnumSource(ElementoBaseCalculo.class)
    void getValor_deveRetornarNullQuandoCampoNaoInformado(ElementoBaseCalculo elemento) {
        ComponentesBaseCalculoInput comp = new ComponentesBaseCalculoInput();
        assertThat(comp.getValor(elemento)).isNull();
    }
}
