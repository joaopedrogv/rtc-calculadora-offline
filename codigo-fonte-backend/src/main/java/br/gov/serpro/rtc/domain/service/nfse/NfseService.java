/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service.nfse;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.util.List;


import org.springframework.stereotype.Service;

import br.gov.serpro.rtc.api.model.input.nfse.NfseBaseCalculoInput;
import br.gov.serpro.rtc.api.model.input.nfse.NfseValidacaoIndicadorOperacaoInput;
import br.gov.serpro.rtc.api.model.output.nfse.NfseBaseCalculoOutput;
import br.gov.serpro.rtc.api.model.output.nfse.NfseSituacaoClassificacaoOutput;
import br.gov.serpro.rtc.api.model.output.nfse.NfseIndicadorOperacaoOutput;
import br.gov.serpro.rtc.api.model.output.nfse.NfseCodigoDescricaoOutput;
import br.gov.serpro.rtc.api.model.output.nfse.NfseLocalOperacaoOutput;
import br.gov.serpro.rtc.api.model.output.nfse.NfseValidacaoIndicadorOperacaoOutput;
import br.gov.serpro.rtc.domain.model.enumeration.LocalFornecimento;
import br.gov.serpro.rtc.domain.model.enumeration.LocalIncidencia;
import br.gov.serpro.rtc.domain.repository.IndicadorOperacaoRepository;
import br.gov.serpro.rtc.domain.service.NbsService;
import br.gov.serpro.rtc.domain.service.exception.CampoInvalidoException;
import br.gov.serpro.rtc.domain.service.exception.IndicadorOperacaoNaoEncontradoException;
import br.gov.serpro.rtc.domain.service.exception.NbsNaoEncontradaException;
import lombok.RequiredArgsConstructor;


/**
 * Serviço responsável por apoiar cenários de NFS-e, calculando base de cálculo
 * e consultando indicadores, local da operação e classificações por NBS.
 */
@RequiredArgsConstructor
@Service
public class NfseService {
    
    private static final int ANO_INICIO_CBS_IBS = 2026;

    private static final int ANO_EXTINCAO_PIS_COFINS = 2027;
    private static final int ANO_EXTINCAO_ISS = 2033;

    private static final String CCLASSTRIB_FALLBACK = "000001";
    
    private static final String MENSAGEM_ERRO_CBS_IBS = "O ano do fato gerador deve ser %d ou superior".formatted(ANO_INICIO_CBS_IBS);

    private final IndicadorOperacaoRepository indicadorOperacaoRepository;
    private final NbsService nbsService;

    
    /**
     * Afere a base de cálculo para NFS-e, considerando os campos que integram e não
     * integram a base de cálculo
     * 
     * @param input
     * @return
     */
    public NfseBaseCalculoOutput calcularBaseCalculo(NfseBaseCalculoInput input) {
        
        final var anoFatoGerador = input.getAnoFatoGerador();
        validaAnoInicioTributo(anoFatoGerador, ANO_INICIO_CBS_IBS, MENSAGEM_ERRO_CBS_IBS);
        
        validaValoresForaLimiteVigenciaPermitido(Map.of(
                "PIS", input.getPis(),
                "COFINS", input.getCofins()), 
                anoFatoGerador, 
                ANO_EXTINCAO_PIS_COFINS);
        
        validaValoresForaLimiteVigenciaPermitido(Map.of(
                "ISS", input.getIss()), 
                anoFatoGerador, 
                ANO_EXTINCAO_ISS);
        
        BigDecimal integra = input.getValorServico();
        
        BigDecimal naoIntegra = input.getDescontoIncondicional()
                .add(input.getVCalcReeRepRes())
                .add(input.getVCalcDedRedIBSCBS())
                .add(input.getPis())
                .add(input.getCofins())
                .add(input.getIss());

        BigDecimal baseCalculo = integra.subtract(naoIntegra);
        
        validaValorFinalBaseCalculo(baseCalculo);

        return NfseBaseCalculoOutput.builder()
                .baseCalculo(baseCalculo)
                .build();
    }


    /**
     * Formata o código de tributação nacional (cTribNac) adicionando pontos nos locais apropriados.
     * Remove zeros à esquerda do primeiro segmento (ex: 01.01 -> 1.01, 03.04 -> 3.04).
     * 
     * @param cTribNac String com 4 ou 6 dígitos
     * @return String formatada com pontos adicionados e sem zeros à esquerda no primeiro segmento, ou null se entrada for nula/vazia
     */
    private static String formatarCTribNac(String cTribNac) {
        if (cTribNac == null || cTribNac.isEmpty()) {
            return null;
        }
        
        if (cTribNac.length() == 4) {
            String primeiroNum = String.valueOf(Integer.parseInt(cTribNac.substring(0, 2)));
            return primeiroNum + "." + cTribNac.substring(2, 4);
        } else if (cTribNac.length() == 6) {
            String primeiroNum = String.valueOf(Integer.parseInt(cTribNac.substring(0, 2)));
            return primeiroNum + "." + cTribNac.substring(2, 4) + "." + cTribNac.substring(4, 6);
        }
        
        return null;
    }

