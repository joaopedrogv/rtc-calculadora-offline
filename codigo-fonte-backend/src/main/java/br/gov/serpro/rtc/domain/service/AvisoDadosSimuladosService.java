/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import br.gov.serpro.rtc.api.model.input.ItemOperacaoInput;
import br.gov.serpro.rtc.api.model.input.OperacaoInput;
import br.gov.serpro.rtc.domain.model.enumeration.TipoWarningDadosSimulados;
import lombok.RequiredArgsConstructor;

/**
 * Serviço responsável por identificar avisos de uso de dados simulados a partir
 * da data do fato gerador e das características dos itens da operação.
 */
@RequiredArgsConstructor
@Service
public class AvisoDadosSimuladosService {

    public TipoWarningDadosSimulados getWarningDadosSimulados(OperacaoInput operacao) {
        LocalDate dataFatoGerador = operacao.getFatoGeradorAplicavel();
        
        boolean temComprasGovernamentais = false;
        boolean temImpostoSeletivo = false;
        boolean temAliquotasFicticias = false;
        
        for (ItemOperacaoInput item : operacao.getItens()) {
            String cst = item.getCst();
            if ("010".equals(cst) || "220".equals(cst) || "221".equals(cst)) {
                temAliquotasFicticias = true;
            }
            
            if (item.getImpostoSeletivo() != null) {
                temImpostoSeletivo = true;
            }
            
            // TODO: Implementar lógica para detectar compras governamentais
        }
        
        if (temAliquotasFicticias && dataFatoGerador.isAfter(LocalDate.of(2025, 12, 31))) {
            return TipoWarningDadosSimulados.CASO_ALIQUOTAS_FICTICIAS;
        }
        
        if (dataFatoGerador.isBefore(LocalDate.of(2027, 1, 1))) {
            return null;
        }
        
        if (temComprasGovernamentais && temImpostoSeletivo) {
            return TipoWarningDadosSimulados.CASO_COMPRAS_GOVERNAMENTAIS_IS;
        }
        
        if (temImpostoSeletivo) {
            return TipoWarningDadosSimulados.CASO_IMPOSTO_SELETIVO;
        }
        
        if (temComprasGovernamentais) {
            return TipoWarningDadosSimulados.CASO_COMPRAS_GOVERNAMENTAIS;
        }
        
        return TipoWarningDadosSimulados.CASO_GERAL;
    }

}