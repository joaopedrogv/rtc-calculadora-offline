/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.api.model.output.dadosabertos;

import br.gov.serpro.rtc.api.model.SerializationVisibility;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Value Object que representa um NCM vigente em uma data específica.
 * <p>
 * Projeta os dados do NCM diretamente da query do repositório (constructor expression),
 * garantindo:
 * <ul>
 *   <li>Código com exatamente 8 dígitos numéricos.</li>
 *   <li>Vigência validada pela data de referência.</li>
 *   <li>Descrição do NCM conforme persistida no banco de dados.</li>
 * </ul>
 * <p>
 * 
 * @param ncm código do NCM (8 dígitos, ex: "22030000")
 * @param descricao descrição textual do NCM conforme tabela de NCMs
 * 
 */
public record NcmVigenciaOutput(
        @Schema(description = "Código da NCM com 8 dígitos", example = "22030000")
        String ncm,
        @Schema(description = "Descrição textual do NCM conforme tabela de Nomenclatura Comum do Mercosul", example = "Bebidas não alcoólicas, exceto sucos e bebidas à base de leite")
        String descricao
) implements SerializationVisibility {
}