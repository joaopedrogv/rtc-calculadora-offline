/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service.dadosabertos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import br.gov.serpro.rtc.api.model.output.dadosabertos.AliquotaDadosAbertosOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.AtorDadosAbertosOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.ClassificacaoTributariaDadosAbertosOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.ClassificacaoTributariaOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.SituacaoClassificacaoAninhadoOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.FundamentacaoClassificacaoDadosAbertosOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.GrupoAtorDadosAbertosOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.GrupoDfeDadosAbertosOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.MunicipioDadosAbertosOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.TipoDfeDadosAbertosOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.NomenclaturaDadosAbertosOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.NbsDadosAbertosOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.NbsListaDadosAbertosOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.NcmDadosAbertosOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.RedutorCompraGovernamentalDadosAbertosOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.SituacaoTributariaDadosAbertosOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.TipoDfeClassificacaoDadosAbertosOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.NbsAplicavelOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.NcmAplicavelOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.TransferenciaCBSDadosAbertosOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.TransferenciaIBSDadosAbertosOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.UfDadosAbertosOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.ValidadeDfeClassificacaoTributariaDadosAbertosOutput;
import br.gov.serpro.rtc.domain.model.dto.AliquotaAdRemDTO;
import br.gov.serpro.rtc.domain.model.dto.AliquotaAdValoremDTO;
import br.gov.serpro.rtc.domain.model.dto.ClassificacaoTributariaDTO;
import br.gov.serpro.rtc.domain.model.entity.ClassificacaoTributaria;
import br.gov.serpro.rtc.domain.model.entity.FundamentacaoLegal;
import br.gov.serpro.rtc.domain.model.entity.SituacaoTributaria;
import br.gov.serpro.rtc.domain.model.entity.TipoDfeClassificacao;
import br.gov.serpro.rtc.domain.model.enumeration.NomenclaturaResultEnum;
import br.gov.serpro.rtc.domain.model.enumeration.SiglasDFeEnum;
import br.gov.serpro.rtc.domain.model.enumeration.PapelAtorEnum;
import br.gov.serpro.rtc.domain.model.enumeration.TipoWarningDadosSimulados;
import br.gov.serpro.rtc.domain.repository.ClassificacaoTributariaRepository;
import br.gov.serpro.rtc.domain.repository.GrupoAtorRepository;
import br.gov.serpro.rtc.domain.repository.GrupoDfeRepository;
import br.gov.serpro.rtc.domain.repository.NbsAplicavelRepository;
import br.gov.serpro.rtc.domain.repository.NbsRepository;
import br.gov.serpro.rtc.domain.repository.NcmRepository;
import br.gov.serpro.rtc.domain.repository.RedutorCompraGovernamentalRepository;
import br.gov.serpro.rtc.domain.repository.SituacaoTributariaRepository;
import br.gov.serpro.rtc.domain.repository.TipoDfeClassificacaoRepository;
import br.gov.serpro.rtc.domain.repository.TransferenciaCBSRepository;
import br.gov.serpro.rtc.domain.repository.TratamentoClassificacaoRepository;
import br.gov.serpro.rtc.domain.service.AliquotaAdRemProdutoService;
import br.gov.serpro.rtc.domain.service.AliquotaAdValoremProdutoService;
import br.gov.serpro.rtc.domain.service.AliquotaAdValoremServicoService;
import br.gov.serpro.rtc.domain.service.AliquotaPadraoService;
import br.gov.serpro.rtc.domain.service.ClassificacaoTributariaService;
import br.gov.serpro.rtc.domain.service.FundamentacaoClassificacaoService;
import br.gov.serpro.rtc.domain.service.NbsAplicavelService;
import br.gov.serpro.rtc.domain.service.NcmAplicavelService;
import br.gov.serpro.rtc.domain.service.MunicipioService;
import br.gov.serpro.rtc.domain.service.NomenclaturaService;
import br.gov.serpro.rtc.domain.service.TipoDfeClassificacaoService;
import br.gov.serpro.rtc.domain.service.TributoSituacaoTributariaService;
import br.gov.serpro.rtc.domain.service.UfService;
import br.gov.serpro.rtc.domain.service.exception.ClassificacaoTributariaNaoEncontradaException;
import br.gov.serpro.rtc.domain.service.exception.ErroGenericoValidacaoException;
import br.gov.serpro.rtc.domain.service.exception.NbsNaoEncontradaException;
import br.gov.serpro.rtc.domain.service.exception.NbsNaoVinculadaException;
import br.gov.serpro.rtc.domain.service.exception.NcmNaoEncontradaException;
import br.gov.serpro.rtc.domain.service.exception.NcmNaoVinculadaException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;

/**
 * Serviço responsável por consolidar as consultas públicas de dados abertos da
 * calculadora tributária. Expõe localidades, classificações, alíquotas,
 * nomenclaturas, redutores e vigências usados por integrações externas.
 */
@RequiredArgsConstructor
@Service
public class DadosAbertosService {

