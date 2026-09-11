package br.gov.serpro.rtc.api.model.input.basecalculo;

import java.math.BigDecimal;

import br.gov.serpro.rtc.api.model.SerializationVisibility;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LocacaoNfseInput implements SerializationVisibility {

    @NotNull
    @PositiveOrZero
    @Schema(description = "Percentual de copropriedade (100 = locador único)", example = "100")
    private BigDecimal pCopropriedade;

    @NotNull
    @PositiveOrZero
    @Schema(description = "Valor total da operação de locação", example = "5000.00")
    private BigDecimal vTotOper;

    @NotNull
    @PositiveOrZero
    @Schema(description = "Valor do desconto incondicional total", example = "500.00")
    private BigDecimal vDescIncondTot;

    @NotNull
    @PositiveOrZero
    @Schema(description = "Soma das parcelas de ajuste de locação de imóveis", example = "100.00")
    private BigDecimal somaAjustesLocImoveis;
}
