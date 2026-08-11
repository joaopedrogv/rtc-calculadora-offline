package br.gov.serpro.rtc.api.model.xml.enumeration;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import br.gov.serpro.rtc.api.model.xml.TipoDocumentoDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enumeração que lista os tipos de documentos fiscais XML suportados pela API.
 */
@Getter
@RequiredArgsConstructor
public enum TipoDocumento {
    NFE("nfe", "NFe", "v1.36", true, true),
    NFCE("nfce", "NFCe", "v1.36", true, true),
    NFSE("nfse", "NFSe", "v1.01.03 - NT004", false, true),
    CTE("cte", "CTe", "v1.14a", true, true),
    CTE_SIMPLIFICADO("cte-simplificado", "CTe Simplificado", "v1.14a", true, true),
    BPE("bpe", "BPe", "v1.14a", true, true),
    BPE_TM("bpe-tm", "BPeTM", "v1.14a", true, true),
    NF3E("nf3e", "NF3E", "v1.14a", true, true);
    /*
    GTVE,
    NFCOM,
    NFSE_VIA,
    DERE;
    */    
    
    private final String mnemonico;
    private final String nome;
    private final String versaoNotaTecnica;
    private final boolean geracao;
    private final boolean validacao;

    @JsonValue
    public String getMnemonico() {
        return mnemonico;
    }

    @JsonCreator
    public static TipoDocumento fromMnemonico(String mnemonico) {
        if (mnemonico == null || mnemonico.trim().isEmpty()) {
            throw new IllegalArgumentException("Mnemonico não pode ser nulo ou vazio");
        }
        for (TipoDocumento tipo : TipoDocumento.values()) {
            if (tipo.getMnemonico().equalsIgnoreCase(mnemonico)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de documento inválido: " + mnemonico);
    }
    
    private static final List<TipoDocumentoDTO> TIPOS_DOCUMENTO_GERACAO = toDto(t -> t.geracao);

    public static List<TipoDocumentoDTO> getDocumentosGeracao() {
        return TIPOS_DOCUMENTO_GERACAO;
    }

    private static final List<TipoDocumentoDTO> TIPOS_DOCUMENTO_VALIDACAO = toDto(t -> t.validacao);

    public static List<TipoDocumentoDTO> getDocumentosValidacao() {
        return TIPOS_DOCUMENTO_VALIDACAO;
    }
    
    private static final List<TipoDocumentoDTO> toDto(Predicate<? super TipoDocumento> predicate){
        return Stream.of(values()).filter(predicate)
                .map(t -> new TipoDocumentoDTO(t.nome, t.mnemonico, t.versaoNotaTecnica)).toList();
    }
    
}