    private final UfService ufService;
    private final MunicipioService municipioService;
    private final SituacaoTributariaRepository situacaoTributariaRepository;
    private final TratamentoClassificacaoRepository tratamentoClassificacaoRepository;
    private final ClassificacaoTributariaService classificacaoTributariaService;
    private final ClassificacaoTributariaRepository classificacaoTributariaRepository;
    private final NbsAplicavelRepository nbsAplicavelRepository;
    private final NcmRepository ncmRepository;
    private final NbsRepository nbsRepository;
    private final RedutorCompraGovernamentalRepository redutorCompraGovernamentalRepository;
    private final TransferenciaCBSRepository transferenciaCBSRepository;
    private final AliquotaAdValoremProdutoService aliquotaAdValoremProdutoService;
    private final AliquotaAdValoremServicoService aliquotaAdValoremServicoService;
    private final AliquotaAdRemProdutoService aliquotaAdRemProdutoService;
    private final FundamentacaoClassificacaoService fundamentacaoClassificacaoService;
    private final TributoSituacaoTributariaService tributoSituacaoTributariaService;
    private final TipoDfeClassificacaoService tipoDfeClassificacaoService;
    private final TipoDfeClassificacaoRepository tipoDfeClassificacaoRepository;
    private final GrupoDfeRepository grupoDfeRepository;
    private final GrupoAtorRepository grupoAtorRepository;
    private final NomenclaturaService nomenclaturaService;
    private final AliquotaPadraoService aliquotaPadraoService;
    private final NbsAplicavelService nbsAplicavelService;
    private final NcmAplicavelService ncmAplicavelService;

    // FIXME aqui retornar somente os dados necessarios para a consulta via service
    public List<UfDadosAbertosOutput> consultarUfs() {
        return ufService.consultarTodos().stream()
                .map(uf -> UfDadosAbertosOutput.builder()
                        .sigla(uf.getSigla())
                        .nome(uf.getNome())
                        .codigo(uf.getCodigo())
                        .build())
                .toList();
    }

    // FIXME aqui retornar somente os dados necessarios para a consulta via service
    public List<MunicipioDadosAbertosOutput> consultarMunicipiosPorSiglaUf(String siglaUf) {
        return municipioService.consultarTodosPorSiglaUf(siglaUf).stream()
                .map(m -> MunicipioDadosAbertosOutput
                        .builder()
                        .codigo(m.getCodigo())
                        .nome(m.getNome())
                        .build())
                .toList();
    }

    public List<SituacaoTributariaDadosAbertosOutput> consultarSituacoesTributarias(Long idTributo, LocalDate data) {
        return situacaoTributariaRepository.consultarPorIdTributo(idTributo, data).stream()
                .map(m -> SituacaoTributariaDadosAbertosOutput
                        .builder()
                        .id(m.getId())
                        .codigo(m.getCodigo())
                        .descricao(m.getDescricao())
                        .build())
                .toList();
    }

    public List<ClassificacaoTributariaDadosAbertosOutput> consultarClassificacoesTributariasPorIdSituacaoTributaria(
            Long idSituacaoTributaria, LocalDate data) {

        return tratamentoClassificacaoRepository
                .consultarTratamentoClassificacaoPorIdSituacaoTributaria(idSituacaoTributaria, data).stream()
                .map(
                        m -> {
                            Long idClassificacaoTributaria = ((Number) m[8]).longValue();

                            List<TipoDfeClassificacaoDadosAbertosOutput> tiposDfeClassificacaoOutput = 
                                    obterListaDfeClassificacaoTributaria(idClassificacaoTributaria, data);

                return ClassificacaoTributariaDadosAbertosOutput
                                    .builder()
                                    .codigo((String) m[0])
                                    .descricao((String) m[1])
                                    .tipoAliquota((String) m[2])
                                    .nomenclatura((String) m[3])
                                    .descricaoTratamentoTributario((String) m[4])
                                    .incompativelComSuspensao(m[5] != null && ((Integer) m[5]) != 0)
                                    .exigeGrupoDesoneracao(m[6] != null && ((Integer) m[6]) != 0)
                                    .possuiPercentualReducao(m[7] != null && ((Integer) m[7]) != 0)
                    .tiposDfeClassificacao(tiposDfeClassificacaoOutput)
                    .dataAtualizacao(parseToLocalDate(m, 9))
                                    .build();
                        })
                .toList();
    }

    public List<ClassificacaoTributariaDadosAbertosOutput> consultarClassificacoesTributariasPorCstETributoTipo(
            String cst, List<String> tributoTipos, LocalDate data) {
        return tratamentoClassificacaoRepository
                .consultarTratamentoClassificacaoPorCstETributoTipo(cst, tributoTipos, data).stream()
                .map(m -> {
                    Long idClassificacaoTributaria = ((Number) m[8]).longValue();

                    List<TipoDfeClassificacaoDadosAbertosOutput> tiposDfeClassificacaoOutput = 
                            obterListaDfeClassificacaoTributaria(idClassificacaoTributaria, data);

            return ClassificacaoTributariaDadosAbertosOutput
                            .builder()
                            .codigo((String) m[0])
                            .descricao((String) m[1])
                            .tipoAliquota((String) m[2])
                            .nomenclatura((String) m[3])
                            .descricaoTratamentoTributario((String) m[4])
                            .incompativelComSuspensao(m[5] != null && ((Integer) m[5]) != 0)
                            .exigeGrupoDesoneracao(m[6] != null && ((Integer) m[6]) != 0)
                            .possuiPercentualReducao(m[7] != null && ((Integer) m[7]) != 0)
                .tiposDfeClassificacao(tiposDfeClassificacaoOutput)
                .dataAtualizacao(parseToLocalDate(m, 9))
                            .build();
                })
                .toList();
    }

