/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service.calculotributo;

import static br.gov.serpro.rtc.core.util.ArredondamentoUtils.CEM;
import static br.gov.serpro.rtc.core.util.ArredondamentoUtils.PRECISAO_INTERNA;

import static br.gov.serpro.rtc.core.util.ArredondamentoUtils.arredondarInterno;
import static br.gov.serpro.rtc.core.util.ArredondamentoUtils.dividir;
import static br.gov.serpro.rtc.core.util.ArredondamentoUtils.dividirPorCem;
import static br.gov.serpro.rtc.core.util.ArredondamentoUtils.multiplicar;
import static br.gov.serpro.rtc.domain.service.calculotributo.domain.VariavelExpressao.AJUSTE;
import static br.gov.serpro.rtc.domain.service.calculotributo.domain.VariavelExpressao.ALIQUOTA;
import static br.gov.serpro.rtc.domain.service.calculotributo.domain.VariavelExpressao.ALIQUOTA_AD_REM;
import static br.gov.serpro.rtc.domain.service.calculotributo.domain.VariavelExpressao.ALIQUOTA_AD_REM_PRINCIPAL;
import static br.gov.serpro.rtc.domain.service.calculotributo.domain.VariavelExpressao.ALIQUOTA_AD_REM_SECUNDARIA;
import static br.gov.serpro.rtc.domain.service.calculotributo.domain.VariavelExpressao.ALIQUOTA_AD_VALOREM;
import static br.gov.serpro.rtc.domain.service.calculotributo.domain.VariavelExpressao.ALIQUOTA_EFETIVA;
import static br.gov.serpro.rtc.domain.service.calculotributo.domain.VariavelExpressao.ALIQUOTA_PADRAO_OU_REFERENCIA;
import static br.gov.serpro.rtc.domain.service.calculotributo.domain.VariavelExpressao.ALIQUOTA_REFERENCIA;
import static br.gov.serpro.rtc.domain.service.calculotributo.domain.VariavelExpressao.BASE_CALCULO;
import static br.gov.serpro.rtc.domain.service.calculotributo.domain.VariavelExpressao.BASE_CALCULO_INFORMADA;
import static br.gov.serpro.rtc.domain.service.calculotributo.domain.VariavelExpressao.IMPOSTO_SELETIVO_CALCULADO;
import static br.gov.serpro.rtc.domain.service.calculotributo.domain.VariavelExpressao.IMPOSTO_SELETIVO_INFORMADO;
import static br.gov.serpro.rtc.domain.service.calculotributo.domain.VariavelExpressao.PERCENTUAL_BIOCOMBUSTIVEL;
import static br.gov.serpro.rtc.domain.service.calculotributo.domain.VariavelExpressao.PERCENTUAL_DIFERIMENTO;
import static br.gov.serpro.rtc.domain.service.calculotributo.domain.VariavelExpressao.PERCENTUAL_REDUCAO;
import static br.gov.serpro.rtc.domain.service.calculotributo.domain.VariavelExpressao.PERCENTUAL_REDUCAO_COMPRA_GOV;
import static br.gov.serpro.rtc.domain.service.calculotributo.domain.VariavelExpressao.QUANTIDADE;
import static br.gov.serpro.rtc.domain.service.calculotributo.domain.VariavelExpressao.REDUTOR;
import static br.gov.serpro.rtc.domain.service.calculotributo.domain.VariavelExpressao.TRIBUTO_CALCULADO;
import static br.gov.serpro.rtc.domain.service.calculotributo.domain.VariavelExpressao.VARIACAO_PONTO_PERCENTUAL;
import static java.math.BigDecimal.ZERO;
import static java.util.Map.ofEntries;
import static java.util.Objects.requireNonNullElse;
import static org.apache.commons.lang3.StringUtils.isNoneBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.stereotype.Service;

