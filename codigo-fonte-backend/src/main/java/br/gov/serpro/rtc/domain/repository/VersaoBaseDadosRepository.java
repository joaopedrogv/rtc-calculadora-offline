/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.gov.serpro.rtc.domain.model.entity.VersaoBaseDados;

/**
 * Repositório Spring Data JPA para acesso a {@link VersaoBaseDados}, incluindo
 * a recuperação da versão mais recente da base de dados.
 */
@Repository
public interface VersaoBaseDadosRepository extends JpaRepository<VersaoBaseDados, Long> {
    Optional<VersaoBaseDados> findTopByOrderByIdDesc();    
}
