package br.gov.serpro.rtc.api.model.roc;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import br.gov.serpro.rtc.api.model.SerializationVisibility;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Representa o resultado do cálculo.
 *
 * Reúne os dados da operação, os objetos calculados e os totais consolidados
 * retornados pelo processamento.
 */
@Getter
@Setter
@Builder
@JsonInclude(NON_NULL)
@JsonPropertyOrder({ "oper", "objetos", "total" })
public final class ROCDomain implements SerializationVisibility {
    
    private OperacaoConsumoDomain oper;
    private List<ObjetoDomain> objetos;
    private ValoresTotaisDomain total;

}
