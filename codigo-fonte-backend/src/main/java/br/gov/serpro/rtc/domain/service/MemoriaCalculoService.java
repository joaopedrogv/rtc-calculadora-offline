/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service;

import static org.apache.commons.lang3.StringUtils.defaultString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.gov.serpro.rtc.api.model.output.CbsIbsOutput;
import br.gov.serpro.rtc.api.model.roc.ImpostoSeletivoDomain;
import br.gov.serpro.rtc.domain.model.dto.FundamentacaoClassificacaoDTO;
import br.gov.serpro.rtc.domain.model.dto.TratamentoClassificacaoDTO;
import br.gov.serpro.rtc.domain.model.entity.TratamentoTributario;
import br.gov.serpro.rtc.domain.service.token.TokenizerService;
import lombok.RequiredArgsConstructor;

/**
 * Serviço responsável por gerar a memória de cálculo textual de CBS, IBS e
 * Imposto Seletivo com base nas fórmulas e fundamentações aplicáveis.
 */
@RequiredArgsConstructor
@Service
public class MemoriaCalculoService {

    private final TokenizerService tokenizerService;
    private final FundamentacaoClassificacaoService fundamentacaoClassificacaoService;
    private final TratamentoTributarioService tratamentoService;
    
    @Value("${application.memoriacalculo.enabled}")
    private boolean memoriaCalculoHabilitada;

    public void gerarMemoriaCalculoCbsIbs(TratamentoClassificacaoDTO tratamentoClassificacao, CbsIbsOutput tributo, BigDecimal quantidade, String unidade, LocalDate data) {
        if (memoriaCalculoHabilitada) {
            // CST sem gIBSCBS (ex: imunidade 410): não há objeto para armazenar a memória de cálculo
            if (tributo == null) {
                return;
            }

            FundamentacaoClassificacaoDTO fundamentacaoClassificacao = fundamentacaoClassificacaoService
                    .buscar(tratamentoClassificacao.idClassificacaoTributaria(), data);
            
            TratamentoTributario tt = tratamentoService
                    .buscar(tratamentoClassificacao.idTratamentoTributario());
            
            String norma = fundamentacaoClassificacao.textoCurto();
            String tratamento = tt.getDescricao();
            String baseCalculo = tributo.getBaseCalculo() != null ? tributo.getBaseCalculo().toString() : "0";
            String aliquotaAdValorem = tributo.getAliquota() != null ? tributo.getAliquota().toString() : "0";
            
            String aliquotaAdRem;
            if (tributo.getGrupoMonofasia() != null && tributo.getGrupoMonofasia().getTributoMonofasico() != null &&
                    tributo.getGrupoMonofasia().getTributoMonofasico().getAliquotaAdRem() != null) {
                aliquotaAdRem = tributo.getGrupoMonofasia().getTributoMonofasico().getAliquotaAdRem().toString();
            } else {
                aliquotaAdRem = "0";
            }
            
            String percentualReducao;
            if (tributo.getGrupoReducao() != null && tributo.getGrupoReducao().getPRedAliq() != null) {
                percentualReducao = tributo.getGrupoReducao().getPRedAliq().toString();
            } else {
                percentualReducao = "0";
            }
            
            String aliquotaDesoneracao = "0";
            String montanteDesoneracao = "0";
            
            String percentualDiferimento;
            String valorDiferimento;
            if (tributo.getGrupoDiferimento() != null && tributo.getGrupoDiferimento().getPDif() != null &&
                    tributo.getGrupoDiferimento().getVDif() != null) {
                percentualDiferimento = tributo.getGrupoDiferimento().getPDif().toString();
                valorDiferimento = tributo.getGrupoDiferimento().getVDif().toString();
            } else {
                percentualDiferimento = "0";
                valorDiferimento = "0";
            }
            Map<String, String> valores = Map.ofEntries(
                    Map.entry("norma", defaultString(norma)),
                    Map.entry("tratamento", defaultString(tratamento)),
                    Map.entry("base_calculo", defaultString(baseCalculo)),
                    Map.entry("aliquota_ad_valorem", defaultString(aliquotaAdValorem)),
                    Map.entry("aliquota_ad_rem", defaultString(aliquotaAdRem)),
                    Map.entry("quantidade", defaultString(quantidade != null ? quantidade.toString() : "0")),
                    Map.entry("unidade", defaultString(unidade)),
                    
                    Map.entry("percentual_reducao", defaultString(percentualReducao)),
                    Map.entry("percentual_diferimento", defaultString(percentualDiferimento)),
                    Map.entry("valor_diferimento", defaultString(valorDiferimento)),
                    Map.entry("aliquota_desoneracao", defaultString(aliquotaDesoneracao)),
                    Map.entry("montante_desoneracao", defaultString(montanteDesoneracao))
                    );
            String texto = fundamentacaoClassificacao.memoriaCalculo();
            String memoria = StringUtils.isBlank(texto) ? "Texto de memória de cálculo não encontrado" : tokenizerService.substituirPlaceholders(texto, valores);
            tributo.setMemoriaCalculo(memoria);
        }
    }

    public void gerarMemoriaCalculoImpostoSeletivo(TratamentoClassificacaoDTO tratamentoClassificacao, ImpostoSeletivoDomain tributo, BigDecimal quantidade, String unidade, LocalDate data) {
        if (memoriaCalculoHabilitada) {
            FundamentacaoClassificacaoDTO fundamentacaoClassificacao = fundamentacaoClassificacaoService
                    .buscar(tratamentoClassificacao.idClassificacaoTributaria(), data);
            String norma = fundamentacaoClassificacao.textoCurto();
            
            TratamentoTributario tt = tratamentoService
                    .buscar(tratamentoClassificacao.idTratamentoTributario());
            String tratamento = tt.getDescricao();
            
            String baseCalculo = tributo.getVBCIS().toString();
            String aliquotaAdValorem = tributo.getPIS() != null ? tributo.getPIS().toString() : null;
            String aliquotaAdRem = tributo.getPISEspec() != null ? tributo.getPISEspec().toString() : null;
            Map<String, String> valores = Map.ofEntries(
                    Map.entry("norma", defaultString(norma)),
                    Map.entry("tratamento", defaultString(tratamento)),
                    Map.entry("base_calculo", defaultString(baseCalculo)),
                    Map.entry("aliquota_ad_valorem", defaultString(aliquotaAdValorem)),
                    Map.entry("aliquota_ad_rem", defaultString(aliquotaAdRem)),
                    Map.entry("quantidade", Objects.toString(quantidade, "")),
                    Map.entry("unidade", defaultString(unidade)));
            String texto = fundamentacaoClassificacao.memoriaCalculo();
            String memoria = StringUtils.isBlank(texto) ? "Texto de memória de cálculo não encontrado": tokenizerService.substituirPlaceholders(texto, valores);
            tributo.setMemoriaCalculo(memoria);
        }
    }

}