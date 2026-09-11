/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import br.gov.serpro.rtc.domain.model.dto.ClassificacaoTributariaDTO;
import br.gov.serpro.rtc.domain.model.entity.TipoDfeClassificacao;
import br.gov.serpro.rtc.domain.model.enumeration.NomenclaturaResultEnum;
import br.gov.serpro.rtc.domain.model.enumeration.SiglasDFeEnum;
import br.gov.serpro.rtc.domain.repository.NcmAplicavelRepository;
import br.gov.serpro.rtc.domain.repository.NbsAplicavelRepository;
import br.gov.serpro.rtc.domain.service.exception.ClassificacaoTributariaNaoEncontradaException;
import br.gov.serpro.rtc.domain.service.exception.SiglaDFeNaoEncontradaException;
import lombok.RequiredArgsConstructor;

/**
 * Serviço responsável por determinar qual nomenclatura (NCM, NBS, MISTO ou SEM)
 * é aplicável para uma dada combinação de siglaDfe, classificação tributária e
 * data.
 */
@RequiredArgsConstructor
@Service
public class NomenclaturaService {
    
    private final ClassificacaoTributariaService classificacaoTributariaService;
    private final TipoDfeClassificacaoService tipoDfeClassificacaoService;
    private final NcmAplicavelRepository ncmAplicavelRepository;
    private final NbsAplicavelRepository nbsAplicavelRepository;

    private static final Set<String> EXCEPTION_CLASSIFICACOES = Set.of("200038");

    /**
     * Determina a nomenclatura (NCM, NBS, MISTO, SEM ou exceção) aplicável para uma
     * dada combinação de siglaDfe, classificação tributária e data.
     * 
     * @param siglaDfe a sigla do documento fiscal eletrônico (ex: "NFe", "CTe", "NFSe")
     * @param cClassTrib o código da classificação tributária
     * @param data a data de vigência para consulta
     * @return um dos valores: "SEM", "NCM", "NBS", "MISTO", "EXCECAO_NCM" ou "EXCECAO_NBS"
     * @throws ClassificacaoTributariaNaoEncontradaException se a classificação tributária não for encontrada
     * @throws SiglaDFeNaoEncontradaException se o siglaDfe for inválido
     */
    @Cacheable(cacheNames = "NomenclaturaService.determinarNomenclatura",
            key = "#siglaDfe + ':' + #cClassTrib + ':' + #data")
    public String determinarNomenclatura(String siglaDfe, String cClassTrib, LocalDate data) {
        
        ClassificacaoTributariaDTO classificacao = classificacaoTributariaService
                .buscarClassificacaoTributariaCbsIbs(cClassTrib, data);
        
        Long idClassificacao = classificacao.id();
        
        if (!siglaDfeCompativel(siglaDfe, idClassificacao, data)) {
            return NomenclaturaResultEnum.SEM.getValue();
        }
        
        boolean ncmAplicavel = ncmAplicavelRepository.tem(idClassificacao, data);
        boolean nbsAplicavel = nbsAplicavelRepository.tem(idClassificacao, data);

        return determinarResultado(siglaDfe, cClassTrib, ncmAplicavel, nbsAplicavel).getValue();
    }
    
    private boolean siglaDfeCompativel(String siglaDfe, Long idClassificacao, LocalDate data) {
        List<TipoDfeClassificacao> tiposDfe = tipoDfeClassificacaoService
                .buscar(idClassificacao, data);
        
        // Normalizar a siglaDfe fornecida (valida a existência da sigla informada)
        SiglasDFeEnum siglaNormalizada = SiglasDFeEnum.getPorSiglaNormalizada(siglaDfe);

        // Verificar se algum dos tipos de DFe aplicáveis corresponde à siglaDfe
        // fornecida, comparando as formas normalizadas (independe do formato
        // textual da sigla gravada no banco).
        return tiposDfe.stream()
                .anyMatch(t -> siglaNormalizada.corresponde(t.getTipoDfe().getSigla()));
    }
    
    private static NomenclaturaResultEnum tratarCasoExcecao(String siglaDfe) {
        SiglasDFeEnum siglaNormalizada = SiglasDFeEnum.getPorSiglaNormalizada(siglaDfe);

        if (siglaNormalizada == SiglasDFeEnum.NFE || siglaNormalizada == SiglasDFeEnum.NFCE) {
            return NomenclaturaResultEnum.EXCECAO_NCM;
        } else if (siglaNormalizada == SiglasDFeEnum.NFSE) {
            return NomenclaturaResultEnum.EXCECAO_NBS;
        }
        return NomenclaturaResultEnum.SEM;
    }

    private static NomenclaturaResultEnum determinarResultado(String siglaDfe, String cClassTrib,
                                                   boolean ncmAplicavel, boolean nbsAplicavel) {
        if (EXCEPTION_CLASSIFICACOES.contains(cClassTrib)) {
            return tratarCasoExcecao(siglaDfe);
        }

        // NFe/NFCe: apenas NCM permitido, NBS é ignorado
        if (SiglasDFeEnum.ncmAplicavel(siglaDfe) && !SiglasDFeEnum.nbsAplicavel(siglaDfe)) {
            return ncmAplicavel ? NomenclaturaResultEnum.NCM : NomenclaturaResultEnum.SEM;
        }

        // NFSe: ambos NCM e NBS permitidos (pode retornar MISTO para renting)
        if (SiglasDFeEnum.nbsAplicavel(siglaDfe)) {
            if (ncmAplicavel && nbsAplicavel) return NomenclaturaResultEnum.MISTO;
            if (ncmAplicavel)                 return NomenclaturaResultEnum.NCM;
            if (nbsAplicavel)                 return NomenclaturaResultEnum.NBS;
            return NomenclaturaResultEnum.SEM;
        }

        // BPe, CTe, outros: nenhuma nomenclatura permitida
        return NomenclaturaResultEnum.SEM;
    }
}