import br.gov.serpro.rtc.api.model.input.ItemOperacaoInput;
import br.gov.serpro.rtc.api.model.input.calculadora.enumeration.TipoEnteGovernamental;
import br.gov.serpro.rtc.api.model.output.CbsIbsOutput;
import br.gov.serpro.rtc.api.model.output.GrupoDiferimentoMonofasiaOutput;
import br.gov.serpro.rtc.api.model.output.GrupoEtapaMonofasiaOutput;
import br.gov.serpro.rtc.api.model.output.GrupoMonofasiaOutput;
import br.gov.serpro.rtc.api.model.output.TributacaoRegularOutput;
import br.gov.serpro.rtc.api.model.roc.DiferimentoDomain;
import br.gov.serpro.rtc.api.model.roc.ReducaoAliquotaDomain;
import br.gov.serpro.rtc.api.model.roc.TributacaoCompraGovernamentalDomain;
import br.gov.serpro.rtc.domain.model.dto.ClassificacaoTributariaCalculoDTO;
import br.gov.serpro.rtc.domain.model.dto.TratamentoClassificacaoDTO;
import br.gov.serpro.rtc.domain.model.entity.SituacaoTributaria;
import br.gov.serpro.rtc.domain.model.entity.TratamentoTributario;
import br.gov.serpro.rtc.domain.model.enumeration.TributoEnum;
import br.gov.serpro.rtc.domain.service.AliquotaAdValoremServicoService;
import br.gov.serpro.rtc.domain.service.AliquotaPadraoService;
import br.gov.serpro.rtc.domain.service.AliquotaReferenciaService;
import br.gov.serpro.rtc.domain.service.ClassificacaoTributariaService;
import br.gov.serpro.rtc.domain.service.PercentualReducaoService;
import br.gov.serpro.rtc.domain.service.SituacaoTributariaService;
import br.gov.serpro.rtc.domain.service.TratamentoTributarioService;
import br.gov.serpro.rtc.domain.service.calculotributo.domain.VariavelExpressao;
import br.gov.serpro.rtc.domain.service.exception.ErroFaltaImplementacaoException;
import br.gov.serpro.rtc.domain.service.exception.ErroGenericoValidacaoException;
import br.gov.serpro.rtc.domain.service.exception.TipoAliquotaDesconhecidoException;
import lombok.RequiredArgsConstructor;

/**
 * Serviço responsável por calcular CBS e IBS a partir do tratamento tributário,
 * das alíquotas aplicáveis e das variáveis da operação.
 */
@RequiredArgsConstructor
@Service
public class CalculoCbsIbsService {
    private static final BigDecimal PBIO = new BigDecimal("30");
    private static final BigDecimal PDIF = new BigDecimal("100");
    private static final BigDecimal VARIACAO_EAC = new BigDecimal("5");
    private static final BigDecimal ALIQUOTA_EAC = new BigDecimal("0.1922");
    private static final BigDecimal ALIQUOTA_GASOLINA_TIPO_A = new BigDecimal("0.7925");
    
    private final PercentualReducaoService percentualReducaoService;
    private final AliquotaReferenciaService aliquotaReferenciaService;
    private final AliquotaPadraoService aliquotaPadraoService;
    private final AliquotaAdValoremServicoService aliquotaAdValoremServicoService;
    private final AvaliadorExpressaoAritmetica avaliador;
    private final TratamentoTributarioService tratamentoService;
    private final ClassificacaoTributariaService classificacaoTributariaService;
    private final SituacaoTributariaService situacaoTributariaService;
       
