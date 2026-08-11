package br.gov.serpro.rtc.config.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários do {@link OffsetDateTimeCustomDeserializer}.
 * <p>
 * Valida o comportamento esperado para datas válidas no padrão
 * {@code yyyy-MM-dd'T'HH:mm:ssXXX}, incluindo offset explícito e sufixo
 * {@code Z} (UTC), e para entradas inválidas que devem resultar em mensagem
 * de erro contendo o nome do campo e o formato esperado.
 * </p>
 */
class OffsetDateTimeCustomDeserializerTest {

    private final OffsetDateTimeCustomDeserializer deserializer = new OffsetDateTimeCustomDeserializer();

    /**
     * Garante que datas/horas válidas com offset explícito e com sufixo
     * {@code Z} (UTC) sejam desserializadas corretamente para
     * {@link OffsetDateTime}.
     *
     * @param validDateTime valor válido de data/hora no formato aceito
     */
    @ParameterizedTest(name = "Data/hora válida: \''{0}\''")
    @ValueSource(strings = {
            "2026-01-15T13:45:00-03:00",
            "2026-05-22T10:57:23Z"
    })
    void shouldDeserializeValidOffsetDateTime(String validDateTime) throws Exception {

        JsonParser parser = mock(JsonParser.class);
        when(parser.getText()).thenReturn(validDateTime);
        DeserializationContext context = mock(DeserializationContext.class);

        OffsetDateTime result = deserializer.deserialize(parser, context);
        assertEquals(OffsetDateTime.parse(validDateTime), result);
    }

    /**
     * Garante que entradas fora do padrão aceito disparem exceção de
     * validação com mensagem contendo o nome do campo e orientação de formato.
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
    void shouldThrowExceptionForInvalidOffsetDateTime(String invalidDateTime) throws Exception {
        JsonParser parser = mock(JsonParser.class);
        when(parser.currentName()).thenReturn("nomeCampoData");
        when(parser.getText()).thenReturn(invalidDateTime);
        DeserializationContext context = mock(DeserializationContext.class);

        assertThatThrownBy(() -> deserializer.deserialize(parser, context))
                .message().contains("nomeCampoData").contains("Formato esperado");
    }

}