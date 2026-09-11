/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.api.model.input.basecalculo.enumeration;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Catálogo de tipos de ajuste à base de cálculo para NFS-e.
 * Cada constante representa um código com seus flags de repercussão
 * (em qual base o ajuste é deduzido) e metadados de validação.
 */
public enum TipoAjusteNfse {

    SUBCONTRATACAO("101",       true,  false, false),
    CONSORCIO("102",            true,  false, false),
    INTERMEDIACAO("103",        true,  false, false),
    CESSAO_MAO_OBRA("104",      true,  false, false),
    RESSARCIMENTO("105",        true,  false, false),
    OUTROS("199",               true,  false, true),
    PROFISSIONAL_PARCEIRO("9",  false, true,  false);

    private final String codigo;
    private final boolean repercuteIbsCbs;
    private final boolean repercuteSN;
    private final boolean requerDescricao;

    private static final Map<String, TipoAjusteNfse> POR_CODIGO =
        Stream.of(values()).collect(Collectors.toMap(TipoAjusteNfse::getCodigo, Function.identity()));

    private static final Set<String> CODIGOS_VALIDOS = POR_CODIGO.keySet();

    TipoAjusteNfse(String codigo, boolean repercuteIbsCbs, boolean repercuteSN, boolean requerDescricao) {
        this.codigo = codigo;
        this.repercuteIbsCbs = repercuteIbsCbs;
        this.repercuteSN = repercuteSN;
        this.requerDescricao = requerDescricao;
    }

    public String getCodigo() {
        return codigo;
    }

    /** Indica se este tipo subtrai da base IBS/CBS (regime geral). */
    public boolean repercuteIbsCbs() {
        return repercuteIbsCbs;
    }

    /** Indica se este tipo subtrai condicionalmente da base do Simples Nacional. */
    public boolean repercuteSN() {
        return repercuteSN;
    }

    /** Indica se o campo xTpAjusteBC é obrigatório para este tipo. */
    public boolean requerDescricao() {
        return requerDescricao;
    }

    // --- Métodos estáticos de lookup ---

    public static Optional<TipoAjusteNfse> porCodigo(String codigo) {
        return Optional.ofNullable(POR_CODIGO.get(codigo));
    }

    public static boolean isConhecido(String codigo) {
        return CODIGOS_VALIDOS.contains(codigo);
    }

    /** Verifica se o código repercute na base IBS/CBS. */
    public static boolean repercuteIbsCbs(String codigo) {
        return porCodigo(codigo).map(TipoAjusteNfse::repercuteIbsCbs).orElse(false);
    }

    /** Verifica se o código repercute na base do SN. */
    public static boolean repercuteSN(String codigo) {
        return porCodigo(codigo).map(TipoAjusteNfse::repercuteSN).orElse(false);
    }

    /** Verifica se o código exige o campo xTpAjusteBC. */
    public static boolean requerDescricao(String codigo) {
        return porCodigo(codigo).map(TipoAjusteNfse::requerDescricao).orElse(false);
    }
}
