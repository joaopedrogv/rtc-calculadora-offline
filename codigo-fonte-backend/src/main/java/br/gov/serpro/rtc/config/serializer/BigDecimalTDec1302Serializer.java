package br.gov.serpro.rtc.config.serializer;

import br.gov.serpro.rtc.core.util.ArredondamentoUtils;

import java.io.IOException;
import java.math.BigDecimal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Serializador para o tipo TDec1302 (até 13 inteiros e 2 decimais, sempre com 2 casas decimais).
 * Regex: 0|0\.[0-9]{2}|[1-9]{1}[0-9]{0,12}(\.[0-9]{2})?
 * Exemplos válidos: 0, 0.01, 1234567890123.45
 *
 * Thread-safe: delega para {@link ArredondamentoUtils#formatarMoeda(BigDecimal)}.
 * DecimalFormat não é thread-safe, mas o método formatarMoeda é sincronizado, garantindo segurança em ambientes multi-thread.
 */
public class BigDecimalTDec1302Serializer extends JsonSerializer<BigDecimal> {

    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else if (value.signum() < 0) {
            throw new IllegalArgumentException("Números negativos não são permitidos para TDec1302");
        } else {
            gen.writeString(ArredondamentoUtils.formatarMoeda(value));
        }
    }
}