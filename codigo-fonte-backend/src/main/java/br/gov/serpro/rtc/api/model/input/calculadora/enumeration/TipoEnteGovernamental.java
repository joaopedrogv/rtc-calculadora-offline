package br.gov.serpro.rtc.api.model.input.calculadora.enumeration;

import java.util.Objects;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonValue;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;

/**
 * Enumeração dos tipos de ente governamentais aceitos nas operações de compra
 * governamental.
 */
@RequiredArgsConstructor
@Schema(type = "integer", 
        description = "Tipo de ente governamental: 1 - União, 2 - Estados, 3 - Distrito Federal, 4 - Municípios", 
        allowableValues = {"1", "2", "3", "4"})
public enum TipoEnteGovernamental {

    @Schema(description = "1 - União")
    UNIAO(1, "União"),
    
    @Schema(description = "2 - Estados")
    ESTADOS(2, "Estados"),
    
    @Schema(description = "3 - Distrito Federal")
    DISTRITO_FEDERAL(3, "Distrito Federal"),
    
    @Schema(description = "4 - Municípios")
    MUNICIPIOS(4, "Municípios");

    private final Integer codigo;
    private final String descricao;
    
    @JsonValue
    public Integer getCodigo() {
        return codigo;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public static TipoEnteGovernamental fromCodigo(Integer codigo) {
        return Stream.of(values()).filter(t -> Objects.equals(t.getCodigo(), codigo)).findFirst()
                .orElseThrow(() -> new RuntimeException("Tipo de ente governamental não mapeado"));
    }

}
