package br.gov.serpro.rtc.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import br.gov.serpro.rtc.api.model.input.basecalculo.ComponentesBaseCalculoInput;
import br.gov.serpro.rtc.api.model.input.basecalculo.enumeration.BaseCalculoNatureza;
import br.gov.serpro.rtc.api.model.input.ItemOperacaoInput;
import br.gov.serpro.rtc.api.model.input.ImpostoSeletivoInput;
import br.gov.serpro.rtc.api.model.input.OperacaoInput;
import br.gov.serpro.rtc.api.model.output.basecalculo.BaseCalculoUnificadaOutput;
import br.gov.serpro.rtc.domain.service.basecalculo.BaseCalculoUnificadaService;
import br.gov.serpro.rtc.domain.service.calculotributo.model.AliquotaImpostoSeletivoModel;
import br.gov.serpro.rtc.domain.service.exception.BaseCalculoComponentesInconsistenteException;
import br.gov.serpro.rtc.domain.service.exception.CampoInvalidoException;
import br.gov.serpro.rtc.domain.service.exception.ErroGenericoValidacaoException;

class ProcessamentoItemServiceTest {

    private static ProcessamentoItemService processamentoItemService;

    @BeforeAll
    static void setUp() {
        processamentoItemService = new ProcessamentoItemService(
                null,
                null, null, null, null, null, null, null, null, null, null, null, null, null, null, new BaseCalculoUnificadaService());
    }

    private static OperacaoInput operacao(int tpDoc) {
        OperacaoInput op = new OperacaoInput();
        op.setId("test");
        op.setVersao("1.0");
        op.setTpDoc(tpDoc);
        op.setMunicipio(1234567L);
        op.setUf("RS");
        op.setDhFatoGerador(OffsetDateTime.parse("2026-01-01T12:00:00Z"));
        return op;
    }

    private static ComponentesBaseCalculoInput componentesBigDecimal(BigDecimal vProd, BigDecimal vFrete,
            BigDecimal vSeg, BigDecimal vOutro, BigDecimal vII, BigDecimal vIS, BigDecimal vDesc,
            BigDecimal vPIS, BigDecimal vCOFINS, BigDecimal vICMS, BigDecimal vICMSUFDest,
            BigDecimal vFCP, BigDecimal vFCPUFDest, BigDecimal vICMSMono, BigDecimal vISSQN) {
        ComponentesBaseCalculoInput comp = new ComponentesBaseCalculoInput();
        comp.setVProd(vProd);
        comp.setVFrete(vFrete);
        comp.setVSeg(vSeg);
        comp.setVOutro(vOutro);
        comp.setVII(vII);
        comp.setVIS(vIS);
        comp.setVDesc(vDesc);
        comp.setVPIS(vPIS);
        comp.setVCOFINS(vCOFINS);
        comp.setVICMS(vICMS);
        comp.setVICMSUFDest(vICMSUFDest);
        comp.setVFCP(vFCP);
        comp.setVFCPUFDest(vFCPUFDest);
        comp.setVICMSMono(vICMSMono);
        comp.setVISSQN(vISSQN);
        return comp;
    }

    @Test
    void deveLancarErroQuandoQuantidadeNaoInformada() {
        ItemOperacaoInput item = new ItemOperacaoInput();

        ImpostoSeletivoInput impostoSeletivo = new ImpostoSeletivoInput();
        impostoSeletivo.setUnidade("UN");

        item.setImpostoSeletivo(impostoSeletivo);

        AliquotaImpostoSeletivoModel aliquota = AliquotaImpostoSeletivoModel.builder()
                .aliquotaAdRem(BigDecimal.ONE)
                .unidadeMedida("UN")
                .build();

        assertThatThrownBy(() -> processamentoItemService.validarQuantidadeEUnidade(item, aliquota))
                .isInstanceOf(ErroGenericoValidacaoException.class)
                .hasMessageContaining(
                        "A quantidade do Imposto Seletivo deve ser informada para alíquota ad rem");
    }

