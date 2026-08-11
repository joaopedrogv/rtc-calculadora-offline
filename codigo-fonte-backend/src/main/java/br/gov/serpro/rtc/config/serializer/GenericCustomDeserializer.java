package br.gov.serpro.rtc.config.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.function.Function;

/**
 * Implementa um deserializador Jackson genérico com validação de formato e
 * mensagem padronizada de erro.
 */
@Slf4j
public class GenericCustomDeserializer<T> extends JsonDeserializer<T> {

    private final String pattern;
    private final String format;
    private final String example;
    private final Function<String, T> transformFunction;
    private final Class<T> type;

    public GenericCustomDeserializer(String pattern, String format, String example,
            Function<String, T> transformFunction, Class<T> type) {
        this.pattern = pattern;
        this.format = format;
        this.example = example;
        this.transformFunction = transformFunction;
        this.type = type;
    }

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        final var value = p.getText();

        if (!value.matches(pattern)) {
            throw new InvalidFormatException(p, getFormattedMessage(p), value, type);
        }
        try {
            return transformFunction.apply(value);
        } catch (Exception e) {
            log.error("Erro de deserializacao", e);
            throw new InvalidFormatException(p, getFormattedMessage(p), value, type);
        }
    }

    private String getFormattedMessage(JsonParser p) throws IOException {
        return String.format("Campo '%s' [%s] inválido. Formato esperado: '%s'. Exemplo de valor: '%s'",
                p.currentName(), p.getText(), format, example);
    }
}