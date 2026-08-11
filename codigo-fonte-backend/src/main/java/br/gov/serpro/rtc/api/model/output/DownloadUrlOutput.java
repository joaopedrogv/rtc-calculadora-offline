package br.gov.serpro.rtc.api.model.output;

import br.gov.serpro.rtc.api.model.SerializationVisibility;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Saída que encapsula a URL de download de arquivos gerados dinamicamente pela
 * API. É usado para devolver o endereço de recursos produzidos sob demanda,
 * como documentos, exportações ou artefatos gerados pela aplicação.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "URL para download de arquivo")
public class DownloadUrlOutput implements SerializationVisibility {

    @Schema(description = "URL para download do arquivo", example = "https://exemplo.com/arquivo.zip")
    private String downloadUrl;
}
