/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service.calculotributo;

import static br.gov.serpro.rtc.domain.model.enumeration.TributoEnum.CBS;
import static br.gov.serpro.rtc.domain.model.enumeration.TributoEnum.IBS_ESTADUAL;
import static br.gov.serpro.rtc.domain.model.enumeration.TributoEnum.IBS_MUNICIPAL;
import static java.math.BigDecimal.ZERO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.gov.serpro.rtc.api.model.input.ItemOperacaoInput;
import br.gov.serpro.rtc.api.model.input.calculadora.enumeration.TipoEnteGovernamental;
import br.gov.serpro.rtc.api.model.output.CbsIbsOutput;
import br.gov.serpro.rtc.api.model.roc.AjusteCompetenciaDomain;
import br.gov.serpro.rtc.api.model.roc.CBSDomain;
import br.gov.serpro.rtc.api.model.roc.CreditoPresumidoIBSZFMDomain;
import br.gov.serpro.rtc.api.model.roc.CreditoPresumidoOperacaoDomain;
import br.gov.serpro.rtc.api.model.roc.DevolucaoTributosDomain;
import br.gov.serpro.rtc.api.model.roc.EstornoCreditoDomain;
import br.gov.serpro.rtc.api.model.roc.GrupoIBSCBSDomain;
import br.gov.serpro.rtc.api.model.roc.IBSCBSDomain;
import br.gov.serpro.rtc.api.model.roc.IBSMunDomain;
import br.gov.serpro.rtc.api.model.roc.IBSUFDomain;
import br.gov.serpro.rtc.api.model.roc.ImpostoSeletivoDomain;
import br.gov.serpro.rtc.api.model.roc.MonofasiaDiferimentoDomain;
import br.gov.serpro.rtc.api.model.roc.MonofasiaDomain;
import br.gov.serpro.rtc.api.model.roc.MonofasiaPadraoDomain;
import br.gov.serpro.rtc.api.model.roc.MonofasiaRetencaoDomain;
import br.gov.serpro.rtc.api.model.roc.MonofasiaRetidoAnteriormenteDomain;
import br.gov.serpro.rtc.api.model.roc.ReducaoAliquotaDomain;
import br.gov.serpro.rtc.api.model.roc.TransferenciaCreditoDomain;
import br.gov.serpro.rtc.api.model.roc.TributacaoCompraGovernamentalDomain;
import br.gov.serpro.rtc.api.model.roc.TributacaoRegularDomain;
import br.gov.serpro.rtc.api.model.roc.TributacaoRegularDomain.TributacaoRegularDomainBuilder;
import br.gov.serpro.rtc.api.model.roc.TributosDomain;
import br.gov.serpro.rtc.domain.model.dto.TratamentoClassificacaoDTO;
import br.gov.serpro.rtc.domain.model.enumeration.TributoEnum;
import br.gov.serpro.rtc.domain.service.MemoriaCalculoService;
import br.gov.serpro.rtc.domain.service.TransferenciaCBSService;
import br.gov.serpro.rtc.domain.service.calculotributo.model.AliquotaImpostoSeletivoModel;
import br.gov.serpro.rtc.domain.service.calculotributo.model.OperacaoModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Serviço responsável por orquestrar o cálculo de CBS, IBS e Imposto Seletivo
 * de um item, inclusive a geração da memória de cálculo.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CalculoTributoService {

	private final CalculoCbsIbsService calculoCbsIbsService;
	private final CalculoImpostoSeletivoService calculoImpostoSeletivoService;
	private final MemoriaCalculoService memoriaCalculoService;
    private final TransferenciaCBSService transferenciaService;

    @Value("${application.ibs.enabled}")
    private boolean calculoIbsHabilitado;

	public TributosDomain calcular(OperacaoModel operacao) {
		ItemOperacaoInput item = operacao.getItem();
		LocalDate data = operacao.getData();
		String nbs = item.getNbs();

		TratamentoClassificacaoDTO tratamentoClassificacaoCbsIbs = operacao
				.getTratamentoClassificacao().getTratamentoClassificacaoCbsIbs();
		TratamentoClassificacaoDTO tratamentoClassificacaoImpostoSeletivo = operacao
				.getTratamentoClassificacao().getTratamentoClassificacaoImpostoSeletivo();

		Boolean temDesoneracao = operacao.getTratamentoClassificacao().getTemDesoneracao();

		TratamentoClassificacaoDTO tratamentoClassificacaoCbsIbsDesoneracao = null;
		if (temDesoneracao) {
			tratamentoClassificacaoCbsIbsDesoneracao = operacao
				.getTratamentoClassificacao().getTratamentoClassificacaoCbsIbsDesoneracao();
		}
		
		TratamentoClassificacaoDTO tratamentoClassificacao = temDesoneracao ? tratamentoClassificacaoCbsIbsDesoneracao : tratamentoClassificacaoCbsIbs;

		ImpostoSeletivoDomain impostoSeletivo = null;
        if (tratamentoClassificacaoImpostoSeletivo != null) {
            AliquotaImpostoSeletivoModel aliquotaImpostoSeletivo = operacao.getTratamentoClassificacao()
                    .getAliquotaImpostoSeletivo();
            impostoSeletivo = calculoImpostoSeletivoService.calcularImpostoSeletivo(1L, item,
                    tratamentoClassificacaoImpostoSeletivo, aliquotaImpostoSeletivo, data);
            memoriaCalculoService.gerarMemoriaCalculoImpostoSeletivo(tratamentoClassificacaoImpostoSeletivo,
                    impostoSeletivo, item.getImpostoSeletivo().getQuantidade(), item.getImpostoSeletivo().getUnidade(),
                    data);
        }

        CbsIbsOutput cbs = null;
        CbsIbsOutput ibsEstadual = null;
        CbsIbsOutput ibsMunicipal = null;
        if (tratamentoClassificacaoCbsIbs != null) {
            final BigDecimal impostoSeletivoCalculado = impostoSeletivo != null
                    ? impostoSeletivo.getValorImpostoSeletivo()
                    : ZERO;
            
            log.debug("Iniciando cálculo assíncrono de CBS/IBS...");
            final CompletableFuture<CbsIbsOutput> cbsFuture = calcularCbsIbsAsync(CBS, null, null, item,
                    tratamentoClassificacaoCbsIbs, impostoSeletivoCalculado, temDesoneracao, data,
                    operacao.getTpEnteGov(), operacao.getPRedutor(), tratamentoClassificacao, true);

            final var calcularIBS = calculoIbsHabilitado || nbs != null;

            final CompletableFuture<CbsIbsOutput> ibsEstadualFuture = calcularCbsIbsAsync(IBS_ESTADUAL,
                    operacao.getCodigoUf(), null, item, tratamentoClassificacaoCbsIbs, impostoSeletivoCalculado,
                    temDesoneracao, data, operacao.getTpEnteGov(), operacao.getPRedutor(), tratamentoClassificacao, calcularIBS);

            final CompletableFuture<CbsIbsOutput> ibsMunicipalFuture = calcularCbsIbsAsync(IBS_MUNICIPAL,
                    null, operacao.getCodigoMunicipio(), item, tratamentoClassificacaoCbsIbs, impostoSeletivoCalculado,
                    temDesoneracao, data, operacao.getTpEnteGov(), operacao.getPRedutor(), tratamentoClassificacao, calcularIBS);

            // Sincroniza e obtém os resultados
            try {
                log.debug("Aguardando cálculo assíncrono de CBS/IBS...");
                CompletableFuture
                    .allOf(cbsFuture, ibsEstadualFuture, ibsMunicipalFuture)
                    .join(); // aguarda a conclusão de todas as tarefas
                log.debug("Cálculo assíncrono de CBS/IBS concluído.");
                cbs = cbsFuture.get();
                ibsEstadual = ibsEstadualFuture.get();
                ibsMunicipal = ibsMunicipalFuture.get();
            } catch (InterruptedException | ExecutionException | CompletionException e) {
                if (e instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
                // Se a causa for uma RuntimeException, relance-a diretamente
                if (e.getCause() instanceof RuntimeException runtimeEx) {
                    throw runtimeEx;
                }
                // Senão, relance como RuntimeException padrão
                throw new RuntimeException("Erro ao calcular CBS/IBS", e.getCause());
            }

        }

        // Regra: Suspensão de CBS e IBS não suspende o Imposto Seletivo automaticamente.
        // if (temDesoneracao && impostoSeletivo != null) {
        //     impostoSeletivo.setVIS(BigDecimal.ZERO);
        // }
		
		return TributosDomain
				.builder()
				.IS(impostoSeletivo)
                .IBSCBS(getIBSCBS(item, cbs, ibsEstadual, ibsMunicipal, operacao.getTpEnteGov(), data))
				.build();
	}
	
	/**
	 * Cálculo assíncrono de CBS/IBS
	 * @param tributo
	 * @param codigoUf
	 * @param codigoMunicipio
	 * @param item
	 * @param tratamentoClassificacaoCbsIbs
	 * @param impostoSeletivoCalculado
	 * @param temDesoneracao
	 * @param data
	 * @param tratamentoClassificacao
	 * @param calcularTributo
	 * @return
	 */
	private CompletableFuture<CbsIbsOutput> calcularCbsIbsAsync(
	        TributoEnum tributo,
	        Long codigoUf,
	        Long codigoMunicipio,
	        ItemOperacaoInput item,
	        TratamentoClassificacaoDTO tratamentoClassificacaoCbsIbs,
	        BigDecimal impostoSeletivoCalculado,
	        Boolean temDesoneracao,
	        LocalDate data,
	        TipoEnteGovernamental tpEnteGov,
	        BigDecimal pRedutor,
	        TratamentoClassificacaoDTO tratamentoClassificacao,
	        boolean calcularTributo
	) {
	    return CompletableFuture.supplyAsync(() -> {
            final var descricao = tributo.getNome() + "(" + item.getNumero() + ")";
	        if (!calcularTributo) {
	            log.debug("> {} - Cálculo desabilitado", descricao);
	            return null;
	        }
	        log.debug("> {} - Iniciando cálculo assíncrono", descricao);
	        CbsIbsOutput output = calculoCbsIbsService.calcularCbsIbs(
	                tributo, codigoUf, codigoMunicipio, item,
	                tratamentoClassificacaoCbsIbs, impostoSeletivoCalculado, temDesoneracao, data, tpEnteGov, pRedutor);
	        log.debug("> {} - Cálculo assíncrono finalizado", descricao);
	        
	        log.debug(">> {} - Iniciando memória de cálculo", descricao);
	        memoriaCalculoService.gerarMemoriaCalculoCbsIbs(
	                tratamentoClassificacao, output, item.getQuantidade(), item.getUnidade(), data);
	        log.debug(">> {} - Memória de cálculo finalizada", descricao);
	        return output;
	    });
	}

    private IBSCBSDomain getIBSCBS(ItemOperacaoInput item, CbsIbsOutput cbs, CbsIbsOutput ibsEstadual,
            CbsIbsOutput ibsMunicipal, TipoEnteGovernamental tpEnteGov, LocalDate dataFatoGerador) {
        final var monofasia = getMonofasia(cbs, ibsEstadual, ibsMunicipal);
        return IBSCBSDomain.builder()
                .CST(item.getCst())
                .cClassTrib(item.getCClassTrib())
                .gIBSCBS(monofasia == null ? getGIBSCBS(cbs, ibsEstadual, ibsMunicipal, tpEnteGov, dataFatoGerador) : null) // FIXME somente se não for monofásico
                .gIBSCBSMono(monofasia)
                .gTransfCred(getTransferenciaCredito(cbs, ibsEstadual, ibsMunicipal))
                .gAjusteCompet(getAjusteCompetencia(cbs, ibsEstadual, ibsMunicipal))
                .gEstornoCred(getEstornoCredito(cbs, ibsEstadual, ibsMunicipal))
                .gCredPresOper(getCreditoPresumidoOperacao(cbs, ibsEstadual, ibsMunicipal))
                .gCredPresIBSZFM(getCreditoPresumidoIBSZFM(ibsEstadual, ibsMunicipal))
                .build();
    }

    private GrupoIBSCBSDomain getGIBSCBS(CbsIbsOutput cbs, CbsIbsOutput ibsEstadual, CbsIbsOutput ibsMunicipal, TipoEnteGovernamental tpEnteGov, LocalDate dataFatoGerador) {
        
        // CST sem gIBSCBS (ex: imunidade): nenhum tributo foi calculado, não gera grupo.
        // Analisar a possibilidade de usar OR.
        if (cbs == null && ibsEstadual == null && ibsMunicipal == null) {
            return null;
        }

        final var pTransferenciaCBS = transferenciaService.getPercentualTransferencia(dataFatoGerador);
        final var compraGovernamental = getCompraGovernamental(cbs, ibsEstadual, ibsMunicipal);
        final var compraGovernamentalEfetiva = compraGovernamental == null ? null : compraGovernamental.getValoresEfetivos(tpEnteGov, pTransferenciaCBS, dataFatoGerador);
        
		/*
		 * Tratar o impacto das compras governamentais efetivas (com rateio entre os
		 * entes já realizado) nos valores do grupo IBS/CBS. Ajustar os dados dos
		 * CbsIbsOutput originais antes de criar os grupos com base nos seus valores 
		 */
        /**
        	Suspensão com compra gov
        	------------------------ 
        	Grupo Principal 	 - tem que ser zero, pois está suspenso
        	Grupo gRed 			 - mesmo não permitido (suspensão) deve ser criado (compra governamental) com o percentual de redução igual a zero (suspensão) e alíquota efetiva igual a zero (suspensão, mesmo que seja compra gov)
        	Grupo gTribRegular   - tem que apresentar o cálculo sem suspensão e com o desconto (se for o caso) e o rateio da compra gov
        	Grupo gTribCompraGov - tem que apresentar o cálculo com suspensão, tudo zerado
         */ 
        
        trataImpactoCompraGovCBS(cbs, compraGovernamentalEfetiva);
        trataImpactoCompraGovIBSUF(ibsEstadual, compraGovernamentalEfetiva);
        trataImpactoCompraGovIBSMun(ibsMunicipal, compraGovernamentalEfetiva);
        
        final var vBC = getVBC(ibsMunicipal, getVBC(ibsEstadual, getVBC(cbs, null)));
        final var gIBSUF = getIBSUF(ibsEstadual, compraGovernamentalEfetiva);
        final var gIBSMun = getIBSMun(ibsMunicipal, compraGovernamentalEfetiva);
        final var vIBS = getVIbs(gIBSUF, gIBSMun);
        final var gCBS = getCbs(cbs, compraGovernamentalEfetiva);
        final var tributacaoRegular = getTributacaoRegular(cbs, ibsEstadual, ibsMunicipal);
        
        if (tributacaoRegular != null && compraGovernamental != null) {
        	// suspensao com compra gov - zera valores
			compraGovernamental.zerarValores();
		}
        		
		return GrupoIBSCBSDomain.builder()
		        .vBC(vBC)
                .gIBSUF(gIBSUF)
                .gIBSMun(gIBSMun)
                .vIBS(vIBS)
		        .gCBS(gCBS)
		        .gTribRegular(tributacaoRegular)
		        .gTribCompraGov(compraGovernamental)
		        .build();
    }
    
	private static void trataImpactoCompraGovCBS(CbsIbsOutput cbs, TributacaoCompraGovernamentalDomain compraGov) {
		if (cbs != null && compraGov != null) {
			var grupoReducaoCbs = cbs.getGrupoReducao();
			if (grupoReducaoCbs == null) {
				grupoReducaoCbs = ReducaoAliquotaDomain.builder().pRedAliq(ZERO).build();
				cbs.setGrupoReducao(grupoReducaoCbs);
			}
			
			final var tributacaoRegular = cbs.getTributacaoRegular();
			if (tributacaoRegular != null) {
				// possui suspensao
				grupoReducaoCbs.setPRedAliq(ZERO);
				grupoReducaoCbs.setPAliqEfet(ZERO);
				cbs.setTributoDevido(ZERO);
				tributacaoRegular.setAliquotaEfetiva(compraGov.getPAliqCBS());
				tributacaoRegular.setTributoDevido(compraGov.getVTribCBS());
			} else {
				grupoReducaoCbs.setPAliqEfet(compraGov.getPAliqCBS());
				cbs.setTributoDevido(compraGov.getVTribCBS());
			}
		}
	}

	private static void trataImpactoCompraGovIBSUF(CbsIbsOutput ibsUF, TributacaoCompraGovernamentalDomain compraGov) {
		if (ibsUF != null && compraGov != null) {
			var grupoReducaoIbsUF = ibsUF.getGrupoReducao();
			if (grupoReducaoIbsUF == null) {
				grupoReducaoIbsUF = ReducaoAliquotaDomain.builder().pRedAliq(ZERO).build();
				ibsUF.setGrupoReducao(grupoReducaoIbsUF);
			}
			
			final var tributacaoRegular = ibsUF.getTributacaoRegular();
			if (tributacaoRegular != null) {
				// possui suspensao
				grupoReducaoIbsUF.setPRedAliq(ZERO);
				grupoReducaoIbsUF.setPAliqEfet(ZERO);
				ibsUF.setTributoDevido(ZERO);
				tributacaoRegular.setAliquotaEfetiva(compraGov.getPAliqIBSUF());
				tributacaoRegular.setTributoDevido(compraGov.getVTribIBSUF());
			} else {
				grupoReducaoIbsUF.setPAliqEfet(compraGov.getPAliqIBSUF());
				ibsUF.setTributoDevido(compraGov.getVTribIBSUF());
			}
		}
	}

	private static void trataImpactoCompraGovIBSMun(CbsIbsOutput ibsMun,
			TributacaoCompraGovernamentalDomain compraGov) {
		if (ibsMun != null && compraGov != null) {
			var grupoReducaoIbsMun = ibsMun.getGrupoReducao();
			if (grupoReducaoIbsMun == null) {
				grupoReducaoIbsMun = ReducaoAliquotaDomain.builder().pRedAliq(ZERO).build();
				ibsMun.setGrupoReducao(grupoReducaoIbsMun);
			}
			
			final var tributacaoRegular = ibsMun.getTributacaoRegular();
			if (tributacaoRegular != null) {
				// possui suspensao
				grupoReducaoIbsMun.setPRedAliq(ZERO);
				grupoReducaoIbsMun.setPAliqEfet(ZERO);
				ibsMun.setTributoDevido(ZERO);
				tributacaoRegular.setAliquotaEfetiva(compraGov.getPAliqIBSMun());
				tributacaoRegular.setTributoDevido(compraGov.getVTribIBSMun());
			} else {
				grupoReducaoIbsMun.setPAliqEfet(compraGov.getPAliqIBSMun());
				ibsMun.setTributoDevido(compraGov.getVTribIBSMun());
			}
		}
	}

    private static BigDecimal getVIbs(IBSUFDomain ibsUF, IBSMunDomain ibsMun) {
        final var vIbsUF = ibsUF != null ? ibsUF.getVIBSUF() : ZERO;
        final var vIbsMun = ibsMun != null ? ibsMun.getVIBSMun() : ZERO;
        return vIbsUF.add(vIbsMun);
    }
	
    private static BigDecimal getVBC(CbsIbsOutput c, BigDecimal valor) {
        if (valor != null) {
            return valor;
        }
        if (c != null) {
            return c.getBaseCalculo();
        }
        return null;
    }
	
    private static IBSUFDomain getIBSUF(CbsIbsOutput ibsEstadual, TributacaoCompraGovernamentalDomain compraGov) {
        if (ibsEstadual == null) {
            return null;
        }
        final IBSUFDomain ibsUF = new IBSUFDomain();
        ibsUF.setPIBSUF(ibsEstadual.getAliquota());
        ibsUF.setGDif(ibsEstadual.getGrupoDiferimento());
        ibsUF.setGDevTrib(getDevolucaoTributos(ibsEstadual));
        ibsUF.setGRed(ibsEstadual.getGrupoReducao());
        ibsUF.setVIBSUF(ibsEstadual.getTributoDevido());
        ibsUF.setMemoriaCalculo(ibsEstadual.getMemoriaCalculo());
        return ibsUF;
    }
	
	private static IBSMunDomain getIBSMun(CbsIbsOutput ibsMunicipal, TributacaoCompraGovernamentalDomain compraGov) {
	    if (ibsMunicipal == null) {
            return null;
        }
        final IBSMunDomain ibsMun = new IBSMunDomain();
        ibsMun.setPIBSMun(ibsMunicipal.getAliquota());
        ibsMun.setGDif(ibsMunicipal.getGrupoDiferimento());
        ibsMun.setGDevTrib(getDevolucaoTributos(ibsMunicipal));
        ibsMun.setGRed(ibsMunicipal.getGrupoReducao());
        ibsMun.setVIBSMun(ibsMunicipal.getTributoDevido());
        ibsMun.setMemoriaCalculo(ibsMunicipal.getMemoriaCalculo());
        return ibsMun;
	}

    private static CBSDomain getCbs(CbsIbsOutput cbsOut, TributacaoCompraGovernamentalDomain compraGov) {
        if (cbsOut == null) {
            return null;
        }
        final CBSDomain cbs = new CBSDomain();
        cbs.setPCBS(cbsOut.getAliquota());
        cbs.setGDif(cbsOut.getGrupoDiferimento());
        cbs.setGDevTrib(getDevolucaoTributos(cbsOut));
        cbs.setGRed(cbsOut.getGrupoReducao());
        cbs.setVCBS(cbsOut.getTributoDevido());
        cbs.setMemoriaCalculo(cbsOut.getMemoriaCalculo());
        return cbs;
    }
    
    // TODO: Implementar devolução de tributos
    private static DevolucaoTributosDomain getDevolucaoTributos(CbsIbsOutput d) {
        return null;
    }
    
    private static TributacaoRegularDomain getTributacaoRegular(CbsIbsOutput cbs, CbsIbsOutput ibsEstadual,
            CbsIbsOutput ibsMunicipal) {
        var builder = getTributacaoRegularCBS(cbs, null);
        builder = getTributacaoRegularIBSUF(ibsEstadual, builder);
        builder = getTributacaoRegularIBSMun(ibsMunicipal, builder);
        if (builder != null) {
            return builder.build();
        }
        return null;
    }
    
    private static TributacaoRegularDomainBuilder getTributacaoRegularIBSUF(CbsIbsOutput ibsUF, TributacaoRegularDomainBuilder builder) {
        if (ibsUF != null && ibsUF.getTributacaoRegular() != null) {
            var tr = ibsUF.getTributacaoRegular();
            if (builder == null) {
                builder = TributacaoRegularDomain.builder();
                builder.CSTReg(tr.getCst())
                    .cClassTribReg(tr.getCClassTrib());
            }
            builder.pAliqEfetRegIBSUF(tr.getAliquotaEfetiva())
                   .vTribRegIBSUF(tr.getTributoDevido());
            return builder;
        }
        return null;
    }
    
    private static TributacaoRegularDomainBuilder getTributacaoRegularIBSMun(CbsIbsOutput ibsMun, TributacaoRegularDomainBuilder builder) {
        if (ibsMun != null && ibsMun.getTributacaoRegular() != null) {
            var tr = ibsMun.getTributacaoRegular();
            if (builder == null) {
                builder = TributacaoRegularDomain.builder();
                builder.CSTReg(tr.getCst())
                    .cClassTribReg(tr.getCClassTrib());
            }
            builder.pAliqEfetRegIBSMun(tr.getAliquotaEfetiva())
                   .vTribRegIBSMun(tr.getTributoDevido());
            return builder;
        }
        return null;
    }
    
    private static TributacaoRegularDomainBuilder getTributacaoRegularCBS(CbsIbsOutput cbs, TributacaoRegularDomainBuilder builder) {
        if (cbs != null && cbs.getTributacaoRegular() != null) {
            var tr = cbs.getTributacaoRegular();
            if (builder == null) {
                builder = TributacaoRegularDomain.builder();
                builder.CSTReg(tr.getCst())
                    .cClassTribReg(tr.getCClassTrib());
            }
            builder.pAliqEfetRegCBS(tr.getAliquotaEfetiva())
                   .vTribRegCBS(tr.getTributoDevido());
            return builder;
        }
        return null;
    }
    
    private static TributacaoCompraGovernamentalDomain getCompraGovernamental(CbsIbsOutput cbs, 
            CbsIbsOutput ibsEstadual, CbsIbsOutput ibsMunicipal) {
        
        final var possuiCompraGovCBS = cbs.possuiCompraGov();
        final var possuiCompraGovIBSUF = ibsEstadual.possuiCompraGov();
        final var possuiCompraGovIBSMun = ibsMunicipal.possuiCompraGov();
        
        boolean possuiCompraGov = possuiCompraGovCBS && possuiCompraGovIBSUF && possuiCompraGovIBSMun;
        if (possuiCompraGov) {
            // aqui, fazer o merge das compras governamentais de cada tributo em um só
            final var compraGovCBS = cbs.getCompraGovernamental();
            final var compraGovIBSUF = ibsEstadual.getCompraGovernamental();
            final var compraGovIBSMun = ibsMunicipal.getCompraGovernamental();
            
            return TributacaoCompraGovernamentalDomain.builder()
                    .pAliqCBS(compraGovCBS.getPAliqCBS())
                    .vTribCBS(compraGovCBS.getVTribCBS())
                    .pAliqIBSUF(compraGovIBSUF.getPAliqIBSUF())
                    .vTribIBSUF(compraGovIBSUF.getVTribIBSUF())
                    .pAliqIBSMun(compraGovIBSMun.getPAliqIBSMun())
                    .vTribIBSMun(compraGovIBSMun.getVTribIBSMun())
                    .build();
        } else {
            boolean possuiAlgumDiferente = possuiCompraGovCBS || possuiCompraGovIBSUF || possuiCompraGovIBSMun;
            if (possuiAlgumDiferente) {
                throw new RuntimeException("Erro ao consolidar valores de compras governamentais. Algum dos tributos não calculou compra governamental.");
            }
        }
        return null;        
    }
    
    // TODO - Implementar transferência de crédito
    private static TransferenciaCreditoDomain getTransferenciaCredito(CbsIbsOutput cbs, 
            CbsIbsOutput ibsEstadual, CbsIbsOutput ibsMunicipal) {
        return null;
    }
    
    // TODO - Implementar ajuste de competência
    private static AjusteCompetenciaDomain getAjusteCompetencia(CbsIbsOutput cbs, CbsIbsOutput ibsEstadual, 
            CbsIbsOutput ibsMunicipal) {
        return null;
    }
    
    // TODO - Implementar estorno de crédito
    private static EstornoCreditoDomain getEstornoCredito(CbsIbsOutput cbs, CbsIbsOutput ibsEstadual, 
            CbsIbsOutput ibsMunicipal) {
        return null;
    }
    
    // TODO - Implementar estorno de crédito
    private static CreditoPresumidoOperacaoDomain getCreditoPresumidoOperacao(CbsIbsOutput cbs, CbsIbsOutput ibsEstadual, 
            CbsIbsOutput ibsMunicipal) {
        return null;
    }
    
    // TODO - Implementar crédito presumido para IBS Zona Franca de Manaus
    private static CreditoPresumidoIBSZFMDomain getCreditoPresumidoIBSZFM(CbsIbsOutput ibsEstadual, 
            CbsIbsOutput ibsMunicipal) {
        return null;
    }
    
    private static MonofasiaDomain getMonofasia(CbsIbsOutput cbs, CbsIbsOutput ibsEstadual, CbsIbsOutput ibsMunicipal) {
        var monoPadrao = MonofasiaPadraoDomain.create(cbs, ibsEstadual, ibsMunicipal);
        var monoReten = MonofasiaRetencaoDomain.create(cbs, ibsEstadual, ibsMunicipal);
        var monoRet = MonofasiaRetidoAnteriormenteDomain.create(cbs, ibsEstadual, ibsMunicipal);
        var monoDif = MonofasiaDiferimentoDomain.create(cbs, ibsEstadual, ibsMunicipal);
        if (monoPadrao != null || monoReten != null || monoRet != null
                || monoDif != null) {
            /** 
             * Cálculo dos totalizadores conforme NT da NFe 1.30:
             * vTotIBSMonoItem = vIBSMono + vIBSMonoReten - vIBSMonoDif
             * vTotCBSMonoItem = vCBSMono + vCBSMonoReten - vCBSMonoDif
             */
            var monofasiaBuilder = MonofasiaDomain.builder()
                    .gMonoPadrao(monoPadrao)
                    .gMonoReten(monoReten)
                    .gMonoRet(monoRet)
                    .gMonoDif(monoDif);
            
            // Calcular totalizadores
            var vIBSMono = monoPadrao != null ? monoPadrao.getVIBSMono() : ZERO;
            var vCBSMono = monoPadrao != null ? monoPadrao.getVCBSMono() : ZERO;
            var vIBSMonoReten = monoReten != null ? monoReten.getVIBSMonoReten() : ZERO;
            var vCBSMonoReten = monoReten != null ? monoReten.getVCBSMonoReten() : ZERO;
            var vIBSMonoDif = monoDif != null ? monoDif.getVIBSMonoDif() : ZERO;
            var vCBSMonoDif = monoDif != null ? monoDif.getVCBSMonoDif() : ZERO;
            
            var vTotIBSMonoItem = vIBSMono.add(vIBSMonoReten).subtract(vIBSMonoDif);
            var vTotCBSMonoItem = vCBSMono.add(vCBSMonoReten).subtract(vCBSMonoDif);
            
            return monofasiaBuilder
                    .vTotIBSMonoItem(vTotIBSMonoItem)
                    .vTotCBSMonoItem(vTotCBSMonoItem)
                    .build();
        }
        return null;
    }

}