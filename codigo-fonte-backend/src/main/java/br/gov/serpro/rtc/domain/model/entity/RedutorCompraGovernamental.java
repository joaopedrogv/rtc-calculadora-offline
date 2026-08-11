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
 * Entidade JPA da tabela {@code REDUTOR_COMPRA_GOVERNAMENTAL} que armazena o
 * redutor aplicável a compras governamentais em cada período de vigência.
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@Entity
@Table(name = "REDUTOR_COMPRA_GOVERNAMENTAL")
public class RedutorCompraGovernamental {

    @EqualsAndHashCode.Include
    @Id
    @Column(name = "RCGO_ID")
    private Long id;

    @PositiveOrZero
    @NotNull
    @Column(name = "RCGO_VALOR")
    private BigDecimal valor;

    @NotNull
    @Column(name = "RCGO_INICIO_VIGENCIA")
    private LocalDate inicioVigencia;

    @Column(name = "RCGO_FIM_VIGENCIA")
    private LocalDate fimVigencia;

}