    public NfseValidacaoIndicadorOperacaoOutput validarIndicadorOperacao(NfseValidacaoIndicadorOperacaoInput input) {
        String cTribNacFormatado = formatarCTribNac(input.getCTribNac());
        
        int resultado = indicadorOperacaoRepository.validarCombinacaoExiste(
                input.getNbs(),
                input.getCClassTrib(),
                input.getCIndOp(),
                cTribNacFormatado,
                input.getDataOcorrenciaFatoGerador().toString());
        
        boolean combinacaoValida = resultado == 1;
        
        return NfseValidacaoIndicadorOperacaoOutput.builder()
                .cIndOp(input.getCIndOp())
                .cClassTrib(input.getCClassTrib())
                .nbs(input.getNbs())
                .cTribNac(input.getCTribNac())
                .dataOcorrenciaFatoGerador(input.getDataOcorrenciaFatoGerador())
                .valido(combinacaoValida)
                .build();
    }

    public NfseLocalOperacaoOutput consultarLocalOperacao(String cIndOp, LocalDate dataOcorrenciaFatoGerador) {
        Object[] resultado = indicadorOperacaoRepository.buscarLocalOperacaoPorIndOp(
            cIndOp, 
            dataOcorrenciaFatoGerador.toString());

        if (resultado == null || resultado.length == 0) {
            throw new IndicadorOperacaoNaoEncontradoException(cIndOp, dataOcorrenciaFatoGerador);
        }

        Object[] resultadoQuery = (Object[]) resultado[0];

        LocalFornecimento localFornecimento = resultadoQuery[0] != null
                ? LocalFornecimento.fromCodigo(((Number) resultadoQuery[0]).intValue())
                : null;
        LocalIncidencia localIncidencia = resultadoQuery[1] != null
                ? LocalIncidencia.fromCodigo(((Number) resultadoQuery[1]).intValue())
                : null;

	return NfseLocalOperacaoOutput.builder()
		.cIndOp(cIndOp)
		.dataOcorrenciaFatoGerador(dataOcorrenciaFatoGerador)
		.codigoLocalFornecimento(localFornecimento)
		.localFornecimento(localFornecimento != null ? localFornecimento.getDescricao() : null)
		.codigoLocalIncidencia(localIncidencia)
		.localIncidencia(localIncidencia != null ? localIncidencia.getDescricao() : null)
		.build();
    }
    
    public List<NfseIndicadorOperacaoOutput> consultarIndicadorOperacao(LocalDate dataOcorrenciaFatoGerador, String nbs) {
        if (nbs != null && !nbs.isEmpty()) {
            if (!nbsService.existeNbs(nbs, dataOcorrenciaFatoGerador)) {
                throw new NbsNaoEncontradaException(nbs, dataOcorrenciaFatoGerador);
            }
            List<Object[]> rawData = indicadorOperacaoRepository.buscarIndicadorOperacaoPorDataComNbs(dataOcorrenciaFatoGerador, nbs);
            if (rawData == null || rawData.isEmpty()) {
                rawData = indicadorOperacaoRepository.buscarIndicadorOperacaoPorData(dataOcorrenciaFatoGerador);
            }
            return rawData.stream()
                    .map(this::mapToIndicadorOperacaoOutput)
                    .toList();
        } else {
            List<Object[]> rawData = indicadorOperacaoRepository.buscarIndicadorOperacaoPorData(dataOcorrenciaFatoGerador);
            return rawData.stream()
                    .map(this::mapToIndicadorOperacaoOutput)
                    .toList();
        }
    }
    
	private NfseIndicadorOperacaoOutput mapToIndicadorOperacaoOutput(Object[] row) {
		LocalFornecimento lf = row[2] != null ? LocalFornecimento.fromCodigo(((Number) row[2]).intValue()) : null;
		LocalIncidencia li = row[4] != null ? LocalIncidencia.fromCodigo(((Number) row[4]).intValue()) : null;
		return NfseIndicadorOperacaoOutput.builder()
				.cIndOp((String) row[0])
				.tipoOperacao((String) row[1])
				.codigoLocalFornecimento(lf)
				.localFornecimento(lf != null ? lf.getDescricao() : null)
				.codigoLocalIncidencia(li)
				.localIncidencia(li != null ? li.getDescricao() : null)
				.prestacaoServicoOnerosa(row[5] != null ? ((Number) row[5]).intValue() == 1 : null)
				.adquirenteExterior(row[6] != null ? ((Number) row[6]).intValue() == 1 : null)
				.build();
	}
    
