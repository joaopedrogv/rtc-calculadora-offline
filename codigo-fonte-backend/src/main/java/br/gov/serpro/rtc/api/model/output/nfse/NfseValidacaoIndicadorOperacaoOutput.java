/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.api.model.output.nfse;

import java.time.LocalDate;

import br.gov.serpro.rtc.api.model.SerializationVisibility;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

/**
 * Saída que informa o resultado da validação do indicador de operação em
 * documentos de NFS-e. Retorna a conclusão da verificação de compatibilidade
 * entre indicador, classificação tributária, NBS e data do fato gerador.
 */
@Value
@Builder
@Schema(name = "NfseValidacaoIndicadorOperacaoOutput", description = "Resultado da validação do indicador de operação")
public class NfseValidacaoIndicadorOperacaoOutput implements SerializationVisibility {
    
    @Schema(description = "Código de Tributação Nacional informado (opcional)", example = "010101")
    private String cTribNac;

    @Schema(description = "Código do Indicador de Operação informado", example = "100301")
    private String cIndOp;

    @Schema(description = "Código de Classificação Tributária informado", example = "000001")
    private String cClassTrib;

    @Schema(description = "Código NBS informado", example = "115021000")
    private String nbs;

    @Schema(description = "Data de ocorrência do fato gerador informada", example = "2025-01-01")
    private LocalDate dataOcorrenciaFatoGerador;

    @Schema(description = "Indica se a combinação informada é válida", example = "true")
    private boolean valido;
}
