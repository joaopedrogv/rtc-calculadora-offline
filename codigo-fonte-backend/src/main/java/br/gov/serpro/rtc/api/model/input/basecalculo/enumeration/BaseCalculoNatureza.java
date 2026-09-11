package br.gov.serpro.rtc.api.model.input.basecalculo.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * Enum que representa a natureza da operação para fins de cálculo de tributos.
 */
public enum BaseCalculoNatureza {

    BEM,
    SERVICO;

    /**
     * Verifica se esta natureza corresponde a uma operação com bens.
     * @return true se for BEM, false caso contrário
     */
    public boolean isBem() {
        return this == BEM;
    }

    /**
     * Verifica se esta natureza corresponde a uma operação com serviços.
     * @return true se for SERVICO, false caso contrário
     */
    public boolean isServico() {
        return this == SERVICO;
    }

    /**
     * Retorma o valor do enum como string (para serialização).
     * @return O nome do enum em maiúsculo
     */
    @JsonValue
    public String toValue() {
        return this.name();
    }

    /**
     * Converte uma string para o enum Natureza, aceitando qualquer caso.
     * Normaliza acentos (aç -> ac, etc).
     * @param valor a string a ser convertida
     * @return o enum correspondente ou null se não for válido
     */
    @JsonCreator
    public static BaseCalculoNatureza fromString(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        String normalized = valor.toUpperCase()
            .replace("Á", "A")
            .replace("À", "A")
            .replace("Ã", "A")
            .replace("Ç", "C")
            .replace("É", "E")
            .replace("Ê", "E")
            .replace("Í", "I")
            .replace("Ó", "O")
            .replace("Ô", "O")
            .replace("Õ", "O")
            .replace("Ú", "U")
            .replace(" ", "_");
        try {
            return BaseCalculoNatureza.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}