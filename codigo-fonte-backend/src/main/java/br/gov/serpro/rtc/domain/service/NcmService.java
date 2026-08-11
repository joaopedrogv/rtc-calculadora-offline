/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import br.gov.serpro.rtc.domain.repository.NcmRepository;
import lombok.RequiredArgsConstructor;

/**
 * Serviço responsável por verificar a existência e a vigência de códigos NCM
 * utilizados na calculadora.
 */
@RequiredArgsConstructor
@Service
public class NcmService {

    private final NcmRepository repository;

    public boolean existeNcm(String ncm, LocalDate data) {
        return repository.existeNcm(ncm, data);
    }

}