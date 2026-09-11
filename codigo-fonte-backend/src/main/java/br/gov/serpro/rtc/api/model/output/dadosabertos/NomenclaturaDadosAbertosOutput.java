/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.api.model.output.dadosabertos;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import br.gov.serpro.rtc.api.model.SerializationVisibility;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(Include.NON_NULL)
public class NomenclaturaDadosAbertosOutput implements SerializationVisibility {

    @Schema(name = "siglaDfe", description = "Sigla do documento fiscal eletrônico", example = "NFe")
    private final String siglaDfe;

    @Schema(name = "cClassTrib", description = "Código da classificação tributária", example = "000001")
    private final String cClassTrib;

    @Schema(name = "data", description = "Data da consulta", example = "2026-01-01")
    private final LocalDate data;

    @Schema(name = "nomenclatura", description = "Nomenclatura aplicável (NCM, NBS, MISTO, SEM, EXCECAO_NCM ou EXCECAO_NBS)", example = "NCM")
    private final String nomenclatura;

    @Schema(name = "obrigatorio", description = "Indica se o preenchimento da nomenclatura é obrigatório: true para NCM, NBS, MISTO, EXCECAO_NCM e EXCECAO_NBS (há anexo, preenchimento obrigatório); false para SEM", example = "true")
    private final Boolean obrigatorio;

    @Schema(name = "ncmOpcional", description = "Desambiguação do resultado SEM: true quando a nomenclatura é SEM e o DFe é NF-e ou NFC-e (o NCM pode ser informado opcionalmente); false nos demais casos", example = "false")
    private final Boolean ncmOpcional;

    @Schema(name = "nbsOpcional", description = "Desambiguação do resultado SEM: true quando a nomenclatura é SEM e o DFe é NFS-e (a NBS pode ser informada opcionalmente); false nos demais casos", example = "false")
    private final Boolean nbsOpcional;
}
