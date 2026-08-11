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
 * Representa a visão de observabilidade do ROC.
 *
 * Reúne os dados da operação, os itens com estado de processamento e erros
 * detalhados, além dos totais calculados para diagnóstico.
 */
@Getter
@Setter
@Builder
@JsonInclude(NON_NULL)
@JsonPropertyOrder({ "oper", "objetos", "total" })
public final class ObservabilidadeROCDomain implements SerializationVisibility {

	private OperacaoConsumoDomain oper;
    private List<ObservabilidadeItemDomain> objetos;
    private ValoresTotaisDomain total;
}
