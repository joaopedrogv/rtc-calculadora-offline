package br.gov.serpro.rtc.domain.model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Entidade JPA da tabela {@code TRANSFERENCIA_CBS_ENTE_GOV} que armazena o
 * percentual de transferência de CBS para ente governamental por vigência.
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@Entity
@Table(name = "TRANSFERENCIA_CBS_ENTE_GOV")
public class TransferenciaCBS {

    @EqualsAndHashCode.Include
    @Id
    @Column(name = "TCEG_ID")
    private Long id;

    @PositiveOrZero
    @NotNull
    @Column(name = "TCEG_VALOR")
    private BigDecimal valor;

    @NotNull
    @Column(name = "TCEG_INICIO_VIGENCIA")
    private LocalDate inicioVigencia;

    @Column(name = "TCEG_FIM_VIGENCIA")
    private LocalDate fimVigencia;

}
