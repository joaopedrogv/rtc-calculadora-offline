/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import br.gov.serpro.rtc.domain.model.entity.TipoDfe;
import br.gov.serpro.rtc.domain.model.enumeration.SiglasDFeEnum;
import br.gov.serpro.rtc.domain.repository.TipoDfeRepository;
import br.gov.serpro.rtc.domain.service.exception.ErroFaltaImplementacaoException;
import br.gov.serpro.rtc.domain.service.exception.TipoDfeNaoEncontradoException;
import lombok.RequiredArgsConstructor;

/**
 * Serviço responsável por resolver o tipo de documento fiscal (tpDoc) da
 * operação para o registro de {@code TIPO_DFE} vigente.
 */
@RequiredArgsConstructor
@Service
public class TipoDfeService {

    private final TipoDfeRepository tipoDfeRepository;

    /**
     * Resolve o código numérico do tipo de documento fiscal ({@code tpDoc},
     * ex.: 55 = NF-e, 65 = NFC-e) para o registro de {@code TIPO_DFE} vigente
     * na data informada.
     *
     * @param tpDoc código numérico do tipo de documento; pode ser {@code null}
     * @param data data do fato gerador aplicável
     * @return o registro vigente de {@link TipoDfe}, ou {@code null} quando
     *         {@code tpDoc} não é informado
     * @throws TipoDfeNaoEncontradoException se o tipo não existe ou está fora
     *         de vigência na data informada
     * @throws ErroFaltaImplementacaoException se a sigla do tipo não possui
     *         tratamento implementado pela calculadora
     */
    public TipoDfe buscarPorTipo(Integer tpDoc, LocalDate data) {
        if (tpDoc == null) {
            return null;
        }
        TipoDfe tipoDfe = tipoDfeRepository.buscarPorTipo(tpDoc, data)
                .orElseThrow(() -> new TipoDfeNaoEncontradoException(tpDoc, data));
        SiglasDFeEnum sigla = SiglasDFeEnum.getPorSiglaNormalizada(tipoDfe.getSigla());
        if (sigla.semTratamentoCalculadora()) {
            throw new ErroFaltaImplementacaoException(
                "Tipo de documento fiscal (tpDoc) de código %d (%s) não possui tratamento implementado na Calculadora",
                tpDoc, sigla.getSigla());
        }
        return tipoDfe;
    }

}
