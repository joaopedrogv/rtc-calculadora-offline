//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.5 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
//


package br.gov.serpro.rtc.api.model.xml.bpe.normal;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;

/**
 * Classe XML que representa o elemento infBPe do documento fiscal Bilhete de
 * Passagem Eletrônico (BPe), conforme o schema fiscal correspondente.
 */
@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "ide",
    "imp"
})
@XmlRootElement(name = "infBPe")
public class InfBPe {

    private InfBPe.Ide ide;

    @XmlElement(required = true)
    private InfBPe.Imp imp;

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
        private TTribBPe IBSCBS;
    }

}
