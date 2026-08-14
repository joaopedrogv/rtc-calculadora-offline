/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.api.model.output.dadosabertos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import br.gov.serpro.rtc.api.model.SerializationVisibility;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Saída da API para validação de NCM aplicável a uma classificação tributária.
 */
@Getter
@Setter
@Builder
@JsonInclude(Include.NON_NULL)
public class NcmAplicavelOutput implements SerializationVisibility {

    @Schema(name = "cClassTrib", description = "Código da Classificação Tributária", example = "000001")
    private String cClassTrib;

    @Schema(name = "ncm", description = "Código NCM (Nomenclatura Comum do Mercosul)", example = "24021000")
    private String ncm;

    @Schema(name = "dataOcorrenciaFatoGerador", description = "Data de ocorrência do fato gerador", example = "2026-01-01")
    private String dataOcorrenciaFatoGerador;

    @Schema(name = "valido", description = "Indicador se o NCM é válido para a classificação tributária informada")
    private boolean valido;

}