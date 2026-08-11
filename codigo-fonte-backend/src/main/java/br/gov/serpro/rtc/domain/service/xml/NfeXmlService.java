package br.gov.serpro.rtc.domain.service.xml;

import static jakarta.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;
import static java.lang.Boolean.TRUE;

import java.io.StringWriter;
import java.math.BigDecimal;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import br.gov.serpro.rtc.api.model.input.calculadora.enumeration.TipoEnteGovernamental;
import br.gov.serpro.rtc.api.model.input.calculadora.enumeration.TipoOperacaoGovernamental;
import br.gov.serpro.rtc.api.model.roc.CompraGovernamentalDomain;
import br.gov.serpro.rtc.api.model.roc.ObjetoDomain;
import br.gov.serpro.rtc.api.model.roc.ROCDomain;
import br.gov.serpro.rtc.api.model.xml.nfe.InfNFe;
import br.gov.serpro.rtc.api.model.xml.nfe.InfNFe.Det;
import br.gov.serpro.rtc.api.model.xml.nfe.InfNFe.Det.Imposto;
import br.gov.serpro.rtc.api.model.xml.nfe.InfNFe.Ide;
import br.gov.serpro.rtc.api.model.xml.nfe.InfNFe.Total;
import br.gov.serpro.rtc.api.model.xml.nfe.TCompraGov;
import br.gov.serpro.rtc.core.util.ArredondamentoUtils;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

/**
 * Serviço para serialização de NFe em XML.
 * 
 * IMPORTANTE: Cria um novo Marshaller para cada operação para garantir thread-safety,
 * já que Marshaller não é thread-safe. O JAXBContext é thread-safe e pode ser reutilizado.
 */
@Service
public class NfeXmlService {

    private final JAXBContext jaxbContext;
    private final ModelMapper mapper;
    
    public NfeXmlService(@Qualifier("jaxbInfNfeContext") JAXBContext jaxbContext) {
        super();
        this.jaxbContext = jaxbContext;
        this.mapper = new ModelMapper();
        configureCompraGovMapping();
    }
    
    public String toXml(ROCDomain roc) throws JAXBException {
        StringWriter writer = new StringWriter();
        // Criar um novo Marshaller para cada operação (thread-safety)
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(JAXB_FORMATTED_OUTPUT, TRUE);
        marshaller.marshal(convert(roc), writer);
        return writer.toString();
    }

    private InfNFe convert(ROCDomain roc) {
        if (roc == null) {
            throw new IllegalArgumentException("ROC não pode ser nulo");
        }
        if (roc.getObjetos() == null || roc.getObjetos().isEmpty()) {
            throw new IllegalArgumentException("ROC deve conter ao menos um objeto");
        }
        if (roc.getTotal() == null || roc.getTotal().getTribCalc() == null) {
            throw new IllegalArgumentException("Total do ROC não informado");
        }
        
        var infNfe = new InfNFe();
        var ide = operToIde(roc);
        if (ide != null) {
            infNfe.setIde(ide);
        }
        infNfe.setDet(roc.getObjetos().stream().map(this::objetoToDet).toList());
        infNfe.setTotal(this.mapper.map(roc.getTotal().getTribCalc(), Total.class));
        return infNfe;
    }

    private Det objetoToDet(ObjetoDomain r) {
        var d = new Det();
        d.setNItem(r.getNObj().toString());
        d.setImposto(this.mapper.map(r.getTribCalc(), Imposto.class));
        return d;
    }

	private Ide operToIde(ROCDomain roc) {
		if (roc.getOper() == null || roc.getOper().getGCompraGov() == null) {
			return null;
		}

		var ide = new Ide();
		ide.setGCompraGov(compraGovToXml(roc.getOper().getGCompraGov()));
		return ide;
	}

	private TCompraGov compraGovToXml(CompraGovernamentalDomain compraGov) {
		return this.mapper.map(compraGov, TCompraGov.class);
	}

	private void configureCompraGovMapping() {
		Converter<TipoEnteGovernamental, String> tpEnteGovConverter = context -> context.getSource() == null ? null
				: String.valueOf(context.getSource().getCodigo());
		Converter<TipoOperacaoGovernamental, String> tpOperGovConverter = context -> context.getSource() == null ? null
				: String.valueOf(context.getSource().getCodigo());
		Converter<BigDecimal, String> pRedutorConverter = context -> context.getSource() == null ? null
				: ArredondamentoUtils.formatarAliquota(context.getSource());

		this.mapper.typeMap(CompraGovernamentalDomain.class, TCompraGov.class).addMappings(mapping -> {
			mapping.using(tpEnteGovConverter).map(CompraGovernamentalDomain::getTpEnteGov, TCompraGov::setTpEnteGov);
			mapping.using(pRedutorConverter).map(CompraGovernamentalDomain::getPRedutor, TCompraGov::setPRedutor);
			mapping.using(tpOperGovConverter).map(CompraGovernamentalDomain::getTpOperGov, TCompraGov::setTpOperGov);
		});
	}

}
