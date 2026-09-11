package br.gov.serpro.rtc.api.model.input.basecalculo;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import br.gov.serpro.rtc.api.model.SerializationVisibility;
import br.gov.serpro.rtc.api.model.input.basecalculo.enumeration.BaseCalculoNatureza;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Input DTO para cálculo unificado das 3 bases de cálculo (RG/IBS-CBS, IS, SN).
 * Compõe {@link ComponentesBaseCalculoInput} para os valores dos componentes,
 * mantendo compatibilidade JSON flat via {@code @JsonUnwrapped}.
 */
@Getter
@Setter
@NoArgsConstructor
public final class BaseCalculoUnificadaInput implements SerializationVisibility {

    @NotNull
    @Schema(name = "anoFatoGerador", description = "Ano do Fato Gerador", example = "2026")
    private Integer anoFatoGerador;

    @NotNull
    @Schema(name = "tipoDocumento", description = "Tipo de documento fiscal", example = "55")
    private Integer tipoDocumento;

    @NotNull
    @Schema(name = "natureza", description = "Natureza da operação (bem ou serviço)", example = "bem")
    private BaseCalculoNatureza natureza;

    @Valid
    @JsonUnwrapped
    private ComponentesBaseCalculoInput componentes = new ComponentesBaseCalculoInput();

    /**
     * Acesso direto aos componentes para uso nos serviços de cálculo.
     */
    public ComponentesBaseCalculoInput getComponentes() {
        return componentes;
    }
}
