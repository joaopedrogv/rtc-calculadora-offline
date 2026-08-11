/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.core.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Stream;

/**
 * Utilitário centralizado de arredondamento para a Calculadora de Tributos da RTC.
 * <p>
 * Regras:
 * <p>
 * <ul>
 *   <li>Todos os cálculos intermediários usam precisão de 8 casas decimais ({@link #PRECISAO_INTERNA}).</li>
 *   <li>O modo de arredondamento é HALF_EVEN (arredondamento bancário).</li>
 *   <li>O arredondamento final para apresentação ocorre exclusivamente na camada de serialização JSON,
 *       através dos métodos {@link #formatarMoeda}, {@link #formatarAliquota} e {@link #formatarQuantidade}
 *       (usados internamente pelos serializers Jackson).</li>
 * </ul>
 * <p>
 * Escalas de saída (aplicadas somente na apresentação):
 * <ul>
 *   <li>Valores monetários: 2 casas decimais → {@link #formatarMoeda}</li>
 *   <li>Alíquotas e percentuais: 2 a 4 casas decimais → {@link #formatarAliquota}</li>
 *   <li>Quantidades: 4 casas decimais ou inteiro → {@link #formatarQuantidade}</li>
 * </ul>
 * <p>
 * Ref.: art. 349, § 14, da LC 214/2025.
 *
 * @see br.gov.serpro.rtc.config.serializer.BigDecimalTDec1302Serializer
 * @see br.gov.serpro.rtc.config.serializer.BigDecimalTDec0302_04Serializer
 * @see br.gov.serpro.rtc.config.serializer.BigDecimalTDec1104OpRTCSerializer
 */
public final class ArredondamentoUtils {

    // ─── Constantes ────────────────────────────────────────────────────────────

    /** Precisão usada em TODOS os cálculos intermediários (8 casas decimais). */
    public static final int PRECISAO_INTERNA = 8;

    /** Modo de arredondamento padrão: HALF_EVEN (arredondamento bancário). */
    public static final RoundingMode MODO = RoundingMode.HALF_EVEN;

    /** Constante 100, usada em conversões de percentual para fator decimal. */
    public static final BigDecimal CEM = new BigDecimal("100");

    /** Escala de apresentação para valores monetários (2 casas decimais). */
    private static final int ESCALA_MOEDA = 2;

    /** Escala máxima de apresentação para alíquotas (4 casas decimais). */
    private static final int ESCALA_ALIQUOTA_MAX = 4;

    /** Escala de apresentação para quantidades (4 casas decimais). */
    private static final int ESCALA_QUANTIDADE = 4;

    private ArredondamentoUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    // ─── Arredondamento intermediário ──────────────────────────────────────────

    /**
     * Aplica arredondamento interno (8 casas, HALF_EVEN) a um valor.
     * Usar em todos os resultados intermediários de cálculo.
     */
    public static BigDecimal arredondarInterno(BigDecimal valor) {
        if (valor == null) {
            return null;
        }
        return valor.setScale(PRECISAO_INTERNA, MODO);
    }

    // ─── Operações aritméticas (com arredondamento interno) ────────────────────

    /**
     * Divide o valor por 100 com precisão interna (8 casas, HALF_EVEN).
     * Uso típico: converter percentual (ex: 30) em fator decimal (ex: 0.30000000).
     */
    public static BigDecimal dividirPorCem(BigDecimal valor) {
        if (valor == null) {
            return null;
        }
        return valor.divide(CEM, PRECISAO_INTERNA, MODO);
    }

    /**
     * Divisão com precisão interna (8 casas, HALF_EVEN).
     */
    public static BigDecimal dividir(BigDecimal dividendo, BigDecimal divisor) {
        if (dividendo == null || divisor == null) {
            return null;
        }
        return dividendo.divide(divisor, PRECISAO_INTERNA, MODO);
    }

