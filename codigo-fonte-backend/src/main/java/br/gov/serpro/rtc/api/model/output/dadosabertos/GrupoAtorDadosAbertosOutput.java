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
 * Saída de dados abertos que representa um grupo de atores com os atores
 * aninhados, usada para montar a seleção agrupada de atores.
 */
@Getter
@Setter
@Builder
@JsonInclude(Include.NON_NULL)
public class GrupoAtorDadosAbertosOutput implements SerializationVisibility {

    @Schema(name = "id", description = "Identificador do grupo de ator", example = "1")
    private Long id;

    @Schema(name = "descricao", description = "Descrição do grupo de ator", example = "Regime regular / contribuinte padrão (arts. 21 ss.)")
    private String descricao;

    @Schema(name = "ordem", description = "Ordem de exibição do grupo", example = "1")
    private Integer ordem;

    @Schema(name = "atores", description = "Lista de atores pertencentes ao grupo")
    private List<AtorDadosAbertosOutput> atores;
}
