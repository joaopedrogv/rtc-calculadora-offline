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

import br.gov.serpro.rtc.domain.model.entity.TransferenciaCBS;

/**
 * Repositório Spring Data JPA para acesso a {@link TransferenciaCBS}, com
 * consultas do percentual vigente e do histórico de transferência de CBS.
 */
@Repository
public interface TransferenciaCBSRepository extends JpaRepository<TransferenciaCBS, Long> {

    @Query("SELECT valor FROM TransferenciaCBS WHERE inicioVigencia <= :data AND (fimVigencia IS NULL OR fimVigencia >= :data)")
    @Cacheable(cacheNames = "TransferenciaCBSRepository.getPercentualTransferencia")
    Optional<BigDecimal> getPercentualTransferencia(@Param("data") LocalDate data);

    @Query("SELECT t FROM TransferenciaCBS t ORDER BY t.inicioVigencia ASC")
    @Cacheable(cacheNames = "TransferenciaCBSRepository.buscarTodos")
    List<TransferenciaCBS> buscarTodos();
}
