package br.gov.serpro.rtc.domain.model.enumeration;

import java.util.Set;
import java.util.stream.Stream;

import br.gov.serpro.rtc.domain.service.exception.SiglaDFeNaoEncontradaException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enumera as siglas de documentos fiscais eletrônicos reconhecidas pelo
 * domínio.
 *
 * Valores como {@code NFE}, {@code NFCE}, {@code CTE}, {@code NFSE}, {@code
 * NFCOM}, {@code DERE}, {@code NFAG} e {@code NFGAS} são usados para
 * normalização e validação de entrada.
 */
@Getter
@RequiredArgsConstructor
public enum SiglasDFeEnum {
    NFEABI("NFe ABI"),
    NFE("NFe"),
    NFCE("NFCe"),
    CTE("CTe"),
    CTEOS("CTe OS"),
    CTESIMPLIFICADO("CT-e Simplificado"),
    BPE("BPe"),
    BPETA("BPe TA"),
    BPETM("BPe TM"),
    NF3E("NF3e"),
    NFSE("NFSe"),
    NFSEVIA("NFSe Via"),
    NFCOM("NFCom"),
    DERE("DERE"),
    DIR("DIR"),
    DUIMP("Duimp"),
    NFAG("NFAg"),
    NFGAS("NFGas");

    private final String sigla;

    private static final Set<String> NCM_APLICAVEL = Set.of("NFSE", "NFE", "NFCE");
    private static final Set<String> NBS_APLICAVEL = Set.of("NFSE");
    private static final Set<String> IMPOSTO_SELETIVO_APLICAVEL = Set.of("NFE", "NFCE");
    private static final Set<SiglasDFeEnum> SEM_TRATAMENTO_CALCULADORA = Set.of(NFEABI, DERE);

    public static String normalizarSigla(String sigla) {
        if (sigla == null) return "";
        return sigla.toUpperCase().replaceAll("[\\s\\-_]", "");
    }

    /**
     * Chave canônica normalizada deste item — equivale a {@code name()} e à
     * forma normalizada de {@link #getSigla()}. Deve ser usada em toda
     * comparação com siglas persistidas no banco, cujo formato textual
     * (ex.: {@code "NF-e"}) pode mudar sem quebrar a correspondência.
     */
    public String getChaveNormalizada() {
        return name();
    }

    /**
     * Indica se a sigla informada — em qualquer formato ({@code "NF-e"},
     * {@code "NFe"}, {@code "nf e"}) — corresponde a este item, comparando as
     * formas normalizadas (ignora caixa, espaços, hífens e underscores).
     */
    public boolean corresponde(String sigla) {
        return name().equals(normalizarSigla(sigla));
    }

    /**
     * Indica se este tipo de documento ainda não possui tratamento nos
     * endpoints de cálculo.
     */
    public boolean semTratamentoCalculadora() {
        return SEM_TRATAMENTO_CALCULADORA.contains(this);
    }

    public static boolean contemSiglaNormalizada(String sigla) {
        String normalizada = normalizarSigla(sigla);
        return Stream.of(values())
            .anyMatch(e -> e.name().equals(normalizada));
    }
    
    public static SiglasDFeEnum getPorSiglaNormalizada(String sigla) {
        String normalizada = normalizarSigla(sigla);
        return Stream.of(values())
            .filter(e -> e.name().equals(normalizada))
            .findFirst()
            .orElseThrow(() -> new SiglaDFeNaoEncontradaException(sigla));
    }
    
    public static boolean ncmAplicavel(String siglaDfe) {
        return NCM_APLICAVEL.contains(normalizarSigla(siglaDfe));
    }

    public static boolean nbsAplicavel(String siglaDfe) {
        return NBS_APLICAVEL.contains(normalizarSigla(siglaDfe));
    }

    public static boolean impostoSeletivoAplicavel(String siglaDfe) {
        return IMPOSTO_SELETIVO_APLICAVEL.contains(normalizarSigla(siglaDfe));
    }
}
