package br.gov.serpro.rtc.api.model.roc;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static java.math.BigDecimal.ZERO;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import br.gov.serpro.rtc.api.model.SerializationVisibility;
import br.gov.serpro.rtc.config.serializer.BigDecimalTDec1302Serializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * Consolida os totais de IBS e CBS do ROC.
 *
 * Agrega a base de cálculo total e os grupos totais de IBS, CBS, monofasia e
 * estorno de crédito.
 */
@Getter
@Setter
@JsonInclude(NON_NULL)
@JsonPropertyOrder({ "vBCIBSCBS", "gIBS", "gCBS", "gMono", "gEstornoCred" })
public class IBSCBSTotalDomain implements SerializationVisibility {

    @JsonSerialize(using = BigDecimalTDec1302Serializer.class)
    @Schema(description = "Valor total da BC do IBS e da CBS")
    private BigDecimal vBCIBSCBS;
    
    @Schema(description = "Grupo total do IBS")
    private IBSTotalDomain gIBS;
    
    @Schema(description = "Grupo total da CBS")
    private CBSTotalDomain gCBS;
    
    @Schema(description = "Grupo total da Monofasia")
    private MonofasiaTotalDomain gMono;
    
    @Schema(description = "Grupo total do Estorno de Crédito")
    private EstornoCreditoDomain gEstornoCred;
    
    // TODO Verificar se é possivel criar o IBSCBSTotal
    public static IBSCBSTotalDomain create(List<ObjetoDomain> detalhes) {
        if (detalhes != null && !detalhes.isEmpty()) {
            final var ibsCbsTotal = new IBSCBSTotalDomain();
            ibsCbsTotal.setVBCIBSCBS(getBaseCalculoIbsCbsTotal(detalhes));
            ibsCbsTotal.setGIBS(IBSTotalDomain.create(detalhes)); 
            
            // Garantir que gCBS sempre exista, mesmo que zerado
            // Necessário para validação do XML
            var gCBS = CBSTotalDomain.create(detalhes);
            ibsCbsTotal.setGCBS(gCBS != null ? gCBS : new CBSTotalDomain());
            
            // Garantir que gMono sempre exista, mesmo que zerado.
            // É obrigatório no layout do ROC: 1-1.
            // Necessário para validação do XML.
            var gMono = MonofasiaTotalDomain.create(detalhes);
            ibsCbsTotal.setGMono(gMono != null ? gMono : new MonofasiaTotalDomain());
            ibsCbsTotal.setGEstornoCred(EstornoCreditoDomain.create(detalhes));
            return ibsCbsTotal;
        }
        return null;
    }

    private static BigDecimal getBaseCalculoIbsCbsTotal(List<ObjetoDomain> detalhes) {
        return detalhes.parallelStream()
                .filter(ObjetoDomain::possuiBaseCalculoIBSCBS)
                .map(ObjetoDomain::getValorBaseCalculoIBSCBS)
                .reduce(ZERO, BigDecimal::add);
    }

}
