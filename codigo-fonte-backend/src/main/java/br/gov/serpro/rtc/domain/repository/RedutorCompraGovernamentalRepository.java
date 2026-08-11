package br.gov.serpro.rtc.domain.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.gov.serpro.rtc.domain.model.entity.RedutorCompraGovernamental;

/**
 * Repositório Spring Data JPA para acesso a {@link RedutorCompraGovernamental},
 * com consultas do valor vigente e do histórico de redutores de compra
 * governamental.
 */
@Repository
public interface RedutorCompraGovernamentalRepository extends JpaRepository<RedutorCompraGovernamental, Long> {

    @Query("SELECT valor FROM RedutorCompraGovernamental WHERE inicioVigencia <= :data AND (fimVigencia IS NULL OR fimVigencia >= :data)")
    @Cacheable(cacheNames = "RedutorCompraGovernamentalRepository.buscarValorRedutor")
    Optional<BigDecimal> buscarValorRedutor(@Param("data") LocalDate data);

    @Query("SELECT r FROM RedutorCompraGovernamental r ORDER BY r.inicioVigencia ASC")
    @Cacheable(cacheNames = "RedutorCompraGovernamentalRepository.buscarTodos")
    List<RedutorCompraGovernamental> buscarTodos();
}
