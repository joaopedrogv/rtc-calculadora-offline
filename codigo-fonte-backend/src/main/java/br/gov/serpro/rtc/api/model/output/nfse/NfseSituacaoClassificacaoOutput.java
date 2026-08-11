/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.api.model.output.nfse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Saída com a situação tributária e a classificação tributária retornadas para
 * NFS-e.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NfseSituacaoClassificacaoOutput {
    private String codigoSituacaoTributaria;
    private String descricaoSituacaoTributaria;
    private String codigoClassificacaoTributaria;
    private String descricaoClassificacaoTributaria;
}