    public NcmDadosAbertosOutput consultarNcm(String ncm, LocalDate data) {

        validarNcm(ncm);

        // Verifica se o NCM existe
        if (!ncmRepository.existeNcm(ncm, data)) {
            throw new NcmNaoEncontradaException(ncm, data);
        }

        AliquotaAdValoremDTO aliquotaAdValorem = aliquotaAdValoremProdutoService.buscarAliquotaAdValorem(ncm, 1L, data);
        AliquotaAdRemDTO aliquotaAdRem = aliquotaAdRemProdutoService.buscarAliquotaAdRem(ncm, 1L, data);

        // A incidência do IS é determinada pela existência da alíquota (DTO não
        // nulo), mesmo com valor ainda não definido em lei; os valores só são
        // expostos quando definidos.
        boolean temAliquotaAdValorem = aliquotaAdValorem != null;
        boolean temAliquotaAdRem = aliquotaAdRem != null;

        return NcmDadosAbertosOutput
                .builder()
                .tributadoPeloImpostoSeletivo(temAliquotaAdValorem || temAliquotaAdRem)
                .temAliquotaAdValorem(temAliquotaAdValorem)
                .temAliquotaAdRem(temAliquotaAdRem)
                .aliquotaAdValorem(temAliquotaAdValorem ? aliquotaAdValorem.valor() : null)
                .aliquotaAdRem(temAliquotaAdRem ? aliquotaAdRem.valor() : null)
                .capitulo(ncmRepository.buscarDescricaoNcm(ncm, 2, data).orElse(null))
                .posicao(ncmRepository.buscarDescricaoNcm(ncm, 4, data).orElse(null))
                .subposicao(ncmRepository.buscarDescricaoNcm(ncm, 6, data).orElse(null))
                .item(ncmRepository.buscarDescricaoNcm(ncm, 7, data).orElse(null))
                .subitem(ncmRepository.buscarDescricaoNcm(ncm, 8, data).orElse(null))
                .unidade(aliquotaAdRem != null ? aliquotaAdRem.unidadeMedida() : null)
                .build();
                
    }

    private static void validarNcm(String ncm) {
        if (ncm == null) {
            throw new ValidationException("O campo NCM é obrigatório.");
        }
        if (!ncm.matches("\\d+")) {
            throw new ValidationException("O campo NCM deve conter somente dígitos.");
        }
        // if (ncm.length() != 8) {
        //     throw new ValidationException("O campo NCM deve ter exatamente 8 dígitos.");
        // }
    }

    public NbsDadosAbertosOutput consultarNbs(String nbs, LocalDate data) {

        validarNbs(nbs);

        // Verifica se a NBS existe
        if (!nbsRepository.existeNbs(nbs, data)) {
            throw new NbsNaoEncontradaException(nbs, data);
        }

        AliquotaAdValoremDTO aliquotaAdValorem = aliquotaAdValoremServicoService
                .buscarAliquotaAdValoremDto(nbs, 1L, null, data);

        // A incidência do IS é determinada pela existência da alíquota (DTO não
        // nulo), mesmo com valor ainda não definido em lei; o valor só é
        // exposto quando definido. NBS não possui alíquota ad rem.
        boolean temAliquotaAdValorem = aliquotaAdValorem != null;

        return NbsDadosAbertosOutput
                .builder()
                .tributadoPeloImpostoSeletivo(temAliquotaAdValorem)
                .temAliquotaAdValorem(temAliquotaAdValorem)
                .aliquotaAdValorem(temAliquotaAdValorem ? aliquotaAdValorem.valor() : null)
                .capitulo(nbsRepository.buscarDescricaoNbs(nbs, 5, data).orElse(null))
                .posicao(nbsRepository.buscarDescricaoNbs(nbs, 6, data).orElse(null))
                .subposicao1(nbsRepository.buscarDescricaoNbs(nbs, 7, data).orElse(null))
                .subposicao2(nbsRepository.buscarDescricaoNbs(nbs, 8, data).orElse(null))
                .item(nbsRepository.buscarDescricaoNbs(nbs, 9, data).orElse(null))
                .build();
                
    }

    private static void validarNbs(String nbs) {
        if (nbs == null) {
            throw new ValidationException("O campo NBS é obrigatório.");
        }
        if (!nbs.matches("\\d+")) {
            throw new ValidationException("O campo NBS deve conter somente dígitos.");
        }
        // if (nbs.length() != 9) {
        //     throw new ValidationException("O campo NBS deve ter exatamente 9 dígitos.");
        // }
    }

    public List<NbsListaDadosAbertosOutput> listarNbs(LocalDate data) {
        return nbsRepository.listarNbs(data).stream()
                .map(nbs -> NbsListaDadosAbertosOutput.builder()
                        .codigo(nbs.getCodigo())
                        .descricao(nbs.getDescricao())
                        .build())
                .toList();
    }

    public List<FundamentacaoClassificacaoDadosAbertosOutput> consultarFundamentacoesLegais(LocalDate data) {
        return fundamentacaoClassificacaoService.buscarTodas(data).stream()
                .map(x -> {
                    final ClassificacaoTributaria classificacao = x.getClassificacaoTributaria();
                    final SituacaoTributaria situacao = classificacao.getSituacaoTributaria();
                    final Long idTributo = tributoSituacaoTributariaService
                            .consultarPorIdSituacaoTributaria(situacao.getId(), data);
                    final FundamentacaoLegal fundamentacao = x.getFundamentacaoLegal();

                    // Remove as fundamentações do imposto seletivo
                    if (idTributo == 1){
                        return null;
                    }

                    return FundamentacaoClassificacaoDadosAbertosOutput
                            .builder()
                            .codigoClassificacaoTributaria(classificacao.getCodigo())
                            .descricaoClassificacaoTributaria(classificacao.getDescricao())
                            .codigoSituacaoTributaria(situacao.getCodigo())
                            .descricaoSituacaoTributaria(situacao.getDescricao())
                            .conjuntoTributo(idTributo == 1 ? "Imposto Seletivo" : "CBS e IBS")
                            .texto(fundamentacao.getTexto())
                            .textoCurto(fundamentacao.getTextoCurto())
                            .referenciaNormativa(fundamentacao.getReferenciaNormativa())
                            .build();
                })
                .filter(output -> output != null)
                .toList();
    }

