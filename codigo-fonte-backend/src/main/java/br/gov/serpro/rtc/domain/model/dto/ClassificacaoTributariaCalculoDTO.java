package br.gov.serpro.rtc.domain.model.dto;

/**
 * DTO interno com os dados mínimos da classificação tributária necessários ao
 * cálculo, incluindo tipo de alíquota e indicadores de grupos monofásicos e de
 * redução.
 */
public record ClassificacaoTributariaCalculoDTO(Long id, String codigo, String tipoAliquota,
        Boolean inGrupoMonofasiaPadrao, Boolean inGrupoMonofasiaReten, Boolean inGrupoMonofasiaRet,
        Boolean inGrupoMonofasiaDiferimento, Boolean inGrupoReducao) {
}
