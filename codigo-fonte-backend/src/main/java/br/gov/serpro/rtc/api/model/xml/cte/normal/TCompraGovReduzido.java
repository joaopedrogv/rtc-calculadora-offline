//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.5 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
//


package br.gov.serpro.rtc.api.model.xml.cte.normal;

import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;

/**
 * Tipo XML que representa o grupo reduzido de compra governamental do documento
 * fiscal Conhecimento de Transporte Eletrônico (CTe), conforme o schema fiscal
 * correspondente.
 */
@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TCompraGovReduzido", propOrder = {
    "tpEnteGov",
    "pRedutor",
    "tpOperGov",
    "refDFeAnt"
})
public class TCompraGovReduzido {

    @XmlElement(required = true)
    private String tpEnteGov;

    @XmlElement(required = true)
    private String pRedutor;

    @XmlElement(required = true)
    private String tpOperGov;

    private List<Object> refDFeAnt;
}
