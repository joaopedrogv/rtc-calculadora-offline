//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.5 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
//


package br.gov.serpro.rtc.api.model.xml.nf3e;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Enumeração XML que representa o indicador de doação do documento fiscal Nota
 * Fiscal de Energia Elétrica Eletrônica (NF3e), conforme o schema fiscal
 * correspondente.
 */
@XmlType(name = "TIndDoacao")
@XmlEnum
public enum TIndDoacao {

    @XmlEnumValue("1")
    v("1");
    private final String value;

    TIndDoacao(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TIndDoacao fromValue(String v) {
        for (TIndDoacao c: TIndDoacao.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
