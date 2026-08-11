package br.gov.serpro.rtc.domain.model.dto;

/**
 * DTO interno que consolida o vínculo entre tratamento e classificação
 * tributária, incluindo flags de incompatibilidade com suspensão e exigência de
 * grupo de desoneração.
 */
public record TratamentoClassificacaoDTO(
        Long idTratamentoTributario,
        Long idClassificacaoTributaria,
        boolean inIncompativelComSuspensao,
        boolean inExigeGrupoDesoneracao
) {}
