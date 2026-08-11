/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.model.dto;

import java.math.BigDecimal;

/**
 * DTO interno que transporta o valor da alíquota ad rem e a sigla da unidade de
 * medida utilizada no cálculo.
 */
public record AliquotaAdRemDTO(BigDecimal valor, String unidadeMedida) {}