    @Test
    void deveLancarErroQuandoQuantidadeIgualAZero() {
        ItemOperacaoInput item = new ItemOperacaoInput();
        item.setQuantidade(BigDecimal.ZERO);
        item.setUnidade("UN");

        AliquotaImpostoSeletivoModel aliquota = AliquotaImpostoSeletivoModel.builder()
                .aliquotaAdRem(BigDecimal.ONE)
                .unidadeMedida("UN")
                .build();

        assertThatThrownBy(() -> processamentoItemService.validarQuantidadeEUnidade(item, aliquota))
                .isInstanceOf(ErroGenericoValidacaoException.class)
                .hasMessageContaining("A quantidade do item deve ser maior do que zero");
    }

    @Test
    void deveLancarErroQuandoQuantidadeMenorQueZero() {
        ItemOperacaoInput item = new ItemOperacaoInput();
        item.setQuantidade(new BigDecimal("-1"));
        item.setUnidade("UN");

        AliquotaImpostoSeletivoModel aliquota = AliquotaImpostoSeletivoModel.builder()
                .aliquotaAdRem(BigDecimal.ONE)
                .unidadeMedida("UN")
                .build();

        assertThatThrownBy(() -> processamentoItemService.validarQuantidadeEUnidade(item, aliquota))
                .isInstanceOf(ErroGenericoValidacaoException.class)
                .hasMessageContaining("A quantidade do item deve ser maior do que zero");
    }

    @Test
    void deveLancarErroQuandoUnidadeNaoInformada() {
        ItemOperacaoInput item = new ItemOperacaoInput();
        item.setQuantidade(BigDecimal.ONE);

        ImpostoSeletivoInput impostoSeletivo = new ImpostoSeletivoInput();
        impostoSeletivo.setQuantidade(BigDecimal.ONE);

        item.setImpostoSeletivo(impostoSeletivo);

        AliquotaImpostoSeletivoModel aliquota = AliquotaImpostoSeletivoModel.builder()
                .aliquotaAdRem(BigDecimal.ONE)
                .build();

        assertThatThrownBy(() -> processamentoItemService.validarQuantidadeEUnidade(item, aliquota))
                .isInstanceOf(ErroGenericoValidacaoException.class)
                .hasMessageContaining(
                        "A unidade de medida do Imposto Seletivo deve ser informada para alíquota ad rem");
    }

    @Test
    void naoDeveCriticarUnidadeDiferenteDaAliquota() {
        // A unidade informada deixou de ser criticada contra a unidade
        // vinculada à NCM; permanece exigido apenas o seu preenchimento
        // quando há alíquota ad rem.
        ItemOperacaoInput item = new ItemOperacaoInput();
        item.setQuantidade(BigDecimal.ONE);

        ImpostoSeletivoInput impostoSeletivo = new ImpostoSeletivoInput();
        impostoSeletivo.setQuantidade(BigDecimal.ONE);
        impostoSeletivo.setUnidade("KG");

        item.setImpostoSeletivo(impostoSeletivo);

        AliquotaImpostoSeletivoModel aliquota = AliquotaImpostoSeletivoModel.builder()
                .aliquotaAdRem(BigDecimal.ONE)
                .unidadeMedida("UN")
                .build();

        assertThatCode(() -> processamentoItemService.validarQuantidadeEUnidade(item, aliquota))
                .doesNotThrowAnyException();
    }

    @Test
    void naoDeveLancarErroQuandoNaoHouverAliquotaAdRem() {
        ItemOperacaoInput item = new ItemOperacaoInput();
        AliquotaImpostoSeletivoModel aliquota = AliquotaImpostoSeletivoModel.builder()
                .build();

        assertThatCode(() -> processamentoItemService.validarQuantidadeEUnidade(item, aliquota))
                .doesNotThrowAnyException();
    }

    @Test
    void naoDeveLancarErroQuandoTudoValido() {
        ItemOperacaoInput item = new ItemOperacaoInput();
        item.setQuantidade(BigDecimal.TEN);
        item.setUnidade("UN");

        AliquotaImpostoSeletivoModel aliquota = AliquotaImpostoSeletivoModel.builder()
                .aliquotaAdRem(BigDecimal.ONE)
                .unidadeMedida("UN")
                .build();

        assertThatCode(() -> processamentoItemService.validarQuantidadeEUnidade(item, aliquota))
                .doesNotThrowAnyException();
    }

    // ─── Cenários com componentes da base de cálculo ───────────────────────────────

