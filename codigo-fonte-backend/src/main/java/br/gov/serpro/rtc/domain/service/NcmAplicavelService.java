/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service;

import java.time.LocalDate;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import br.gov.serpro.rtc.domain.repository.NcmAplicavelRepository;
import br.gov.serpro.rtc.domain.service.exception.NcmCompletoNaoInformadoException;
import br.gov.serpro.rtc.domain.service.exception.NcmNaoVinculadaException;
import lombok.RequiredArgsConstructor;

/**
 * Serviço responsável por validar se um NCM pode ser utilizado para a
 * classificação tributária e a vigência informadas.
 */
@RequiredArgsConstructor
@Service
public class NcmAplicavelService {

    private final NcmAplicavelRepository repository;

    /**
     * Valida a exigência e a crítica de NCM para a classificação tributária:
     * classificação com anexo (linha vigente em NCM_APLICAVEL) exige NCM
     * completo e o valor informado é criticado contra o anexo; sem anexo, o
     * NCM informado é aceito sem crítica.
     */
    
    // TODO: verificar comportamento do cache, algumas vezes o cache permanece ativo 
    // mesmo após reiniciar a aplicação no perfil offline.
    @Cacheable(cacheNames = "NcmAplicavelService.validarNcmAplicavel",
            key = "#ncm + ':' + #idClassificacaoTributaria + ':' + #data")
    public boolean validarNcmAplicavel(String ncm, Long idClassificacaoTributaria, String codigoClassificacaoTributaria,
            LocalDate data, String tributos) {

        final boolean possuiNcmCompleto = StringUtils.length(ncm) == 8;

        // sob demanda da plataforma
        final boolean temClassificacaoTributaria = repository.tem(idClassificacaoTributaria, data);

        if (temClassificacaoTributaria && !possuiNcmCompleto) {
            throw new NcmCompletoNaoInformadoException();
        }

        if (possuiNcmCompleto) {
            criticar(ncm, idClassificacaoTributaria, codigoClassificacaoTributaria, data, tributos,
                    temClassificacaoTributaria);
        }
        return true;
    }

    /**
     * Critica um NCM completo contra o anexo da classificação tributária,
     * quando esta possui anexo; classificação sem anexo aceita qualquer NCM.
     */
    public void criticarNcmContraAnexo(String ncm, Long idClassificacaoTributaria,
            String codigoClassificacaoTributaria, LocalDate data, String tributos) {
        criticar(ncm, idClassificacaoTributaria, codigoClassificacaoTributaria, data, tributos,
                repository.tem(idClassificacaoTributaria, data));
    }

    /*
     * Tabela-verdade da crítica contra o anexo, para NCM completo e
     * classificação com anexo vigente, considerando os predicados
     * A = temNcmAplicavel (vínculo genérico vigente que cobre o NCM),
     * E = temExcecaoNcmAplicavel (exceção vigente cujo código cobre o NCM) e
     * S = temNcmAplicavelSemExcecao (vínculo vigente cobrindo o NCM sem
     * NENHUMA exceção vigente associada — o anexo modela o vínculo genérico
     * duplicado, um por exceção):
     *
     *  A E S | resultado
     *  0 0 0 | rejeitar (NCM não consta do anexo)
     *  0 1 0 | rejeitar (vínculo expirado/futuro; a exceção vigente não o
     *                    restaura) — antes passava em silêncio
     *  1 0 0 | aceitar (as exceções vigentes dos vínculos não cobrem o NCM)
     *  1 0 1 | aceitar (vínculo vigente sem exceções)
     *  1 1 0 | rejeitar (NCM genérico aplicável com exceção específica: o
     *                    excepcionado não é aceito)
     *  1 1 1 | aceitar (outro vínculo vigente sem exceções cobre o NCM)
     *  0 * 1 | impossível (S implica A)
     *
     * Regra resultante: rejeitar quando !A ou quando (E e !S).
     */
    private void criticar(String ncm, Long idClassificacaoTributaria, String codigoClassificacaoTributaria,
            LocalDate data, String tributos, boolean temClassificacaoTributaria) {
        if (!temClassificacaoTributaria) {
            return; // classificação sem anexo: aceitar sem crítica
        }
        if (!repository.temNcmAplicavel(ncm, idClassificacaoTributaria, data)) {
            throw new NcmNaoVinculadaException(ncm, codigoClassificacaoTributaria, tributos);
        }
        if (repository.temExcecaoNcmAplicavel(ncm, idClassificacaoTributaria, data)
                && !repository.temNcmAplicavelSemExcecao(ncm, idClassificacaoTributaria, data)) {
            throw new NcmNaoVinculadaException(ncm, codigoClassificacaoTributaria, tributos);
        }
    }

}
