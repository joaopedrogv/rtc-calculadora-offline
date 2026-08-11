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
import br.gov.serpro.rtc.api.model.roc.ROCDomain;
import br.gov.serpro.rtc.api.model.xml.cte.normal.InfCte;
import br.gov.serpro.rtc.api.model.xml.cte.normal.TCompraGovReduzido;
import br.gov.serpro.rtc.api.model.xml.cte.normal.TTribCTe;
import br.gov.serpro.rtc.api.model.xml.cte.normal.InfCte.Ide;
import br.gov.serpro.rtc.api.model.xml.cte.normal.InfCte.Imp;
import br.gov.serpro.rtc.core.util.ArredondamentoUtils;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

/**
 * Serviço para serialização de CTe em XML.
 * 
 * IMPORTANTE: Cria um novo Marshaller para cada operação para garantir thread-safety.
 */
@Service
public class CteXmlService {

    private final JAXBContext jaxbContext;
    private final ModelMapper mapper;
    
    public CteXmlService(@Qualifier("jaxbInfCteContext") JAXBContext jaxbContext) {
        super();
        this.jaxbContext = jaxbContext;
        this.mapper = new ModelMapper();
        configureCompraGovMapping();
    }

    public String toXml(ROCDomain roc) throws JAXBException {
        final StringWriter writer = new StringWriter();
        // Criar um novo Marshaller para cada operação (thread-safety)
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(JAXB_FORMATTED_OUTPUT, TRUE);
        marshaller.marshal(convert(roc), writer);
        return writer.toString();
    }

    private InfCte convert(ROCDomain roc) {
        if (roc == null) {
            throw new IllegalArgumentException("ROC não pode ser nulo");
        }
        if (roc.getObjetos() == null || roc.getObjetos().isEmpty() || roc.getObjetos().size() > 1) {
            throw new IllegalArgumentException("ROC deve conter um item");
        }

        final var imp = new Imp();
        final var ibsCbs = roc.getObjetos().getFirst().getTribCalc().getIBSCBS();
        imp.setIBSCBS(this.mapper.map(ibsCbs, TTribCTe.class));
        
        final var cte = new InfCte();
        var ide = operToIde(roc);
        if (ide != null) {
            cte.setIde(ide);
        }
        cte.setImp(imp);
        return cte;
    }

	private Ide operToIde(ROCDomain roc) {
		if (roc.getOper() == null || roc.getOper().getGCompraGov() == null) {
			return null;
		}

		var ide = new Ide();
		ide.setGCompraGov(compraGovToXml(roc.getOper().getGCompraGov()));
		return ide;
	}

	private TCompraGovReduzido compraGovToXml(CompraGovernamentalDomain compraGov) {
		return this.mapper.map(compraGov, TCompraGovReduzido.class);
	}

	private void configureCompraGovMapping() {
		Converter<TipoEnteGovernamental, String> tpEnteGovConverter = context -> context.getSource() == null ? null
				: String.valueOf(context.getSource().getCodigo());
		Converter<TipoOperacaoGovernamental, String> tpOperGovConverter = context -> context.getSource() == null ? null
				: String.valueOf(context.getSource().getCodigo());
		Converter<BigDecimal, String> pRedutorConverter = context -> context.getSource() == null ? null
				: ArredondamentoUtils.formatarAliquota(context.getSource());

		this.mapper.typeMap(CompraGovernamentalDomain.class, TCompraGovReduzido.class).addMappings(mapping -> {
			mapping.using(tpEnteGovConverter).map(CompraGovernamentalDomain::getTpEnteGov,
					TCompraGovReduzido::setTpEnteGov);
			mapping.using(pRedutorConverter).map(CompraGovernamentalDomain::getPRedutor,
					TCompraGovReduzido::setPRedutor);
			mapping.using(tpOperGovConverter).map(CompraGovernamentalDomain::getTpOperGov,
					TCompraGovReduzido::setTpOperGov);
		});
	}

}
