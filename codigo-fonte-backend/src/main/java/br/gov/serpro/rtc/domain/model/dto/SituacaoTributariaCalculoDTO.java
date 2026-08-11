package br.gov.serpro.rtc.domain.model.dto;

/**
 * DTO interno com os indicadores da situação tributária usados nas regras de
 * cálculo de IBS/CBS, monofasia, redução, diferimento e créditos.
 */
public record SituacaoTributariaCalculoDTO(
        Long id,
        String codigo,
        Boolean inGrupoIbsCbs,
        Boolean inGrupoIbsCbsMono,
        Boolean inGrupoReducao,
        Boolean inGrupoDiferimento,
        Boolean inGrupoTransferenciaCredito,
        Boolean inGrupoCreditoPresumidoIbsZfm,
        Boolean inGrupoAjusteCompetencia
) {}
