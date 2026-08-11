/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.api.model.output.nfse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

import br.gov.serpro.rtc.api.model.SerializationVisibility;

/**
 * Saída com o valor calculado da base de CBS e IBS para operações de NFS-e.
 */
@Value
@Builder
public class NfseBaseCalculoOutput implements SerializationVisibility {

    @Schema(name = "baseCalculo", description = "Base de cálculo da operação", example = "100.55")
    private final BigDecimal baseCalculo;

}
