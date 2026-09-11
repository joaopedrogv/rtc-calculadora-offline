package br.gov.serpro.rtc.domain.service.basecalculo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import br.gov.serpro.rtc.api.model.input.basecalculo.BaseCalculoUnificadaInput;
import br.gov.serpro.rtc.api.model.input.basecalculo.ComponentesBaseCalculoInput;
import br.gov.serpro.rtc.api.model.input.basecalculo.enumeration.BaseCalculoNatureza;
import br.gov.serpro.rtc.api.model.output.basecalculo.BaseCalculoUnificadaOutput;
import br.gov.serpro.rtc.domain.service.exception.CampoInvalidoException;

class BaseCalculoUnificadaServiceTest {

    private static BaseCalculoUnificadaService service;

    @BeforeAll
    static void setUp() {
        service = new BaseCalculoUnificadaService();
    }

    private static ComponentesBaseCalculoInput comp(BigDecimal vProd, BigDecimal vFrete, BigDecimal vSeg,
            BigDecimal vOutro, BigDecimal vII, BigDecimal vIS, BigDecimal vDesc, BigDecimal vPIS,
            BigDecimal vCOFINS, BigDecimal vICMS, BigDecimal vICMSUFDest, BigDecimal vFCP,
            BigDecimal vFCPUFDest, BigDecimal vICMSMono, BigDecimal vISSQN) {
        ComponentesBaseCalculoInput c = new ComponentesBaseCalculoInput();
        c.setVProd(vProd);
        c.setVFrete(vFrete);
        c.setVSeg(vSeg);
        c.setVOutro(vOutro);
        c.setVII(vII);
        c.setVIS(vIS);
        c.setVDesc(vDesc);
        c.setVPIS(vPIS);
        c.setVCOFINS(vCOFINS);
        c.setVICMS(vICMS);
        c.setVICMSUFDest(vICMSUFDest);
        c.setVFCP(vFCP);
        c.setVFCPUFDest(vFCPUFDest);
        c.setVICMSMono(vICMSMono);
        c.setVISSQN(vISSQN);
        return c;
    }

    @Test
    void deveCalcularBCParaNFe() {
        ComponentesBaseCalculoInput componentes = comp(
                new BigDecimal("1000"), new BigDecimal("50"), new BigDecimal("20"),
                null, null, null, new BigDecimal("40"),
                null, null, new BigDecimal("180"),
                null, null, null, null, null);
        BaseCalculoUnificadaOutput resultado = service.calcular(componentes, 2026, BaseCalculoNatureza.BEM, 55);
        assertThat(resultado.getBaseRG()).isEqualByComparingTo("850.00000000");
    }

    @Test
    void deveCalcularBCParaNFCe() {
        ComponentesBaseCalculoInput componentes = comp(
                new BigDecimal("1000"), new BigDecimal("50"), null,
                null, new BigDecimal("150"), null, new BigDecimal("40"),
                null, null, new BigDecimal("180"), new BigDecimal("20"),
                null, null, null, null);
        BaseCalculoUnificadaOutput resultado = service.calcular(componentes, 2026, BaseCalculoNatureza.BEM, 65);
        assertThat(resultado.getBaseRG()).isEqualByComparingTo("830.00000000");
    }

    @Test
    void deveIgnorarCamposNaoAplicaveis() {
        ComponentesBaseCalculoInput componentes = comp(
                new BigDecimal("1000"), null, null,
                null, null, null, null,
                null, null, null, null,
                null, null, null, new BigDecimal("20"));
        BigDecimal resultadoBem = service.calcular(componentes, 2026, BaseCalculoNatureza.BEM, 55).getBaseRG();
        BigDecimal resultadoServico = service.calcular(componentes, 2026, BaseCalculoNatureza.SERVICO, 55).getBaseRG();
        assertThat(resultadoBem).isEqualByComparingTo("1000.00000000");
        assertThat(resultadoServico).isEqualByComparingTo("980.00000000");
    }

    @Test
    void deveTratarNullsComoZero() {
        ComponentesBaseCalculoInput componentes = comp(null, null, null,
                null, null, null, null,
                null, null, null,
                null, null, null, null, null);
        BigDecimal resultado = service.calcular(componentes, 2026, BaseCalculoNatureza.BEM, 55).getBaseRG();
        assertThat(resultado).isEqualByComparingTo("0.00000000");
    }

    @Test
    void deveCalcularBCNegativa() {
        ComponentesBaseCalculoInput componentes = comp(
                new BigDecimal("100"), null, null,
                null, null, null, new BigDecimal("200"),
                null, null, null,
                null, null, null, null, null);
        BigDecimal resultado = service.calcular(componentes, 2026, BaseCalculoNatureza.BEM, 55).getBaseRG();
        assertThat(resultado).isEqualByComparingTo("-100.00000000");
    }

    @Test
    void deveCalcularViaInputDTO() {
        BaseCalculoUnificadaInput input = new BaseCalculoUnificadaInput();
        input.setAnoFatoGerador(2026);
        input.setTipoDocumento(55);
        input.setNatureza(BaseCalculoNatureza.BEM);
        input.getComponentes().setVProd(new BigDecimal("1000"));
        input.getComponentes().setVDesc(new BigDecimal("40"));
        input.getComponentes().setVICMS(new BigDecimal("180"));

        BaseCalculoUnificadaOutput resultado = service.calcular(input);
        assertThat(resultado.getBaseRG()).isEqualByComparingTo("780.00000000");
    }

    @Test
    void deveLancarExcecaoParaTipoDocumentoInvalido() {
        BaseCalculoUnificadaInput input = new BaseCalculoUnificadaInput();
        input.setAnoFatoGerador(2026);
        input.setTipoDocumento(57);
        input.setNatureza(BaseCalculoNatureza.BEM);
        input.getComponentes().setVProd(new BigDecimal("1000"));

        assertThatThrownBy(() -> service.calcular(input))
                .isInstanceOf(CampoInvalidoException.class)
                .hasMessageContaining("55");
    }

    @Test
    void deveRetornarAvisosQuandoCamposIgnorados() {
        ComponentesBaseCalculoInput componentes = comp(
                new BigDecimal("1000"), null, null,
                null, null, null, null,
                null, null, null, null,
                null, null, null, new BigDecimal("20"));
        BaseCalculoUnificadaOutput resultado = service.calcular(componentes, 2026, BaseCalculoNatureza.BEM, 55);
        assertThat(resultado.getAvisos()).isNotEmpty();
        assertThat(resultado.getAvisos().get(0)).contains("vISSQN");
    }

    @Test
    void deveLancarExcecaoParaTipoDocumentoInvalidoViaComponentes() {
        ComponentesBaseCalculoInput componentes = comp(new BigDecimal("1000"), null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        assertThatThrownBy(() -> service.calcular(componentes, 2026, BaseCalculoNatureza.BEM, null))
                .isInstanceOf(CampoInvalidoException.class);
        assertThatThrownBy(() -> service.calcular(componentes, 2026, BaseCalculoNatureza.BEM, 57))
                .isInstanceOf(CampoInvalidoException.class);
    }
}
