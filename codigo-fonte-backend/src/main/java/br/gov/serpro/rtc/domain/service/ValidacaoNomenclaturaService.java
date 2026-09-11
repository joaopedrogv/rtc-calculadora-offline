/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service;

import java.time.LocalDate;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import br.gov.serpro.rtc.domain.model.enumeration.ModoValidacaoNomenclatura;
import br.gov.serpro.rtc.domain.model.enumeration.NomenclaturaResultEnum;
import br.gov.serpro.rtc.domain.model.enumeration.SiglasDFeEnum;
import br.gov.serpro.rtc.domain.service.exception.NbsCompletoNaoInformadoException;
import br.gov.serpro.rtc.domain.service.exception.NcmCompletoNaoInformadoException;
import br.gov.serpro.rtc.domain.service.exception.NomenclaturaIncompativelTipoDfeException;
import br.gov.serpro.rtc.domain.service.exception.NomenclaturaNaoPermitidaTipoDfeException;
import br.gov.serpro.rtc.domain.service.exception.NomenclaturaObrigatoriaNaoInformadaException;
import lombok.RequiredArgsConstructor;

/**
 * Serviço responsável por validar a obrigatoriedade e a permissão de NCM/NBS
 * de um item quando o tipo de documento fiscal (tpDoc) da operação é
 * conhecido. A exigência é dirigida pelo resultado de
 * {@link NomenclaturaService#determinarNomenclatura(String, String, LocalDate)}:
 *
 * - {@code NCM}/{@code EXCECAO_NCM}: exige NCM completo; NBS informado gera erro;
 * - {@code NBS}/{@code EXCECAO_NBS}: exige NBS completo; NCM informado gera erro;
 * - {@code MISTO}: exige um dos dois completo (qualquer um — a escolha é do
 *   emissor/frontend);
 * - {@code SEM}: nada é exigido; o tratamento de NCM/NBS informado depende do
 *   documento (trio NF-e/NFC-e/NFS-e sempre rígido) e do
 *   {@link ModoValidacaoNomenclatura} (documentos fora do trio).
 *
 * Quando a nomenclatura exigida é informada, o valor é criticado contra o
 * anexo da classificação (tabelas NCM_APLICAVEL/NBS_APLICAVEL com as exceções
 * EXCECAO_NCM_APLICAVEL/EXCECAO_NBS_APLICAVEL).
 */
@RequiredArgsConstructor
@Service
public class ValidacaoNomenclaturaService {

    /**
     * Documentos para os quais a regra de nomenclatura é sempre rígida,
     * independentemente do modo de permissividade.
     */
    private static final Set<SiglasDFeEnum> TRIO_RIGIDO =
            Set.of(SiglasDFeEnum.NFE, SiglasDFeEnum.NFCE, SiglasDFeEnum.NFSE);

    private final NomenclaturaService nomenclaturaService;
    private final NcmAplicavelService ncmAplicavelService;
    private final NbsAplicavelService nbsAplicavelService;

    /**
     * Valida a nomenclatura (NCM/NBS) do item para a classificação tributária
     * e o tipo de documento fiscal informados.
     *
     * @param siglaDfe sigla normalizada do documento fiscal derivada do tpDoc
     * @param idClassificacaoTributaria id da classificação tributária efetiva
     * @param codigoClassificacaoTributaria código (cClassTrib) da classificação efetiva
     * @param ncm NCM informado no item (pode ser nulo/vazio)
     * @param nbs NBS informado no item (pode ser nulo/vazio)
     * @param data data do fato gerador aplicável
     * @param tributos rótulo do conjunto de tributos para mensagens de erro
     * @param modo modo de permissividade para documentos fora do trio
     */
    public void validar(SiglasDFeEnum siglaDfe, Long idClassificacaoTributaria,
            String codigoClassificacaoTributaria, String ncm, String nbs, LocalDate data,
            String tributos, ModoValidacaoNomenclatura modo) {

        final NomenclaturaResultEnum resultado = NomenclaturaResultEnum.valueOf(
                nomenclaturaService.determinarNomenclatura(
                        siglaDfe.getChaveNormalizada(), codigoClassificacaoTributaria, data));

        switch (resultado) {
            case NCM, EXCECAO_NCM -> exigirNcm(siglaDfe, idClassificacaoTributaria,
                    codigoClassificacaoTributaria, ncm, nbs, data, tributos);
            case NBS, EXCECAO_NBS -> exigirNbs(siglaDfe, idClassificacaoTributaria,
                    codigoClassificacaoTributaria, ncm, nbs, data, tributos);
            case MISTO -> exigirNcmOuNbs(siglaDfe, idClassificacaoTributaria,
                    codigoClassificacaoTributaria, ncm, nbs, data, tributos);
            case SEM -> validarSemExigencia(siglaDfe, idClassificacaoTributaria,
                    codigoClassificacaoTributaria, ncm, nbs, data, tributos, modo);
        }
    }

