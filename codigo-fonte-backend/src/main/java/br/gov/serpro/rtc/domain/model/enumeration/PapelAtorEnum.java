/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.model.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum que representa o papel do ator na operação tributária.
 * Valores válidos: FORNECEDOR e ADQUIRENTE, mapeados respectivamente para
 * "Fornecedor" e "Adquirente" na coluna ATCL_IN_PAPEL do banco de dados.
 */
public enum PapelAtorEnum {

    FORNECEDOR("Fornecedor"),
    ADQUIRENTE("Adquirente");

    private final String valorBanco;

    PapelAtorEnum(String valorBanco) {
        this.valorBanco = valorBanco;
    }

    /**
     * Retorna o valor do enum tal como armazenado na coluna ATCL_IN_PAPEL.
     */
    public String getValorBanco() {
        return valorBanco;
    }

    /**
     * Serializa o enum como seu nome em maiúsculo (FORNECEDOR / ADQUIRENTE).
     */
    @JsonValue
    public String toValue() {
        return this.name();
    }

    /**
     * Converte uma string para o enum PapelAtorEnum, aceitando qualquer variação
     * de caixa (fornecedor, FORNECEDOR, Fornecedor, etc.).
     *
     * @param valor a string a ser convertida
     * @return o enum correspondente ou null se não for válido
     */
    @JsonCreator
    public static PapelAtorEnum fromString(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        try {
            return PapelAtorEnum.valueOf(valor.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
