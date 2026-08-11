/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service.calculotributo.model;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * Modelo interno que representa as alíquotas ad valorem e ad rem do Imposto
 * Seletivo, com a respectiva unidade de medida.
 */
@ToString
@Getter
@Builder
public final class AliquotaImpostoSeletivoModel {

    private final BigDecimal aliquotaAdValorem;
    private final BigDecimal aliquotaAdRem;
    private final String unidadeMedida;

}