    public CbsIbsOutput calcularCbsIbs(
        TributoEnum tributo,
        Long codigoUf,
        Long codigoMunicipio,
        ItemOperacaoInput item,
        TratamentoClassificacaoDTO tratamentoClassificacao,
        BigDecimal impostoSeletivoCalculado,
        Boolean temTributacaoRegular,
        LocalDate data,
        TipoEnteGovernamental tpEnteGov,
        BigDecimal pRedutorCompraGov) {
        
        Long idTributo = tributo.getCodigo();

        String cst = null;
        String cClassTrib = null;

        TratamentoTributario tratamentoTributario = tratamentoService
                .buscar(tratamentoClassificacao.idTratamentoTributario());
        ClassificacaoTributariaCalculoDTO classificacaoTributaria = classificacaoTributariaService
                .buscarClassificacaoTributariaCalculo(tratamentoClassificacao.idClassificacaoTributaria());

        SituacaoTributaria situacaoTributaria = situacaoTributariaService
                .consultarSituacaoTributariaPorCodigo(item.getCst(), idTributo, data);

        if (temTributacaoRegular) {
            cst = item.getTributacaoRegular().getCst();
            cClassTrib = item.getTributacaoRegular().getCClassTrib();
        }

        String expressaoAliquota = tratamentoTributario.getExpressaoAliquota();
        String expressaoBaseCalculo = tratamentoTributario.getExpressaoBaseCalculo();
        String expressaoAliquotaEfetiva = tratamentoTributario.getExpressaoAliquotaEfetiva();
        String expressaoTributoCalculado = tratamentoTributario.getExpressaoTributoCalculado();
        String expressaoTributoDevido = tratamentoTributario.getExpressaoTributoDevido();
        String expressaoPercentualDiferimento = tratamentoTributario.getExpressaoPercentualDiferimento();
        String expressaoValorDiferimento = tratamentoTributario.getExpressaoValorDiferimento();
        
        boolean possuiAjuste = tratamentoTributario.isInPossuiAjuste();

        if (possuiAjuste) {
            throw new ErroFaltaImplementacaoException(classificacaoTributaria.codigo());
        }

        /*
        boolean incompativelComSuspensao = tratamentoTributario.isInIncompativelComSuspensao();
        boolean exigeGrupoDesoneracao = tratamentoTributario.isInExigeGrupoDesoneracao();
        boolean possuiAjuste = tratamentoTributario.isInPossuiAjuste();
        boolean possuiRedutor = tratamentoTributario.isInPossuiRedutor();
        */

        boolean possuiPercentualReducao = tratamentoTributario.isInPossuiPercentualReducao();

        BigDecimal valorPercentualReducao = null;
        BigDecimal valorAjuste = null;
        BigDecimal valorRedutor = null;

        BigDecimal baseCalculoInformada = item.getBaseCalculo();
        BigDecimal quantidade = item.getQuantidade();
        BigDecimal impostoSeletivoInformado = null;
        if (item.getImpostoSeletivo() != null && impostoSeletivoCalculado.compareTo(ZERO) != 0) {
            impostoSeletivoInformado = item.getImpostoSeletivo().getValorImpostoSeletivoInformado();
        }

        /////////////////////////////////
        BigDecimal valorAliquotaPadraoOuReferencia = null;
        BigDecimal valorAliquotaReferencia = null;
        BigDecimal valorAliquotaAdValorem = null;
        BigDecimal valorAliquotaAdRem = null;
        BigDecimal valorAliquota = null;
        // para tratar a monofasia
        BigDecimal valorAliquotaAdRemPrincipal = null;
        BigDecimal valorAliquotaAdRemSecundaria = null;
        BigDecimal variacaoPontoPercentual = null;
        BigDecimal pBio = null;
        BigDecimal pDif = null;
        /////////////////////////////////

        /////////////////////////////
        BigDecimal resultadoAliquota = null;
        BigDecimal resultadoAliquotaEfetiva = null;
        BigDecimal resultadoBaseCalculo = null;
        BigDecimal resultadoTributoCalculado = null;
        // novos
        BigDecimal resultadoTributoDevido = null;
        BigDecimal resultadoPercentualDiferimento = null;
        BigDecimal resultadoValorDiferimento = null;
        /////////////////////////////

        if (possuiPercentualReducao) {
            BigDecimal percentualReducao = percentualReducaoService
                    .buscar(classificacaoTributaria.id(), idTributo, data);
            valorPercentualReducao = dividirPorCem(percentualReducao);
        }

        // ajuste para 510001 => seria interessante que o tipo de alíquota fosse o padrão na planilha...
        String tipoAliquota;
        if ("510001".equals(classificacaoTributaria.codigo())) {
            tipoAliquota = "Padrão";
        } else {
            tipoAliquota = classificacaoTributaria.tipoAliquota();
        }

        boolean aliquotaDivididaPorCem = false;
        switch (tipoAliquota) {
            case "Padrão":
                valorAliquota = buscarAliquotaPadrao(idTributo, codigoUf, codigoMunicipio, data);
                aliquotaDivididaPorCem = true;
                break;

            case "Uniforme nacional (referência)":
                valorAliquota = buscarAliquotaReferencia(idTributo, data);
                aliquotaDivididaPorCem = true;
                break;

            case "Uniforme setorial":
                // esse tipo de alíquota provavelmente vai exigir vínculo com cClassTrib para serviços financeiros
                if (tratamentoTributario.isInPossuiMonofasia()) {
                    // resolver o problema da alíquota com nova modelagem de dados, usando código ANP em vez de NCM
                    // valorAliquota = buscarAliquotaAdRem(item.getNcm(), idTributo, data);
                    // valorAliquotaAdRem = valorAliquota;
                    valorAliquotaAdRemPrincipal = ALIQUOTA_GASOLINA_TIPO_A;
                    valorAliquotaAdRemSecundaria = ALIQUOTA_EAC;
                    pBio = dividirPorCem(PBIO);
                    pDif = dividirPorCem(PDIF);
                    variacaoPontoPercentual = VARIACAO_EAC;

                } else {
                    valorAliquota = buscarAliquotaUniformeSetorial(item.getNbs(), idTributo, classificacaoTributaria.id(), data);
                    aliquotaDivididaPorCem = true;
                }
                break;

            case "Fixa":
                valorAliquota = buscarAliquotaFixa(idTributo, classificacaoTributaria.id(), data);
                aliquotaDivididaPorCem = true;
                break;

            case "Sem alíquota":
                // fixar o valor 0 no tratamento tributário, por enquanto
                valorAliquota = ZERO;
                break;

            case "Alíquotas Combinadas (Ad Valorem e Ad Rem)":
                // para fazer
                break;

            default:
                throw new TipoAliquotaDesconhecidoException(tipoAliquota, cClassTrib, cst);
        }

        if (isNotBlank(expressaoAliquota)) {
            var variaveis0 = ofEntries(
                    entry(ALIQUOTA, valorAliquota),
                    entry(QUANTIDADE, quantidade),
                    entry(PERCENTUAL_REDUCAO, valorPercentualReducao),
                    entry(ALIQUOTA_PADRAO_OU_REFERENCIA, valorAliquotaPadraoOuReferencia),
                    entry(ALIQUOTA_REFERENCIA, valorAliquotaReferencia),
                    entry(ALIQUOTA_AD_VALOREM, valorAliquotaAdValorem),
                    entry(ALIQUOTA_AD_REM, valorAliquotaAdRem),
                    entry(AJUSTE, valorAjuste),
                    entry(REDUTOR, valorRedutor),
                    entry(ALIQUOTA_AD_REM_PRINCIPAL, valorAliquotaAdRemPrincipal),
                    entry(ALIQUOTA_AD_REM_SECUNDARIA, valorAliquotaAdRemSecundaria),
                    entry(VARIACAO_PONTO_PERCENTUAL, variacaoPontoPercentual),
                    entry(PERCENTUAL_BIOCOMBUSTIVEL, pBio),
                    entry(PERCENTUAL_DIFERIMENTO, pDif));

            resultadoAliquota = avaliador.evaluate(expressaoAliquota, variaveis0, PRECISAO_INTERNA);
        }

        if (isNotBlank(expressaoAliquotaEfetiva)) {
            var variaveis1 = ofEntries(
                    entry(QUANTIDADE, quantidade),
                    entry(ALIQUOTA, resultadoAliquota),
                    entry(PERCENTUAL_REDUCAO, valorPercentualReducao),
                    entry(PERCENTUAL_REDUCAO_COMPRA_GOV, pRedutorCompraGov),
                    entry(ALIQUOTA_PADRAO_OU_REFERENCIA, valorAliquotaPadraoOuReferencia),
                    entry(ALIQUOTA_REFERENCIA, valorAliquotaReferencia),
                    entry(ALIQUOTA_AD_VALOREM, valorAliquotaAdValorem),
                    entry(ALIQUOTA_AD_REM, valorAliquotaAdRem),
                    entry(AJUSTE, valorAjuste),
                    entry(REDUTOR, valorRedutor),
                    entry(ALIQUOTA_AD_REM_PRINCIPAL, valorAliquotaAdRemPrincipal),
                    entry(ALIQUOTA_AD_REM_SECUNDARIA, valorAliquotaAdRemSecundaria),
                    entry(VARIACAO_PONTO_PERCENTUAL, variacaoPontoPercentual),
                    entry(PERCENTUAL_BIOCOMBUSTIVEL, pBio),
                    entry(PERCENTUAL_DIFERIMENTO, pDif));

            resultadoAliquotaEfetiva = avaliador.evaluate(expressaoAliquotaEfetiva,
                    variaveis1, PRECISAO_INTERNA);
        }

        var variaveis2 = ofEntries(
                entry(QUANTIDADE, quantidade),
                entry(ALIQUOTA, resultadoAliquota),
                entry(ALIQUOTA_EFETIVA, resultadoAliquotaEfetiva),
                entry(IMPOSTO_SELETIVO_INFORMADO, impostoSeletivoInformado),
                entry(BASE_CALCULO_INFORMADA, baseCalculoInformada),
                entry(IMPOSTO_SELETIVO_CALCULADO, impostoSeletivoCalculado),
                entry(PERCENTUAL_REDUCAO, valorPercentualReducao),
                entry(ALIQUOTA_PADRAO_OU_REFERENCIA, valorAliquotaPadraoOuReferencia),
                entry(ALIQUOTA_REFERENCIA, valorAliquotaReferencia),
                entry(ALIQUOTA_AD_VALOREM, valorAliquotaAdValorem),
                entry(ALIQUOTA_AD_REM, valorAliquotaAdRem),
                entry(AJUSTE, valorAjuste),
                entry(REDUTOR, valorRedutor),
                entry(ALIQUOTA_AD_REM_PRINCIPAL, valorAliquotaAdRemPrincipal),
                entry(ALIQUOTA_AD_REM_SECUNDARIA, valorAliquotaAdRemSecundaria),
                entry(VARIACAO_PONTO_PERCENTUAL, variacaoPontoPercentual),
                entry(PERCENTUAL_BIOCOMBUSTIVEL, pBio),
                entry(PERCENTUAL_DIFERIMENTO, pDif));

        resultadoBaseCalculo = avaliador.evaluate(expressaoBaseCalculo, variaveis2, PRECISAO_INTERNA);

        if (resultadoBaseCalculo.compareTo(ZERO) < 0) {
            resultadoBaseCalculo = ZERO;
            // aqui deveria ser lançada uma exceção
            // flexibilidade para a plataforma
        }

        var variaveis3 = ofEntries(
                entry(QUANTIDADE, quantidade),
                entry(BASE_CALCULO, resultadoBaseCalculo),
                entry(ALIQUOTA, resultadoAliquota),
                entry(ALIQUOTA_EFETIVA, resultadoAliquotaEfetiva),
                entry(BASE_CALCULO_INFORMADA, baseCalculoInformada),
                entry(IMPOSTO_SELETIVO_INFORMADO, impostoSeletivoInformado),
                entry(IMPOSTO_SELETIVO_CALCULADO, impostoSeletivoCalculado),
                entry(PERCENTUAL_REDUCAO, valorPercentualReducao),
                entry(ALIQUOTA_PADRAO_OU_REFERENCIA, valorAliquotaPadraoOuReferencia),
                entry(ALIQUOTA_REFERENCIA, valorAliquotaReferencia),
                entry(ALIQUOTA_AD_VALOREM, valorAliquotaAdValorem),
                entry(ALIQUOTA_AD_REM, valorAliquotaAdRem),
                entry(AJUSTE, valorAjuste),
                entry(REDUTOR, valorRedutor),
                entry(ALIQUOTA_AD_REM_PRINCIPAL, valorAliquotaAdRemPrincipal),
                entry(ALIQUOTA_AD_REM_SECUNDARIA, valorAliquotaAdRemSecundaria),
                entry(VARIACAO_PONTO_PERCENTUAL, variacaoPontoPercentual),
                entry(PERCENTUAL_BIOCOMBUSTIVEL, pBio),
                entry(PERCENTUAL_DIFERIMENTO, pDif));

        resultadoTributoCalculado = avaliador.evaluate(expressaoTributoCalculado, variaveis3, PRECISAO_INTERNA);

        if (isNotBlank(expressaoTributoDevido)) {
            var variaveis4 = ofEntries(
                    entry(TRIBUTO_CALCULADO, resultadoTributoCalculado),
                    entry(QUANTIDADE, quantidade),
                    entry(BASE_CALCULO, resultadoBaseCalculo),
                    entry(ALIQUOTA, resultadoAliquota),
                    entry(ALIQUOTA_EFETIVA, resultadoAliquotaEfetiva),
                    entry(BASE_CALCULO_INFORMADA, baseCalculoInformada),
                    entry(IMPOSTO_SELETIVO_INFORMADO, impostoSeletivoInformado),
                    entry(IMPOSTO_SELETIVO_CALCULADO, impostoSeletivoCalculado),
                    entry(PERCENTUAL_REDUCAO, valorPercentualReducao),
                    entry(ALIQUOTA_PADRAO_OU_REFERENCIA, valorAliquotaPadraoOuReferencia),
                    entry(ALIQUOTA_REFERENCIA, valorAliquotaReferencia),
                    entry(ALIQUOTA_AD_VALOREM, valorAliquotaAdValorem),
                    entry(ALIQUOTA_AD_REM, valorAliquotaAdRem),
                    entry(AJUSTE, valorAjuste),
                    entry(REDUTOR, valorRedutor),
                    entry(ALIQUOTA_AD_REM_PRINCIPAL, valorAliquotaAdRemPrincipal),
                    entry(ALIQUOTA_AD_REM_SECUNDARIA, valorAliquotaAdRemSecundaria),
                    entry(VARIACAO_PONTO_PERCENTUAL, variacaoPontoPercentual),
                    entry(PERCENTUAL_BIOCOMBUSTIVEL, pBio),
                    entry(PERCENTUAL_DIFERIMENTO, pDif));

            resultadoTributoDevido = avaliador.evaluate(expressaoTributoDevido, variaveis4, PRECISAO_INTERNA);
        }

        if (isNoneBlank(expressaoPercentualDiferimento, expressaoValorDiferimento)) {
            var variaveis5 = ofEntries(
                    entry(TRIBUTO_CALCULADO, resultadoTributoCalculado),
                    entry(QUANTIDADE, quantidade),
                    entry(BASE_CALCULO, resultadoBaseCalculo),
                    entry(ALIQUOTA, resultadoAliquota),
                    entry(ALIQUOTA_EFETIVA, resultadoAliquotaEfetiva),
                    entry(BASE_CALCULO_INFORMADA, baseCalculoInformada),
                    entry(IMPOSTO_SELETIVO_INFORMADO, impostoSeletivoInformado),
                    entry(IMPOSTO_SELETIVO_CALCULADO, impostoSeletivoCalculado),
                    entry(PERCENTUAL_REDUCAO, valorPercentualReducao),
                    entry(ALIQUOTA_PADRAO_OU_REFERENCIA, valorAliquotaPadraoOuReferencia),
                    entry(ALIQUOTA_REFERENCIA, valorAliquotaReferencia),
                    entry(ALIQUOTA_AD_VALOREM, valorAliquotaAdValorem),
                    entry(ALIQUOTA_AD_REM, valorAliquotaAdRem),
                    entry(AJUSTE, valorAjuste),
                    entry(REDUTOR, valorRedutor));

            resultadoPercentualDiferimento = avaliador.evaluate(expressaoPercentualDiferimento, variaveis5, PRECISAO_INTERNA);

            var variaveis6 = ofEntries(
                    entry(PERCENTUAL_DIFERIMENTO, resultadoPercentualDiferimento),
                    entry(TRIBUTO_CALCULADO, resultadoTributoCalculado),
                    entry(QUANTIDADE, quantidade),
                    entry(BASE_CALCULO, resultadoBaseCalculo),
                    entry(ALIQUOTA, resultadoAliquota),
                    entry(ALIQUOTA_EFETIVA, resultadoAliquotaEfetiva),
                    entry(BASE_CALCULO_INFORMADA, baseCalculoInformada),
                    entry(IMPOSTO_SELETIVO_INFORMADO, impostoSeletivoInformado),
                    entry(IMPOSTO_SELETIVO_CALCULADO, impostoSeletivoCalculado),
                    entry(PERCENTUAL_REDUCAO, valorPercentualReducao),
                    entry(ALIQUOTA_PADRAO_OU_REFERENCIA, valorAliquotaPadraoOuReferencia),
                    entry(ALIQUOTA_REFERENCIA, valorAliquotaReferencia),
                    entry(ALIQUOTA_AD_VALOREM, valorAliquotaAdValorem),
                    entry(ALIQUOTA_AD_REM, valorAliquotaAdRem),
                    entry(AJUSTE, valorAjuste),
                    entry(REDUTOR, valorRedutor));

            resultadoValorDiferimento = avaliador.evaluate(expressaoValorDiferimento, variaveis6, PRECISAO_INTERNA);

            resultadoTributoCalculado = arredondarInterno(resultadoTributoCalculado.subtract(resultadoValorDiferimento));
            resultadoTributoDevido = arredondarInterno(resultadoTributoCalculado.subtract(resultadoValorDiferimento));

            if (resultadoTributoCalculado.compareTo(ZERO) < 0) {
                resultadoTributoCalculado = ZERO;
            }

            if (resultadoTributoDevido.compareTo(ZERO) < 0) {
                resultadoTributoDevido = ZERO;
            }
            
        }

        if (!situacaoTributaria.getInGrupoIbsCbs()) {
            return null;
            // return CbsIbsOutput
            //     .builder()
            //     .grupoReducao(null)
            //     .tributacaoRegular(null)
            //     .grupoDiferimento(null)
            //     .grupoMonofasia(null)
            //     .tributoCalculado(ZERO)
            //     .tributoDevido(ZERO)
            //     .aliquota(ZERO)
            //     .baseCalculo(ZERO)
            //     .quantidade(ZERO)
            //     .build();
        }
        
        return CbsIbsOutput
                .builder()
                .grupoReducao(!classificacaoTributaria.inGrupoReducao() || (temTributacaoRegular && tpEnteGov == null) ? null : obterGrupoReducao(classificacaoTributaria, resultadoAliquotaEfetiva, valorPercentualReducao, aliquotaDivididaPorCem))
                .compraGovernamental(obterGrupoCompraGov(tpEnteGov, TributoEnum.get(idTributo), resultadoAliquotaEfetiva, resultadoTributoDevido))
                .tributacaoRegular(temTributacaoRegular ?
                    obterGrupoDesoneracao(
                        cst,
                        cClassTrib,
                        resultadoBaseCalculo,
                        resultadoAliquota,
                        requireNonNullElse(valorPercentualReducao, ZERO),
                        aliquotaDivididaPorCem,
                        possuiPercentualReducao)
                    : null)
                .grupoDiferimento(obterGrupoDiferimento(resultadoValorDiferimento, resultadoPercentualDiferimento))
                .grupoMonofasia(obterGrupoMonofasia(classificacaoTributaria, quantidade, resultadoAliquota, resultadoAliquotaEfetiva, resultadoTributoCalculado, resultadoTributoDevido, variacaoPontoPercentual))
                .tributoCalculado(temTributacaoRegular ? ZERO : resultadoTributoCalculado)
                .tributoDevido(temTributacaoRegular ? ZERO : resultadoTributoDevido)
                .aliquota(temTributacaoRegular ? ZERO : (aliquotaDivididaPorCem && resultadoAliquota != null ? resultadoAliquota.movePointRight(2) : resultadoAliquota))
                .baseCalculo(resultadoBaseCalculo)
                .quantidade(quantidade)
                .build();
    }

