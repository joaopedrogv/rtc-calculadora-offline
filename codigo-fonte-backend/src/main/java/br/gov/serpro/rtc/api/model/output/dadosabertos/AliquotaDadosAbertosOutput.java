/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.api.model.output.dadosabertos;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import br.gov.serpro.rtc.api.model.SerializationVisibility;
import br.gov.serpro.rtc.domain.model.enumeration.FormaAplicacaoEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * Saída de dados abertos com informações de alíquota de referência, alíquota
 * própria e forma de aplicação.
 */
@Getter
@Builder
@JsonInclude(Include.NON_NULL)
public class AliquotaDadosAbertosOutput implements SerializationVisibility {

    @Schema(name = "aliquotaReferencia", description = "Alíquota de referência", example = "0")
    private BigDecimal aliquotaReferencia;

    @Schema(name = "aliquotaPropria", description = "Alíquota própria", example = "0")
    private BigDecimal aliquotaPropria;

    @Schema(name = "formaAplicacao", description = "Forma de aplicação da alíquota própria em relação à alíquota de referência", example = "ACRESCIMO")
    private FormaAplicacaoEnum formaAplicacao;

}
