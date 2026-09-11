/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.api.model.output.basecalculo;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

/**
 * Output DTO para cálculo unificado da base de cálculo NFS-e.
 * Estrutura flat com 4 campos: baseRG, baseSN, avisos, erros.
 * SEM @JsonUnwrapped.
 */
@Getter
@Builder
public class BaseCalculoUnificadaNfseOutput {

    @JsonInclude(JsonInclude.Include.ALWAYS)
    private final BigDecimal baseRG;

    @JsonInclude(JsonInclude.Include.ALWAYS)
    private final BigDecimal baseSN;

    @Builder.Default
    private final List<String> avisos = List.of();

    @Builder.Default
    private final List<ErroBaseCalculoNfse> erros = List.of();
}
