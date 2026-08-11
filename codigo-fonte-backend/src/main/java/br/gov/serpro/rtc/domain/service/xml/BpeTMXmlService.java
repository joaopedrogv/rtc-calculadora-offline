package br.gov.serpro.rtc.domain.service.xml;

import static jakarta.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;
import static java.lang.Boolean.TRUE;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.List;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import br.gov.serpro.rtc.api.model.roc.CompraGovernamentalDomain;
import br.gov.serpro.rtc.api.model.roc.ObjetoDomain;
import br.gov.serpro.rtc.api.model.roc.ROCDomain;
import br.gov.serpro.rtc.api.model.input.calculadora.enumeration.TipoEnteGovernamental;
import br.gov.serpro.rtc.api.model.input.calculadora.enumeration.TipoOperacaoGovernamental;
import br.gov.serpro.rtc.api.model.xml.bpe.tm.InfBPe;
import br.gov.serpro.rtc.api.model.xml.bpe.tm.InfBPe.DetBPeTM;
import br.gov.serpro.rtc.api.model.xml.bpe.tm.InfBPe.DetBPeTM.Det;
import br.gov.serpro.rtc.api.model.xml.bpe.tm.InfBPe.DetBPeTM.Det.Imp;
import br.gov.serpro.rtc.api.model.xml.bpe.tm.InfBPe.Ide;
import br.gov.serpro.rtc.api.model.xml.bpe.tm.InfBPe.Total;
import br.gov.serpro.rtc.api.model.xml.bpe.tm.TCompraGovReduzido;
import br.gov.serpro.rtc.core.util.ArredondamentoUtils;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

/**
 * Serviço para serialização de BPe TM em XML.
 * 
 * IMPORTANTE: Cria um novo Marshaller para cada operação para garantir thread-safety.
 */
@Service
public class BpeTMXmlService {

    private final JAXBContext jaxbContext;
    private final ModelMapper mapper;
    
    public BpeTMXmlService(@Qualifier("jaxbInfBPeTMContext") JAXBContext jaxbContext) {
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

    private InfBPe convert(ROCDomain roc) {
        if (roc == null) {
            throw new IllegalArgumentException("ROC não pode ser nulo");
        }
        if (roc.getObjetos() == null || roc.getObjetos().isEmpty() || roc.getObjetos().size() > 1) {
            throw new IllegalArgumentException("ROC deve conter um item");
        }
        if (roc.getTotal() == null || roc.getTotal().getTribCalc() == null) {
            throw new IllegalArgumentException("Total do ROC não informado");
        }
        
        /**
         * Atualmente, o BPe TM tera somente um item:
         * 
         * infBPe
         *  detBPeTM [1..99]    <-- atualmente, conseguimos gerar somente 1 DetBPeTM para a BPe TM...
         *   det [1..990]       <-- ... e alocamos todos os ObjetosDomain aqui
         *    nViagem
         *    imp               <-- aqui vao os tributos do ObjetoDomain
         *  total
         */
        var infBPe = new InfBPe();
        var detBPeTM = new DetBPeTM();
        infBPe.setDetBPeTM(List.of(detBPeTM));

        var ide = operToIde(roc);
        if (ide != null) {
            infBPe.setIde(ide);
        }
        
        var det = objetoToDet(roc.getObjetos().getFirst());
        detBPeTM.setDet(List.of(det));
        
        infBPe.setTotal(this.mapper.map(roc.getTotal().getTribCalc(), Total.class));
        return infBPe;
    }

    private Det objetoToDet(ObjetoDomain r) {
        var det = new Det();
        det.setNViagem(r.getNObj().toString());
        det.setImp(this.mapper.map(r.getTribCalc(), Imp.class));
        return det;
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
