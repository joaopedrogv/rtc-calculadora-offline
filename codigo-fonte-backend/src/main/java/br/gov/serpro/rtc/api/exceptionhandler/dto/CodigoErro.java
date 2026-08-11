package br.gov.serpro.rtc.api.exceptionhandler.dto;

import br.gov.serpro.rtc.domain.model.enumeration.EstadoItemEnum;

/**
 * Representa o código de erro de cálculo associado a um {@code ProblemType}.
 * Contém o código (ex: "REG-001"), o campo padrão de referência e o estado resultante do item.
 * O campo `campoDefault` representa o campo padrão de referência para o erro comum entre ProblemType e 
 * CodigoValidacao, podendo ser sobrescrito por um campo específico do erro.
 */
public record CodigoErro(String codigo, String campoDefault, EstadoItemEnum estado) {}