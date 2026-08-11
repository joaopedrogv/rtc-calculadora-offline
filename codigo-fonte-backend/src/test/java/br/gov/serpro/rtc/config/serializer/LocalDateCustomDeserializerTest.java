package br.gov.serpro.rtc.config.serializer;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;

class LocalDateCustomDeserializerTest {

    private final LocalDateCustomDeserializer deserializer = new LocalDateCustomDeserializer();

    @Test
    void shouldDeserializeValidDate() throws Exception {
        JsonParser parser = mock(JsonParser.class);
        when(parser.getText()).thenReturn("2026-01-15");
        DeserializationContext context = mock(DeserializationContext.class);

        LocalDate result = deserializer.deserialize(parser, context);
        assertEquals(LocalDate.of(2026, 1, 15), result);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "15/01/2026",      // formato inválido
        "2026-02-30",      // data inválida: fevereiro com 30 dias
        "2026-13-01",      // mês inválido
        "2026-01-32",      // dia inválido
        "2026-00-15",      // mês zero
        "2026-01-00",      // dia zero
        "2026-01-1",       // dia com um dígito
        "2026-1-15",       // mês com um dígito
        "2026/01/15",      // separador errado
        "abcd-ef-gh"       // texto aleatório
    })
    void shouldThrowExceptionForInvalidDate(String invalidDate) throws Exception {
        JsonParser parser = mock(JsonParser.class);
        when(parser.currentName()).thenReturn("nomeCampoData");
        when(parser.getText()).thenReturn(invalidDate);
        DeserializationContext context = mock(DeserializationContext.class);

        assertThatThrownBy(() -> deserializer.deserialize(parser, context))
                .message().contains("nomeCampoData").contains("Formato esperado");
    }
}