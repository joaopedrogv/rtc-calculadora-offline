/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.model.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Entidade JPA da tabela {@code EXCECAO_NBS_APLICAVEL} que registra exceções de
 * NBS para vínculos de NBS aplicável por classificação tributária.
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@Entity
@Table(name = "EXCECAO_NBS_APLICAVEL")
public class ExcecaoNbsAplicavel {

    @EqualsAndHashCode.Include
    @Id
    @Column(name = "ENBS_ID")
    private Long id;

    @NotNull
    @Column(name = "ENBS_NBS_CD")
    private String nbs;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "ENBS_NBSA_ID")
    private NbsAplicavel nbsAplicavel;

    @NotNull
    @Column(name = "ENBS_INICIO_VIGENCIA")
    private LocalDate inicioVigencia;

    @Column(name = "ENBS_FIM_VIGENCIA")
    private LocalDate fimVigencia;

}
