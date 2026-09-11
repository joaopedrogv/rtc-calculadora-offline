package br.gov.serpro.rtc.api.model.input.basecalculo;

import java.math.BigDecimal;

import br.gov.serpro.rtc.api.model.SerializationVisibility;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DocumentoAjusteNfseInput implements SerializationVisibility {

    @NotBlank
    @Schema(description = "Tipo de ajuste à base de cálculo (ex: 101, 102, 103, 104, 105, 199, 9)", example = "101")
    private String tpAjusteBC;

    @NotNull
    @PositiveOrZero
    @Schema(description = "Valor do ajuste aplicado", example = "50.00")
    private BigDecimal vAjusteAplic;

    @Schema(description = "Descrição do tipo de ajuste (obrigatório para tipo 199)", nullable = true, example = "Ajuste personalizado")
    private String xTpAjusteBC;
}
