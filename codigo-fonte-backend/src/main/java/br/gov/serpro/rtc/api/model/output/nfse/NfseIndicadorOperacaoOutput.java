/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.api.model.output.nfse;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import br.gov.serpro.rtc.api.model.SerializationVisibility;
import br.gov.serpro.rtc.domain.model.enumeration.LocalFornecimento;
import br.gov.serpro.rtc.domain.model.enumeration.LocalIncidencia;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

/**
 * Saída com o indicador de operação de NFS-e, o tipo de operação e as
 * informações do local de fornecimento.
 */
@Value
@Builder
@JsonInclude(Include.NON_NULL)
@Schema(name = "NfseIndicadorOperacaoOutput", description = "Informações sobre o indicador de operação")
public class NfseIndicadorOperacaoOutput implements SerializationVisibility{

        @Schema(description = "Código do Indicador de Operação", example = "020301") 
        String cIndOp;

        @Schema(description = "Tipo de operação", example = "Serviço de administração e intermediação de bem imóvel") 
        String tipoOperacao;

        @Schema(description = "Código do local de fornecimento", example = "14") 
        LocalFornecimento codigoLocalFornecimento;

        @Schema(description = "Local de fornecimento", example = "Estabelecimento do Fornecedor")
        private String localFornecimento;

        @Schema(description = "Código do local de incidência", example = "3")
        private LocalIncidencia codigoLocalIncidencia;

        @Schema(description = "Local de incidência", example = "Estabelecimento do Prestador/Fornecedor")
        private String localIncidencia;
        
        @Schema(description = "Indicador se é prestação de serviço onerosa (apenas quando NBS é informado)", example = "true") 
        Boolean prestacaoServicoOnerosa;
        
        @Schema(description = "Indicador se o adquirente é do exterior (apenas quando NBS é informado)", example = "false") 
        Boolean adquirenteExterior;
        
}
