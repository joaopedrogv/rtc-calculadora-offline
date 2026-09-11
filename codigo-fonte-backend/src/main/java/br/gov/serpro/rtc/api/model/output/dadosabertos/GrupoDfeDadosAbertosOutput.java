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
 * Saída de dados abertos que representa uma categoria de documento fiscal
 * eletrônico (DFe) com os tipos selecionáveis aninhados, usada para montar a
 * seleção agrupada do tipo de documento.
 */
@Getter
@Setter
@Builder
@JsonInclude(Include.NON_NULL)
public class GrupoDfeDadosAbertosOutput implements SerializationVisibility {

    @Schema(name = "id", description = "Identificador estável do grupo", example = "1")
    private String id;

    @Schema(name = "titulo", description = "Título da categoria de documento", example = "MERCADORIAS")
    private String titulo;

    @Schema(name = "ordem", description = "Ordem de exibição do grupo", example = "1")
    private Integer ordem;

    @Schema(name = "tipos", description = "Tipos de DFe selecionáveis do grupo")
    private List<TipoDfeDadosAbertosOutput> tipos;

}
