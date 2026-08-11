package br.gov.serpro.rtc.testesintegracao.calculoscorretos.comprasgovernamentais;

import static br.gov.serpro.rtc.util.AssertUtils.isEqualByComparingTo;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.gov.serpro.rtc.api.model.input.OperacaoInput;
import br.gov.serpro.rtc.domain.service.CalculadoraService;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-testes.yml")
@ActiveProfiles("testes")
class CompraGovServiceTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CalculadoraService calculadoraService;

    @Test
    void deveCalcularTributosCompraGov(
            final @Value("classpath:entradas/calculoscorretos/compras-governamentais/entrada.json") Resource resourceFile)
            throws Exception {
        final var operacao = objectMapper.readValue(resourceFile.getInputStream(), OperacaoInput.class);
        final var roc = calculadoraService.calcularTributos(operacao);

        assertThat(roc).isNotNull();

        // oper.gCompraGov
        final var compraGov = roc.getOper().getGCompraGov();
        assertThat(compraGov).isNotNull();
        assertThat(compraGov.getTpEnteGov()).isNotNull();
        assertThat(compraGov.getTpOperGov()).isNotNull();
        isEqualByComparingTo(compraGov.getPRedutor(), "50.00");

        // objetos[0]
        final var objetos = roc.getObjetos();
        assertThat(objetos).isNotNull().hasSize(1);
        final var obj = objetos.get(0);
        assertThat(obj.getNObj()).isEqualTo(1);

        final var trib = obj.getTribCalc();

        // objetos[0].tribCalc.IS
        final var is = trib.getIS();
        isEqualByComparingTo(is.getVBCIS(), "200.00");
        isEqualByComparingTo(is.getPIS(), "14.00");
        isEqualByComparingTo(is.getPISEspec(), "21.30");
        isEqualByComparingTo(is.getQTrib(), "1");
        isEqualByComparingTo(is.getVIS(), "49.30");

        // objetos[0].tribCalc.IBSCBS.gIBSCBS
        final var g = trib.getIBSCBS().getGIBSCBS();
        isEqualByComparingTo(g.getVBC(), "249.30");

        // gIBSUF
        final var ibsUf = g.getGIBSUF();
        isEqualByComparingTo(ibsUf.getPIBSUF(), "0.00");
        isEqualByComparingTo(ibsUf.getGRed().getPRedAliq(), "0.00");
        isEqualByComparingTo(ibsUf.getGRed().getPAliqEfet(), "0.00");
        isEqualByComparingTo(ibsUf.getVIBSUF(), "0.00");

        // gIBSMun
        final var ibsMun = g.getGIBSMun();
        isEqualByComparingTo(ibsMun.getPIBSMun(), "0.00");
        isEqualByComparingTo(ibsMun.getGRed().getPRedAliq(), "0.00");
        isEqualByComparingTo(ibsMun.getGRed().getPAliqEfet(), "0.00");
        isEqualByComparingTo(ibsMun.getVIBSMun(), "0.00");

        isEqualByComparingTo(g.getVIBS(), "0.00");

        // gCBS
        final var cbs = g.getGCBS();
        isEqualByComparingTo(cbs.getPCBS(), "0.00");
        isEqualByComparingTo(cbs.getGRed().getPRedAliq(), "0.00");
        isEqualByComparingTo(cbs.getGRed().getPAliqEfet(), "0.00");
        isEqualByComparingTo(cbs.getVCBS(), "0.00");

        // gTribRegular
        final var reg = g.getGTribRegular();
        isEqualByComparingTo(reg.getPAliqEfetRegIBSUF(), "0.54");
        isEqualByComparingTo(reg.getVTribRegIBSUF(), "1.35");
        isEqualByComparingTo(reg.getPAliqEfetRegIBSMun(), "0.00");
        isEqualByComparingTo(reg.getVTribRegIBSMun(), "0.00");
        isEqualByComparingTo(reg.getPAliqEfetRegCBS(), "1.53");
        isEqualByComparingTo(reg.getVTribRegCBS(), "3.81");

        // gTribCompraGov
        final var tcg = g.getGTribCompraGov();
        isEqualByComparingTo(tcg.getPAliqIBSUF(), "0.00");
        isEqualByComparingTo(tcg.getVTribIBSUF(), "0.00");
        isEqualByComparingTo(tcg.getPAliqIBSMun(), "0.00");
        isEqualByComparingTo(tcg.getVTribIBSMun(), "0.00");
        isEqualByComparingTo(tcg.getPAliqCBS(), "0.00");
        isEqualByComparingTo(tcg.getVTribCBS(), "0.00");

        // total.tribCalc
        final var tot = roc.getTotal().getTribCalc();
        isEqualByComparingTo(tot.getISTot().getVIS(), "49.30");

        final var ibsCbsTot = tot.getIBSCBSTot();
        isEqualByComparingTo(ibsCbsTot.getVBCIBSCBS(), "249.30");

        final var totIbs = ibsCbsTot.getGIBS();
        isEqualByComparingTo(totIbs.getGIBSUF().getVDif(), "0.00");
        isEqualByComparingTo(totIbs.getGIBSUF().getVDevTrib(), "0.00");
        isEqualByComparingTo(totIbs.getGIBSUF().getVIBSUF(), "0.00");
        isEqualByComparingTo(totIbs.getGIBSMun().getVDif(), "0.00");
        isEqualByComparingTo(totIbs.getGIBSMun().getVDevTrib(), "0.00");
        isEqualByComparingTo(totIbs.getGIBSMun().getVIBSMun(), "0.00");
        isEqualByComparingTo(totIbs.getVIBS(), "0.00");
        isEqualByComparingTo(totIbs.getVCredPres(), "0.00");
        isEqualByComparingTo(totIbs.getVCredPresCondSus(), "0.00");

        final var totCbs = ibsCbsTot.getGCBS();
        isEqualByComparingTo(totCbs.getVDif(), "0.00");
        isEqualByComparingTo(totCbs.getVDevTrib(), "0.00");
        isEqualByComparingTo(totCbs.getVCBS(), "0.00");
        isEqualByComparingTo(totCbs.getVCredPres(), "0.00");
        isEqualByComparingTo(totCbs.getVCredPresCondSus(), "0.00");

        final var mono = ibsCbsTot.getGMono();
        isEqualByComparingTo(mono.getVIBSMono(), "0.00");
        isEqualByComparingTo(mono.getVCBSMono(), "0.00");
        isEqualByComparingTo(mono.getVIBSMonoReten(), "0.00");
        isEqualByComparingTo(mono.getVCBSMonoReten(), "0.00");
        isEqualByComparingTo(mono.getVIBSMonoRet(), "0.00");
        isEqualByComparingTo(mono.getVCBSMonoRet(), "0.00");
    }
}