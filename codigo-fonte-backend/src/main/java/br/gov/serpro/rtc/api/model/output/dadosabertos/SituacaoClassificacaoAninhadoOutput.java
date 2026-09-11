/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.api.model.output.dadosabertos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import br.gov.serpro.rtc.api.model.SerializationVisibility;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Saída de dados abertos com código, descrição da situação tributária (CST)
 * e lista aninhada de classificações tributárias aplicáveis.
 */
@Getter
@Setter
@Builder
@JsonInclude(Include.NON_NULL)
public class SituacaoClassificacaoAninhadoOutput implements SerializationVisibility {

    @Schema(name = "id", description = "Identificador único da situação tributária", example = "1")
    private Long id;

    @Schema(name = "codigo", description = "Código da situação tributária (CST)", example = "000")
    private String codigo;

    @Schema(name = "descricao", description = "Descrição da situação tributária (CST)", example = "Tributação Integral")
    private String descricao;

    @Schema(name = "classificacoesTributarias", description = "Lista de classificações tributárias aplicáveis")
    private List<ClassificacaoTributariaOutput> classificacoesTributarias;

}
