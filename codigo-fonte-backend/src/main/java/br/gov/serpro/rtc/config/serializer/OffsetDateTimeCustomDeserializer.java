package br.gov.serpro.rtc.config.serializer;

import static java.time.OffsetDateTime.parse;
import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

import java.io.IOException;
import java.time.OffsetDateTime;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import lombok.extern.slf4j.Slf4j;

/**
 * Desserializador customizado para campos {@link OffsetDateTime} no payload JSON.
 * <p>
 * A implementação delega a lógica de validação e conversão para
 * {@link GenericCustomDeserializer}, restringindo os valores ao padrão
 * {@code yyyy-MM-dd'T'HH:mm:ssXXX} com offset explícito (por exemplo,
 * {@code -03:00}) ou sufixo {@code Z} (UTC).
 * </p>
 * <p>
 * Exemplos válidos:
 * {@code 2026-01-05T13:45:00-03:00} e {@code 2026-05-22T10:57:23Z}.
 * </p>
 */
@Slf4j
@Component
public final class OffsetDateTimeCustomDeserializer extends JsonDeserializer<OffsetDateTime> {
    private static final String PATTERN = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}([+-]\\d{2}:\\d{2}|Z)";
    private static final String FORMAT = "yyyy-MM-dd'T'HH:mm:ssXXX";
    private static final String VALID_EXAMPLE = "2026-01-05T13:45:00-03:00 ou 2026-05-22T10:57:23Z";

    private static final JsonDeserializer<OffsetDateTime> INSTANCE =     
            new GenericCustomDeserializer<>(
                    PATTERN,
                    FORMAT,
                    VALID_EXAMPLE,
                    value -> parse(value, ISO_OFFSET_DATE_TIME),
                    OffsetDateTime.class
                );

    /**
     * Desserializa o valor textual recebido no JSON para {@link OffsetDateTime}.
     *
     * @param p parser do Jackson posicionado no valor de data/hora
     * @param ctxt contexto de desserialização do Jackson
     * @return instância de {@link OffsetDateTime} validada e convertida
     * @throws IOException quando houver falha de leitura ou valor inválido
     */
    @Override
    public OffsetDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return INSTANCE.deserialize(p, ctxt);
    }
    
    /**
     * Retorna o desserializador responsável pela implementação efetiva.
     *
     * @return instância interna de {@link GenericCustomDeserializer}
     */
    @Override
    public JsonDeserializer<?> getDelegatee() {
        return INSTANCE;
    }

}