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
 * Saída de dados abertos com as versões publicadas da aplicação, do banco de
 * dados e do ambiente.
 */
@Getter
@Setter
@Builder
@JsonInclude(Include.NON_NULL)
public class VersaoOutput implements SerializationVisibility {

    @Schema(name = "versaoApp", description = "Versão do aplicativo", example = "SNAPSHOT-b7bb06d6")
    private final String versaoApp;

    @Schema(name = "descricaoVersaoApp", description = "Descrição da versão do aplicativo", example = "demanda5430315Cofis42615ValidadeNcm-SNAPSHOT-b7bb06d6")
    private final String descricaoVersaoApp;

    @Schema(name = "versaoDb", description = "Versão do banco de dados", example = "1.0.0")
    private final String versaoDb;

    @Schema(name = "descricaoVersaoDb", description = "Descrição da versão do banco de dados", example = "Versão de homologação")
    private final String descricaoVersaoDb;

    @Schema(name = "dataVersaoDb", description = "Data da versão do banco de dados", example = "2026-01-01")
    private final String dataVersaoDb;

    @Schema(name = "ambiente", description = "Ambiente da aplicação", example = "Online")
    private final String ambiente;

}
