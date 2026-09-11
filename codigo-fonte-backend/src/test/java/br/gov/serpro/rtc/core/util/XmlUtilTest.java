package br.gov.serpro.rtc.core.util;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.xml.sax.SAXParseException;

import br.gov.serpro.rtc.api.model.xml.enumeration.TipoDocumento;
import br.gov.serpro.rtc.api.model.xml.enumeration.TipoXml;

/**
 * Suíte de testes para validação de XML contra XSD usando {@link XmlUtil}.
 * <p>
 * A classe cobre três dimensões:
 * </p>
 * <ul>
 * <li>validação dos XMLs de exemplo válidos;</li>
 * <li>rejeição de XMLs inválidos derivados dos exemplos válidos por mutação;
 * </li>
 * <li>resolução de schema para todas as combinações de
 * {@link TipoDocumento}/{@link TipoXml}.</li>
 * </ul>
 * <p>
 * Estratégia de mutação para cenários inválidos:
 * </p>
 * <ul>
 * <li>para {@link TipoXml#NOTA}, substitui o primeiro {@code tpAmb} por valor
 * inválido ({@code 9});</li>
 * <li>para {@link TipoXml#GRUPO}, remove uma tag obrigatória mapeada por
 * {@link TipoDocumento}, forçando erro estrutural de conteúdo obrigatório
 * ausente.</li>
 * </ul>
 */
class XmlUtilTest {

	private final XmlUtil xmlUtil = new XmlUtil();

	/**
	 * Garante que todos os exemplos classificados como válidos continuam
	 * compatíveis com o schema do documento/tipo correspondente.
	 */
	@ParameterizedTest(name = "deve validar XML de exemplo {0} para {1}/{2}")
	@MethodSource("xmlsDeExemploExistentes")
	void deveValidarDemaisCenariosDeXmlDeExemplo(String caminhoXml, TipoDocumento tipoDocumento, TipoXml tipoXml)
			throws Exception {
		final var schema = xmlUtil.getSchema(tipoDocumento, tipoXml);
		final var xml = lerXmlDoClasspath(caminhoXml);

		assertThatCode(() -> schema.newValidator().validate(new StreamSource(new StringReader(xml))))
				.doesNotThrowAnyException();
	}

	/**
	 * Gera, em memória, uma variação inválida de cada XML válido e verifica se o
	 * validador acusa erro de schema.
	 */
	@ParameterizedTest(name = "deve rejeitar XML invalido derivado de {0} para {1}/{2}")
	@MethodSource("xmlsDeExemploInvalidos")
	void deveRejeitarCenariosInvalidosDerivadosDosExemplos(String caminhoXml, TipoDocumento tipoDocumento,
			TipoXml tipoXml) throws Exception {
		final var schema = xmlUtil.getSchema(tipoDocumento, tipoXml);
		final var xmlValido = lerXmlDoClasspath(caminhoXml);
		final var xmlInvalido = gerarXmlInvalido(xmlValido, tipoDocumento, tipoXml);

		assertThatThrownBy(() -> schema.newValidator().validate(new StreamSource(new StringReader(xmlInvalido))))
				.isInstanceOf(SAXParseException.class);
	}

	/**
	 * Verifica a criação de {@link Schema} para todas as combinações possíveis de
	 * documento e subtipo XML.
	 */
	@ParameterizedTest(name = "deve resolver schema para {0}/{1} com resultado esperado")
	@MethodSource("todasCombinacoesDeDocumentoETipoXml")
	void deveResolverSchemaParaTodasAsCombinacoesDeDocumentoETipoXml(TipoDocumento tipoDocumento, TipoXml tipoXml)
			throws Exception {
		final Schema schema = xmlUtil.getSchema(tipoDocumento, tipoXml);
		assertThat(schema).isNotNull();
		assertThatCode(schema::newValidator).doesNotThrowAnyException();
	}

	private static Stream<Arguments> todasCombinacoesDeDocumentoETipoXml() {
		return Stream.of(TipoDocumento.values()).flatMap(
				tipoDocumento -> Stream.of(TipoXml.values()).map(tipoXml -> Arguments.of(tipoDocumento, tipoXml)));
	}

	private static Stream<Arguments> xmlsDeExemploExistentes() {
		return exemplosValidos();
	}

	private static Stream<Arguments> xmlsDeExemploInvalidos() {
		return exemplosValidos();
	}

