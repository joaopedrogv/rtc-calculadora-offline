//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.5 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
//


package br.gov.serpro.rtc.api.model.xml.cte.normal;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;

/**
 * Classe XML que representa o elemento infCte do documento fiscal Conhecimento
 * de Transporte Eletrônico (CTe), conforme o schema fiscal correspondente.
 */
@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "ide",
    "imp"
})
@XmlRootElement(name = "infCte")
public class InfCte {

    private InfCte.Ide ide;

    @XmlElement(required = true)
    private InfCte.Imp imp;

    @Getter
    @Setter
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "gCompraGov"
    })
    public static class Ide {

        private TCompraGovReduzido gCompraGov;
    }


    @Getter
    @Setter
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "IBSCBS"
    })
    public static class Imp {

        @XmlElement(required = true)
        private TTribCTe IBSCBS;
    }

}
