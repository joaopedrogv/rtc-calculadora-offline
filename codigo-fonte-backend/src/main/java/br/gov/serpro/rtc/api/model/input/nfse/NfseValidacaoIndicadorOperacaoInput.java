/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.api.model.input.nfse;

import java.time.LocalDate;

import br.gov.serpro.rtc.api.model.SerializationVisibility;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entrada que transporta os dados necessários para validar a compatibilidade do
 * indicador de operação em NFS-e. Reúne classificação tributária, NBS,
 * indicador informado e data do fato gerador para verificar se a combinação
 * atende às regras vigentes.
 */
@Getter
@Setter
@NoArgsConstructor
public class NfseValidacaoIndicadorOperacaoInput implements SerializationVisibility {

    @Pattern(regexp = "^\\d{4}$|^\\d{6}$", message = "O campo cTribNac deve conter exatamente 4 ou 6 dígitos numéricos")
    @Schema(name = "cTribNac", description = "Código de Tributação Nacional - opcional (4 ou 6 dígitos)", example = "0101")
    private String cTribNac;

    @NotBlank(message = "O campo cIndOp é obrigatório")
    @NotNull(message = "O campo cIndOp não pode ser nulo")
    @Pattern(regexp = "^\\d{6}$", message = "O campo cIndOp deve conter exatamente 6 dígitos numéricos")
    @Schema(name = "cIndOp", description = "Código do Indicador de Operação (6 dígitos)", example = "100301")
    private String cIndOp;

    @NotBlank(message = "O campo cClassTrib é obrigatório")
    @NotNull(message = "O campo cClassTrib não pode ser nulo")
    @Pattern(regexp = "^\\d{6}$", message = "O campo cClassTrib deve conter exatamente 6 dígitos numéricos")
    @Schema(name = "cClassTrib", description = "Código de Classificação Tributária (6 dígitos)", example = "620001")
    private String cClassTrib;

    @NotBlank(message = "O campo nbs é obrigatório")
    @NotNull(message = "O campo nbs não pode ser nulo")
    @Pattern(regexp = "^\\d{9}$", message = "O campo nbs deve conter exatamente 9 dígitos numéricos")
    @Schema(name = "nbs", description = "Código NBS - Nomenclatura Brasileira de Serviços (9 dígitos)", example = "115021000")
    private String nbs;

    @NotNull(message = "O campo dataOcorrenciaFatoGerador é obrigatório")
    @Schema(name = "dataOcorrenciaFatoGerador", description = "Data de ocorrência do fato gerador da operação (formato: YYYY-MM-DD)", example = "2026-01-15")
    private LocalDate dataOcorrenciaFatoGerador;
}
