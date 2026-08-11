package br.gov.serpro.rtc.config.serializer;

import br.gov.serpro.rtc.core.util.ArredondamentoUtils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Serializador para o tipo TDec_1104OpRTC (quantidade na RTC).
 * <p>
 * Padrão: 0\.[1-9]{1}[0-9]{3}|0\.[0-9]{3}[1-9]{1}|0\.[0-9]{2}[1-9]{1}[0-9]{1}|0\.[0-9]{1}[1-9]{1}[0-9]{2}|[1-9]{1}[0-9]{0,10}(\.[0-9]{4})?
 * <p>
 * Regras:
 * <ul>
 *   <li>Números inteiros: 1 a 11 dígitos, sem zeros à esquerda (ex: 1, 1000, 99999999999)</li>
 *   <li>Números decimais: exatamente 4 casas decimais (ex: 55.5555, 55.5500, 0.0001)</li>
 * </ul>
 *
 * Thread-safe: delega para {@link ArredondamentoUtils#formatarQuantidade(BigDecimal)}.
 * DecimalFormat não é thread-safe, mas o método formatarQuantidade é sincronizado, garantindo segurança em ambientes multi-thread.
 */
public class BigDecimalTDec1104OpRTCSerializer extends JsonSerializer<BigDecimal> {

    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else if (value.signum() < 0) {
            throw new IllegalArgumentException("Números negativos não são permitidos para TDec_1104OpRTC");
        } else {
            gen.writeString(ArredondamentoUtils.formatarQuantidade(value));
        }
    }
}
