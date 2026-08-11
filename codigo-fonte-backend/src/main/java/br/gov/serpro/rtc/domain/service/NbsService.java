/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import br.gov.serpro.rtc.domain.repository.NbsRepository;
import lombok.RequiredArgsConstructor;

/**
 * Serviço responsável por verificar a existência e a vigência de códigos NBS
 * utilizados na calculadora.
 */
@RequiredArgsConstructor
@Service
public class NbsService {

    private final NbsRepository repository;

    public boolean existeNbs(String nbs, LocalDate data) {
        return repository.existeNbs(nbs, data);
    }

}