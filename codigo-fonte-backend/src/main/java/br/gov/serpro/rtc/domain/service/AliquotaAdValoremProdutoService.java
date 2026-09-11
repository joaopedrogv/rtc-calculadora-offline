/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import br.gov.serpro.rtc.domain.model.dto.AliquotaAdValoremDTO;
import br.gov.serpro.rtc.domain.model.enumeration.TributoEnum;
import br.gov.serpro.rtc.domain.repository.AliquotaAdValoremProdutoRepository;
import lombok.RequiredArgsConstructor;

/**
 * Serviço responsável por consultar a alíquota ad valorem de produtos sujeita
 * ao Imposto Seletivo, considerando NCM, tributo e vigência.
 */
@RequiredArgsConstructor
@Service
public class AliquotaAdValoremProdutoService {

    private final AliquotaAdValoremProdutoRepository repository;

    public AliquotaAdValoremDTO buscarAliquotaAdValorem(String ncm, Long idTributo, LocalDate data) {
        return repository.buscarAliquotaAdValorem(ncm, idTributo, data);
    }

    public boolean existeNcmAdValorem(String ncm, TributoEnum tributo, LocalDate data) {
        return repository.existeNcmAdValorem(ncm, tributo, data);
    }

}
