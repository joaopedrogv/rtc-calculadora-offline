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
 * Saída de dados abertos que representa um ator individual dentro de um grupo
 * de atores, com identificador, descrição e ordem de exibição.
 */
@Getter
@Setter
@Builder
@JsonInclude(Include.NON_NULL)
public class AtorDadosAbertosOutput implements SerializationVisibility {

    @Schema(name = "id", description = "Identificador do ator", example = "22")
    private Long id;

    @Schema(name = "descricao", description = "Descrição do ator", example = "Contribuinte sujeito ao regime regular do IBS e da CBS")
    private String descricao;

    @Schema(name = "ordem", description = "Ordem de exibição do ator dentro do grupo", example = "1")
    private Integer ordem;
}
