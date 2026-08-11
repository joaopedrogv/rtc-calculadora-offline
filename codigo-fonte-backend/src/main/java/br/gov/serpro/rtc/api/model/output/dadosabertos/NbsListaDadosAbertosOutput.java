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
 * Saída da API de dados abertos para listagem resumida de códigos da
 * Nomenclatura Brasileira de Serviços. Fornece apenas os dados essenciais para
 * busca, seleção e validação rápida de NBS sem retornar o detalhamento
 * tributário completo.
 */
@Getter
@Setter
@Builder
@JsonInclude(Include.NON_NULL)
public class NbsListaDadosAbertosOutput implements SerializationVisibility {

    @Schema(name = "codigo", description = "Código NBS", example = "10101")
    private final String codigo;

    @Schema(name = "descricao", description = "Descrição da NBS", example = "Serviços de construção de edificações")
    private final String descricao;

}
