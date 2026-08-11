//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.5 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
//


package br.gov.serpro.rtc.api.model.xml.bpe.normal;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;

/**
 * Tipo XML que representa o grupo de tributos IBS/CBS do documento fiscal
 * Bilhete de Passagem Eletrônico (BPe), conforme o schema fiscal
 * correspondente.
 */
@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TTribBPe", propOrder = {
    "CST",
    "cClassTrib",
    "indDoacao",
    "gIBSCBS",
    "gEstornoCred"
})
public class TTribBPe {

    @XmlElement(required = true)
    private String CST;

    @XmlElement(required = true)
    private String cClassTrib;

    private String indDoacao;

    private TCIBS gIBSCBS;

    private TEstornoCred gEstornoCred;
}
