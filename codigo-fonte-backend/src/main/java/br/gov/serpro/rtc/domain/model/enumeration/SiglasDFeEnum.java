package br.gov.serpro.rtc.domain.model.enumeration;

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
    BPE("BPe"),
    BPETA("BPe TA"),
    BPETM("BPe TM"),
    NF3E("NF3e"),
    NFSE("NFSe"),
    NFSEVIA("NFSe Via"),
    NFCOM("NFCom"),
    DERE("DERE"),
    NFAG("NFAg"),
    NFGAS("NFGas");

    private final String sigla;

    public static String normalizarSigla(String sigla) {
        if (sigla == null) return "";
        return sigla.toUpperCase().replaceAll("[\\s\\-_]", "");
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
}