    private TributacaoCompraGovernamentalDomain obterGrupoCompraGov(TipoEnteGovernamental tpEnteGov, TributoEnum t,
            BigDecimal aliquota, BigDecimal valor) {
        if (tpEnteGov == null) {
            return null;
        }
        final var b = TributacaoCompraGovernamentalDomain.builder();
        switch (t) {
        case CBS: {
            b.pAliqCBS(aliquota.movePointRight(2)); // nao divide por 100
            b.vTribCBS(valor);
            break;
        }
        case IBS_ESTADUAL: {
            b.pAliqIBSUF(aliquota.movePointRight(2));  // nao divide por 100
            b.vTribIBSUF(valor);
            break;
        }
        case IBS_MUNICIPAL: {
            b.pAliqIBSMun(aliquota.movePointRight(2));  // nao divide por 100
            b.vTribIBSMun(valor);
            break;
        }
        default:
            throw new RuntimeException("Tributo " + t + " nao possui compra governamental");
        }
        return b.build();
    }

    private BigDecimal buscarAliquotaReferencia(Long idTributo, LocalDate data) {
        return dividirPorCem(
                aliquotaReferenciaService.buscar(idTributo, data));
    }

    protected BigDecimal buscarAliquotaPadrao(Long idTributo, Long codigoUf, Long codigoMunicipio, LocalDate data) {
        final var aliquota = aliquotaPadraoService.buscarAliquota(idTributo, codigoUf, codigoMunicipio, data);
        return dividirPorCem(aliquota.valorAplicavel());
    }

