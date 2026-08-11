package br.gov.serpro.rtc.api.model.roc;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import br.gov.serpro.rtc.api.exceptionhandler.dto.ErroDetalhe;
import br.gov.serpro.rtc.api.model.SerializationVisibility;
import br.gov.serpro.rtc.domain.model.enumeration.EstadoItemEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Representa o item de observabilidade do ROC, com número do objeto, estado do
 * processamento, tributos calculados e erros detalhados.
 */
@Getter
@Setter
@Builder
@JsonInclude(NON_NULL)
@JsonPropertyOrder({ "nObj", "estadoItem", "tribCalc", "erros" })
public final class ObservabilidadeItemDomain implements SerializationVisibility {

    private Integer nObj;
    private EstadoItemEnum estadoItem;
    private TributosDomain tribCalc;
    private List<ErroDetalhe> erros;
}
