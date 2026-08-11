//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.5 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
//


package br.gov.serpro.rtc.api.model.xml.cte.simplificado;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;

/**
 * Tipo XML que representa o grupo de diferimento de IBS/CBS do documento fiscal
 * Conhecimento de Transporte Eletrônico Simplificado (CTe Simplificado),
 * conforme o schema fiscal correspondente.
 */
@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TDif", propOrder = {
    "pDif",
    "vDif"
})
public class TDif {

    @XmlElement(required = true)
    private String pDif;

    @XmlElement(required = true)
    private String vDif;
}
