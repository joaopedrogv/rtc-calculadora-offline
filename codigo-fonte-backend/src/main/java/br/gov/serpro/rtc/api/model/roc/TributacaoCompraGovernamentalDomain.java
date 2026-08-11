package br.gov.serpro.rtc.api.model.roc;

import static br.gov.serpro.rtc.api.model.input.calculadora.enumeration.TipoEnteGovernamental.ESTADOS;
import static br.gov.serpro.rtc.api.model.input.calculadora.enumeration.TipoEnteGovernamental.UNIAO;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static java.math.BigDecimal.ZERO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import br.gov.serpro.rtc.api.model.SerializationVisibility;
import br.gov.serpro.rtc.api.model.input.calculadora.enumeration.TipoEnteGovernamental;
import br.gov.serpro.rtc.config.serializer.BigDecimalTDec0302_04Serializer;
import br.gov.serpro.rtc.config.serializer.BigDecimalTDec1302Serializer;
import br.gov.serpro.rtc.core.util.ArredondamentoUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Detalha a composição do IBS e da CBS em compras governamentais, incluindo
 * alíquotas, valores por ente e regras de cálculo dos valores efetivos.
 */
@Getter
@Setter
@Builder
@JsonInclude(NON_NULL)
@JsonPropertyOrder({ "pAliqIBSUF", "vTribIBSUF", "pAliqIBSMun", "vTribIBSMun", "pAliqCBS", "vTribCBS" })
public class TributacaoCompraGovernamentalDomain implements SerializationVisibility {

    @JsonSerialize(using = BigDecimalTDec0302_04Serializer.class)
    @Schema(description = "Alíquota do IBS de competência do Estado")
    @Builder.Default
    private BigDecimal pAliqIBSUF = ZERO;

    @JsonSerialize(using = BigDecimalTDec1302Serializer.class)
    @Schema(description = "Valor do Tributo do IBS da UF calculado")
    @Builder.Default
    private BigDecimal vTribIBSUF = ZERO;

    @JsonSerialize(using = BigDecimalTDec0302_04Serializer.class)
    @Schema(description = "Alíquota do IBS de competência do Município")
    @Builder.Default
    private BigDecimal pAliqIBSMun = ZERO;

    @JsonSerialize(using = BigDecimalTDec1302Serializer.class)
    @Schema(description = "Valor do Tributo do IBS do Município calculado")
    @Builder.Default
    private BigDecimal vTribIBSMun = ZERO;

    @JsonSerialize(using = BigDecimalTDec0302_04Serializer.class)
    @Schema(description = "Alíquota da CBS")
    @Builder.Default
    private BigDecimal pAliqCBS = ZERO;

    @JsonSerialize(using = BigDecimalTDec1302Serializer.class)
    @Schema(description = "Valor do Tributo da CBS calculado")
    @Builder.Default
    private BigDecimal vTribCBS = ZERO;
    
    public void zerarValores() {
		this.setPAliqIBSUF(ZERO);
		this.setVTribIBSUF(ZERO);
		this.setPAliqIBSMun(ZERO);
		this.setVTribIBSMun(ZERO);
		this.setPAliqCBS(ZERO);
		this.setVTribCBS(ZERO);
	}
    
    /**
     * Calcula o valor que efetivamente cada ente federativo receberá em cmpra governamental
     * 
     * @param tpEnteGov tipo de ente governamental que realizou a operacao
     * @param pTransferenciaCBS percentual de transferencia de CBS para outros entes
     * @param dataFatoGerador data da operacao
     * @return
     */
    @JsonIgnore
    public TributacaoCompraGovernamentalDomain getValoresEfetivos(TipoEnteGovernamental tpEnteGov,
            BigDecimal pTransferenciaCBS, LocalDate dataFatoGerador) {
        if (tpEnteGov == null) {
            return null;
        }
        
        Objects.requireNonNull(dataFatoGerador, "Data do fato gerador obrigatória");
        Objects.requireNonNull(pTransferenciaCBS, "Percentual de transferência da CBS obrigatória");
        
        if (dataFatoGerador.isBefore(LocalDate.of(2027, 1, 1))) {
            // antes de 2027, cada um fica com os seus proprios valores
            return this;
        }
        
        if (tpEnteGov == UNIAO) {
            // se uniao compra, fica com tudo - estados e municipios ficam com zero
            return TributacaoCompraGovernamentalDomain.builder()
                    .pAliqCBS(pAliqCBS.add(pAliqIBSUF).add(pAliqIBSMun))
                    .vTribCBS(vTribCBS.add(vTribIBSUF).add(vTribIBSMun))
            .build();
        } else  {
            final var pTransferenciaCBSEmDecimal = ArredondamentoUtils.dividirPorCem(pTransferenciaCBS); // dividido por 100
            final var pAliqCBSTransferir = pAliqCBS.multiply(pTransferenciaCBSEmDecimal); // percentual CBS a transferir
            final var vTribCBSTransferir = vTribCBS.multiply(pTransferenciaCBSEmDecimal); // valor CBS a transferir
            
            final var pAliqCBSFinal = pAliqCBS.subtract(pAliqCBSTransferir);
            final var vTribCBSFinal = vTribCBS.subtract(vTribCBSTransferir);
            
            final var pcAliquotaFinal = pAliqCBSTransferir.add(pAliqIBSUF).add(pAliqIBSMun);
            final var valorFinal = vTribCBSTransferir.add(vTribIBSUF).add(vTribIBSMun);
            
            final var b = TributacaoCompraGovernamentalDomain.builder()
                .pAliqCBS(pAliqCBSFinal)
                .vTribCBS(vTribCBSFinal);
            
            if (tpEnteGov == ESTADOS) {
                /*
                 * Se estado compra, fica com o valor do municipio e (possivelmente) parte do CBS:
                 *  
                 * pAliqCBS = pAliqCBS - (pAliqCBS * pTransferenciaCBS / 100)
                 * vTribCBS = vTribCBS - (vTribCBS * pTransferenciaCBS / 100)
                 *  
                 * pAliqIBSUF = pAliqIBSUF + pAliqIBSMun + (pAliqCBS * pTransferenciaCBS / 100)
                 * vTribIBSUF = vTribIBSUF + vTribIBSMun + (vTribCBS * pTransferenciaCBS / 100)
                 * 
                 * pAliqIBSMun = ZERO
                 * vTribIBSMun = ZERO
                 */
                b.pAliqIBSUF(pcAliquotaFinal);
                b.vTribIBSUF(valorFinal);
                
            } else {// municipios / DF
                /*
                 * Se municipio ou DF compra, fica com o valor da UF e (possivelmente) parte do CBS:
                 * 
                 * pAliqCBS = pAliqCBS - (pAliqCBS * pTransferenciaCBS / 100)
                 * vTribCBS = vTribCBS - (vTribCBS * pTransferenciaCBS / 100)
                 *  
                 * pAliqIBSUF = ZERO
                 * vTribIBSUF = ZERO
                 * 
                 * pAliqIBSMun = pAliqIBSUF + pAliqIBSMun + (pAliqCBS * pTransferenciaCBS / 100)
                 * vTribIBSMun = vTribIBSUF + vTribIBSMun + (vTribCBS * pTransferenciaCBS / 100)
                 */
                b.pAliqIBSMun(pcAliquotaFinal);
                b.vTribIBSMun(valorFinal);
            }
            return b.build();
        }
        
    }
}
