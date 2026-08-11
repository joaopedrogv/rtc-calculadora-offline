//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.5 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
//


package br.gov.serpro.rtc.api.model.xml.nfce;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;

/**
 * Tipo XML que representa o grupo do Imposto Seletivo (IS) do documento fiscal
 * Nota Fiscal de Consumidor Eletrônica (NFCe), conforme o schema fiscal
 * correspondente.
 */
@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TIS", propOrder = {
    "CSTIS",
    "cClassTribIS",
    "vBCIS",
    "pIS",
    "pISEspec",
    "uTrib",
    "qTrib",
    "vIS"
})
public class TIS {

    @XmlElement(required = true)
    private String CSTIS;

    @XmlElement(required = true)
    private String cClassTribIS;

    private String vBCIS;

    private String pIS;

    private String pISEspec;

    private String uTrib;

    private String qTrib;

    private String vIS;
}
