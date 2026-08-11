package br.gov.serpro.rtc.domain.service;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Service;

import br.gov.serpro.rtc.domain.repository.TransferenciaCBSRepository;
import lombok.RequiredArgsConstructor;

/**
 * Serviço responsável por consultar o percentual de transferência de CBS
 * utilizado na distribuição dos valores calculados.
 */
@Service
@RequiredArgsConstructor
public class TransferenciaCBSService {

    private final TransferenciaCBSRepository repository;

    public BigDecimal getPercentualTransferencia(LocalDate data) {
        return repository.getPercentualTransferencia(data).orElseThrow(
                () -> new RuntimeException("Percentual de transferência de CBS para ente federativo não encontrada."));
    }

}
