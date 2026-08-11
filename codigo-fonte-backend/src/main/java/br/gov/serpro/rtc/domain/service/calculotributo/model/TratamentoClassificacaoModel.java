/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service.calculotributo.model;

import br.gov.serpro.rtc.domain.model.dto.TratamentoClassificacaoDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * Modelo interno que agrupa os tratamentos de classificação e os metadados de
 * desoneração aplicáveis ao item.
 */
@ToString
@Getter
@Builder
public final class TratamentoClassificacaoModel {

    private final TratamentoClassificacaoDTO tratamentoClassificacaoCbsIbs;
    private final TratamentoClassificacaoDTO tratamentoClassificacaoImpostoSeletivo;
    private final TratamentoClassificacaoDTO tratamentoClassificacaoCbsIbsDesoneracao;
    private final AliquotaImpostoSeletivoModel aliquotaImpostoSeletivo;
    private final Boolean temDesoneracao;

}
