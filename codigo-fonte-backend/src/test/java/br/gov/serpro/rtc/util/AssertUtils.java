package br.gov.serpro.rtc.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.math.RoundingMode;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class AssertUtils {

    /**
     * Compara BigDecimals arredondando o valor atual para a escala do esperado (HALF_EVEN).
     * Isso é necessário porque os cálculos internos usam precisão de 8 casas decimais,
     * enquanto os testes verificam os valores arredondados para apresentação.
     */
    public static void isEqualByComparingTo(BigDecimal actual, String expected) {
        assertThat(actual).isNotNull();
        BigDecimal expectedBD = new BigDecimal(expected);
        int scale = expectedBD.scale();
        // Devemos verificar se "scale" é igual à precisão interna?
        BigDecimal actualRounded = scale >= 0 ? actual.setScale(scale, RoundingMode.HALF_EVEN) : actual;
        assertThat(actualRounded).isEqualByComparingTo(expectedBD);
    }

    public static void isEqualByComparingTo(BigDecimal actual, BigDecimal expected) {
        assertThat(actual).isNotNull();
        int scale = expected.scale();
        // Devemos verificar se "scale" é igual à precisão interna?
        BigDecimal actualRounded = scale >= 0 ? actual.setScale(scale, RoundingMode.HALF_EVEN) : actual;
        assertThat(actualRounded).isEqualByComparingTo(expected);
    }

}