    private BigDecimal buscarAliquotaUniformeSetorial(String nbs, Long idTributo, Long idClassificacaoTributaria, LocalDate data) {
        BigDecimal aliquotaUniformeSetorial = aliquotaAdValoremServicoService.buscarAliquotaAdValorem(nbs, idTributo, idClassificacaoTributaria, data);
        if (aliquotaUniformeSetorial == null) {
            aliquotaUniformeSetorial = aliquotaAdValoremServicoService.buscarAliquotaAdValoremPorClassificacaoTributaria(idTributo, idClassificacaoTributaria, data);
            if (aliquotaUniformeSetorial == null) {
                throw new ErroGenericoValidacaoException("Alíquota uniforme setorial não encontrada em " + data);
            }
        }
        return dividirPorCem(aliquotaUniformeSetorial);
    }

    private BigDecimal buscarAliquotaFixa(Long idTributo, Long idClassificacaoTributaria, LocalDate data) {
        BigDecimal aliquotaFixa = aliquotaAdValoremServicoService
                .buscarAliquotaAdValoremPorClassificacaoTributaria(idTributo, idClassificacaoTributaria, data);
        if (aliquotaFixa == null) {
            throw new ErroGenericoValidacaoException("Alíquota fixa não encontrada em " + data);
        }
        return dividirPorCem(aliquotaFixa);
    }

