/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service;

import java.time.LocalDate;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import br.gov.serpro.rtc.domain.repository.NbsAplicavelRepository;
import br.gov.serpro.rtc.domain.service.exception.NbsCompletoNaoInformadoException;
import br.gov.serpro.rtc.domain.service.exception.NbsNaoVinculadaException;
import lombok.RequiredArgsConstructor;

/**
 * Serviço responsável por validar se uma NBS pode ser utilizada para a
 * classificação tributária e a vigência informadas.
 */
@RequiredArgsConstructor
@Service
public class NbsAplicavelService {

    private final NbsAplicavelRepository repository;

    /**
     * Valida a exigência e a crítica de NBS para a classificação tributária:
     * classificação com anexo (linha vigente em NBS_APLICAVEL) exige NBS
     * completa e o valor informado é criticado contra o anexo; sem anexo, a
     * NBS informada é aceita sem crítica.
     */
    // TODO: verificar comportamento do cache, algumas vezes o cache permanece ativo 
    // mesmo após reiniciar a aplicação no perfil offline.
    @Cacheable(cacheNames = "NbsAplicavelService.validarNbsAplicavel",
            key = "#nbs + ':' + #idClassificacaoTributaria + ':' + #data")
    public boolean validarNbsAplicavel(String nbs, Long idClassificacaoTributaria, String codigoClassificacaoTributaria,
            LocalDate data, String tributos) {

        final boolean possuiNbsCompleto = StringUtils.length(nbs) == 9;
        final boolean temClassificacaoTributaria = repository.tem(idClassificacaoTributaria, data);

        if (temClassificacaoTributaria && !possuiNbsCompleto) {
            throw new NbsCompletoNaoInformadoException();
        }

        if (possuiNbsCompleto) {
            criticar(nbs, idClassificacaoTributaria, codigoClassificacaoTributaria, data, tributos,
                    temClassificacaoTributaria);
        }
        return true;
    }

    /**
     * Critica uma NBS completa contra o anexo da classificação tributária,
     * quando esta possui anexo; classificação sem anexo aceita qualquer NBS.
     */
    public void criticarNbsContraAnexo(String nbs, Long idClassificacaoTributaria,
            String codigoClassificacaoTributaria, LocalDate data, String tributos) {
        criticar(nbs, idClassificacaoTributaria, codigoClassificacaoTributaria, data, tributos,
                repository.tem(idClassificacaoTributaria, data));
    }

    /*
     * Tabela-verdade da crítica contra o anexo — ver o comentário equivalente
     * em NcmAplicavelService: com A = temNbsAplicavel, E = temExcecaoNbsAplicavel
     * e S = temNbsAplicavelSemExcecao, a regra é rejeitar quando !A (não consta
     * do anexo, inclusive vínculo fora de vigência com exceção vigente — caso
     * que antes passava em silêncio) ou quando E e !S (excepcionado sem outro
     * vínculo vigente livre de exceções).
     */
    private void criticar(String nbs, Long idClassificacaoTributaria, String codigoClassificacaoTributaria,
            LocalDate data, String tributos, boolean temClassificacaoTributaria) {
        if (!temClassificacaoTributaria) {
            return; // classificação sem anexo: aceitar sem crítica
        }
        if (!repository.temNbsAplicavel(nbs, idClassificacaoTributaria, data)) {
            throw new NbsNaoVinculadaException(nbs, codigoClassificacaoTributaria, tributos);
        }
        if (repository.temExcecaoNbsAplicavel(nbs, idClassificacaoTributaria, data)
                && !repository.temNbsAplicavelSemExcecao(nbs, idClassificacaoTributaria, data)) {
            throw new NbsNaoVinculadaException(nbs, codigoClassificacaoTributaria, tributos);
        }
    }

}