    public List<ClassificacaoTributariaDadosAbertosOutput> consultarClassificacoesTributariasCbsIbs(LocalDate data) {
        return tratamentoClassificacaoRepository
                .consultarTratamentoClassificacaoCbsIbs(null, data).stream()
                .map(
                        m -> {
                            Long idClassificacaoTributaria = ((Number) m[0]).longValue();

                            List<TipoDfeClassificacaoDadosAbertosOutput> tiposDfeClassificacaoOutput = 
                                    obterListaDfeClassificacaoTributaria(idClassificacaoTributaria, data);

                            return ClassificacaoTributariaDadosAbertosOutput
                                    .builder()
                                    .codigo((String) m[1])
                                    .descricao((String) m[2])
                                    .tipoAliquota((String) m[3])
                                    .nomenclatura((String) m[4])
                                    .descricaoTratamentoTributario((String) m[5])
                                    .incompativelComSuspensao(m[6] != null && ((Integer) m[6]) != 0)
                                    .exigeGrupoDesoneracao(m[7] != null && ((Integer) m[7]) != 0)
                                    .possuiPercentualReducao(m[8] != null && ((Integer) m[8]) != 0)
                                    .indicaApropriacaoCreditoAdquirenteCbs(m[9] != null && ((Integer) m[9]) != 0)
                                    .indicaApropriacaoCreditoAdquirenteIbs(m[10] != null && ((Integer) m[10]) != 0)
                                    .indicaCreditoPresumidoFornecedor(m[11] != null && ((Integer) m[11]) != 0)
                                    .indicaCreditoPresumidoAdquirente(m[12] != null && ((Integer) m[12]) != 0)
                                    .creditoOperacaoAntecedente((String) m[13])
                                    .percentualReducaoCbs(m[14] != null ? convertToBigDecimal(m[14]) : null)
                                    .percentualReducaoIbsUf(m[15] != null ? convertToBigDecimal(m[15]) : null)
                                    .percentualReducaoIbsMun(m[16] != null ? convertToBigDecimal(m[16]) : null)
                                    .tiposDfeClassificacao(tiposDfeClassificacaoOutput)
                                    .dataAtualizacao(parseToLocalDate(m, 18))
                                    .build();
                        })
                .toList();
    }

    /**
     * Consulta as classificações tributárias CBS/IBS vigentes na data,
     * filtradas pelos atores da operação e pelo tipo de DF-e. Uma classificação
     * sem vínculo de ator vigente em um papel é aplicável a qualquer ator
     * naquele papel e permanece no resultado; quando fornecedor e adquirente
     * são informados juntos, as duas condições devem ser satisfeitas
     * simultaneamente. Sem filtros, o resultado é idêntico ao de
     * {@link #consultarClassificacoesTributariasCbsIbs(LocalDate)}.
     *
     * @param data       data de referência para filtragem de vigência
     * @param fornecedor ATOR_ID do ator no papel de fornecedor (opcional)
     * @param adquirente ATOR_ID do ator no papel de adquirente (opcional)
     * @param siglaDfe   sigla do tipo de DF-e, em qualquer formato (opcional)
     */
    public List<ClassificacaoTributariaDadosAbertosOutput> consultarClassificacoesTributariasCbsIbsPorAtores(
            LocalDate data, Long fornecedor, Long adquirente, String siglaDfe) {
        List<ClassificacaoTributariaDadosAbertosOutput> classificacoes = consultarClassificacoesTributariasCbsIbs(data);

        if (fornecedor == null && adquirente == null && siglaDfe == null) {
            return classificacoes;
        }

        String siglaNormalizada = siglaDfe != null ? SiglasDFeEnum.normalizarSigla(siglaDfe) : null;
        Set<String> codigosAdmitidos = new HashSet<>(classificacaoTributariaRepository
                .listarCodigosClassificacoesPorAtores(data, fornecedor, adquirente, siglaNormalizada));

        return classificacoes.stream()
                .filter(c -> codigosAdmitidos.contains(c.getCodigo()))
                .toList();
    }

