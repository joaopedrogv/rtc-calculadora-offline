/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.api.model.output;

import java.math.BigDecimal;

import br.gov.serpro.rtc.api.model.roc.DiferimentoDomain;
import br.gov.serpro.rtc.api.model.roc.ReducaoAliquotaDomain;
import br.gov.serpro.rtc.api.model.roc.TributacaoCompraGovernamentalDomain;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Saída com o resultado do cálculo de CBS e IBS.
 *
 * Retorna valores apurados, tributação regular, reduções, diferimento,
 * monofasia e memória de cálculo quando aplicáveis.
 */
@ToString
@Getter
@Setter
@Builder
public final class CbsIbsOutput {

    private BigDecimal aliquota;
    private BigDecimal baseCalculo;
    private BigDecimal quantidade;
    private BigDecimal tributoCalculado;
    private BigDecimal tributoDevido;
    private ReducaoAliquotaDomain grupoReducao;
    private TributacaoRegularOutput tributacaoRegular;
    private TributacaoCompraGovernamentalDomain compraGovernamental;
    private DiferimentoDomain grupoDiferimento;
    private GrupoMonofasiaOutput grupoMonofasia;
    private String memoriaCalculo;
    
    public boolean possuiCompraGov() {
        return compraGovernamental != null;
    }

}