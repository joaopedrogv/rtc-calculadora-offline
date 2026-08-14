/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.api.model.output.nfse;

import java.time.LocalDate;

import br.gov.serpro.rtc.api.model.SerializationVisibility;
import br.gov.serpro.rtc.domain.model.enumeration.LocalFornecimento;
import br.gov.serpro.rtc.domain.model.enumeration.LocalIncidencia;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

/**
 * Saída com o local da operação de NFS-e determinado a partir do indicador de
 * operação informado.
 */
@Value
@Builder
@Schema(name = "NfseLocalOperacaoOutput", description = "Informações sobre o local da operação baseado no indicador de operação")
public class NfseLocalOperacaoOutput implements SerializationVisibility {
    
    @Schema(description = "Código do Indicador de Operação informado", example = "100301")
    private String cIndOp;

    @Schema(description = "Data de ocorrência do fato gerador informada", example = "2026-01-01")
    private LocalDate dataOcorrenciaFatoGerador;

    @Schema(description = "Código do local de fornecimento", example = "5")
    private LocalFornecimento codigoLocalFornecimento;

    @Schema(description = "Local de fornecimento", example = "Estabelecimento do Fornecedor")
    private String localFornecimento;

    @Schema(description = "Código do local de incidência", example = "3")
    private LocalIncidencia codigoLocalIncidencia;
    
    @Schema(description = "Local de incidência", example = "Estabelecimento do Prestador/Fornecedor")
    private String localIncidencia;

}
