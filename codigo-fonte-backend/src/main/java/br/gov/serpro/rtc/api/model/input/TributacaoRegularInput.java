/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.api.model.input;

import br.gov.serpro.rtc.api.model.SerializationVisibility;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entrada que representa a tributação regular de IBS e CBS aplicável ao item da
 * operação. Informa a situação tributária e a classificação tributária usadas
 * para determinar as regras e alíquotas do regime geral.
 */
@ToString
@Getter
@Setter
@NoArgsConstructor
public final class TributacaoRegularInput implements SerializationVisibility {

    @NotNull
    @Pattern(regexp = "\\d+", message = "Informar somente dígitos")
    @Size(min = 3, max = 3)
    @Schema(name = "cst", description = "Código da situação tributária", example = "000")
    private String cst;

    @NotNull
    @Pattern(regexp = "\\d+", message = "Informar somente dígitos")
    @Size(min = 6, max = 6)
    @Schema(name = "cClassTrib", description = "Código da classificação tributária", example = "000000")
    private String cClassTrib;

}