    /**
     * Multiplicação com precisão interna (8 casas, HALF_EVEN).
     */
    public static BigDecimal multiplicar(BigDecimal v1, BigDecimal v2) {
        if (v1 == null || v2 == null) {
            return null;
        }
        return v1.multiply(v2).setScale(PRECISAO_INTERNA, MODO);
    }

    /**
     * Subtração com precisão interna (8 casas, HALF_EVEN).
     */
    public static BigDecimal subtrair(BigDecimal v1, BigDecimal v2) {
        if (v1 == null || v2 == null) {
            return null;
        }
        return v1.subtract(v2).setScale(PRECISAO_INTERNA, MODO);
    }

    /**
     * Multiplica o valor por 100 (operação exata, sem arredondamento).
     */
    public static BigDecimal multiplicarPorCem(BigDecimal valor) {
        if (valor == null) {
            return null;
        }
        return valor.multiply(CEM);
    }

    /**
     * Totaliza (soma) um stream de BigDecimal, sem arredondamento intermediário.
     * O arredondamento ocorre somente na serialização.
     */
    public static BigDecimal totalizar(Stream<BigDecimal> valores) {
        return valores.reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ─── Formatação para apresentação (equivalentes aos Serializers) ───────────
    //
    // TODAS as conversões BigDecimal → String para saída JSON passam por aqui.
    // Nenhum outro arquivo deve chamar setScale() + toPlainString() diretamente.
    //
    // - formatarMoeda       (2 casas, HALF_EVEN)
    // - formatarAliquota    (2-4 casas, HALF_EVEN)
    // - formatarQuantidade  (4 casas ou inteiro)

    /**
     * Formata um valor monetário: arredondamento HALF_EVEN com exatamente 2 casas decimais.
     * Usar em TODOS os campos monetários (vCBS, vIBS, vIBSUF, vDif, vIS, etc.).
     * <p>
     * Equivalente Jackson: {@code BigDecimalTDec1302Serializer}
     *
     * @param value valor a formatar (não nulo)
     * @return string com exatamente 2 casas decimais (ex: "804.37")
     */
    public static String formatarMoeda(BigDecimal value) {
        return value.setScale(ESCALA_MOEDA, MODO).toPlainString();
    }

    /**
     * Formata uma alíquota/percentual com casas decimais variáveis (mínimo 2, máximo 4).
     * O valor é arredondado com HALF_EVEN para 4 casas, depois os zeros à direita
     * são removidos, respeitando o mínimo de 2 casas.
     * <p>
     * Equivalente Jackson: {@code BigDecimalTDec0302_04Serializer}
     * <p>
     * Exemplos:
     * <ul>
     *   <li>{@code formatarAliquota(0.2080)} → "0.208" (remove zeros, mantém mín. 2)</li>
     *   <li>{@code formatarAliquota(0.2000)} → "0.20" (remove zeros, garante mín. 2)</li>
     *   <li>{@code formatarAliquota(60.0000)} → "60.00" (mín. 2 casas)</li>
     * </ul>
     *
     * @param value valor a formatar (não nulo)
     * @return string com 2 a 4 casas decimais
     */
    public static String formatarAliquota(BigDecimal value) {
        BigDecimal scaled = value.setScale(ESCALA_ALIQUOTA_MAX, MODO);
        BigDecimal stripped = scaled.stripTrailingZeros();
        if (stripped.scale() < ESCALA_MOEDA) {
            stripped = stripped.setScale(ESCALA_MOEDA);
        }
        return stripped.toPlainString();
    }

    /**
     * Formata uma quantidade: inteiro quando possível, senão 4 casas decimais com HALF_EVEN.
     * <p>
     * Equivalente Jackson: {@code BigDecimalTDec1104OpRTCSerializer}
     *
     * @param value valor a formatar (não nulo)
     * @return string inteira (ex: "1000") ou com 4 casas decimais (ex: "55.5500")
     */
    public static String formatarQuantidade(BigDecimal value) {
        if (value.stripTrailingZeros().scale() <= 0) {
            return value.toBigInteger().toString();
        }
        return value.setScale(ESCALA_QUANTIDADE, MODO).toPlainString();
    }
}