    public ClassificacaoTributariaDadosAbertosOutput consultarClassificacaoTributariaCbsIbsPorCodigo(String cClassTrib, LocalDate data) {
        List<Object[]> resultado = tratamentoClassificacaoRepository
                .consultarTratamentoClassificacaoCbsIbs(cClassTrib, data);
        
        if (resultado == null || resultado.isEmpty()) {
            throw new ClassificacaoTributariaNaoEncontradaException(
                cClassTrib, "CBS e IBS", data
            );
        }

        Object[] row = resultado.get(0);

        Long idClassificacaoTributaria = ((Number) row[0]).longValue();
        List<TipoDfeClassificacaoDadosAbertosOutput> tiposDfeClassificacaoOutput = 
                obterListaDfeClassificacaoTributaria(idClassificacaoTributaria, data);

        return ClassificacaoTributariaDadosAbertosOutput
                .builder()
                .codigo((String) row[1])
                .descricao((String) row[2])
                .tipoAliquota((String) row[3])
                .nomenclatura((String) row[4])
                .descricaoTratamentoTributario((String) row[5])
                .incompativelComSuspensao(row[6] != null && ((Integer) row[6]) != 0)
                .exigeGrupoDesoneracao(row[7] != null && ((Integer) row[7]) != 0)
                .possuiPercentualReducao(row[8] != null && ((Integer) row[8]) != 0)
                .indicaApropriacaoCreditoAdquirenteCbs(row[9] != null && ((Integer) row[9]) != 0)
                .indicaApropriacaoCreditoAdquirenteIbs(row[10] != null && ((Integer) row[10]) != 0)
                .indicaCreditoPresumidoFornecedor(row[11] != null && ((Integer) row[11]) != 0)
                .indicaCreditoPresumidoAdquirente(row[12] != null && ((Integer) row[12]) != 0)
                .creditoOperacaoAntecedente((String) row[13])
                .percentualReducaoCbs(row[14] != null ? convertToBigDecimal(row[14]) : null)
                .percentualReducaoIbsUf(row[15] != null ? convertToBigDecimal(row[15]) : null)
                .percentualReducaoIbsMun(row[16] != null ? convertToBigDecimal(row[16]) : null)
                .tipoReceitaBrutaSimplesNacional(row[19] != null ? ((Number) row[19]).intValue() : null)
                .tiposDfeClassificacao(tiposDfeClassificacaoOutput)
                .dataAtualizacao(parseToLocalDate(row, 18))
                .build();
    }

    public ClassificacaoTributariaDadosAbertosOutput consultarClassificacaoTributariaIsPorCodigo(String cClassTrib, LocalDate data) {
        List<Object[]> resultado = tratamentoClassificacaoRepository
                .consultarTratamentoClassificacaoImpostoSeletivo(cClassTrib, data);

        if (resultado == null || resultado.isEmpty()) {
            throw new ClassificacaoTributariaNaoEncontradaException(
                cClassTrib, "IS", data
            );
        }

        Object[] row = resultado.get(0);

        Long idClassificacaoTributaria = ((Number) row[0]).longValue();
        List<TipoDfeClassificacaoDadosAbertosOutput> tiposDfeClassificacaoOutput =
                obterListaDfeClassificacaoTributaria(idClassificacaoTributaria, data);

        return ClassificacaoTributariaDadosAbertosOutput
                .builder()
                .codigo((String) row[1])
                .descricao((String) row[2])
                .tipoAliquota((String) row[3])
                .nomenclatura((String) row[4])
                .descricaoTratamentoTributario((String) row[5])
                .incompativelComSuspensao(row[6] != null && ((Integer) row[6]) != 0)
                .exigeGrupoDesoneracao(row[7] != null && ((Integer) row[7]) != 0)
                .possuiPercentualReducao(row[8] != null && ((Integer) row[8]) != 0)
                .tiposDfeClassificacao(tiposDfeClassificacaoOutput)
                .dataAtualizacao(parseToLocalDate(row, 10))
                .build();
    }

    public List<ClassificacaoTributariaDadosAbertosOutput> consultarClassificacoesTributariasImpostoSeletivo(LocalDate data) {
        return tratamentoClassificacaoRepository
                .consultarTratamentoClassificacaoImpostoSeletivo(null, data).stream()
                .map(m -> {
                    Long idClassificacaoTributaria = ((Number) m[0]).longValue();
                    List<TipoDfeClassificacaoDadosAbertosOutput> tiposDfeClassificacaoOutput =
                            obterListaDfeClassificacaoTributaria(idClassificacaoTributaria, data);
                    return ClassificacaoTributariaDadosAbertosOutput
                            .builder()
                            .codigo((String) m[1])
                            .descricao((String) m[2])
                            .tipoAliquota((String) m[3])
                            .nomenclatura((String) m[4])
                            .descricaoTratamentoTributario((String) m[5])
                            .incompativelComSuspensao(m[6] != null && ((Integer) m[6]) != 0)
                            .exigeGrupoDesoneracao(m[7] != null && ((Integer) m[7]) != 0)
                            .possuiPercentualReducao(m[8] != null && ((Integer) m[8]) != 0)
                            .tiposDfeClassificacao(tiposDfeClassificacaoOutput)
                            .dataAtualizacao(parseToLocalDate(m, 10))
                            .build();
                })
                .toList();
    }

    private LocalDate parseToLocalDate(Object[] m, int index) {
        try {
            if (m == null || index < 0 || index >= m.length) return null;
            Object v = m[index];
            if (v == null) return null;
            if (v instanceof Date) {
                return ((Date) v).toLocalDate();
            }
            if (v instanceof LocalDate) {
                return (LocalDate) v;
            }
            
            String s = v.toString();
            if (s.isBlank()) return null;
            return LocalDate.parse(s);
        } catch (Exception e) {
            return null;
        }
    }

    public AliquotaDadosAbertosOutput consultarAliquota(Long idTributo, Long codigoUf, Long codigoMunicipio, LocalDate data) {
        final var aliquota = aliquotaPadraoService.buscarAliquota(idTributo, codigoUf, codigoMunicipio, data);
        return AliquotaDadosAbertosOutput
                .builder()
                .aliquotaReferencia(aliquota.valorReferencia())
                .aliquotaPropria(aliquota.valorPadrao())
                .formaAplicacao(aliquota.formaAplicacao())
                .build();
    }

