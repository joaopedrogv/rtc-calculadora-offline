package br.gov.serpro.rtc.config.serializer;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Testes para o serializador de quantidade TDec_1104OpRTC.
 * 
 * Validações do padrão:
 * - Inteiros: 1 a 11 dígitos sem zeros à esquerda
 * - Decimais: exatamente 4 casas decimais, pelo menos 1 != 0
 */
class BigDecimalTDec1104OpRTCSerializerTest {

    private final ObjectMapper mapper = new ObjectMapper();
    
    static class Wrapper {
        @JsonSerialize(using = BigDecimalTDec1104OpRTCSerializer.class)
        public BigDecimal value;
        
        public Wrapper(String v) {
            this.value = new BigDecimal(v);
        }
    }

    @ParameterizedTest(name = "{index} => valor={0}, esperado={1}")
    @MethodSource("cenariosQuantidade")
    void deveSerializarQuantidadeConformePadrao(String input, String valorEsperado) throws JsonProcessingException {
        var wrapper = new Wrapper(input);
        var json = mapper.writeValueAsString(wrapper);
        var jsonEsperado = "{\"value\":\"" + valorEsperado + "\"}";

        assertThat(json).isEqualTo(jsonEsperado);
    }

    private static Stream<Arguments> cenariosQuantidade() {
        return Stream.of(
            Arguments.of("1000", "1000"),
            Arguments.of("1", "1"),
            Arguments.of("55.55", "55.5500"),
            Arguments.of("55.5555", "55.5555"),
            Arguments.of("55.550", "55.5500"),
            Arguments.of("55.5500", "55.5500"),
            Arguments.of("0.0001", "0.0001"),
            Arguments.of("0.1", "0.1000"),
            Arguments.of("0", "0")
        );
    }
}
