package br.gov.serpro.rtc.config.serializer;

import br.gov.serpro.rtc.core.util.ArredondamentoUtils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Serializador para o tipo TDec_0302_04 (até 3 inteiros, de 2 a 4 decimais).
 * Exemplo válido: 0.01, 0.1234, 123.12, 999.9999
 *
 * Thread-safe: delega para {@link ArredondamentoUtils#formatarAliquota(BigDecimal)}.
 * DecimalFormat não é thread-safe, mas o método formatarAliquota é sincronizado, garantindo segurança em ambientes multi-thread.
 */
public class BigDecimalTDec0302_04Serializer extends JsonSerializer<BigDecimal> {

    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else if (value.signum() < 0) {
            throw new IllegalArgumentException("Números negativos não são permitidos para TDec_0302_04");
        } else {
            gen.writeString(ArredondamentoUtils.formatarAliquota(value));
        }
    }
}