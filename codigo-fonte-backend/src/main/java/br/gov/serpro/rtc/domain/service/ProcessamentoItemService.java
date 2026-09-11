package br.gov.serpro.rtc.domain.service;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import br.gov.serpro.rtc.api.model.input.basecalculo.ComponentesBaseCalculoInput;
import br.gov.serpro.rtc.api.model.input.AliquotasNominaisImpostoSeletivo;
import br.gov.serpro.rtc.api.model.input.AliquotasNominaisInput;
import br.gov.serpro.rtc.api.model.input.ImpostoSeletivoInput;
import br.gov.serpro.rtc.api.model.input.ItemOperacaoInput;
import br.gov.serpro.rtc.api.model.input.OperacaoInput;
import br.gov.serpro.rtc.api.model.output.basecalculo.BaseCalculoUnificadaOutput;
import br.gov.serpro.rtc.api.model.roc.TributosDomain;
import br.gov.serpro.rtc.core.util.ArredondamentoUtils;

import br.gov.serpro.rtc.api.model.input.basecalculo.enumeration.BaseCalculoNatureza;
import br.gov.serpro.rtc.domain.model.dto.AliquotaAdRemDTO;
import br.gov.serpro.rtc.domain.model.dto.AliquotaAdValoremDTO;
import br.gov.serpro.rtc.domain.model.dto.ClassificacaoTributariaDTO;
import br.gov.serpro.rtc.domain.model.dto.TratamentoClassificacaoDTO;
import br.gov.serpro.rtc.domain.model.entity.SituacaoTributaria;
import br.gov.serpro.rtc.domain.model.entity.TipoDfe;
import br.gov.serpro.rtc.domain.model.enumeration.ModoValidacaoNomenclatura;
import br.gov.serpro.rtc.domain.model.enumeration.SiglasDFeEnum;
import br.gov.serpro.rtc.domain.model.enumeration.TributoEnum;
import br.gov.serpro.rtc.domain.service.basecalculo.BaseCalculoUnificadaService;
import br.gov.serpro.rtc.domain.service.calculotributo.CalculoTributoService;
import br.gov.serpro.rtc.domain.service.calculotributo.model.AliquotaImpostoSeletivoModel;
import br.gov.serpro.rtc.domain.service.calculotributo.model.OperacaoModel;
import br.gov.serpro.rtc.domain.service.calculotributo.model.TratamentoClassificacaoModel;
import br.gov.serpro.rtc.domain.service.exception.AliquotaImpostoSeletivoNaoInformadaException;
import br.gov.serpro.rtc.domain.service.exception.AliquotasNominaisInformadasIndevidamenteException;
import br.gov.serpro.rtc.domain.service.exception.AliquotasNominaisNaoInformadasException;
import br.gov.serpro.rtc.domain.service.exception.BaseCalculoComponentesInconsistenteException;
import br.gov.serpro.rtc.domain.service.exception.BaseCalculoInconsistenteException;
import br.gov.serpro.rtc.domain.service.exception.BaseCalculoMenorBaseCalculoImpostoSeletivoException;
import br.gov.serpro.rtc.domain.service.exception.BaseCalculoNaoInformadaException;
import br.gov.serpro.rtc.domain.service.exception.ClassificacaoTributariaNaoVinculadaSituacaoTributariaException;
import br.gov.serpro.rtc.domain.service.exception.ErroGenericoValidacaoException;
import br.gov.serpro.rtc.domain.service.exception.ImpostoSeletivoNaoAdmitidoTipoDfeException;
import br.gov.serpro.rtc.domain.service.exception.ImpostoSeletivoNaoInformadoException;
import br.gov.serpro.rtc.domain.service.exception.IncompatibilidadeSuspensaoException;
import br.gov.serpro.rtc.domain.service.exception.NbsNaoEncontradaException;
import br.gov.serpro.rtc.domain.service.exception.NcmNaoEncontradaException;
import br.gov.serpro.rtc.domain.service.exception.NcmNbsSimultaneasException;
import br.gov.serpro.rtc.domain.service.exception.TributacaoRegularNaoInformadaException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service compartilhado que encapsula a lógica de processamento de um item individual.
 * Utilizado por CalculadoraService e ObservabilidadeService.
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class ProcessamentoItemService {

    private static final int ANO_LIMITE_SUPERIOR_ALIQUOTAS_NOMINAIS = 2027;

    private final CalculoTributoService calculoTributoService;
    private final TratamentoClassificacaoService tratamentoClassificacaoService;
    private final ClassificacaoTributariaService classificacaoTributariaService;
    private final UfService ufService;
    private final AliquotaAdValoremProdutoService aliquotaAdValoremProdutoService;
    private final AliquotaAdRemProdutoService aliquotaAdRemProdutoService;
    private final AliquotaAdValoremServicoService aliquotaAdValoremServicoService;
    private final NcmAplicavelService ncmAplicavelService;
    private final NbsAplicavelService nbsAplicavelService;
    private final TipoDfeClassificacaoService tipoDfeClassificacaoService;
    private final ValidacaoNomenclaturaService validacaoNomenclaturaService;
    private final NcmService ncmService;
    private final NbsService nbsService;
    private final SituacaoTributariaService situacaoTributariaService;
    private final RedutorCompraGovernamentalService redutorCompraGovService;
    private final BaseCalculoUnificadaService baseCalculoUnificadaService;

    public TributosDomain processarItem(OperacaoInput operacao, ItemOperacaoInput item, LocalDate data) {
        return processarItem(operacao, item, data, null);
    }

    /**
     * Processa um item da operação. Quando {@code tipoDfe} é informado
     * (resolvido do {@code tpDoc} da operação), aplicam-se adicionalmente a
     * validação de vínculo cClassTrib × tipo de DFe e a exigência/permissão de
     * NCM/NBS dirigida pela nomenclatura do documento.
     */
    public TributosDomain processarItem(OperacaoInput operacao, ItemOperacaoInput item, LocalDate data,
            TipoDfe tipoDfe) {
        // INÍCIO de bloco para a plataforma
        // 1. Avaliar a criação de um método para buscar alíquotas ad rem de CBS, IBS e Imposto Seletivo,
        // para validação de unidade e quantidade em uma única etapa.
        // 2. Avaliar quantidade e unidade do item na monofasia.
        // 3. Diferenciar unidade e quantidade de item e imposto seletivo, permitindo que um seja informado sem o outro, mas validando ambos quando forem informados.
        if (item.getQuantidade() == null) item.setQuantidade(BigDecimal.ONE);
        if (item.getUnidade() == null) item.setUnidade("UN");
        // if (item.getImpostoSeletivo() != null) {
        //     if (item.getImpostoSeletivo().getQuantidade() == null) item.getImpostoSeletivo().setQuantidade(BigDecimal.ONE);
        //     if (item.getImpostoSeletivo().getUnidade() == null) item.getImpostoSeletivo().setUnidade("UN");
        // }
        // FIM de bloco para a plataforma

        var componentes = item.getGComponentesBC();
        BigDecimal baseCalculo = item.getBaseCalculo();

        if (componentes != null && componentes.possuiComponentes()) {
            validarBaseCalculoComComponentes(operacao, item, componentes);
            baseCalculo = item.getBaseCalculo();
        }

        if (componentes == null || !componentes.possuiComponentes()) {
            validarBaseCalculoLegado(item);
            baseCalculo = item.getBaseCalculo();
        }

        validarNcmNbs(item.getNcm(), item.getNbs(), data);

        // tentar resolver a suspensão aqui
        situacaoTributariaService.validarCst(item.getCst(), 2L, data);

        validarAliquotasNominais(item, data);

        TratamentoClassificacaoModel tratamentoClassificacao = obterTratamentoClassificacao(item, data, tipoDfe);

        Long codigoUf = ufService.buscar(operacao.getUf());

        OperacaoModel operacaoModel = OperacaoModel
                .builder()
                .data(data)
                .codigoMunicipio(operacao.getMunicipio())
                .codigoUf(codigoUf)
                .ncm(item.getNcm())
                .nbs(item.getNbs())
                .tpEnteGov(operacao.getTpEnteGov())
                .pRedutor(redutorCompraGovService.buscarValorRedutor(operacao.getTpEnteGov(), data))
                .item(item)
                .tratamentoClassificacao(tratamentoClassificacao)
                .build();
        return calculoTributoService.calcular(operacaoModel);
    }

    private TratamentoClassificacaoModel obterTratamentoClassificacao(ItemOperacaoInput item, LocalDate data,
            TipoDfe tipoDfe) {

        ClassificacaoTributariaDTO classificacaoTributariaCbsIbs = null;
        ClassificacaoTributariaDTO classificacaoTributariaImpostoSeletivo = null;
        TratamentoClassificacaoDTO tratamentoClassificacaoCbsIbs = null;
        TratamentoClassificacaoDTO tratamentoClassificacaoImpostoSeletivo = null;
        TratamentoClassificacaoDTO tratamentoClassificacaoCbsIbsDesoneracao = null;

        String cst = null;
        String cClassTrib = null;
        boolean temDesoneracao = false;
        String ncm = item.getNcm();
        String nbs = item.getNbs();

        classificacaoTributariaCbsIbs = classificacaoTributariaService
                .buscarClassificacaoTributariaCbsIbs(item.getCClassTrib(), data);
        validarClassificacaoTributariaCbsIbs(classificacaoTributariaCbsIbs, item.getCst(), ncm, nbs);
        validarVinculoTipoDfe(tipoDfe, classificacaoTributariaCbsIbs, data);
        tratamentoClassificacaoCbsIbs = tratamentoClassificacaoService
                .buscarTratamentoClassificacao(classificacaoTributariaCbsIbs.id(), data);

        cst = item.getCst();
        cClassTrib = item.getCClassTrib();

        if (tratamentoClassificacaoCbsIbs.inExigeGrupoDesoneracao()) {
            if (item.getTributacaoRegular() == null) {
                throw new TributacaoRegularNaoInformadaException(cClassTrib, cst);
            }
            cst = item.getTributacaoRegular().getCst();
            cClassTrib = item.getTributacaoRegular().getCClassTrib();
            tratamentoClassificacaoCbsIbsDesoneracao = tratamentoClassificacaoCbsIbs;
            classificacaoTributariaCbsIbs = classificacaoTributariaService
                    .buscarClassificacaoTributariaCbsIbs(cClassTrib, data);
            validarClassificacaoTributariaCbsIbs(classificacaoTributariaCbsIbs, cst, ncm, nbs);
            validarVinculoTipoDfe(tipoDfe, classificacaoTributariaCbsIbs, data);
            tratamentoClassificacaoCbsIbs = tratamentoClassificacaoService
                    .buscarTratamentoClassificacao(classificacaoTributariaCbsIbs.id(), data);
            if (tratamentoClassificacaoCbsIbs.inIncompativelComSuspensao()) {
                throw new IncompatibilidadeSuspensaoException(cClassTrib, cst);
            }
            temDesoneracao = true;
        } else {
            if (item.getTributacaoRegular() != null) {
                //throw new DesoneracaoInformadaIndevidamenteException(cClassTrib, cst);
                if (tipoDfe != null) {
                    // O grupo de tributação regular não participa do cálculo neste
                    // caso, mas o vínculo do seu cClassTrib com o tipo de DFe é
                    // validado quando tpDoc é informado.
                    validarVinculoTipoDfe(tipoDfe, classificacaoTributariaService
                            .buscarClassificacaoTributariaCbsIbs(item.getTributacaoRegular().getCClassTrib(), data),
                            data);
                }
            }
        }

        if (tipoDfe != null) {
            // Exigência/permissão de NCM/NBS dirigida pela nomenclatura do
            // documento. Modo PERMISSIVO fixado neste call site por ora; a troca
            // futura para RIGIDO é a alteração desta linha.
            validacaoNomenclaturaService.validar(SiglasDFeEnum.getPorSiglaNormalizada(tipoDfe.getSigla()),
                    classificacaoTributariaCbsIbs.id(), classificacaoTributariaCbsIbs.codigo(), ncm, nbs, data,
                    "CBS e IBS", ModoValidacaoNomenclatura.PERMISSIVO);
        } else {
            if (isNullOrEmpty(ncm) && isNullOrEmpty(nbs)) {
                ncmAplicavelService.validarNcmAplicavel(ncm, classificacaoTributariaCbsIbs.id(), classificacaoTributariaCbsIbs.codigo(), data, "CBS e IBS");
                nbsAplicavelService.validarNbsAplicavel(nbs, classificacaoTributariaCbsIbs.id(), classificacaoTributariaCbsIbs.codigo(), data, "CBS e IBS");
            }

            // if ncm not null and nbs null
            if (!isNullOrEmpty(ncm) && isNullOrEmpty(nbs)) {
                ncmAplicavelService.validarNcmAplicavel(ncm, classificacaoTributariaCbsIbs.id(), classificacaoTributariaCbsIbs.codigo(), data, "CBS e IBS");
            }

            // if nbs not null and ncm null
            if (!isNullOrEmpty(nbs) && isNullOrEmpty(ncm)) {
                nbsAplicavelService.validarNbsAplicavel(nbs, classificacaoTributariaCbsIbs.id(), classificacaoTributariaCbsIbs.codigo(), data, "CBS e IBS");
            }
        }

        AliquotaImpostoSeletivoModel aliquotaImpostoSeletivo = analisarAliquotaImpostoSeletivo(item, data);

        if (tipoDfe != null && !SiglasDFeEnum.impostoSeletivoAplicavel(tipoDfe.getSigla())
                && aliquotaImpostoSeletivo != null) {
            // O NCM/NBS exige Imposto Seletivo, mas o tipo de documento não está
            // na whitelist de documentos que admitem IS (hoje NF-e e NFC-e).
            // Grupo impostoSeletivo informado sem exigência segue sendo ignorado,
            // como no fluxo sem tpDoc.
            throw new ImpostoSeletivoNaoAdmitidoTipoDfeException(tipoDfe.getTipo(),
                    StringUtils.isNotBlank(ncm) ? "NCM" : "NBS",
                    StringUtils.isNotBlank(ncm) ? ncm : nbs);
        }

        // A partir de 2027, a incidência do Imposto Seletivo é determinada pela
        // existência do registro de alíquota (mesmo com valor ainda não
        // definido em lei), e não mais pelo valor resolvido.
        if (aliquotaImpostoSeletivo == null && data.getYear() >= ANO_LIMITE_SUPERIOR_ALIQUOTAS_NOMINAIS
                && existeIncidenciaImpostoSeletivo(ncm, nbs, data)) {
            if (item.getImpostoSeletivo() == null) {
                throw new ImpostoSeletivoNaoInformadoException(
                        StringUtils.isNotBlank(ncm) ? "NCM" : "NBS",
                        StringUtils.isNotBlank(ncm) ? ncm : nbs, data);
            }
            // Grupo presente, mas sem valor de alíquota resolvível: não há
            // valor no banco e o usuário não informou a alíquota nominal.
            throw new AliquotaImpostoSeletivoNaoInformadaException(
                    StringUtils.isNotBlank(ncm) ? "NCM" : "NBS",
                    StringUtils.isNotBlank(ncm) ? ncm : nbs, data);
        }

        if (aliquotaImpostoSeletivo != null) {
            if (item.getImpostoSeletivo() == null) {
                throw new ImpostoSeletivoNaoInformadoException(
                        StringUtils.isNotBlank(ncm) ? "NCM" : "NBS",
                        StringUtils.isNotBlank(ncm) ? ncm : nbs, data);
            }
            validarQuantidadeEUnidade(item, aliquotaImpostoSeletivo);
            // Validar CST do Imposto Seletivo
            situacaoTributariaService.validarCst(item.getImpostoSeletivo().getCst(), 1L, data);
            classificacaoTributariaImpostoSeletivo = classificacaoTributariaService
                    .buscarClassificacaoTributariaImpostoSeletivo(item.getImpostoSeletivo().getCClassTrib(), data);
            validarClassificacaoTributariaImpostoSeletivo(classificacaoTributariaImpostoSeletivo, item.getImpostoSeletivo().getCst());
            tratamentoClassificacaoImpostoSeletivo = tratamentoClassificacaoService
                    .buscarTratamentoClassificacao(classificacaoTributariaImpostoSeletivo.id(), data);
        } else if (aliquotaImpostoSeletivo == null && item.getImpostoSeletivo() != null) {
            //throw new ImpostoSeletivoInformadoIndevidamenteException(ncm, data);
        }

        if (aliquotaImpostoSeletivo != null) {
            validarNomenclaturaAplicavelImpostoSeletivo(ncm, nbs, classificacaoTributariaImpostoSeletivo, data);
        }

        return TratamentoClassificacaoModel
                .builder()
                .tratamentoClassificacaoCbsIbs(tratamentoClassificacaoCbsIbs)
                .tratamentoClassificacaoImpostoSeletivo(tratamentoClassificacaoImpostoSeletivo)
                .tratamentoClassificacaoCbsIbsDesoneracao(tratamentoClassificacaoCbsIbsDesoneracao)
                .aliquotaImpostoSeletivo(aliquotaImpostoSeletivo)
                .temDesoneracao(temDesoneracao)
                .build();
    }

    /**
     * Valida o vínculo da classificação tributária com o tipo de documento
     * fiscal quando o tpDoc da operação é informado. Sem tpDoc, nenhuma
     * validação de vínculo se aplica.
     */
    private void validarVinculoTipoDfe(TipoDfe tipoDfe, ClassificacaoTributariaDTO classificacaoTributaria,
            LocalDate data) {
        if (tipoDfe == null) {
            return;
        }
        tipoDfeClassificacaoService.validarVinculo(tipoDfe, classificacaoTributaria.id(),
                classificacaoTributaria.codigo(), data, "CBS e IBS");
    }

    /**
     * Validação de NCM/NBS-aplicável do Imposto Seletivo desativada: a crítica
     * do NCM/NBS contra o anexo da classificação tributária do IS não possui
     * previsão legal. O código anterior é mantido comentado para referência.
     */
    @SuppressWarnings("unused")
    private void validarNomenclaturaAplicavelImpostoSeletivo(String ncm, String nbs,
            ClassificacaoTributariaDTO classificacaoTributariaImpostoSeletivo, LocalDate data) {
        // if (isNullOrEmpty(ncm) && isNullOrEmpty(nbs)) {
        //     ncmAplicavelService.validarNcmAplicavel(ncm, classificacaoTributariaImpostoSeletivo.id(),
        //             classificacaoTributariaImpostoSeletivo.codigo(), data, "Imposto Seletivo");
        //     nbsAplicavelService.validarNbsAplicavel(nbs, classificacaoTributariaImpostoSeletivo.id(),
        //             classificacaoTributariaImpostoSeletivo.codigo(), data, "Imposto Seletivo");
        // }

        // // if ncm not null and nbs null
        // if (!isNullOrEmpty(ncm) && isNullOrEmpty(nbs)) {
        //     ncmAplicavelService.validarNcmAplicavel(ncm, classificacaoTributariaImpostoSeletivo.id(),
        //             classificacaoTributariaImpostoSeletivo.codigo(), data, "Imposto Seletivo");
        // }

        // // if nbs not null and ncm null
        // if (!isNullOrEmpty(nbs) && isNullOrEmpty(ncm)) {
        //     nbsAplicavelService.validarNbsAplicavel(nbs, classificacaoTributariaImpostoSeletivo.id(),
        //             classificacaoTributariaImpostoSeletivo.codigo(), data, "Imposto Seletivo");
        // }
    }

    /**
     * Validação da base de cálculo quando componentes são informados no grupo
     * gComponentesBC: calcula a BC a partir dos componentes e valida consistência
     * com a BC declarada ou preenche automaticamente.
     *
     * @param operacao a operação principal (contém tpDoc e fatoGeradorAplicavel)
     * @param item o item sendo validado
     * @param componentes os componentes da BC
     */
    void validarBaseCalculoComComponentes(OperacaoInput operacao,
                                        ItemOperacaoInput item,
                                        ComponentesBaseCalculoInput componentes) {
        Integer tpDoc = operacao.getTpDoc();
        BaseCalculoNatureza natureza = inferirNatureza(item);
        int ano = operacao.getFatoGeradorAplicavel().getYear();

        BaseCalculoUnificadaOutput resultado = baseCalculoUnificadaService.calcular(componentes, ano, natureza, tpDoc);

        ImpostoSeletivoInput impostoSeletivo = item.getImpostoSeletivo();

        if (impostoSeletivo != null) {
            BigDecimal bcCalculada = resultado.getBaseIS();
            if (bcCalculada.compareTo(BigDecimal.ZERO) < 0) {
                throw new ErroGenericoValidacaoException(
                    "A base de cálculo do Imposto Seletivo calculada a partir dos componentes é negativa");
            }
            BigDecimal baseDeclarada = impostoSeletivo.getBaseCalculo();
            if (baseDeclarada != null && baseDeclarada.compareTo(ArredondamentoUtils.arredondarInterno(bcCalculada)) != 0) {
                throw new BaseCalculoComponentesInconsistenteException(baseDeclarada, bcCalculada);
            }
            if (baseDeclarada == null) {
                impostoSeletivo.setBaseCalculo(bcCalculada);
            }
            return;
        }

        BigDecimal bcCalculada = resultado.getBaseRG();
        if (bcCalculada.compareTo(BigDecimal.ZERO) < 0) {
            throw new ErroGenericoValidacaoException(
                "A base de cálculo calculada a partir dos componentes é negativa");
        }
        BigDecimal baseDeclarada = item.getBaseCalculo();
        if (baseDeclarada != null && baseDeclarada.compareTo(ArredondamentoUtils.arredondarInterno(bcCalculada)) != 0) {
            throw new BaseCalculoComponentesInconsistenteException(baseDeclarada, bcCalculada);
        }
        if (baseDeclarada == null) {
            item.setBaseCalculo(bcCalculada);
        }
    }

    private BaseCalculoNatureza inferirNatureza(ItemOperacaoInput item) {
        boolean possuiNcm = StringUtils.isNotBlank(item.getNcm());
        boolean possuiNbs = StringUtils.isNotBlank(item.getNbs());
        if (possuiNcm && possuiNbs) {
            return null;
        }
        if (possuiNcm) {
            return BaseCalculoNatureza.BEM;
        }
        if (possuiNbs) {
            return BaseCalculoNatureza.SERVICO;
        }
        return null;
    }

    protected void validarBaseCalculoLegado(ItemOperacaoInput item) {
        final ImpostoSeletivoInput impostoSeletivo = item.getImpostoSeletivo();
        final BigDecimal baseCalculo = item.getBaseCalculo();

        if (impostoSeletivo == null) {
            if (baseCalculo == null) {
                throw new BaseCalculoNaoInformadaException();
            }
            return;
        }

        final BigDecimal baseCalculoImpostoSeletivo = impostoSeletivo.getBaseCalculo();
        if (baseCalculoImpostoSeletivo == null) {
            throw new ErroGenericoValidacaoException("A base de cálculo do Imposto Seletivo deve ser informada quando o grupo de Imposto Seletivo é informado");
        }

        final BigDecimal impostoInformado = impostoSeletivo.getImpostoInformado();

        if (baseCalculo == null) {
            item.setBaseCalculo(ArredondamentoUtils.arredondarInterno(
                    baseCalculoImpostoSeletivo.add(impostoSeletivo.getValorImpostoSeletivoInformado())));
            return;
        }

        if (impostoInformado != null) {
            if (baseCalculo.compareTo(baseCalculoImpostoSeletivo.add(impostoInformado)) != 0) {
                throw new BaseCalculoInconsistenteException(baseCalculo, baseCalculoImpostoSeletivo, impostoInformado);
            }
        } else if (baseCalculo.compareTo(baseCalculoImpostoSeletivo) < 0) {
            throw new BaseCalculoMenorBaseCalculoImpostoSeletivoException(baseCalculo, baseCalculoImpostoSeletivo);
        }
    }

    protected void validarQuantidadeEUnidade(ItemOperacaoInput item,
            AliquotaImpostoSeletivoModel aliquotaImpostoSeletivo) {
        if (aliquotaImpostoSeletivo.getAliquotaAdRem() != null && item.getImpostoSeletivo() != null) {
            if (item.getImpostoSeletivo().getQuantidade() == null) {
                throw new ErroGenericoValidacaoException("A quantidade do Imposto Seletivo deve ser informada para alíquota ad rem");
            }
            if (item.getImpostoSeletivo().getQuantidade().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ErroGenericoValidacaoException("A quantidade do Imposto Seletivo deve ser maior do que zero para alíquota ad rem");
            }
            if (item.getImpostoSeletivo().getUnidade() == null) {
                throw new ErroGenericoValidacaoException("A unidade de medida do Imposto Seletivo deve ser informada para alíquota ad rem");
            }

            // A unidade de medida informada não é mais criticada contra a
            // unidade vinculada à NCM; exige-se apenas o seu preenchimento
            // quando há alíquota ad rem.
        }
        if (item.getQuantidade() != null && item.getQuantidade().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ErroGenericoValidacaoException("A quantidade do item deve ser maior do que zero");
        }
    }

    /**
     * Valida o grupo aliquotasNominais conforme o ano do fato gerador:
     * proibido até 2026 (inclusive), pois a calculadora resolve as alíquotas
     * vigentes; obrigatório (cbs, ibsEstadual e ibsMunicipal) a partir de 2027
     * quando o CST do item exige o grupo IBS/CBS.
     */
    private void validarAliquotasNominais(ItemOperacaoInput item, LocalDate data) {
        if (data.getYear() < ANO_LIMITE_SUPERIOR_ALIQUOTAS_NOMINAIS) {
            if (informouAliquotasNominais(item.getAliquotasNominais())) {
                throw new AliquotasNominaisInformadasIndevidamenteException();
            }
            return;
        }

        SituacaoTributaria situacaoTributaria = situacaoTributariaService
                .consultarSituacaoTributariaPorCodigo(item.getCst(), TributoEnum.CBS.getCodigo(), data);
        boolean cstExigeGrupo = situacaoTributaria != null && Boolean.TRUE.equals(situacaoTributaria.getInGrupoIbsCbs());
        if (!cstExigeGrupo) {
            return;
        }

        AliquotasNominaisInput aliquotas = item.getAliquotasNominais();
        if (aliquotas == null || aliquotas.getCbs() == null || aliquotas.getIbsEstadual() == null
                || aliquotas.getIbsMunicipal() == null) {
            throw new AliquotasNominaisNaoInformadasException();
        }
    }

    private static boolean informouAliquotasNominais(AliquotasNominaisInput aliquotas) {
        if (aliquotas == null) {
            return false;
        }
        AliquotasNominaisImpostoSeletivo impostoSeletivo = aliquotas.getImpostoSeletivo();
        return aliquotas.getCbs() != null || aliquotas.getIbsEstadual() != null || aliquotas.getIbsMunicipal() != null
                || (impostoSeletivo != null
                        && (impostoSeletivo.getAdValorem() != null || impostoSeletivo.getAdRem() != null));
    }

    /**
     * Verifica se a NCM/NBS completa possui incidência do Imposto Seletivo na
     * data, pela existência do registro de alíquota (ad valorem ou ad rem),
     * mesmo quando o valor ainda não foi definido em lei.
     */
    private boolean existeIncidenciaImpostoSeletivo(String ncm, String nbs, LocalDate data) {
        if (StringUtils.isNotBlank(ncm) && StringUtils.length(ncm) == 8) { // NCM Completo
            return aliquotaAdValoremProdutoService.existeNcmAdValorem(ncm, TributoEnum.IS, data)
                    || aliquotaAdRemProdutoService.buscarAliquotaAdRem(ncm, TributoEnum.IS.getCodigo(), data) != null;
        }
        if (StringUtils.isNotBlank(nbs) && StringUtils.length(nbs) == 9) { // NBS Completo
            return aliquotaAdValoremServicoService.existeNbsAdValorem(nbs, TributoEnum.IS, data);
        }
        return false;
    }

    private AliquotaImpostoSeletivoModel analisarAliquotaImpostoSeletivo(
            ItemOperacaoInput item, LocalDate data) {

        final String ncm = item.getNcm();
        final String nbs = item.getNbs();

        // remover check de unidade quando é alíquota ad rem

        final AliquotasNominaisImpostoSeletivo aliquotasInformadas = obterAliquotasNominaisImpostoSeletivo(item);
        final boolean possuiAliquotaAdRemInformada = aliquotasInformadas != null
                && aliquotasInformadas.getAdRem() != null;

        if (possuiAliquotaAdRemInformada && StringUtils.isBlank(item.getImpostoSeletivo().getUnidade())) {
            throw new ErroGenericoValidacaoException(
                    "A unidade de medida do Imposto Seletivo deve ser informada quando a alíquota ad rem é informada pelo usuário");
        }

        BigDecimal aliquotaAdValorem = aliquotasInformadas != null
                ? aliquotasInformadas.getAdValorem()
                : null;
        BigDecimal aliquotaAdRem = aliquotasInformadas != null
                ? aliquotasInformadas.getAdRem()
                : null;
        String unidadeMedida = aliquotaAdRem != null ? item.getImpostoSeletivo().getUnidade() : null;

        // preparação do imposto seletivo
        // sob demanda da plataforma
        if (StringUtils.isNotBlank(ncm) && StringUtils.length(ncm) == 8) { // NCM Completo
            if (aliquotaAdValorem == null) {
                AliquotaAdValoremDTO aliquotaAdValoremBanco = aliquotaAdValoremProdutoService
                        .buscarAliquotaAdValorem(ncm, 1L, data);
                if (aliquotaAdValoremBanco != null) {
                    aliquotaAdValorem = aliquotaAdValoremBanco.valor();
                }
            }

            if (aliquotaAdRem == null) {
                AliquotaAdRemDTO aliquotaAdRemBanco = aliquotaAdRemProdutoService
                        .buscarAliquotaAdRem(ncm, 1L, data);
                if (aliquotaAdRemBanco != null) {
                    aliquotaAdRem = aliquotaAdRemBanco.valor();
                    unidadeMedida = aliquotaAdRemBanco.unidadeMedida();
                }
            }
        } else if (StringUtils.isNotBlank(nbs) && StringUtils.length(nbs) == 9) { // NBS Completo
            if (aliquotaAdValorem == null) {
                aliquotaAdValorem = aliquotaAdValoremServicoService
                        .buscarAliquotaAdValorem(nbs, 1L, null, data);
            }
        }

        // Qualquer componente encontrada, seja no request ou no banco, habilita o cálculo.
        if (aliquotaAdValorem != null || aliquotaAdRem != null) {
            return AliquotaImpostoSeletivoModel
                    .builder()
                    .aliquotaAdValorem(aliquotaAdValorem)
                    .aliquotaAdRem(aliquotaAdRem)
                    .unidadeMedida(unidadeMedida)
                    .build();
        }

        return null;
    }

    private AliquotasNominaisImpostoSeletivo obterAliquotasNominaisImpostoSeletivo(ItemOperacaoInput item) {
        if (item.getImpostoSeletivo() == null) {
            if (item.getAliquotasNominais() != null && item.getAliquotasNominais().getImpostoSeletivo() != null) {
                log.debug("Alíquotas nominais do Imposto Seletivo ignoradas porque o grupo impostoSeletivo não foi informado");
            }
            return null;
        }
        return item.getAliquotasNominais() != null
                ? item.getAliquotasNominais().getImpostoSeletivo()
                : null;
    }

    private static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    private void validarClassificacaoTributariaCbsIbs(ClassificacaoTributariaDTO classificacaoTributaria, String cst, String ncm, String nbs) {

        // sob demanda da plataforma
        if (isNullOrEmpty(ncm) && isNullOrEmpty(nbs)) {
            return;
        }

        // A coluna CLTR_NOMENCLATURA não participa mais das validações: a
        // exigência/permissão de NCM/NBS é dirigida pelos anexos
        // (NCM_APLICAVEL/NBS_APLICAVEL) e, com tpDoc informado, pela
        // NomenclaturaService.

        if (!classificacaoTributaria.cst().equals(cst)) {
            throw new ClassificacaoTributariaNaoVinculadaSituacaoTributariaException(classificacaoTributaria.codigo(), cst, "CBS e IBS");
        }
    }

    private void validarClassificacaoTributariaImpostoSeletivo(ClassificacaoTributariaDTO classificacaoTributaria, String cst) {
        if (!classificacaoTributaria.cst().equals(cst)) {
            throw new ClassificacaoTributariaNaoVinculadaSituacaoTributariaException(classificacaoTributaria.codigo(), cst, "Imposto Seletivo");
        }
    }

    private void validarNcmNbs(String ncm, String nbs, LocalDate data) {
        // sob demanda da plataforma
        // if (isNullOrEmpty(ncm) && isNullOrEmpty(nbs)) {
        //     throw new NcmNbsNaoInformadasException();
        // }
        if (!isNullOrEmpty(ncm) && !isNullOrEmpty(nbs)) {
            throw new NcmNbsSimultaneasException();
        }
        if (!isNullOrEmpty(ncm) && !ncmService.existeNcm(ncm, data)) {
            throw new NcmNaoEncontradaException(ncm, data);
        }
        if (!isNullOrEmpty(nbs) && !nbsService.existeNbs(nbs, data)) {
            throw new NbsNaoEncontradaException(nbs, data);
        }
    }

}
