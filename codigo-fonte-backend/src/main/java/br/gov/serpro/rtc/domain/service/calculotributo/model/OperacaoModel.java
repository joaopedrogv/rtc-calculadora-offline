/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service.calculotributo.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.gov.serpro.rtc.api.model.input.ItemOperacaoInput;
import br.gov.serpro.rtc.api.model.input.calculadora.enumeration.TipoEnteGovernamental;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * Modelo interno que reúne os dados da operação necessários ao cálculo
 * tributário de um item.
 */
@ToString
@Getter
@Builder
public final class OperacaoModel {

    private final LocalDate data;
    private final Long codigoMunicipio;
    private final Long codigoUf;
    private final String ncm;
    private final String nbs;
    private final TipoEnteGovernamental tpEnteGov;
    private final BigDecimal pRedutor;
    private final ItemOperacaoInput item;
    private final TratamentoClassificacaoModel tratamentoClassificacao;

}
