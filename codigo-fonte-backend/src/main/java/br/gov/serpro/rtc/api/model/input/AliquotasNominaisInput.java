package br.gov.serpro.rtc.api.model.input;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AliquotasNominaisInput {

    @PositiveOrZero
    @Digits(integer = 3, fraction = 4)
    @DecimalMax(value = "100.00", inclusive = true)
    @Schema(name = "cbs", description = "Alíquota nominal CBS (%)", example = "8.8")
    private BigDecimal cbs;

    @PositiveOrZero
    @Digits(integer = 3, fraction = 4)
    @DecimalMax(value = "100.00", inclusive = true)
    @Schema(name = "ibsEstadual", description = "Alíquota nominal IBS estadual (%)", example = "8.85")
    private BigDecimal ibsEstadual;

    @PositiveOrZero
    @Digits(integer = 3, fraction = 4)
    @DecimalMax(value = "100.00", inclusive = true)
    @Schema(name = "ibsMunicipal", description = "Alíquota nominal IBS municipal (%)", example = "8.85")
    private BigDecimal ibsMunicipal;

    @Valid
    @Schema(name = "impostoSeletivo", description = "Alíquotas nominais do Imposto Seletivo")
    private AliquotasNominaisImpostoSeletivo impostoSeletivo;
}