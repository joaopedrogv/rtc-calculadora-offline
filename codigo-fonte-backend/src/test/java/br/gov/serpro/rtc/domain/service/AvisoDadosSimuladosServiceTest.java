package br.gov.serpro.rtc.domain.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import br.gov.serpro.rtc.api.model.input.ItemOperacaoInput;
import br.gov.serpro.rtc.api.model.input.OperacaoInput;
import br.gov.serpro.rtc.api.model.input.ImpostoSeletivoInput;
import br.gov.serpro.rtc.domain.model.enumeration.TipoWarningDadosSimulados;

class CalculadoraServiceTest {

    private static AvisoDadosSimuladosService avisoDadosSimuladosService;

    @BeforeAll
    static void setUp() {
        // No momento não estamos testando dependências, então podem ser nulas
        avisoDadosSimuladosService = new AvisoDadosSimuladosService();
    }

    @Test
    void deveRetornarNullQuandoDataAnteriorA2027() {
        OperacaoInput operacao = new OperacaoInput();
        operacao.setDhFatoGerador(OffsetDateTime.parse("2026-12-31T23:59:59-03:00"));
        operacao.setItens(List.of(new ItemOperacaoInput()));

        TipoWarningDadosSimulados resultado = avisoDadosSimuladosService.getWarningDadosSimulados(operacao);

        assertThat(resultado).isNull();
    }

    @ParameterizedTest
    @ValueSource(strings = { "010", "220", "221" })
    void deveRetornarCasoAliquotasFicticiasParaQualquerData(String cst) {
        Map<String, TipoWarningDadosSimulados> datasAte2026 = Map.of(
                "2026-01-01T03:00:00-03:00", TipoWarningDadosSimulados.CASO_ALIQUOTAS_FICTICIAS,
                "2026-12-31T23:59:59-03:00", TipoWarningDadosSimulados.CASO_ALIQUOTAS_FICTICIAS,
                "2027-01-01T00:00:00-03:00", TipoWarningDadosSimulados.CASO_CALCULOS_SIMULADOS,
                "2028-05-10T12:00:00-03:00", TipoWarningDadosSimulados.CASO_CALCULOS_SIMULADOS);
        datasAte2026.forEach((data, expected) -> {
            OperacaoInput operacao = new OperacaoInput();
            operacao.setDhFatoGerador(OffsetDateTime.parse(data));
            ItemOperacaoInput item = new ItemOperacaoInput();
            item.setCst(cst);
            operacao.setItens(List.of(item));
            TipoWarningDadosSimulados resultado = avisoDadosSimuladosService.getWarningDadosSimulados(operacao);
            assertThat(resultado)
                    .withFailMessage("Esperado "+ expected +" para CST %s na data %s",
                            cst, data)
                    .isEqualTo(expected);
            assertThat(resultado.getValor()).isEqualTo(expected.getValor());
        });
    }

    @Test
    void deveRetornarCasoImpostoSeletivo() {
        OperacaoInput operacao = new OperacaoInput();
        operacao.setDhFatoGerador(OffsetDateTime.parse("2027-01-01T03:00:00-03:00"));

        ItemOperacaoInput item = new ItemOperacaoInput();
        item.setCst("000");
        item.setImpostoSeletivo(new ImpostoSeletivoInput());
        operacao.setItens(List.of(item));

        TipoWarningDadosSimulados resultado = avisoDadosSimuladosService.getWarningDadosSimulados(operacao);

        assertThat(resultado).isEqualTo(TipoWarningDadosSimulados.CASO_CALCULOS_SIMULADOS);
        assertThat(resultado.getValor()).isEqualTo(6);
    }

    @Test
    void deveRetornarCasoGeral() {
        OperacaoInput operacao = new OperacaoInput();
        operacao.setDhFatoGerador(OffsetDateTime.parse("2027-01-01T03:00:00-03:00"));

        ItemOperacaoInput item = new ItemOperacaoInput();
        item.setCst("000");
        operacao.setItens(List.of(item));

        TipoWarningDadosSimulados resultado = avisoDadosSimuladosService.getWarningDadosSimulados(operacao);

        assertThat(resultado).isEqualTo(TipoWarningDadosSimulados.CASO_CALCULOS_SIMULADOS);
        assertThat(resultado.getValor()).isEqualTo(6);
    }
}