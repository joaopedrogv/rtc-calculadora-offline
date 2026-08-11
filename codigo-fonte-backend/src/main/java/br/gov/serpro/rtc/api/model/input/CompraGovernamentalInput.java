package br.gov.serpro.rtc.api.model.input;

import br.gov.serpro.rtc.api.model.SerializationVisibility;
import br.gov.serpro.rtc.api.model.input.calculadora.enumeration.TipoEnteGovernamental;
import br.gov.serpro.rtc.api.model.input.calculadora.enumeration.TipoOperacaoGovernamental;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entrada que encapsula os dados de compra governamental usados nas regras
 * tributárias da operação. Informa o tipo de ente público envolvido e o tipo de
 * operação governamental para permitir a aplicação dos redutores e tratamentos
 * específicos do poder público.
 */
@Getter
@Setter
@NoArgsConstructor
public class CompraGovernamentalInput implements SerializationVisibility {

    @NotNull
    public TipoEnteGovernamental tpEnteGov;
    
    @NotNull
    public TipoOperacaoGovernamental tpOperGov;
}