    private void exigirNcm(SiglasDFeEnum siglaDfe, Long idClassificacaoTributaria, String codigo,
            String ncm, String nbs, LocalDate data, String tributos) {
        if (StringUtils.isNotBlank(nbs)) {
            throw new NomenclaturaIncompativelTipoDfeException("NBS", codigo, siglaDfe.getSigla());
        }
        if (StringUtils.length(ncm) != 8) {
            throw new NcmCompletoNaoInformadoException();
        }
        ncmAplicavelService.criticarNcmContraAnexo(ncm, idClassificacaoTributaria, codigo, data, tributos);
    }

    private void exigirNbs(SiglasDFeEnum siglaDfe, Long idClassificacaoTributaria, String codigo,
            String ncm, String nbs, LocalDate data, String tributos) {
        if (StringUtils.isNotBlank(ncm)) {
            throw new NomenclaturaIncompativelTipoDfeException("NCM", codigo, siglaDfe.getSigla());
        }
        if (StringUtils.length(nbs) != 9) {
            throw new NbsCompletoNaoInformadoException();
        }
        nbsAplicavelService.criticarNbsContraAnexo(nbs, idClassificacaoTributaria, codigo, data, tributos);
    }

    private void exigirNcmOuNbs(SiglasDFeEnum siglaDfe, Long idClassificacaoTributaria, String codigo,
            String ncm, String nbs, LocalDate data, String tributos) {
        // A informação simultânea de NCM e NBS é rejeitada antes, na validação
        // estrutural do item (NcmNbsSimultaneasException).
        if (StringUtils.isNotBlank(ncm)) {
            if (StringUtils.length(ncm) != 8) {
                throw new NcmCompletoNaoInformadoException();
            }
            ncmAplicavelService.criticarNcmContraAnexo(ncm, idClassificacaoTributaria, codigo, data, tributos);
        } else if (StringUtils.isNotBlank(nbs)) {
            if (StringUtils.length(nbs) != 9) {
                throw new NbsCompletoNaoInformadoException();
            }
            nbsAplicavelService.criticarNbsContraAnexo(nbs, idClassificacaoTributaria, codigo, data, tributos);
        } else {
            throw new NomenclaturaObrigatoriaNaoInformadaException(codigo, siglaDfe.getSigla());
        }
    }

    private void validarSemExigencia(SiglasDFeEnum siglaDfe, Long idClassificacaoTributaria, String codigo,
            String ncm, String nbs, LocalDate data, String tributos, ModoValidacaoNomenclatura modo) {

        final boolean ncmInformado = StringUtils.isNotBlank(ncm);
        final boolean nbsInformado = StringUtils.isNotBlank(nbs);
        if (!ncmInformado && !nbsInformado) {
            return;
        }

        if (TRIO_RIGIDO.contains(siglaDfe)) {
            // Dentro do trio a regra é sempre rígida: apenas a nomenclatura
            // natural do documento é admitida. Resultado SEM implica ausência
            // de anexo aplicável ao documento, portanto o valor admitido é
            // aceito sem crítica contra lista.
            if (siglaDfe == SiglasDFeEnum.NFSE) {
                if (ncmInformado) {
                    throw new NomenclaturaIncompativelTipoDfeException("NCM", codigo, siglaDfe.getSigla());
                }
            } else if (nbsInformado) {
                throw new NomenclaturaIncompativelTipoDfeException("NBS", codigo, siglaDfe.getSigla());
            }
            return;
        }

        if (modo == ModoValidacaoNomenclatura.RIGIDO) {
            throw new NomenclaturaNaoPermitidaTipoDfeException(codigo, siglaDfe.getSigla());
        }

        // PERMISSIVO: não bloqueia; se a classificação possui anexo, critica o
        // valor contra o anexo (exigindo o código completo); sem anexo, aceita
        // em silêncio.
        if (ncmInformado) {
            ncmAplicavelService.validarNcmAplicavel(ncm, idClassificacaoTributaria, codigo, data, tributos);
        } else {
            nbsAplicavelService.validarNbsAplicavel(nbs, idClassificacaoTributaria, codigo, data, tributos);
        }
    }

}
