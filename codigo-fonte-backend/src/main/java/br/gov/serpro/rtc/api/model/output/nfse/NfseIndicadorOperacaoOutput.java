/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.api.model.output.nfse;

import br.gov.serpro.rtc.api.model.SerializationVisibility;
import br.gov.serpro.rtc.domain.model.enumeration.LocalFornecimento;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Saída com o indicador de operação de NFS-e, o tipo de operação e as
 * informações do local de fornecimento.
 */
@Schema(name = "NfseIndicadorOperacaoOutput", description = "Informações sobre o indicador de operação")
public record NfseIndicadorOperacaoOutput(
        @Schema(description = "Código do Indicador de Operação", example = "020301") 
        String cIndOp,

        @Schema(description = "Tipo de operação", example = "Serviço de administração e intermediação de bem imóvel") 
        String tipoOperacao,

        @Schema(description = "Código do local de fornecimento", example = "14") 
        LocalFornecimento codigoLocalFornecimento,
        
        @Schema(description = "Indicador se é prestação de serviço onerosa (apenas quando NBS é informado)", example = "true") 
        Boolean prestacaoServicoOnerosa,
        
        @Schema(description = "Indicador se o adquirente é do exterior (apenas quando NBS é informado)", example = "false") 
        Boolean adquirenteExterior
        ) implements SerializationVisibility {
}