    private static ReducaoAliquotaDomain obterGrupoReducao(ClassificacaoTributariaCalculoDTO classificacaoTributaria, BigDecimal aliquotaEfetiva, BigDecimal percentualReducao, boolean aliquotaDivididaPorCem) {
        if (anyNull(percentualReducao, aliquotaEfetiva) || !classificacaoTributaria.inGrupoReducao()) {
            return null;
        }
        return ReducaoAliquotaDomain
            .builder()
            .pRedAliq(percentualReducao.movePointRight(2)) // para nao enviar o percentual dividido por 100
            .pAliqEfet(aliquotaDivididaPorCem ? aliquotaEfetiva.movePointRight(2) : aliquotaEfetiva) // para nao enviar a aliquota dividida por 100
            .build();
    }

    private static TributacaoRegularOutput obterGrupoDesoneracao(
        String cst,
        String cClassTrib,
        BigDecimal baseCalculo,
        BigDecimal aliquota,
        BigDecimal percentualReducao,
        boolean aliquotaDivididaPorCem,
        boolean possuiPercentualReducao) {
        
        BigDecimal aliquotaEfetiva = aliquota;
        if (possuiPercentualReducao && percentualReducao != null) {
            BigDecimal redutor = BigDecimal.ONE.subtract(percentualReducao);
            aliquotaEfetiva = multiplicar(aliquota, redutor);
        }
        BigDecimal tributoDevido = multiplicar(baseCalculo, aliquotaEfetiva);
        return TributacaoRegularOutput
            .builder()
            .cst(cst)
            .cClassTrib(cClassTrib)
            .baseCalculo(baseCalculo)
            .aliquotaEfetiva(aliquotaDivididaPorCem ? aliquotaEfetiva.movePointRight(2) : aliquotaEfetiva) // para nao enviar a aliquota dividida por 100
            .tributoDevido(tributoDevido)
            .build();
    }