    @Test
    void deveCalcularBCAutomaticamenteQuandoSomenteComponentesInformados() {
        OperacaoInput op = operacao(55);
        ComponentesBaseCalculoInput comp = componentesBigDecimal(
                new BigDecimal("1000"), new BigDecimal("50"), new BigDecimal("20"),
                null, null, null, new BigDecimal("40"),
                null, null, new BigDecimal("180"),
                null, null, null, null, null);
        ItemOperacaoInput item = new ItemOperacaoInput();
        item.setNumero(1);
        item.setCst("000");
        item.setCClassTrib("000001");
        item.setNcm("24021000");
        item.setGComponentesBC(comp);

        processamentoItemService.validarBaseCalculoComComponentes(op, item, comp);

        BigDecimal bc = item.getBaseCalculo();
        assertThat(bc).isNotNull();
        assertThat(bc).isEqualByComparingTo("850.00");
    }

    @Test
    void deveRejeitarBCDivergenteDosComponentes() {
        OperacaoInput op = operacao(55);
        ComponentesBaseCalculoInput comp = componentesBigDecimal(
                new BigDecimal("1000"), new BigDecimal("50"), new BigDecimal("20"),
                null, null, null, new BigDecimal("40"),
                null, null, new BigDecimal("180"),
                null, null, null, null, null);
        ItemOperacaoInput item = new ItemOperacaoInput();
        item.setNumero(1);
        item.setCst("000");
        item.setCClassTrib("000001");
        item.setNcm("24021000");
        item.setBaseCalculo(new BigDecimal("900"));
        item.setGComponentesBC(comp);

        assertThatThrownBy(() -> processamentoItemService.validarBaseCalculoComComponentes(op, item, comp))
                .isInstanceOf(BaseCalculoComponentesInconsistenteException.class)
                .hasMessageContaining("850.00");
    }

    @Test
    void deveAceitarBCConsistenteComComponentes() {
        OperacaoInput op = operacao(55);
        ComponentesBaseCalculoInput comp = componentesBigDecimal(
                new BigDecimal("1000"), new BigDecimal("50"), new BigDecimal("20"),
                null, null, null, new BigDecimal("40"),
                null, null, new BigDecimal("180"),
                null, null, null, null, null);
        ItemOperacaoInput item = new ItemOperacaoInput();
        item.setNumero(1);
        item.setCst("000");
        item.setCClassTrib("000001");
        item.setNcm("24021000");
        item.setBaseCalculo(new BigDecimal("850"));
        item.setGComponentesBC(comp);

        assertThatCode(() -> processamentoItemService.validarBaseCalculoComComponentes(op, item, comp))
                .doesNotThrowAnyException();
    }

    @Test
    void deveRejeitarQuandoTpDocNaoInformadoComComponentes() {
        OperacaoInput op = new OperacaoInput();
        op.setId("test");
        op.setVersao("1.0");
        op.setMunicipio(1234567L);
        op.setUf("RS");
        op.setDhFatoGerador(OffsetDateTime.parse("2026-01-01T12:00:00Z"));
        ComponentesBaseCalculoInput comp = componentesBigDecimal(
                new BigDecimal("1000"), null, null, null, null, null, null,
                null, null, null, null, null, null, null, null);
        ItemOperacaoInput item = new ItemOperacaoInput();
        item.setNumero(1);
        item.setCst("000");
        item.setCClassTrib("000001");
        item.setGComponentesBC(comp);

        assertThatThrownBy(() -> processamentoItemService.validarBaseCalculoComComponentes(op, item, comp))
                .isInstanceOf(CampoInvalidoException.class);
    }

    @Test
    void deveRejeitarQuandoTpDocInvalidoComComponentes() {
        OperacaoInput op = operacao(57);
        ComponentesBaseCalculoInput comp = componentesBigDecimal(
                new BigDecimal("1000"), null, null, null, null, null, null,
                null, null, null, null, null, null, null, null);
        ItemOperacaoInput item = new ItemOperacaoInput();
        item.setNumero(1);
        item.setCst("000");
        item.setCClassTrib("000001");
        item.setGComponentesBC(comp);

        assertThatThrownBy(() -> processamentoItemService.validarBaseCalculoComComponentes(op, item, comp))
                .isInstanceOf(CampoInvalidoException.class);
    }

