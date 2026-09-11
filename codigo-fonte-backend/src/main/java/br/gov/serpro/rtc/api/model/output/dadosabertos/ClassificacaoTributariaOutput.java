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
 * Saída de dados abertos com código e descrição da classificação tributária.
 */
@Getter
@Setter
@Builder
@JsonInclude(Include.NON_NULL)
public class ClassificacaoTributariaOutput implements SerializationVisibility {

    @Schema(name = "codigo", description = "Código da classificação tributária", example = "000001")
    private String codigo;

    @Schema(name = "descricao", description = "Descrição da classificação tributária", example = "ICMS - Operação Interna")
    private String descricao;

}
