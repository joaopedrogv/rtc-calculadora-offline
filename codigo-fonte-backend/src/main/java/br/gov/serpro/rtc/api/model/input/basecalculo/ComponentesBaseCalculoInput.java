package br.gov.serpro.rtc.api.model.input.basecalculo;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.stream.Stream;

import br.gov.serpro.rtc.api.model.SerializationVisibility;
import br.gov.serpro.rtc.api.model.input.basecalculo.enumeration.ElementoBaseCalculo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Contém os valores dos componentes que compõem a base de cálculo.
 * Tipo compartilhado entre o endpoint unificado e o grupo de componentes do item.
 * Campos ausentes (null) são tratados como zero no cálculo.
 */
@Getter
@Setter
@NoArgsConstructor
public class ComponentesBaseCalculoInput implements SerializationVisibility {

    @PositiveOrZero
    @Schema(description = "Valor dos produtos/serviços", example = "1000.00")
    private BigDecimal vProd;

    @PositiveOrZero
    @Schema(description = "Valor do frete", example = "50.00")
    private BigDecimal vFrete;

    @PositiveOrZero
    @Schema(description = "Valor do seguro", example = "20.00")
    private BigDecimal vSeg;

    @PositiveOrZero
    @Schema(description = "Outras despesas acessórias", example = "10.00")
    private BigDecimal vOutro;

    @PositiveOrZero
    @Schema(description = "Imposto de importação", example = "150.00")
    private BigDecimal vII;

    @PositiveOrZero
    @Schema(description = "Imposto Seletivo", example = "30.00")
    private BigDecimal vIS;

    @PositiveOrZero
    @Schema(description = "Desconto incondicional", example = "50.00")
    private BigDecimal vDesc;

    @PositiveOrZero
    @Schema(description = "PIS (vigente 2026)", example = "9.25")
    private BigDecimal vPIS;

    @PositiveOrZero
    @Schema(description = "COFINS (vigente 2026)", example = "46.25")
    private BigDecimal vCOFINS;

    @PositiveOrZero
    @Schema(description = "ICMS (vigente 2026-2032)", example = "180.00")
    private BigDecimal vICMS;

    @PositiveOrZero
    @Schema(description = "ICMS UF Destino (vigente 2026-2032)", example = "20.00")
    private BigDecimal vICMSUFDest;

    @PositiveOrZero
    @Schema(description = "FCP (vigente 2026-2032)", example = "10.00")
    private BigDecimal vFCP;

    @PositiveOrZero
    @Schema(description = "FCP UF Destino (vigente 2026-2032)", example = "5.00")
    private BigDecimal vFCPUFDest;

    @PositiveOrZero
    @Schema(description = "ICMS Monofásico (vigente 2026-2032)", example = "100.00")
    private BigDecimal vICMSMono;

    @PositiveOrZero
    @Schema(description = "ISSQN (vigente 2026-2032, apenas serviço)", example = "30.00")
    private BigDecimal vISSQN;

    /**
     * Retorna true se ao menos um componente foi informado (não-nulo).
     */
    public boolean possuiComponentes() {
        return Stream.of(vProd, vFrete, vSeg, vOutro, vII, vIS, vDesc,
                         vPIS, vCOFINS, vICMS, vICMSUFDest, vFCP,
                         vFCPUFDest, vICMSMono, vISSQN)
                     .anyMatch(Objects::nonNull);
    }

    /**
     * Retorna o valor do componente correspondente ao elemento informado.
     *
     * @param elemento o elemento da base de cálculo
     * @return o valor do campo correspondente, ou null se não informado
     */
    public BigDecimal getValor(ElementoBaseCalculo elemento) {
        return switch (elemento) {
            case V_PROD -> vProd;
            case V_FRETE -> vFrete;
            case V_SEG -> vSeg;
            case V_OUTRO -> vOutro;
            case V_II -> vII;
            case V_IS -> vIS;
            case V_DESC -> vDesc;
            case V_PIS -> vPIS;
            case V_COFINS -> vCOFINS;
            case V_ICMS -> vICMS;
            case V_ICMS_UF_DEST -> vICMSUFDest;
            case V_FCP -> vFCP;
            case V_FCP_UF_DEST -> vFCPUFDest;
            case V_ICMS_MONO -> vICMSMono;
            case V_ISSQN -> vISSQN;
        };
    }

}
