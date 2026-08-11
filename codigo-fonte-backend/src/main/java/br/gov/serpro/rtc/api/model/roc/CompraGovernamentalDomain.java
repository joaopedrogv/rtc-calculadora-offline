package br.gov.serpro.rtc.api.model.roc;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import br.gov.serpro.rtc.api.model.SerializationVisibility;
import br.gov.serpro.rtc.api.model.input.calculadora.enumeration.TipoEnteGovernamental;
import br.gov.serpro.rtc.api.model.input.calculadora.enumeration.TipoOperacaoGovernamental;
import br.gov.serpro.rtc.config.serializer.BigDecimalTDec0302_04Serializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * Descreve os parâmetros de compra governamental da operação, como ente
 * público, redutor de alíquota e tipo de operação.
 */
@Getter
@Setter
@JsonInclude(NON_NULL)
@JsonPropertyOrder({ "tpEnteGov", "pRedutor", "tpOperGov" })
public class CompraGovernamentalDomain implements SerializationVisibility {

    private TipoEnteGovernamental tpEnteGov;
    
    @JsonSerialize(using = BigDecimalTDec0302_04Serializer.class)
    @Schema(description = "Percentual de redução de alíquota em compra governamental")
    private BigDecimal pRedutor = BigDecimal.ZERO;
    
    private TipoOperacaoGovernamental tpOperGov;
}