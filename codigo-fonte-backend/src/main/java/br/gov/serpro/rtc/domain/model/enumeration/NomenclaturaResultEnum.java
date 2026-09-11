/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.model.enumeration;

/**
 * Enumeração que representa os possíveis resultados de classificação de nomenclatura
 * (NCM, NBS, MISTO ou SEM) aplicável para uma dada combinação de siglaDfe e
 * classificação tributária.
 * 
 * Estados de resultado:
 * - SEM: Nenhuma nomenclatura é aplicável
 * - NCM: Apenas NCM é aplicável
 * - NBS: Apenas NBS é aplicável
 * - MISTO: Ambas NCM e NBS são aplicáveis
 * - EXCECAO_NCM: Exceção para NFe/NFCe (ex: cClassTrib 200038)
 * - EXCECAO_NBS: Exceção para NFSe (ex: cClassTrib 200038)
 */
public enum NomenclaturaResultEnum {
    SEM("SEM"),
    NCM("NCM"),
    NBS("NBS"),
    MISTO("MISTO"),
    EXCECAO_NCM("EXCECAO_NCM"),
    EXCECAO_NBS("EXCECAO_NBS");

    private final String value;

    NomenclaturaResultEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
