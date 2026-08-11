package br.gov.serpro.rtc.config.serializer;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.OffsetDateTime;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import br.gov.serpro.rtc.api.model.input.OperacaoInput;
import br.gov.serpro.rtc.api.model.roc.ROCDomain;
import br.gov.serpro.rtc.domain.service.AvisoDadosSimuladosService;
import br.gov.serpro.rtc.domain.service.CalculadoraService;
import br.gov.serpro.rtc.domain.service.VersaoAplicacaoService;

/**
 * Testes de integração do endpoint de regime-geral para validar se o Jackson da
 * aplicação está usando o {@link OffsetDateTimeCustomDeserializer}.
 * <p>
 * Os cenários cobrem entradas válidas com offset explícito e com sufixo
 * {@code Z} (UTC) — ambos tratados por um único teste parametrizado — e
 * entradas inválidas fora do formato aceito.
 * </p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-testes.yml")
@ActiveProfiles("testes")
class OffsetDateTimeCustomDeserializerApiIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private CalculadoraService calculadoraService;

	@MockitoBean
	private VersaoAplicacaoService versaoAplicacaoService;

	@MockitoBean
	private AvisoDadosSimuladosService avisoDadosSimuladosService;

    /**
     * Garante que datas/horas válidas — com offset explícito e com sufixo
     * {@code Z} (UTC) — são desserializadas corretamente e entregues ao serviço.
     *
     * @param validDateTime valor válido de data/hora no formato aceito
     */
    @ParameterizedTest(name = "Data/hora válida: \''{0}\''")
    @ValueSource(strings = {
            "2026-01-15T13:45:00-03:00",
            "2026-05-22T10:57:23Z"
    })
    void shouldUseApplicationJacksonDeserializerForValidDateTime(String validDateTime) throws Exception {
        when(calculadoraService.calcularTributos(any(OperacaoInput.class))).thenReturn(ROCDomain.builder().build());
        when(versaoAplicacaoService.getHeaders()).thenReturn(new HttpHeaders());
        when(avisoDadosSimuladosService.getWarningDadosSimulados(any(OperacaoInput.class))).thenReturn(null);

        mockMvc.perform(post("/calculadora/regime-geral")
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildRegimeGeralPayload(validDateTime)))
                .andExpect(status().isOk());

        ArgumentCaptor<OperacaoInput> captor = ArgumentCaptor.forClass(OperacaoInput.class);
        verify(calculadoraService).calcularTributos(captor.capture());
        verify(versaoAplicacaoService).getHeaders();
        verify(avisoDadosSimuladosService).getWarningDadosSimulados(any(OperacaoInput.class));

        assertEquals(OffsetDateTime.parse(validDateTime), captor.getValue().getDhFatoGerador());
    }

    /**
     * Garante que entradas fora do padrão aceito são rejeitadas pela regra
     * customizada de desserialização com resposta HTTP 400, sem acionar o serviço.
     *
     * @param invalidDateTime valor de data/hora inválido fornecido pelo teste
     */
    @ParameterizedTest(name = "Data/hora inválida: \''{0}\''")
    @ValueSource(strings = {
            "2026-01-15T13:45:00+0300",
            "2026-01-15T13:45:00+03",
            "2026-01-15T13:45:00.000-03:00",
            "2026-01-15T13:45:00.123456-03:00",
            "2026-01-15T13:45:00+03:00[Europe/Moscow]",
            "2026-01-15T13:45:00",
            "2026-01-15 13:45:00",
            "2026-02-28T25:45:00.000-03:00",
            "2026-02-28T25:45:00.000Z",
            "2026-02-28T25:45-03:00",
            "2026-02-30T13:45:00-03:00", // fevereiro com 30 dias
            "2026-02-28T25:45:00-03:00", // hora inválida
            "QUALQUERCOISAQUENAOSEJAUMADATAHORA"
    })
    void shouldRejectInvalidDateTimeByCustomRule(String invalidDateTime) throws Exception {

        mockMvc.perform(post("/calculadora/regime-geral")
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildRegimeGeralPayload(invalidDateTime)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", containsString("dhFatoGerador")))
                .andExpect(jsonPath("$.detail", containsString("Formato esperado")))
                .andExpect(jsonPath("$.detail", containsString("yyyy-MM-dd'T'HH:mm:ssXXX")));

        verify(calculadoraService, never()).calcularTributos(any(OperacaoInput.class));
        verify(versaoAplicacaoService, never()).getHeaders();
        verify(avisoDadosSimuladosService, never()).getWarningDadosSimulados(any(OperacaoInput.class));
    }

    /**
     * Monta o payload mínimo válido do endpoint de regime-geral para os testes.
     *
     * @param dhFatoGerador valor da data/hora a ser testada
     * @return JSON de entrada com os demais campos obrigatórios preenchidos
     */
    private static String buildRegimeGeralPayload(String dhFatoGerador) {
        return """
                {
                  "id": "6194602ea71cbf9431c236de4409d920",
                  "versao": "0.0.1",
                  "dhFatoGerador": "%s",
                  "municipio": 4314902,
                  "uf": "RS",
                  "itens": [
                    {
                      "numero": 1,
                      "ncm": "99999999",
                      "cst": "000",
                      "cClassTrib": "000001",
                      "baseCalculo": 200,
                      "quantidade": 1,
                      "unidade": "LT"
                    }
                  ]
                }
                """.formatted(dhFatoGerador);
    }
}