    public List<NfseCodigoDescricaoOutput> consultarLocaisFornecimento(Integer codigo) {
        if (codigo != null) {
            try {
                LocalFornecimento lf = LocalFornecimento.fromCodigo(codigo);
                return List.of(NfseCodigoDescricaoOutput.builder()
                        .codigo(lf.getCodigo())
                        .descricao(lf.getDescricao())
                        .build());
            } catch (IllegalArgumentException e) {
                throw new CampoInvalidoException(e.getMessage());
            }
        }
        return Arrays.stream(LocalFornecimento.values())
                .map(lf -> NfseCodigoDescricaoOutput.builder()
                        .codigo(lf.getCodigo())
                        .descricao(lf.getDescricao())
                        .build())
                .toList();
    }

    public List<NfseCodigoDescricaoOutput> consultarLocaisIncidencia(Integer codigo) {
        if (codigo != null) {
            try {
                LocalIncidencia li = LocalIncidencia.fromCodigo(codigo);
                return List.of(NfseCodigoDescricaoOutput.builder()
                        .codigo(li.getCodigo())
                        .descricao(li.getDescricao())
                        .build());
            } catch (IllegalArgumentException e) {
                throw new CampoInvalidoException(e.getMessage());
            }
        }
        return Arrays.stream(LocalIncidencia.values())
                .map(li -> NfseCodigoDescricaoOutput.builder()
                        .codigo(li.getCodigo())
                        .descricao(li.getDescricao())
                        .build())
                .toList();
    }

    public List<NfseSituacaoClassificacaoOutput> consultarSituacoesClassificacoesPorNbs(String nbs, LocalDate data) {
        if (!nbsService.existeNbs(nbs, data)) {
            throw new NbsNaoEncontradaException(nbs, data);
        }

        String dataStr = data.toString();

        List<NfseSituacaoClassificacaoOutput> resultado =
                indicadorOperacaoRepository.buscarSituacoesClassificacoesPorNbsEData(nbs, dataStr);

        if (resultado == null || resultado.isEmpty()) {
            NfseSituacaoClassificacaoOutput fallback =
                    indicadorOperacaoRepository.buscarSituacaoClassificacaoPorClassificacao(CCLASSTRIB_FALLBACK, dataStr);
            if (fallback == null) {
                return List.of();
            }
            resultado = List.of(fallback);
        }

        return resultado;
    }
    
    /**
     * Valida se o ano do fato gerador é inferior ao ano de início do tributo. Nesse
     * caso, lança uma exceção com a mensagem de erro informada.
     * 
     * @param anoFatoGerador
     * @param anoInicio
     * @param mensagemErro
     */
    protected void validaAnoInicioTributo(final Integer anoFatoGerador, final int anoInicio,
            final String mensagemErro) {
        if (anoFatoGerador < anoInicio) {
            throw new CampoInvalidoException(mensagemErro);
        }
    }
    
    /**
     * Valida se foram enviados os valores diferentes de zero para os campos
     * contidos no mapa caso o ano do fato gerador informado seja igual ou superior
     * ao ano de extinção informado.
     * 
     * @param campos
     * @param anoFatoGerador
     * @param anoExtincao
     */
    protected void validaValoresForaLimiteVigenciaPermitido(Map<String, BigDecimal> campos, int anoFatoGerador,
            int anoExtincao) {
        if (anoFatoGerador >= anoExtincao) {
            final String camposInformados = campos
                    .entrySet()
                    .stream()
                    .filter(entry -> ehDiferenteDeZero(entry.getValue()))
                    .map(Map.Entry::getKey)
                    .sorted()
                    .collect(Collectors.joining(", "));

            if (!camposInformados.isEmpty()) {
                throw new CampoInvalidoException("Os seguintes campos não podem ser informados a partir de %d: %s"
                        .formatted(anoExtincao, camposInformados));
            }
        }
    }
    
    /**
     * Valida se o valor final da base de cálculo é negativo. Caso seja, lança uma
     * exceção
     * 
     * @param baseCalculo
     */
    protected void validaValorFinalBaseCalculo(BigDecimal baseCalculo) {
        if (baseCalculo.compareTo(BigDecimal.ZERO) < 0) {
            throw new CampoInvalidoException(
                    "Base de cálculo não pode ser negativa. Valores que não integram a base são maiores que o valor do serviço.");
        }
    }
    
    protected boolean ehDiferenteDeZero(BigDecimal valor) {
        return valor != null && valor.compareTo(BigDecimal.ZERO) != 0;
    }

}
