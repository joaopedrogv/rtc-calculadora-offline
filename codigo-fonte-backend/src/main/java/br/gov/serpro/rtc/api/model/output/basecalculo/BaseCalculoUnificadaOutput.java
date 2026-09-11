/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.api.model.output.basecalculo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * Output DTO para cálculo unificado das 3 bases de cálculo.
 * Retorna as bases RG (IBS/CBS), IS (Imposto Seletivo) e SN (RBSNItem).
 */
@Getter
@Builder
@NoArgsConstructor
public class BaseCalculoUnificadaOutput {
    
    /**
     * Base de cálculo do Regime Geral (IBS/CBS) - compartilhada para ambos
     */
    private BigDecimal baseRG;
    
    /**
     * Base do Imposto Seletivo
     */
    private BigDecimal baseIS;
    
    /**
     * Base do Simples Nacional = RBSNItem
     */
    private BigDecimal baseSN;

    /**
     * Lista de avisos informativos sobre campos ignorados por não comporem o
     * contexto (vigência, natureza ou modelo de documento). Não bloqueia o
     * cálculo: as bases retornadas já desconsideram esses campos.
     */
    @Schema(name = "avisos", description = "Avisos informativos sobre campos ignorados por não comporem o contexto "
            + "(vigência, natureza ou modelo de documento). As bases retornadas já desconsideram esses campos.")
    private List<String> avisos;

    public BaseCalculoUnificadaOutput(BigDecimal baseRG, BigDecimal baseIS, BigDecimal baseSN) {
        this.baseRG = baseRG;
        this.baseIS = baseIS;
        this.baseSN = baseSN;
        this.avisos = Collections.emptyList();
    }

    public BaseCalculoUnificadaOutput(BigDecimal baseRG, BigDecimal baseIS, BigDecimal baseSN, List<String> avisos) {
        this.baseRG = baseRG;
        this.baseIS = baseIS;
        this.baseSN = baseSN;
        this.avisos = avisos != null ? avisos : new ArrayList<>();
    }


}