    /**
     * Converte um Object para BigDecimal, tratando diferentes tipos numéricos
     */
    private BigDecimal convertToBigDecimal(Object value) {
        if (value == null) {
            return null;
        }
        
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        } else if (value instanceof Float) {
            return BigDecimal.valueOf(((Float) value).doubleValue());
        } else if (value instanceof Double) {
            return BigDecimal.valueOf((Double) value);
        } else if (value instanceof Integer) {
            return BigDecimal.valueOf((Integer) value);
        } else if (value instanceof Long) {
            return BigDecimal.valueOf((Long) value);
        } else if (value instanceof String) {
            try {
                return new BigDecimal((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        } else {
            // Para outros tipos, tenta converter para string e depois para BigDecimal
            try {
                return new BigDecimal(value.toString());
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }

    public ValidadeDfeClassificacaoTributariaDadosAbertosOutput consultarValidadeDfeClassificacaoTributaria(String siglaDfe, String cClassTrib, LocalDate data) {
        Object[] resultado = tratamentoClassificacaoRepository.consultarValidadeDfeClassificacaoTributaria(cClassTrib, data);
        
        if (resultado == null || resultado.length == 0) {
            throw new ClassificacaoTributariaNaoEncontradaException(
                cClassTrib, siglaDfe, data
            );
        }

        if (resultado[0] instanceof Object[]) {
            resultado = (Object[]) resultado[0];
        }

        if (!(resultado[0] instanceof Number)) {
            throw new ErroGenericoValidacaoException("ID da classificação tributária inválido.");
        }

        Long idClassificacaoTributaria = ((Number) resultado[0]).longValue();

        List<TipoDfeClassificacaoDadosAbertosOutput> tiposDfeClassificacaoOutput = 
                obterListaDfeClassificacaoTributaria(idClassificacaoTributaria, data);

        SiglasDFeEnum siglaEnum = SiglasDFeEnum.getPorSiglaNormalizada(siglaDfe);
        boolean siglaDfeValida = tiposDfeClassificacaoOutput.stream()
            .anyMatch(t -> siglaEnum.corresponde(t.getSigla()));

        return ValidadeDfeClassificacaoTributariaDadosAbertosOutput
                .builder()
                .siglaDfeInformado(siglaDfe)
                .validoParaSiglaDfeInformado(siglaDfeValida)
                .nomenclatura((String) resultado[1])
                .exigeGrupoTributacaoRegular(resultado[2] != null && ((Number) resultado[2]).intValue() != 0)
                .permiteDiferimento(resultado[3] != null && ((Number) resultado[3]).intValue() != 0)
                .possibilidadeCreditoPresumido(resultado[4] != null && ((Number) resultado[4]).intValue() == 1)
                .build();
    }

    public List<SituacaoClassificacaoAninhadoOutput> consultarCstsComClassificacoesCbsIbs(String siglaDfe, LocalDate data) {
        List<Object[]> resultados;
        if (siglaDfe == null || siglaDfe.isBlank()) {
            resultados = tipoDfeClassificacaoRepository.buscarCstsComClassificacoesCbsIbsTodosDfes(data);
        } else {
            String siglaNormalizada = SiglasDFeEnum.getPorSiglaNormalizada(siglaDfe).getChaveNormalizada();
            resultados = tipoDfeClassificacaoRepository.buscarCstsComClassificacoesCbsIbs(siglaNormalizada, data);
        }
        return agruparCstsComClassificacoes(resultados);
    }

    public List<SituacaoClassificacaoAninhadoOutput> consultarCstsComClassificacoesIs(String siglaDfe, LocalDate data) {
        // TODO: filtrar por DFe após atualização do banco conectar valores de IS com informações do DFe
        if (siglaDfe != null && !siglaDfe.isBlank()) {
            String siglaNormalizada = SiglasDFeEnum.getPorSiglaNormalizada(siglaDfe).getChaveNormalizada();
            if (!SiglasDFeEnum.impostoSeletivoAplicavel(siglaNormalizada)) {
                return List.of();
            }
        }
        List<Object[]> resultados = tipoDfeClassificacaoRepository.buscarCstsComClassificacoesIs(data);
        return agruparCstsComClassificacoes(resultados);
    }

    private List<SituacaoClassificacaoAninhadoOutput> agruparCstsComClassificacoes(List<Object[]> resultados) {
        Map<String, SituacaoClassificacaoAninhadoOutput> cstMap = new LinkedHashMap<>();

        for (Object[] row : resultados) {
            Long idCst = row[0] == null ? null : ((Number) row[0]).longValue();
            String codigoCst = (String) row[1];
            String descricaoCst = (String) row[2];
            String codigoClassif = (String) row[3];
            String descricaoClassif = (String) row[4];

            SituacaoClassificacaoAninhadoOutput cst = cstMap.computeIfAbsent(codigoCst, k ->
                SituacaoClassificacaoAninhadoOutput.builder()
                    .id(idCst)
                    .codigo(codigoCst)
                    .descricao(descricaoCst)
                    .classificacoesTributarias(new ArrayList<>())
                    .build()
            );

            cst.getClassificacoesTributarias().add(
                ClassificacaoTributariaOutput.builder()
                    .codigo(codigoClassif)
                    .descricao(descricaoClassif)
                    .build()
            );
        }

        return new ArrayList<>(cstMap.values());
    }

    private List<TipoDfeClassificacaoDadosAbertosOutput> obterListaDfeClassificacaoTributaria(Long idClassificacaoTributaria, LocalDate data) {
        List<TipoDfeClassificacao> tiposDfeClassificacaoEntity = tipoDfeClassificacaoService
                .buscar(idClassificacaoTributaria, data);

        return tiposDfeClassificacaoEntity
                .stream()
                .map(t -> TipoDfeClassificacaoDadosAbertosOutput
                        .builder()
                        .tipo(t.getTipoDfe().getTipo())
                        .sigla(t.getTipoDfe().getSigla())
                        .descricao(t.getTipoDfe().getDescricao())
                        .build())
                .toList();
    }

    public List<RedutorCompraGovernamentalDadosAbertosOutput> consultarRedutoresCompraGovernamental() {
        return redutorCompraGovernamentalRepository.buscarTodos().stream()
                .map(r -> RedutorCompraGovernamentalDadosAbertosOutput
                        .builder()
                        .valor(r.getValor())
                        .inicioVigencia(r.getInicioVigencia())
                        .fimVigencia(r.getFimVigencia())
                        .build())
                .toList();
    }

    public List<TransferenciaCBSDadosAbertosOutput> consultarTransferenciasCBS() {
        return transferenciaCBSRepository.buscarTodos().stream()
                .map(t -> TransferenciaCBSDadosAbertosOutput
                        .builder()
                        .valor(t.getValor())
                        .inicioVigencia(t.getInicioVigencia())
                        .fimVigencia(t.getFimVigencia())
                        .build())
                .toList();
    }
    
    public List<TransferenciaIBSDadosAbertosOutput> consultarTransferenciasIBS() {
        List<TransferenciaIBSDadosAbertosOutput> lista = new ArrayList<>();
        lista.add(TransferenciaIBSDadosAbertosOutput.builder()
                .valor(BigDecimal.ZERO)
                .inicioVigencia(LocalDate.of(2026, 1, 1))
                .fimVigencia(LocalDate.of(2026, 12, 31))
                .build());
        lista.add(TransferenciaIBSDadosAbertosOutput.builder()
                .valor(BigDecimal.valueOf(100))
                .inicioVigencia(LocalDate.of(2027, 1, 1))
                .fimVigencia(null)
                .build());
        return lista;
    }

    public List<NbsListaDadosAbertosOutput> listarNbsAplicaveisPorClassificacao(String cClassTrib, LocalDate data) {
        ClassificacaoTributariaDTO classificacao = classificacaoTributariaService
                .buscarClassificacaoTributariaCbsIbs(cClassTrib, data);
 
        if (!classificacao.nomenclatura().contains("NBS"))
        {
            return List.of();
        }

        boolean temVinculo = nbsAplicavelRepository.existeVinculoParaClassificacao(classificacao.id()) == 1;
        
        if (!temVinculo) {
            return listarNbs(data);
        }

        List<Object[]> results = nbsAplicavelRepository.listarNbsAplicaveisPorClassificacao(classificacao.id(), data);

        return results.stream()
                .map(row -> NbsListaDadosAbertosOutput.builder()
                        .codigo((String) row[0])
                        .descricao((String) row[1])
                        .build())
                        .toList();
    }

    public List<String> listarClassificacaoAplicavelPorNbs(String nbs, LocalDate data) {
        if (!nbsRepository.existeNbs(nbs, data)) {
            throw new NbsNaoEncontradaException(nbs, data);
        }
        
        List<String> vinculados = nbsAplicavelRepository.listarCodigosClassificacoesVinculadasPorNbs(nbs, data);
        List<String> semVinculo = classificacaoTributariaService.listarCodigosClassificacoesServicoSemVinculoNbs(data);
        List<String> resultado = new ArrayList<>();
        resultado.addAll(vinculados);
        resultado.addAll(semVinculo);
        return resultado;
    }

    public TipoWarningDadosSimulados getWarningDadosSimuladosPorData(LocalDate data) {
        LocalDate dataLimite = LocalDate.of(2027, 1, 1);
        
        if (data.isBefore(dataLimite)) {
            return null;
        }
        
        return TipoWarningDadosSimulados.CASO_GERAL;
    }

    public NbsAplicavelOutput validarNbsAplicavel(String cClassTrib, String nbs, LocalDate dataOcorrenciaFatoGerador) {
        validarNbs(nbs);
        if (nbs.length() != 9) {
            throw new ValidationException("O campo NBS deve ter exatamente 9 dígitos.");
        }
        var classificacao = classificacaoTributariaService.buscarClassificacaoTributariaCbsIbs(cClassTrib, dataOcorrenciaFatoGerador);
        boolean valido;

        try {
            valido = nbsAplicavelService.validarNbsAplicavel(
                nbs,
                classificacao.id(),
                cClassTrib,
                dataOcorrenciaFatoGerador,
                "CBS e IBS"
            );
        } catch (NbsNaoVinculadaException e) {
            valido = false;
        }

        return NbsAplicavelOutput.builder()
            .cClassTrib(cClassTrib)
            .nbs(nbs)
            .dataOcorrenciaFatoGerador(dataOcorrenciaFatoGerador.toString())
            .valido(valido)
            .build();
    }

    public NcmAplicavelOutput validarNcmAplicavel(String cClassTrib, String ncm, LocalDate dataOcorrenciaFatoGerador) {
        validarNcm(ncm);
        if (ncm.length() != 8) {
            throw new ValidationException("O campo NCM deve ter exatamente 8 dígitos.");
        }

        var classificacao = classificacaoTributariaService.buscarClassificacaoTributariaCbsIbs(cClassTrib, dataOcorrenciaFatoGerador);
        boolean valido;

        try {
            valido = ncmAplicavelService.validarNcmAplicavel(
                ncm,
                classificacao.id(),
                cClassTrib,
                dataOcorrenciaFatoGerador,
                "CBS e IBS"
            );
        } catch (NcmNaoVinculadaException e) {
            valido = false;
        }

        return NcmAplicavelOutput.builder()
            .cClassTrib(cClassTrib)
            .ncm(ncm)
            .dataOcorrenciaFatoGerador(dataOcorrenciaFatoGerador.toString())
            .valido(valido)
            .build();
    }

    /**
     * Consulta os tipos de DFe vigentes na data informada, agrupados pela sua
     * categoria (grupo). Grupos e tipos são ordenados sequencialmente e o
     * {@code codigo} de cada tipo é a sigla normalizada, idêntica ao
     * {@code siglaDfe} consumido pelos demais endpoints. Grupos sem tipos
     * vigentes não são incluídos.
     *
     * @param data data de vigência; quando {@code null}, usa a data atual.
     */
    public List<GrupoDfeDadosAbertosOutput> consultarTiposDfeAgrupados(LocalDate data) {
        LocalDate dataConsulta = data != null ? data : LocalDate.now();
        List<Object[]> linhas = grupoDfeRepository.buscarTiposDfeAgrupados(dataConsulta);

        Map<Long, GrupoDfeDadosAbertosOutput> gruposPorId = new LinkedHashMap<>();

        for (Object[] row : linhas) {
            Long grupoId = ((Number) row[0]).longValue();
            String grupoTitulo = (String) row[1];
            String tipoSigla = (String) row[2];
            String tipoTitulo = (String) row[3];
            String modelo = row[4] != null ? String.valueOf(((Number) row[4]).intValue()) : null;

            GrupoDfeDadosAbertosOutput grupo = gruposPorId.computeIfAbsent(grupoId, k ->
                GrupoDfeDadosAbertosOutput.builder()
                    .id(String.valueOf(grupoId))
                    .titulo(grupoTitulo)
                    .tipos(new ArrayList<>())
                    .build()
            );

            grupo.getTipos().add(
                TipoDfeDadosAbertosOutput.builder()
                    .codigo(SiglasDFeEnum.normalizarSigla(tipoSigla))
                    .titulo(tipoTitulo)
                    .modelo(modelo)
                    .ordem(grupo.getTipos().size() + 1)
                    .build()
            );
        }

        int ordemGrupo = 1;
        for (GrupoDfeDadosAbertosOutput grupo : gruposPorId.values()) {
            grupo.setOrdem(ordemGrupo++);
        }

        return new ArrayList<>(gruposPorId.values());
    }

    public NomenclaturaDadosAbertosOutput consultarNomenclatura(String siglaDfe, String cClassTrib, LocalDate data) {
        String nomenclatura = nomenclaturaService.determinarNomenclatura(siglaDfe, cClassTrib, data);

        // Sinais de obrigatoriedade para o frontend: qualquer resultado com
        // anexo (diferente de SEM) exige o preenchimento; para SEM, os campos
        // ncmOpcional/nbsOpcional desambiguam "nenhum campo" de "campo opcional"
        // conforme o documento (NCM opcional em NF-e/NFC-e; NBS opcional em NFS-e).
        boolean sem = NomenclaturaResultEnum.SEM.getValue().equals(nomenclatura);
        SiglasDFeEnum sigla = SiglasDFeEnum.getPorSiglaNormalizada(siglaDfe);

        return NomenclaturaDadosAbertosOutput.builder()
                .siglaDfe(siglaDfe)
                .cClassTrib(cClassTrib)
                .data(data)
                .nomenclatura(nomenclatura)
                .obrigatorio(!sem)
                .ncmOpcional(sem && (sigla == SiglasDFeEnum.NFE || sigla == SiglasDFeEnum.NFCE))
                .nbsOpcional(sem && sigla == SiglasDFeEnum.NFSE)
                .build();
    }

    /**
     * Retorna os atores vigentes na data informada, agrupados por grupo de ator,
     * opcionalmente filtrados pelo papel (Fornecedor/Adquirente). Grupos sem
     * atores vigentes não são incluídos.
     *
     * @param data  data de referência para filtragem de vigência
     * @param papel papel do ator (opcional); quando {@code null}, retorna todos
     */
    public List<GrupoAtorDadosAbertosOutput> consultarAtoresAgrupados(LocalDate data, PapelAtorEnum papel) {
        String papelDb = papel != null ? papel.getValorBanco() : null;
        List<Object[]> linhas = grupoAtorRepository.buscarAtoresAgrupados(data, papelDb);

        Map<Long, GrupoAtorDadosAbertosOutput> gruposPorId = new LinkedHashMap<>();

        for (Object[] row : linhas) {
            Long grupoId = ((Number) row[0]).longValue();
            String grupoDescricao = (String) row[1];
            Integer grupoOrdem = ((Number) row[2]).intValue();
            Long atorId = ((Number) row[3]).longValue();
            String atorDescricao = (String) row[4];
            Integer atorOrdem = ((Number) row[5]).intValue();

            GrupoAtorDadosAbertosOutput grupo = gruposPorId.computeIfAbsent(grupoId, k ->
                GrupoAtorDadosAbertosOutput.builder()
                    .id(grupoId)
                    .descricao(grupoDescricao)
                    .ordem(grupoOrdem)
                    .atores(new ArrayList<>())
                    .build()
            );

            grupo.getAtores().add(
                AtorDadosAbertosOutput.builder()
                    .id(atorId)
                    .descricao(atorDescricao)
                    .ordem(atorOrdem)
                    .build()
            );
        }

        return new ArrayList<>(gruposPorId.values());
    }
}