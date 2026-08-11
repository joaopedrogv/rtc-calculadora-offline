package br.gov.serpro.rtc.domain.service;

import static java.math.BigDecimal.ZERO;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Service;

import br.gov.serpro.rtc.api.model.input.calculadora.enumeration.TipoEnteGovernamental;
import br.gov.serpro.rtc.domain.repository.RedutorCompraGovernamentalRepository;
import lombok.RequiredArgsConstructor;

/**
 * Serviço responsável por recuperar o redutor aplicável às operações de compra
 * governamental conforme o ente e a data do fato gerador.
 */
@Service
@RequiredArgsConstructor
public class RedutorCompraGovernamentalService {
    
    private final RedutorCompraGovernamentalRepository repository;

    public BigDecimal buscarValorRedutor(TipoEnteGovernamental tpEnteGov, LocalDate data) {
        if (tpEnteGov == null) {
            return ZERO;
        }
        return repository.buscarValorRedutor(data)
                .orElseThrow(() -> new RuntimeException("Redutor de compra governamental não encontrada."));
    }

}
