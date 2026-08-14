/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.api.model.output.nfse;

import br.gov.serpro.rtc.api.model.SerializationVisibility;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@Schema(name = "NfseCodigoDescricaoOutput", description = "Código e descrição de um valor de enumeração")
public class NfseCodigoDescricaoOutput implements SerializationVisibility {

    @Schema(description = "Código do valor", example = "1")
    private int codigo;

    @Schema(description = "Descrição do valor", example = "Endereço do Destinatário")
    private String descricao;
}
