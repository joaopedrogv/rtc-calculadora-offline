package br.gov.serpro.rtc.api.model.roc;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import br.gov.serpro.rtc.api.model.SerializationVisibility;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Representa os dados da operação de consumo que influenciam o ROC, atualmente
 * com o grupo de compra governamental.
 */
@Getter
@Setter
@Builder
@JsonInclude(NON_NULL)
@JsonPropertyOrder({ "gCompraGov" })
public class OperacaoConsumoDomain implements SerializationVisibility {

    private CompraGovernamentalDomain gCompraGov;
}