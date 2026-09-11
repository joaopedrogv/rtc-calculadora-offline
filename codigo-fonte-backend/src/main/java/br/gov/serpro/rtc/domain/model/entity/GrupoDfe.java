/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.model.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Entidade JPA da tabela {@code GRUPO_DFE} que representa as categorias de
 * documento fiscal eletrônico (ex.: MERCADORIAS, TRANSPORTES, SERVIÇOS) usadas
 * para agrupar os {@link TipoDfe} na seleção do tipo de documento.
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@Entity
@Table(name = "GRUPO_DFE")
public class GrupoDfe {

    @EqualsAndHashCode.Include
    @Id
    @Column(name = "GRDF_ID")
    private Long id;

    @NotNull
    @Column(name = "GRDF_DESCRICAO")
    private String descricao;

    @NotNull
    @Column(name = "GRDF_INICIO_VIGENCIA")
    private LocalDate inicioVigencia;

    @Column(name = "GRDF_FIM_VIGENCIA")
    private LocalDate fimVigencia;

}