	private static Stream<Arguments> exemplosValidos() {
		return Stream.of(
				Arguments.of("xml/nfe/valido/grupo/nfe_valido.xml", TipoDocumento.NFE, TipoXml.GRUPO),
				Arguments.of("xml/nfe/valido/nota/nfe_valido.xml", TipoDocumento.NFE, TipoXml.NOTA),
				Arguments.of("xml/nfce/valido/grupo/nfce_valido.xml", TipoDocumento.NFCE, TipoXml.GRUPO),
				Arguments.of("xml/nfce/valido/nota/nfce_valido.xml", TipoDocumento.NFCE, TipoXml.NOTA),
				Arguments.of("xml/nfse/valido/grupo/nfse_valido.xml", TipoDocumento.NFSE, TipoXml.GRUPO),
				Arguments.of("xml/nfse/valido/nota/nfse_valido_com_IBSCBS.xml", TipoDocumento.NFSE, TipoXml.NOTA),
				Arguments.of("xml/nfse/valido/nota/nfse_valido_sem_IBSCBS.xml", TipoDocumento.NFSE, TipoXml.NOTA),
				Arguments.of("xml/cte/valido/grupo/cte_valido.xml", TipoDocumento.CTE, TipoXml.GRUPO),
				Arguments.of("xml/cte/valido/nota/cte_valido.xml", TipoDocumento.CTE, TipoXml.NOTA),
				Arguments.of("xml/cte-simplificado/valido/grupo/cte_simplificado_valido.xml", TipoDocumento.CTE_SIMPLIFICADO, TipoXml.GRUPO),
				Arguments.of("xml/cte-simplificado/valido/nota/cte_simplificado_valido.xml", TipoDocumento.CTE_SIMPLIFICADO, TipoXml.NOTA),
				Arguments.of("xml/bpe/valido/grupo/bpe_valido.xml", TipoDocumento.BPE, TipoXml.GRUPO),
				Arguments.of("xml/bpe/valido/nota/bpe_valido.xml", TipoDocumento.BPE, TipoXml.NOTA),
				Arguments.of("xml/bpe-tm/valido/grupo/bpe_tm_valido.xml", TipoDocumento.BPE_TM, TipoXml.GRUPO),
				Arguments.of("xml/bpe-tm/valido/nota/bpe_tm_valido.xml", TipoDocumento.BPE_TM, TipoXml.NOTA),
				Arguments.of("xml/nf3e/valido/grupo/nf3e_valido.xml", TipoDocumento.NF3E, TipoXml.GRUPO),
				Arguments.of("xml/nf3e/valido/nota/nf3e_valido.xml", TipoDocumento.NF3E, TipoXml.NOTA));
	}

	private static String lerXmlDoClasspath(String caminhoXml) throws IOException {
		final var classLoader = XmlUtilTest.class.getClassLoader();
		final var recurso = classLoader.getResource(caminhoXml);
		assertThat(recurso).isNotNull();
		try (var stream = recurso.openStream()) {
			return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
		}
	}

	/**
	 * Aplica mutação determinística para transformar um XML válido em inválido.
	 */
	private static String gerarXmlInvalido(String xmlValido, TipoDocumento tipoDocumento, TipoXml tipoXml) {
		return switch (tipoXml) {
		case NOTA -> substituirPrimeiraOcorrencia(xmlValido, "<tpAmb>", "</tpAmb>", "9");
		case GRUPO -> removerPrimeiraTagCompleta(xmlValido, tagObrigatoriaDoGrupo(tipoDocumento));
		};
	}

	/**
	 * Define a tag obrigatória removida em cenários de grupo para cada tipo de
	 * documento.
	 */
	private static String tagObrigatoriaDoGrupo(TipoDocumento tipoDocumento) {
		return switch (tipoDocumento) {
		case NFE -> "det";
		case NFCE -> "det";
		case NFSE -> "IBSCBS";
		case CTE, CTE_SIMPLIFICADO, BPE -> "imp";
		case BPE_TM -> "detBPeTM";
		case NF3E -> "NFdet";
		};
	}

	/**
	 * Substitui o conteúdo da primeira ocorrência delimitada por tags de início e
	 * fim, preservando o restante do XML.
	 */
	private static String substituirPrimeiraOcorrencia(String xml, String inicio, String fim, String novoValor) {
		final int indiceInicio = xml.indexOf(inicio);
		final int indiceFim = xml.indexOf(fim, indiceInicio);
		assertThat(indiceInicio).isGreaterThanOrEqualTo(0);
		assertThat(indiceFim).isGreaterThan(indiceInicio);
		return xml.substring(0, indiceInicio + inicio.length()) + novoValor + xml.substring(indiceFim);
	}

	/**
	 * Remove a primeira ocorrência completa de uma tag, incluindo conteúdo
	 * interno, aceitando abertura com atributos.
	 */
	private static String removerPrimeiraTagCompleta(String xml, String nomeTag) {
		final String inicio = "<" + nomeTag;
		final String fim = "</" + nomeTag + ">";
		final int indiceInicio = xml.indexOf(inicio);
		final int indiceFimAbertura = xml.indexOf('>', indiceInicio);
		final int indiceFim = xml.indexOf(fim, indiceFimAbertura);
		assertThat(indiceInicio).isGreaterThanOrEqualTo(0);
		assertThat(indiceFimAbertura).isGreaterThan(indiceInicio);
		assertThat(indiceFim).isGreaterThan(indiceFimAbertura);
		return xml.substring(0, indiceInicio) + xml.substring(indiceFim + fim.length());
	}
}
