/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.model.dto;

import java.math.BigDecimal;

/**
 * DTO interno que transporta o valor da alíquota ad valorem. A instância
 * não nula indica que existe alíquota cadastrada, mesmo quando o valor ainda
 * não foi definido em lei (valor nulo).
 */
public record AliquotaAdValoremDTO(BigDecimal valor) {}
