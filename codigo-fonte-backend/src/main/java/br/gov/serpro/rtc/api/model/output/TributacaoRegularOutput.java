/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.api.model.output;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Saída da tributação regular, com CST, classificação tributária, base de
 * cálculo, alíquota efetiva e tributo devido.
 */
@ToString
@Getter
@Setter
@Builder
public class TributacaoRegularOutput {

    private String cst;
    private String cClassTrib;
    private BigDecimal baseCalculo;
    private BigDecimal aliquotaEfetiva;
    private BigDecimal tributoDevido;
 
}
