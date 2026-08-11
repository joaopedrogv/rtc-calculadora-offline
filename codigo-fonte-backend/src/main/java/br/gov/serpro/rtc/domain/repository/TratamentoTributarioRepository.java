/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.gov.serpro.rtc.domain.model.entity.TratamentoTributario;

/**
 * Repositório Spring Data JPA para acesso aos registros de {@link
 * TratamentoTributario}. Centraliza as operações padrão de persistência e
 * consulta dessa entidade de referência tributária.
 */
@Repository
public interface TratamentoTributarioRepository extends JpaRepository<TratamentoTributario, Long> {
}
