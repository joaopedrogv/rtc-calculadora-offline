package br.gov.serpro.rtc.api.model.input.calculadora.enumeration;

import java.util.Objects;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonValue;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;

/**
 * Enumeração dos tipos de operação com ente governamental aceitos na API.
 */
@RequiredArgsConstructor
@Schema(type = "integer", 
        description = "Tipo de operação com o ente governamental: 1 - Fornecimento, 2 - Recebimento do pagamento", 
        allowableValues = {"1", "2"})
public enum TipoOperacaoGovernamental {
    
    @Schema(description = "1 - Fornecimento")
    FORNECIMENTO(1, "Fornecimento"),
    
    @Schema(description = "2 - Recebimento do pagamento")
    RECEBIMENTO_PAGAMENTO(2, "Recebimento do pagamento");
    
    private final Integer codigo;
    private final String descricao;
    
    @JsonValue
    public Integer getCodigo() {
        return codigo;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public static TipoOperacaoGovernamental fromCodigo(Integer codigo) {
        return Stream.of(values()).filter(t -> Objects.equals(t.getCodigo(), codigo)).findFirst()
                .orElseThrow(() -> new RuntimeException("Tipo de operação com ente governamental não mapeado"));
    }
    
}
