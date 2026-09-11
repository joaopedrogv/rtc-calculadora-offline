package br.gov.serpro.rtc.api.model.input.basecalculo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.gov.serpro.rtc.api.model.output.basecalculo.BaseCalculoUnificadaOutput;
import br.gov.serpro.rtc.domain.service.basecalculo.BaseCalculoUnificadaService;
import br.gov.serpro.rtc.domain.service.exception.CampoInvalidoException;

class BaseCalculoUnificadaInputTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static BaseCalculoUnificadaService service;

    @BeforeAll
    static void setUp() {
        service = new BaseCalculoUnificadaService();
    }

    private static BaseCalculoUnificadaInput input(int ano, int tipoDocumento, String natureza,
            Map<String, Object> valores) {
        Map<String, Object> json = new HashMap<>();
        json.put("anoFatoGerador", ano);
        json.put("tipoDocumento", tipoDocumento);
        json.put("natureza", natureza);
        json.putAll(valores);
        return MAPPER.convertValue(json, BaseCalculoUnificadaInput.class);
    }

    // ─── Cálculo das três bases ────────────────────────────────────────────────

    @Test
    void deveCalcularAsTresBasesComTodosOsCamposAtivos() {
        BaseCalculoUnificadaOutput output = service.calcular(input(2026, 55, "BEM", Map.ofEntries(
                Map.entry("vProd", "1000.00"),
                Map.entry("vFrete", "50.00"),
                Map.entry("vSeg", "20.00"),
                Map.entry("vOutro", "10.00"),
                Map.entry("vII", "150.00"),
                Map.entry("vDesc", "50.00"),
                Map.entry("vPIS", "9.25"),
                Map.entry("vCOFINS", "46.25"),
                Map.entry("vICMS", "180.00"),
                Map.entry("vICMSUFDest", "20.00"),
                Map.entry("vFCP", "10.00"),
                Map.entry("vFCPUFDest", "5.00"),
                Map.entry("vICMSMono", "100.00"))));

        assertThat(output.getBaseRG()).isEqualByComparingTo("809.50");
        assertThat(output.getBaseIS()).isEqualByComparingTo("809.50");
        assertThat(output.getBaseSN()).isEqualByComparingTo("1030.00");
        assertThat(output.getAvisos()).isEmpty();
    }

    // ─── Vigência ──────────────────────────────────────────────────────────────

    @Test
    void vPisEVCofinsDevemComporAsBasesEm2026() {
        BaseCalculoUnificadaOutput output = service.calcular(input(2026, 55, "BEM",
                Map.of("vProd", "1000.00", "vPIS", "100.00", "vCOFINS", "50.00")));

        assertThat(output.getBaseRG()).isEqualByComparingTo("850.00");
        assertThat(output.getBaseIS()).isEqualByComparingTo("850.00");
        assertThat(output.getBaseSN()).isEqualByComparingTo("1000.00");
        assertThat(output.getAvisos()).isEmpty();
    }

    @Test
    void vPisEVCofinsDevemSerIgnoradosComAvisoEm2027() {
        BaseCalculoUnificadaOutput output = service.calcular(input(2027, 55, "BEM",
                Map.of("vProd", "1000.00", "vPIS", "100.00", "vCOFINS", "50.00")));

        assertThat(output.getBaseRG()).isEqualByComparingTo("1000.00");
        assertThat(output.getBaseIS()).isEqualByComparingTo("1000.00");
        assertThat(output.getAvisos()).containsExactlyInAnyOrder(
                "Campo vPIS não compõe as bases para o ano 2027 (vigência 2026–2026) e foi ignorado no cálculo",
                "Campo vCOFINS não compõe as bases para o ano 2027 (vigência 2026–2026) e foi ignorado no cálculo");
    }

    @Test
    void vIsDeveSerIgnoradoComAvisoEm2026() {
        BaseCalculoUnificadaOutput output = service.calcular(input(2026, 55, "BEM",
                Map.of("vProd", "1000.00", "vIS", "30.00")));

        assertThat(output.getBaseRG()).isEqualByComparingTo("1000.00");
        assertThat(output.getBaseSN()).isEqualByComparingTo("1000.00");
        assertThat(output.getAvisos()).containsExactly(
                "Campo vIS não compõe as bases para o ano 2026 (vigência 2027–2033) e foi ignorado no cálculo");
    }

    @Test
    void vIsDeveComporAsBasesAPartirDe2027() {
        BaseCalculoUnificadaOutput output = service.calcular(input(2027, 55, "BEM",
                Map.of("vProd", "1000.00", "vIS", "30.00")));

        assertThat(output.getBaseRG()).isEqualByComparingTo("1030.00");
        assertThat(output.getBaseIS()).isEqualByComparingTo("1000.00");
        assertThat(output.getBaseSN()).isEqualByComparingTo("1030.00");
        assertThat(output.getAvisos()).isEmpty();
    }

    @Test
    void familiaIcmsDeveComporAsBasesAte2032() {
        BaseCalculoUnificadaOutput output = service.calcular(input(2032, 55, "BEM",
                Map.of("vProd", "1000.00", "vICMS", "100.00", "vFCP", "10.00", "vICMSMono", "40.00")));

        assertThat(output.getBaseRG()).isEqualByComparingTo("850.00");
        assertThat(output.getBaseIS()).isEqualByComparingTo("850.00");
        assertThat(output.getBaseSN()).isEqualByComparingTo("1000.00");
        assertThat(output.getAvisos()).isEmpty();
    }

    @Test
    void familiaIcmsDeveSerIgnoradaComAvisoEm2033() {
        BaseCalculoUnificadaOutput output = service.calcular(input(2033, 55, "BEM",
                Map.of("vProd", "1000.00", "vICMS", "100.00", "vFCP", "10.00", "vICMSMono", "40.00")));

        assertThat(output.getBaseRG()).isEqualByComparingTo("1000.00");
        assertThat(output.getBaseIS()).isEqualByComparingTo("1000.00");
        assertThat(output.getAvisos()).containsExactlyInAnyOrder(
                "Campo vICMS não compõe as bases para o ano 2033 (vigência 2026–2032) e foi ignorado no cálculo",
                "Campo vFCP não compõe as bases para o ano 2033 (vigência 2026–2032) e foi ignorado no cálculo",
                "Campo vICMSMono não compõe as bases para o ano 2033 (vigência 2026–2032) e foi ignorado no cálculo");
    }

    // ─── Natureza ──────────────────────────────────────────────────────────────

    @Test
    void vIssqnDeveComporAsBasesApenasComServico() {
        BaseCalculoUnificadaOutput comServico = service.calcular(input(2026, 55, "SERVICO",
                Map.of("vProd", "1000.00", "vISSQN", "50.00")));

        assertThat(comServico.getBaseRG()).isEqualByComparingTo("950.00");
        assertThat(comServico.getBaseIS()).isEqualByComparingTo("950.00");
        assertThat(comServico.getBaseSN()).isEqualByComparingTo("1000.00");
        assertThat(comServico.getAvisos()).isEmpty();

        BaseCalculoUnificadaOutput comBem = service.calcular(input(2026, 55, "BEM",
                Map.of("vProd", "1000.00", "vISSQN", "50.00")));

        assertThat(comBem.getBaseRG()).isEqualByComparingTo("1000.00");
        assertThat(comBem.getAvisos()).containsExactly(
                "Campo vISSQN não compõe as bases para a natureza BEM (aplicável apenas a SERVICO) e foi ignorado no cálculo");
    }

    @Test
    void camposDeBemDevemSerIgnoradosComAvisoParaServico() {
        BaseCalculoUnificadaOutput output = service.calcular(input(2026, 55, "SERVICO",
                Map.of("vProd", "1000.00", "vII", "150.00", "vICMS", "100.00")));

        assertThat(output.getBaseRG()).isEqualByComparingTo("1000.00");
        assertThat(output.getBaseIS()).isEqualByComparingTo("1000.00");
        assertThat(output.getBaseSN()).isEqualByComparingTo("1000.00");
        assertThat(output.getAvisos()).containsExactlyInAnyOrder(
                "Campo vII não compõe as bases para a natureza SERVICO (aplicável apenas a BEM) e foi ignorado no cálculo",
                "Campo vICMS não compõe as bases para a natureza SERVICO (aplicável apenas a BEM) e foi ignorado no cálculo");
    }

    // ─── Modelo de documento ───────────────────────────────────────────────────

    @Test
    void camposExclusivosDoModelo55DevemSerIgnoradosComAvisoNoModelo65() {
        BaseCalculoUnificadaOutput output = service.calcular(input(2026, 65, "BEM",
                Map.of("vProd", "1000.00", "vII", "150.00", "vICMSUFDest", "20.00", "vFCPUFDest", "5.00")));

        assertThat(output.getBaseRG()).isEqualByComparingTo("1000.00");
        assertThat(output.getBaseIS()).isEqualByComparingTo("1000.00");
        assertThat(output.getBaseSN()).isEqualByComparingTo("1000.00");
        assertThat(output.getAvisos()).containsExactlyInAnyOrder(
                "Campo vII não compõe as bases para o documento modelo 65 e foi ignorado no cálculo",
                "Campo vICMSUFDest não compõe as bases para o documento modelo 65 e foi ignorado no cálculo",
                "Campo vFCPUFDest não compõe as bases para o documento modelo 65 e foi ignorado no cálculo");
    }

    // ─── Avisos apenas com valor diferente de zero ─────────────────────────────

    @Test
    void campoIgnoradoComValorZeroOuAusenteNaoDeveGerarAviso() {
        BaseCalculoUnificadaOutput output = service.calcular(input(2027, 55, "BEM",
                Map.of("vProd", "1000.00", "vPIS", "0.00")));

        assertThat(output.getBaseRG()).isEqualByComparingTo("1000.00");
        assertThat(output.getAvisos()).isEmpty();
    }

    @Test
    void avisosNaoDevemAfetarOCalculoDasBases() {
        BaseCalculoUnificadaOutput semAvisos = service.calcular(input(2027, 55, "BEM",
                Map.of("vProd", "1000.00", "vDesc", "50.00")));
        BaseCalculoUnificadaOutput comAvisos = service.calcular(input(2027, 55, "BEM",
                Map.of("vProd", "1000.00", "vDesc", "50.00", "vPIS", "100.00", "vCOFINS", "50.00")));

        assertThat(comAvisos.getAvisos()).hasSize(2);
        assertThat(semAvisos.getAvisos()).isEmpty();
        assertThat(comAvisos.getBaseRG()).isEqualByComparingTo(semAvisos.getBaseRG());
        assertThat(comAvisos.getBaseIS()).isEqualByComparingTo(semAvisos.getBaseIS());
        assertThat(comAvisos.getBaseSN()).isEqualByComparingTo(semAvisos.getBaseSN());
    }

    // ─── Tipo de documento inválido ────────────────────────────────────────────

    @Test
    void tipoDocumentoDiferenteDe55E65DeveLancarExcecao() {
        BaseCalculoUnificadaInput inp = input(2026, 44, "BEM", Map.of("vProd", "1000.00"));

        assertThatThrownBy(() -> service.calcular(inp))
                .isInstanceOf(CampoInvalidoException.class)
                .hasMessage("Tipo de documento deve ser 55 (NF-e) ou 65 (NFC-e)");
    }
}
