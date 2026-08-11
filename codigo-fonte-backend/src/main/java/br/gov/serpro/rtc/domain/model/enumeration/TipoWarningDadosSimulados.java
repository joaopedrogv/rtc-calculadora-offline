/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.model.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enumera os avisos associados ao uso de dados simulados pela calculadora.
 *
 * Valores: {@code CASO_GERAL}, {@code CASO_COMPRAS_GOVERNAMENTAIS}, {@code
 * CASO_IMPOSTO_SELETIVO}, {@code CASO_COMPRAS_GOVERNAMENTAIS_IS} e {@code
 * CASO_ALIQUOTAS_FICTICIAS}.
 */
@Getter
@RequiredArgsConstructor
public enum TipoWarningDadosSimulados {
    CASO_GERAL(1),
    CASO_COMPRAS_GOVERNAMENTAIS(2),
    CASO_IMPOSTO_SELETIVO(3),
    CASO_COMPRAS_GOVERNAMENTAIS_IS(4),
    CASO_ALIQUOTAS_FICTICIAS(5);
    
    private final int valor;
}
