package br.gov.serpro.rtc.config.serializer;

import static java.time.LocalDate.parse;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

import java.io.IOException;
import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import lombok.extern.slf4j.Slf4j;

/**
 * Deserializa valores de {@link LocalDate} no formato ISO yyyy-MM-dd com
 * validação prévia.
 */
@Slf4j
@Component
public final class LocalDateCustomDeserializer extends JsonDeserializer<LocalDate> {
    private static final String DATE_PATTERN = "\\d{4}-\\d{2}-\\d{2}";
    private static final String FORMAT = "yyyy-MM-dd";
    private static final String VALID_EXAMPLE = "2026-01-05";
    
    private static final JsonDeserializer<LocalDate> INSTANCE =     
            new GenericCustomDeserializer<>(
                    DATE_PATTERN,
                    FORMAT,
                    VALID_EXAMPLE,
                    value -> parse(value, ISO_LOCAL_DATE),
                    LocalDate.class
                );
    
    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return INSTANCE.deserialize(p, ctxt);
    }
    
    @Override
    public JsonDeserializer<?> getDelegatee() {
        return INSTANCE;
    }

}