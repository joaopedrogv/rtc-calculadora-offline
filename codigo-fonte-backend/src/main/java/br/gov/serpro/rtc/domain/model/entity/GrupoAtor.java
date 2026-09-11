/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Entidade JPA mínima da tabela {@code GRUPO_ATOR}, necessária para satisfazer
 * o contrato de {@code JpaRepository<GrupoAtor, Long>}. Toda a lógica de
 * consulta utiliza query nativa.
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@Entity
@Table(name = "GRUPO_ATOR")
public class GrupoAtor {

    @EqualsAndHashCode.Include
    @Id
    @Column(name = "GRAT_ID")
    private Long id;
}