    private static DiferimentoDomain obterGrupoDiferimento( BigDecimal valorDiferimento, BigDecimal percentualDiferimento) {
        if (anyNull(valorDiferimento, percentualDiferimento)) {
            return null;
        }
        return DiferimentoDomain
            .builder()
            .vDif(valorDiferimento)
            .pDif(percentualDiferimento.movePointRight(2))
            .build();
    }

    private static GrupoMonofasiaOutput obterGrupoMonofasia(ClassificacaoTributariaCalculoDTO classificacaoTributaria, BigDecimal quantidade, BigDecimal aliquotaAdRemPrincipal, BigDecimal aliquotaAdRemSecundaria, BigDecimal tributoCalculado, BigDecimal tributoDevido, BigDecimal variacaoPontoPercentual) {

        GrupoEtapaMonofasiaOutput grupoGMonoPadrao = null;
        GrupoEtapaMonofasiaOutput grupoGMonoReten = null;
        GrupoEtapaMonofasiaOutput grupoGMonoRet = null;
        GrupoDiferimentoMonofasiaOutput grupoGMonoDiferimento = null;

        if (classificacaoTributaria.inGrupoMonofasiaPadrao()) {
            grupoGMonoPadrao = GrupoEtapaMonofasiaOutput
                .builder()
                .quantidade(!classificacaoTributaria.inGrupoMonofasiaRet() ? quantidade : dividir(quantidade.multiply(variacaoPontoPercentual), CEM))
                .aliquotaAdRem(!classificacaoTributaria.inGrupoMonofasiaRet() ? aliquotaAdRemPrincipal : aliquotaAdRemSecundaria)
                .valor(tributoCalculado)
                .build();
        }
        if (classificacaoTributaria.inGrupoMonofasiaReten()) {
            grupoGMonoReten = GrupoEtapaMonofasiaOutput
                .builder()
                .quantidade(multiplicar(quantidade, obterFatorBioCombustivel()))
                .aliquotaAdRem(aliquotaAdRemSecundaria)
                .valor(tributoDevido)
                .build();
        }
        if (classificacaoTributaria.inGrupoMonofasiaRet()) {
            grupoGMonoRet = GrupoEtapaMonofasiaOutput
                .builder()
                .quantidade(quantidade)
                .aliquotaAdRem(aliquotaAdRemPrincipal)
                .valor(tributoDevido)
                .build();
        }
        if (classificacaoTributaria.inGrupoMonofasiaDiferimento()) {
            grupoGMonoDiferimento = GrupoDiferimentoMonofasiaOutput
                .builder()
                .percentualDiferimento(dividir(tributoCalculado.subtract(tributoDevido), tributoCalculado).movePointRight(2))
                .valorDiferimento(tributoCalculado.subtract(tributoDevido))
                .build();
        }
        return GrupoMonofasiaOutput
            .builder()
            .tributoMonofasico(grupoGMonoPadrao)
            .tributoSujeitoRetencao(grupoGMonoReten)
            .tributoRetido(grupoGMonoRet)
            .tributoDiferido(grupoGMonoDiferimento)
            .build();
    }
    
    private static Entry<String, BigDecimal> entry(VariavelExpressao c, BigDecimal v) {
        return Map.entry(c.getNome(), requireNonNullElse(v, ZERO));
    }

    private static boolean anyNull(Object... params) {
        if (params != null && params.length > 0) {
            for (Object param : params) {
                if (param == null) {
                    return true;
                }
            }
        }
        return false;
    }

    private static BigDecimal obterFatorBioCombustivel() {
        return dividir(PBIO, CEM.subtract(PBIO));
    }

}