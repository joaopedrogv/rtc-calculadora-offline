//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.5 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
//


package br.gov.serpro.rtc.api.model.xml.nfe;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;

/**
 * Tipo XML que representa o grupo de tributação monofásica de IBS/CBS do
 * documento fiscal Nota Fiscal Eletrônica (NFe), conforme o schema fiscal
 * correspondente.
 */
@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TMonofasia", propOrder = {
    "gMonoPadrao",
    "gMonoReten",
    "gMonoRet",
    "gMonoDif",
    "vTotIBSMonoItem",
    "vTotCBSMonoItem"
})
public class TMonofasia {

    private TMonofasia.GMonoPadrao gMonoPadrao;

    private TMonofasia.GMonoReten gMonoReten;

    private TMonofasia.GMonoRet gMonoRet;

    private TMonofasia.GMonoDif gMonoDif;

    @XmlElement(required = true)
    private String vTotIBSMonoItem;

    @XmlElement(required = true)
    private String vTotCBSMonoItem;

    @Getter
    @Setter
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "pDifIBS",
        "vIBSMonoDif",
        "pDifCBS",
        "vCBSMonoDif"
    })
    public static class GMonoDif {

        @XmlElement(required = true)
        private String pDifIBS;

        @XmlElement(required = true)
        private String vIBSMonoDif;

        @XmlElement(required = true)
        private String pDifCBS;

        @XmlElement(required = true)
        private String vCBSMonoDif;
    }


    @Getter
    @Setter
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "qBCMono",
        "adRemIBS",
        "adRemCBS",
        "vIBSMono",
        "vCBSMono"
    })
    public static class GMonoPadrao {

        @XmlElement(required = true)
        private String qBCMono;

        @XmlElement(required = true)
        private String adRemIBS;

        @XmlElement(required = true)
        private String adRemCBS;

        @XmlElement(required = true)
        private String vIBSMono;

        @XmlElement(required = true)
        private String vCBSMono;
    }


    @Getter
    @Setter
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "qBCMonoRet",
        "adRemIBSRet",
        "vIBSMonoRet",
        "adRemCBSRet",
        "vCBSMonoRet"
    })
    public static class GMonoRet {

        @XmlElement(required = true)
        private String qBCMonoRet;

        @XmlElement(required = true)
        private String adRemIBSRet;

        @XmlElement(required = true)
        private String vIBSMonoRet;

        @XmlElement(required = true)
        private String adRemCBSRet;

        @XmlElement(required = true)
        private String vCBSMonoRet;
    }


    @Getter
    @Setter
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "qBCMonoReten",
        "adRemIBSReten",
        "vIBSMonoReten",
        "adRemCBSReten",
        "vCBSMonoReten"
    })
    public static class GMonoReten {

        @XmlElement(required = true)
        private String qBCMonoReten;

        @XmlElement(required = true)
        private String adRemIBSReten;

        @XmlElement(required = true)
        private String vIBSMonoReten;

        @XmlElement(required = true)
        private String adRemCBSReten;

        @XmlElement(required = true)
        private String vCBSMonoReten;
    }

}
