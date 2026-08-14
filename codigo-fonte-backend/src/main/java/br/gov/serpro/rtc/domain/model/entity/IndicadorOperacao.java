/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.model.entity;

import java.time.LocalDate;

import br.gov.serpro.rtc.domain.model.enumeration.LocalFornecimento;
import br.gov.serpro.rtc.domain.model.enumeration.LocalIncidencia;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Entidade JPA da tabela {@code INDICADOR_OPERACAO_IBS_CBS} que armazena os
 * indicadores de operação do IBS/CBS, com local de incidência, tipo de operação
 * e código de local de fornecimento.
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@Entity
@Table(name = "INDICADOR_OPERACAO_IBS_CBS")
public class IndicadorOperacao {

    @EqualsAndHashCode.Include
    @Id
    @Column(name = "IOIC_CD")
    private String codigo;

    @NotNull
    @Column(name = "IOIC_LOCAL_INCIDENCIA")
    private String localIncidencia;

    @NotNull
    @Column(name = "IOIC_INICIO_VIGENCIA")
    private LocalDate inicioVigencia;

    @Column(name = "IOIC_FIM_VIGENCIA")
    private LocalDate fimVigencia;
    
    @Column(name = "IOIC_TIPO_OPERACAO")
    private String tipoOperacao;
    
    @Column(name = "IOIC_CD_LOCAL_FORNECIMENTO_DFE")
    private LocalFornecimento localFornecimento;

    @Column(name = "IOIC_CD_LOCAL_INCIDENCIA")
    private LocalIncidencia codigoLocalIncidencia;
}
