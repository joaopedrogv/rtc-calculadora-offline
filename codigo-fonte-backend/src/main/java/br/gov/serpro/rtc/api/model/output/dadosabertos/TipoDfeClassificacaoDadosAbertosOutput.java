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
 * Saída de dados abertos com o tipo de DFe aceito para a classificação
 * tributária.
 */
@Getter
@Setter
@Builder
@JsonInclude(Include.NON_NULL)
public class TipoDfeClassificacaoDadosAbertosOutput implements SerializationVisibility {

    @Schema(name = "tipo", description = "Tipo de DFE", example = "55")
    private Integer tipo;

    @Schema(name = "sigla", description = "Sigla do DFE", example = "NF-e")
    private String sigla;

    @Schema(name = "descricao", description = "Descrição do DFE", example = "Documento Fiscal Eletrônico")
    private String descricao;

}
