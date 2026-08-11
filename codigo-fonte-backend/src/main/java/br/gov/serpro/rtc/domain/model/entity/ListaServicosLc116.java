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
 * Entidade JPA da tabela {@code LISTA_SERVICOS_LC_116} que representa itens da
 * lista de serviços da LC 116 com código, descrição e vigência.
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@Entity
@Table(name = "LISTA_SERVICOS_LC_116")
public class ListaServicosLc116 {

    @EqualsAndHashCode.Include
    @Id
    @Column(name = "LSLC_CD")
    private String codigo;

    @NotNull
    @Column(name = "LSLC_DESCRICAO")
    private String descricao;

    @NotNull
    @Column(name = "LSLC_INICIO_VIGENCIA")
    private LocalDate inicioVigencia;

    @Column(name = "LSLC_FIM_VIGENCIA")
    private LocalDate fimVigencia;
}
