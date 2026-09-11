package br.gov.serpro.rtc.api.model.input;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AliquotasNominaisImpostoSeletivo {

    @PositiveOrZero
    @Digits(integer = 3, fraction = 4)
	@DecimalMax(value = "100.00", inclusive = true)
	@Schema(name = "adValorem", description = "Alíquota ad valorem do Imposto Seletivo (%)", example = "10.0")
	private BigDecimal adValorem;

	@DecimalMin(value = "0.00", inclusive = true)
	@Schema(name = "adRem", description = "Alíquota ad rem do Imposto Seletivo", example = "0.5")
	private BigDecimal adRem;
}
