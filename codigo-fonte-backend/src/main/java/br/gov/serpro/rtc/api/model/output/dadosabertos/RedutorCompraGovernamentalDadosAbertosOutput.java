package br.gov.serpro.rtc.api.model.output.dadosabertos;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import br.gov.serpro.rtc.api.model.SerializationVisibility;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Saída de dados abertos com o redutor aplicável à compra governamental e sua
 * vigência.
 */
@Getter
@Setter
@Builder
@JsonInclude(Include.NON_NULL)
public class RedutorCompraGovernamentalDadosAbertosOutput implements SerializationVisibility {

    @Schema(description = "Valor do redutor", example = "0.6")
    private BigDecimal valor;

    @Schema(description = "Data de início da vigência", example = "2026-01-01")
    private LocalDate inicioVigencia;

    @Schema(description = "Data de fim da vigência", example = "2026-12-31")
    private LocalDate fimVigencia;

}