    @Test
    void deveManterComportamentoLegadoQuandoSemComponentes() {
        ItemOperacaoInput item = new ItemOperacaoInput();
        item.setNumero(1);
        item.setCst("000");
        item.setCClassTrib("000001");
        item.setBaseCalculo(new BigDecimal("850"));

        assertThatCode(() -> processamentoItemService.validarBaseCalculoLegado(item))
                .doesNotThrowAnyException();
    }

    // ─── Cenários com base IS (Imposto Seletivo) via componentes ───────────────────

    @Test
    void deveCalcularBaseISAutomaticamenteQuandoImpostoSeletivoInformadoSemBaseIS() {
        OperacaoInput op = operacao(55);
        ComponentesBaseCalculoInput comp = componentesBigDecimal(
                new BigDecimal("1000"), new BigDecimal("50"), new BigDecimal("20"),
                null, null, null, new BigDecimal("40"),
                null, null, new BigDecimal("180"),
                null, null, null, null, null);

        ItemOperacaoInput item = new ItemOperacaoInput();
        item.setNumero(1);
        item.setCst("000");
        item.setCClassTrib("000001");
        item.setNcm("24021000");
        item.setGComponentesBC(comp);

        ImpostoSeletivoInput is = new ImpostoSeletivoInput();
        is.setCst("000");
        is.setCClassTrib("000000");
        // baseCalculo IS not set (null) - should be auto-calculated
        item.setImpostoSeletivo(is);

        processamentoItemService.validarBaseCalculoComComponentes(op, item, comp);

        // Base IS should be auto-calculated from components
        assertThat(item.getImpostoSeletivo().getBaseCalculo()).isNotNull();
        // With IS present, item.baseCalculo is NOT set (only impostoSeletivo.baseCalculo is relevant)
        assertThat(item.getBaseCalculo()).isNull();
    }

    @Test
    void deveLancarExcecaoQuandoBaseISDeclaradaInconsistenteComComponentes() {
        OperacaoInput op = operacao(55);
        ComponentesBaseCalculoInput comp = componentesBigDecimal(
                new BigDecimal("1000"), new BigDecimal("50"), new BigDecimal("20"),
                null, null, null, new BigDecimal("40"),
                null, null, new BigDecimal("180"),
                null, null, null, null, null);

        ItemOperacaoInput item = new ItemOperacaoInput();
        item.setNumero(1);
        item.setCst("000");
        item.setCClassTrib("000001");
        item.setNcm("24021000");
        item.setGComponentesBC(comp);

        ImpostoSeletivoInput is = new ImpostoSeletivoInput();
        is.setCst("000");
        is.setCClassTrib("000000");
        is.setBaseCalculo(new BigDecimal("999.99")); // Wrong value
        item.setImpostoSeletivo(is);

        assertThatThrownBy(() -> processamentoItemService.validarBaseCalculoComComponentes(op, item, comp))
                .isInstanceOf(BaseCalculoComponentesInconsistenteException.class);
    }

    @Test
    void deveAceitarQuandoBaseISDeclaradaConsistenteComComponentes() {
        OperacaoInput op = operacao(55);
        ComponentesBaseCalculoInput comp = componentesBigDecimal(
                new BigDecimal("1000"), new BigDecimal("50"), new BigDecimal("20"),
                null, null, null, new BigDecimal("40"),
                null, null, new BigDecimal("180"),
                null, null, null, null, null);

        ItemOperacaoInput item = new ItemOperacaoInput();
        item.setNumero(1);
        item.setCst("000");
        item.setCClassTrib("000001");
        item.setNcm("24021000");
        item.setGComponentesBC(comp);

        // First calculate to find what baseIS should be
        BaseCalculoUnificadaOutput resultado = new BaseCalculoUnificadaService().calcular(comp, 2026, BaseCalculoNatureza.BEM, 55);

        ImpostoSeletivoInput is = new ImpostoSeletivoInput();
        is.setCst("000");
        is.setCClassTrib("000000");
        is.setBaseCalculo(resultado.getBaseIS()); // Correct value
        item.setImpostoSeletivo(is);

        assertThatCode(() -> processamentoItemService.validarBaseCalculoComComponentes(op, item, comp))
                .doesNotThrowAnyException();

        // With IS present, item.baseCalculo is NOT set (only impostoSeletivo.baseCalculo is relevant)
        assertThat(item.getBaseCalculo()).isNull();
    }

}
