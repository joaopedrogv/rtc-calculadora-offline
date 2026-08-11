package br.gov.serpro.rtc.config.serializer;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.OffsetDateTime;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import br.gov.serpro.rtc.config.JacksonConfig;

/**
 * Testes de <em>wiring</em> que verificam se o
 * {@link br.gov.serpro.rtc.config.JacksonConfig} da aplicação registra o
 * {@link OffsetDateTimeCustomDeserializer} no {@link ObjectMapper} do contexto
 * do Spring.
 * <p>
 * Usa {@code @JsonTest} para iniciar apenas a camada de serialização/
 * desserialização, mantendo o ciclo de execução rápido.
 * </p>
 */
@JsonTest
@Import({JacksonConfig.class, OffsetDateTimeCustomDeserializer.class, LocalDateCustomDeserializer.class})
@TestPropertySource(locations = "classpath:application-testes.yml")
@ActiveProfiles("testes")
class OffsetDateTimeCustomDeserializerJacksonConfigTest {

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Garante que o {@link ObjectMapper} do contexto Spring desserializa
     * corretamente entradas válidas de {@link OffsetDateTime}, tanto com
     * offset explícito quanto com sufixo {@code Z} (UTC).
     *
     * @param validDateTime valor de data/hora válido fornecido pelo teste
     */
    @ParameterizedTest(name = "Data/hora válida: \''{0}\''")
    @ValueSource(strings = {
            "2026-01-15T13:45:00-03:00",
            "2026-05-22T10:57:23Z"
    })
    void shouldDeserializeValidOffsetDateTime(String validDateTime) throws Exception {

        OffsetDateTime result = objectMapper.readValue("\"" + validDateTime + "\"", OffsetDateTime.class);

        assertEquals(OffsetDateTime.parse(validDateTime), result);
    }

    /**
     * Garante que o {@link ObjectMapper} do contexto Spring rejeita entradas
     * fora do padrão aceito com {@link InvalidFormatException} contendo a
     * mensagem de formato definida por {@link OffsetDateTimeCustomDeserializer}.
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
    void shouldRejectInvalidOffsetDateTimeWithCustomMessage(String invalidDateTime) {
        assertThatThrownBy(() -> objectMapper.readValue("\"" + invalidDateTime + "\"", OffsetDateTime.class))
                .isInstanceOf(InvalidFormatException.class)
                .message().contains("Formato esperado")
                           .contains("yyyy-MM-dd'T'HH:mm:ssXXX");
    }
}
