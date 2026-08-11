/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.api.model.input;

import static java.math.BigDecimal.ZERO;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.gov.serpro.rtc.api.model.SerializationVisibility;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entrada que reúne as informações necessárias para cálculo e validação do
 * Imposto Seletivo de um item. Transporta CST, classificação tributária, base
 * de cálculo, quantidade, unidade de medida e valor informado para o imposto
 * seletivo.
 */
@ToString
@Getter
@Setter
@NoArgsConstructor
public final class ImpostoSeletivoInput implements SerializationVisibility {

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

    @NotNull
    @PositiveOrZero
    @Digits(integer = 13, fraction = 2)
    @Schema(name = "baseCalculo", description = "Base de cálculo do imposto", example = "200.00")
    private BigDecimal baseCalculo;

    @PositiveOrZero
    @Digits(integer = 11, fraction = 4)
    @Schema(name = "quantidade", description = "Quantidade", example = "1")
    private BigDecimal quantidade;

    @Schema(name = "unidade", description = "Unidade de medida", example = "LT")
    private String unidade;

    @NotNull
    @PositiveOrZero
    @Digits(integer = 13, fraction = 2)
    @Schema(name = "impostoInformado", description = "Imposto Seletivo informado pelo contribuinte", example = "12.00")
    private BigDecimal impostoInformado;

    @JsonIgnore
    public BigDecimal getValorImpostoSeletivoInformado() {
        return impostoInformado != null ? impostoInformado : ZERO;
    }

}