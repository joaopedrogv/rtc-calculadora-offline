//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.5 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
//


package br.gov.serpro.rtc.api.model.xml.nf3e;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;

/**
 * Tipo XML que representa o grupo de tributos IBS/CBS do documento fiscal Nota
 * Fiscal de Energia Elétrica Eletrônica (NF3e), conforme o schema fiscal
 * correspondente.
 */
@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TTribNF3e", propOrder = {
    "CST",
    "cClassTrib",
    "indDoacao",
    "gIBSCBS",
    "gEstornoCred"
})
public class TTribNF3E {

    @XmlElement(required = true)
    private String CST;

    @XmlElement(required = true)
    private String cClassTrib;

    @XmlSchemaType(name = "string")
    private TIndDoacao indDoacao;

    private TCIBS gIBSCBS;

    private TEstornoCred gEstornoCred;
}
