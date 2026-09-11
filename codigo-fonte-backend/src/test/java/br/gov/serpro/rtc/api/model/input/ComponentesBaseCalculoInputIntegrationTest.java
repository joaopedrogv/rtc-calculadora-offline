package br.gov.serpro.rtc.api.model.input;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import br.gov.serpro.rtc.api.model.input.basecalculo.ComponentesBaseCalculoInput;
import com.fasterxml.jackson.databind.ObjectMapper;

class ComponentesBaseCalculoInputTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @BeforeAll
    static void setUp() {
        MAPPER.findAndRegisterModules();
    }

    @Test
    void deveSerializarECarregarPayloadCenario1_JSON() throws Exception {
        Map<String, Object> json = new HashMap<>();
        json.put("numero", 1);
        json.put("cst", "000");
        json.put("cClassTrib", "000001");
        json.put("baseCalculo", "850.00");

        OperacaoInput operacao = MAPPER.readValue("{\"id\":\"ROC001\",\"versao\":\"1\",\"dhFatoGerador\":\"2026-06-15T12:00:00Z\",\"municipio\":3550308,\"itens\":[%s]}".formatted(MAPPER.writeValueAsString(json)), OperacaoInput.class);

        assertThat(operacao.getItens()).hasSize(1);
        assertThat(operacao.getItens().get(0).getBaseCalculo()).isEqualByComparingTo("850.00");
        assertThat(operacao.getItens().get(0).getGComponentesBC()).isNull();
    }

    @Test
    void deveSerializarECarregarPayloadCenario2_JSON() throws Exception {
        Map<String, Object> item = new HashMap<>();
        item.put("numero", 1);
        item.put("cst", "000");
        item.put("cClassTrib", "000001");
        item.put("baseCalculo", "850.00");

        Map<String, Object> componentes = new HashMap<>();
        componentes.put("vProd", "1000.00");
        componentes.put("vFrete", "50.00");
        componentes.put("vSeg", "20.00");
        componentes.put("vDesc", "40.00");
        componentes.put("vICMS", "180.00");

        item.put("gComponentesBC", componentes);

        OperacaoInput operacao = MAPPER.readValue("{\"id\":\"ROC001\",\"versao\":\"1\",\"dhFatoGerador\":\"2026-06-15T12:00:00Z\",\"municipio\":3550308,\"itens\":[%s]}".formatted(MAPPER.writeValueAsString(item)), OperacaoInput.class);

        assertThat(operacao.getItens()).hasSize(1);
        assertThat(operacao.getItens().get(0).getBaseCalculo()).isEqualByComparingTo("850.00");

        ComponentesBaseCalculoInput comps = operacao.getItens().get(0).getGComponentesBC();
        assertThat(comps).isNotNull();
        assertThat(comps.getVProd()).isEqualByComparingTo("1000.00");
        assertThat(comps.getVFrete()).isEqualByComparingTo("50.00");
        assertThat(comps.getVSeg()).isEqualByComparingTo("20.00");
        assertThat(comps.getVDesc()).isEqualByComparingTo("40.00");
        assertThat(comps.getVICMS()).isEqualByComparingTo("180.00");
        assertThat(comps.possuiComponentes()).isTrue();
    }

    @Test
    void deveSerializarECarregarPayloadCenario3_JSON() throws Exception {
        Map<String, Object> item = new HashMap<>();
        item.put("numero", 1);
        item.put("cst", "000");
        item.put("cClassTrib", "000001");

        Map<String, Object> componentes = new HashMap<>();
        componentes.put("vProd", "1000.00");
        componentes.put("vFrete", "50.00");
        componentes.put("vSeg", "20.00");
        componentes.put("vDesc", "40.00");
        componentes.put("vICMS", "180.00");

        item.put("gComponentesBC", componentes);

        OperacaoInput operacao = MAPPER.readValue("{\"id\":\"ROC001\",\"versao\":\"1\",\"dhFatoGerador\":\"2026-06-15T12:00:00Z\",\"municipio\":3550308,\"itens\":[%s]}".formatted(MAPPER.writeValueAsString(item)), OperacaoInput.class);

        assertThat(operacao.getItens()).hasSize(1);
        assertThat(operacao.getItens().get(0).getBaseCalculo()).isNull();

        ComponentesBaseCalculoInput comps = operacao.getItens().get(0).getGComponentesBC();
        assertThat(comps).isNotNull();
        assertThat(comps.getVProd()).isEqualByComparingTo("1000.00");
    }

    @Test
    void deveSerializarECarregarPayloadCenario2Inconsistencia_JSON() throws Exception {
        Map<String, Object> item = new HashMap<>();
        item.put("numero", 1);
        item.put("cst", "000");
        item.put("cClassTrib", "000001");
        item.put("baseCalculo", "900.00");

        Map<String, Object> componentes = new HashMap<>();
        componentes.put("vProd", "1000.00");
        componentes.put("vFrete", "50.00");
        componentes.put("vSeg", "20.00");
        componentes.put("vDesc", "40.00");
        componentes.put("vICMS", "180.00");

        item.put("gComponentesBC", componentes);

        OperacaoInput operacao = MAPPER.readValue("{\"id\":\"ROC001\",\"versao\":\"1\",\"dhFatoGerador\":\"2026-06-15T12:00:00Z\",\"municipio\":3550308,\"itens\":[%s]}".formatted(MAPPER.writeValueAsString(item)), OperacaoInput.class);

        assertThat(operacao.getItens()).hasSize(1);
        assertThat(operacao.getItens().get(0).getBaseCalculo()).isEqualByComparingTo("900.00");

        ComponentesBaseCalculoInput comps = operacao.getItens().get(0).getGComponentesBC();
        assertThat(comps).isNotNull();
        assertThat(comps.possuiComponentes()).isTrue();
    }

    @Test
    void deveManterComponentesNulosQuandoAusentes() throws Exception {
        Map<String, Object> json = new HashMap<>();
        json.put("numero", 1);
        json.put("cst", "000");
        json.put("cClassTrib", "000001");
        json.put("baseCalculo", "850.00");
        json.put("gComponentesBC", new HashMap<>());

        OperacaoInput operacao = MAPPER.readValue("{\"id\":\"ROC001\",\"versao\":\"1\",\"dhFatoGerador\":\"2026-06-15T12:00:00Z\",\"municipio\":3550308,\"itens\":[%s]}".formatted(MAPPER.writeValueAsString(json)), OperacaoInput.class);

        ComponentesBaseCalculoInput comps = operacao.getItens().get(0).getGComponentesBC();
        assertThat(comps).isNotNull();
        assertThat(comps.possuiComponentes()).isFalse();
    }

    @Test
    void deveSerializarComponentesComTodosOs15Campos() throws Exception {
        Map<String, Object> json = new HashMap<>();
        json.put("numero", 1);
        json.put("cst", "000");
        json.put("cClassTrib", "000001");

        Map<String, Object> componentes = new HashMap<>();
        componentes.put("vProd", "1000.00");
        componentes.put("vFrete", "50.00");
        componentes.put("vSeg", "20.00");
        componentes.put("vOutro", "10.00");
        componentes.put("vII", "150.00");
        componentes.put("vIS", "30.00");
        componentes.put("vDesc", "40.00");
        componentes.put("vPIS", "9.25");
        componentes.put("vCOFINS", "46.25");
        componentes.put("vICMS", "180.00");
        componentes.put("vICMSUFDest", "20.00");
        componentes.put("vFCP", "10.00");
        componentes.put("vFCPUFDest", "5.00");
        componentes.put("vICMSMono", "100.00");
        componentes.put("vISSQN", "30.00");

        json.put("gComponentesBC", componentes);

        OperacaoInput operacao = MAPPER.readValue("{\"id\":\"ROC001\",\"versao\":\"1\",\"dhFatoGerador\":\"2026-06-15T12:00:00Z\",\"municipio\":3550308,\"tpDoc\":55,\"itens\":[%s]}".formatted(MAPPER.writeValueAsString(json)), OperacaoInput.class);

        ComponentesBaseCalculoInput comps = operacao.getItens().get(0).getGComponentesBC();
        assertThat(comps).isNotNull();
        assertThat(comps.getVProd()).isEqualByComparingTo("1000.00");
        assertThat(comps.getVFrete()).isEqualByComparingTo("50.00");
        assertThat(comps.getVSeg()).isEqualByComparingTo("20.00");
        assertThat(comps.getVOutro()).isEqualByComparingTo("10.00");
        assertThat(comps.getVII()).isEqualByComparingTo("150.00");
        assertThat(comps.getVIS()).isEqualByComparingTo("30.00");
        assertThat(comps.getVDesc()).isEqualByComparingTo("40.00");
        assertThat(comps.getVPIS()).isEqualByComparingTo("9.25");
        assertThat(comps.getVCOFINS()).isEqualByComparingTo("46.25");
        assertThat(comps.getVICMS()).isEqualByComparingTo("180.00");
        assertThat(comps.getVICMSUFDest()).isEqualByComparingTo("20.00");
        assertThat(comps.getVFCP()).isEqualByComparingTo("10.00");
        assertThat(comps.getVFCPUFDest()).isEqualByComparingTo("5.00");
        assertThat(comps.getVICMSMono()).isEqualByComparingTo("100.00");
        assertThat(comps.getVISSQN()).isEqualByComparingTo("30.00");
        assertThat(comps.possuiComponentes()).isTrue();
    }

    @Test
    void deveSerializarPayloadRetornandoValorCorretoBaseCalculo() throws Exception {
        // Payload do cenário 2 com consistência — verifica que a BC declarada
        // é mantida após desserialização (a validação contra componentes é feita no service)
        Map<String, Object> json = new HashMap<>();
        json.put("numero", 1);
        json.put("cst", "000");
        json.put("cClassTrib", "000001");
        json.put("baseCalculo", "850.00");

        Map<String, Object> componentes = new HashMap<>();
        componentes.put("vProd", "1000.00");
        componentes.put("vFrete", "50.00");
        componentes.put("vSeg", "20.00");
        componentes.put("vDesc", "40.00");
        componentes.put("vICMS", "180.00");

        json.put("gComponentesBC", componentes);

        String jsonStr = MAPPER.writeValueAsString(json);
        OperacaoInput operacao = MAPPER.readValue(
                "{\"id\":\"ROC001\",\"versao\":\"1\",\"dhFatoGerador\":\"2026-06-15T12:00:00Z\",\"municipio\":3550308,\"tpDoc\":55,\"itens\":[%s]}"
                        .formatted(jsonStr),
                OperacaoInput.class);

        assertThat(operacao.getTpDoc()).isEqualTo(55);
        assertThat(operacao.getItens()).hasSize(1);
        assertThat(operacao.getItens().get(0).getBaseCalculo()).isEqualByComparingTo("850.00");

        // Garante o roundtrip: serializa de volta e compara numericamente
        String jsonRoundTrip = MAPPER.writeValueAsString(operacao.getItens().get(0));
        Map<String, Object> itemRoundTrip = MAPPER.readValue(jsonRoundTrip, Map.class);
        assertThat(new BigDecimal(itemRoundTrip.get("baseCalculo").toString())).isEqualByComparingTo("850.00");
    }
}
