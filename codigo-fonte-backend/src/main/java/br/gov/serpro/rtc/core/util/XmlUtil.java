package br.gov.serpro.rtc.core.util;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.util.Objects;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXException;

import br.gov.serpro.rtc.api.model.xml.enumeration.TipoDocumento;
import br.gov.serpro.rtc.api.model.xml.enumeration.TipoXml;
import lombok.extern.slf4j.Slf4j;

/**
 * Utilitario responsavel por localizar schemas XSD no classpath, compila-los em
 * objetos {@link Schema} reutilizaveis e apoiar a validacao segura de XML.
 * <p>
 * O objetivo desta classe e combinar tres requisitos que normalmente entram em
 * tensao entre si:
 * </p>
 * <ul>
 * <li>permitir validacao baseada em XSD empacotado com a aplicacao;</li>
 * <li>preservar o funcionamento de {@code xs:include} e {@code xs:import}
 * entre arquivos internos do projeto;</li>
 * <li>bloquear acesso a recursos externos para reduzir superficie de XXE e de
 * resolucao indevida de schemas por protocolo.</li>
 * </ul>
 * <p>
 * Motivacao das principais classes e interfaces utilizadas nesta implementacao:
 * </p>
 * <ul>
 * <li>{@link SchemaFactory}: API padrao do JAXP para compilar XSD em objetos
 * {@link Schema}; concentra a configuracao de seguranca e o resolver
 * customizado.</li>
 * <li>{@link Schema}: representacao compilada e reutilizavel do XSD, adequada
 * para cache e posterior criacao de validators.</li>
 * <li>{@link StreamSource}: encapsula o fluxo do XSD raiz e seu
 * {@code systemId}, permitindo ao parser resolver referencias relativas de forma
 * deterministica.</li>
 * <li>{@link XMLConstants}: fornece as constantes oficiais do namespace XML
 * Schema e das propriedades de seguranca, evitando strings magicas.</li>
 * <li>{@link LSResourceResolver}: ponto de extensao usado para interceptar a
 * resolucao de includes/imports e limita-la ao classpath da aplicacao.</li>
 * <li>{@link LSInput}: formato esperado pelo parser para consumir o conteudo
 * retornado pelo resolver customizado.</li>
 * <li>{@link ClassLoader}: mecanismo padrao da JVM para localizar recursos
 * empacotados em {@code src/main/resources} ou equivalentes no artefato final.</li>
 * <li>{@link URL} e {@link InputStream}: usados para abrir o XSD raiz e os XSDs
 * referenciados de maneira controlada, sem expor protocolos externos.</li>
 * <li>{@link URI}: usada para normalizar caminhos relativos e impedir escapes de
 * escopo com segmentos como {@code ..}.</li>
 * </ul>
 * <p>
 * A classe nao mantem estado mutavel compartilhado entre chamadas. O objeto
 * retornado ({@link Schema}) e o elemento cacheado; a configuracao de parser e o
 * resolver sao recriados a cada compilacao do schema.
 * </p>
 */
@Slf4j
@Component
public class XmlUtil {

    private static final String CLASSPATH_URI_PREFIX = "classpath:/";

    /**
     * Localiza, compila e retorna o {@link Schema} associado ao tipo e subtipo
     * informados.
     * <p>
     * O resultado e cacheado por {@link Cacheable} para evitar recompilacao de
     * XSD a cada validacao, reduzindo custo de CPU e de acesso a recurso.
     * </p>
     * <p>
     * Estrategia adotada neste metodo:
     * </p>
     * <ul>
     * <li>monta o caminho do XSD raiz a partir de {@link TipoDocumento} e
     * {@link TipoXml};</li>
     * <li>carrega esse XSD exclusivamente do classpath da aplicacao;</li>
     * <li>configura a {@link SchemaFactory} com hardening contra resolucao
     * externa de DTD e schema;</li>
     * <li>registra um {@link ClasspathResourceResolver} para permitir somente
     * imports/includes internos e controlados;</li>
     * <li>fornece um {@link StreamSource} com {@code systemId} no formato
     * {@code classpath:/...}, o que permite resolver referencias relativas sem
     * depender de {@code file:}, {@code jar:} ou rede.</li>
     * </ul>
     * <p>
     * Configuracoes de seguranca relevantes:
     * </p>
     * <ul>
     * <li>{@link XMLConstants#FEATURE_SECURE_PROCESSING}: ativa limites
     * defensivos do parser.</li>
     * <li>{@link XMLConstants#ACCESS_EXTERNAL_DTD}: definido como vazio para
     * bloquear DTD externo.</li>
     * <li>{@link XMLConstants#ACCESS_EXTERNAL_SCHEMA}: definido como vazio para
     * bloquear carregamento de schema por protocolo externo.</li>
     * <li>{@code disallow-doctype-decl}: aplicado quando o parser suporta essa
     * feature especifica; se nao suportar, a classe continua operando com as
     * protecoes padrao acima.</li>
     * </ul>
     * <p>
     * Restricao funcional importante: includes/imports so podem apontar para
     * recursos dentro do escopo do diretorio base do XSD raiz. Referencias para
     * caminhos externos, absolutos ou fora desse escopo sao rejeitadas.
     * </p>
     *
     * @param tipo categoria logica do documento, usada para compor o caminho do
     *             schema no classpath
     * @param subtipo arquivo de schema especifico dentro da categoria do
     *                documento
     * @return schema compilado e pronto para criacao de validators
     * @throws NullPointerException se tipo ou subtipo forem nulos
     * @throws IOException se o XSD raiz nao puder ser localizado ou aberto no
     *                     classpath
     * @throws SAXException se a compilacao do schema falhar por erro estrutural
     *                      do XSD ou por violacao de restricao do parser
     */
    @Cacheable(cacheNames = "XmlUtil.getSchema")
    public Schema getSchema(TipoDocumento tipo, TipoXml subtipo) throws IOException, SAXException {
        Objects.requireNonNull(tipo, "tipo nao pode ser nulo");
        Objects.requireNonNull(subtipo, "subtipo nao pode ser nulo");

        // obter o arquivo XSD como URL do classpath
        final ClassLoader classLoader = getClass().getClassLoader();
        final var path = String.format("xml/%s/%s", tipo.getMnemonico(), subtipo.getMnemonico());
        final var rootDir = path.substring(0, path.lastIndexOf('/') + 1);
        final URL xsdUrl = classLoader.getResource(path);
        if (xsdUrl == null) {
            throw new IOException("XSD file not found in classpath");
        }

        // usar o caminho do arquivo para que os includes dos arquivos XSD sejam
        // resolvidos corretamente
        final SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        // Endurece o parser para evitar XXE e outros acessos externos
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        factory.setResourceResolver(new ClasspathResourceResolver(classLoader, rootDir));

        // Essa feature e especifica de alguns parsers (ex.: Xerces). Quando nao
        // estiver disponivel, as protecoes padrao acima ainda mantem DTD/schema
        // externo bloqueados.
        try {
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        } catch (SAXNotRecognizedException | SAXNotSupportedException e) {
        	log.error("ERRO: parser atual nao suporta essa feature especifica", e);
        }
        try (InputStream xsdStream = xsdUrl.openStream()) {
            return factory.newSchema(new StreamSource(xsdStream, CLASSPATH_URI_PREFIX + path));
        }
    }

    /**
     * Resolver de recursos XSD que converte {@code xs:include} e
     * {@code xs:import} em leituras controladas do classpath.
     * <p>
     * Esta classe existe para permitir includes/imports locais sem reabrir o
     * parser para protocolos externos. O resolver aceita apenas referencias que,
     * apos normalizacao, continuem dentro do diretorio base do schema raiz.
     * </p>
     * <p>
     * Com isso, a resolucao de schemas segue funcional para estruturas como
     * {@code ./originais/arquivo.xsd}, mas rejeita referencias externas ou fora
     * do escopo permitido.
     * </p>
     */
    private static final class ClasspathResourceResolver implements LSResourceResolver {

        private final ClassLoader classLoader;
        private final String rootDir;

        private ClasspathResourceResolver(ClassLoader classLoader, String rootDir) {
            this.classLoader = classLoader;
            this.rootDir = rootDir;
        }

        /**
         * Resolve um recurso solicitado pelo parser para um recurso do
         * classpath.
         * <p>
         * O fluxo e o seguinte:
         * </p>
         * <ul>
         * <li>recusa imediatamente identificadores externos ou malformados;</li>
         * <li>converte {@code systemId} e {@code baseURI} em um caminho interno
         * normalizado;</li>
         * <li>verifica se o caminho permanece dentro do escopo permitido;</li>
         * <li>carrega o recurso do classpath e o devolve como {@link LSInput}.</li>
         * </ul>
         * <p>
         * Quando o {@code systemId} aponta para protocolo externo, o metodo
         * retorna {@code null}; o parser continua bloqueado pelas propriedades de
         * seguranca configuradas na {@link SchemaFactory}.
         * </p>
         */
        @Override
        public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
            if (systemId == null || hasExternalScheme(systemId)) {
                return null;
            }

            final String classpathLocation = toClasspathLocation(systemId, baseURI, rootDir);
            final URL resourceUrl = classLoader.getResource(classpathLocation);
            if (resourceUrl == null) {
                throw new IllegalArgumentException(
                        "Schema referenciado nao encontrado no classpath: " + classpathLocation);
            }

            try (InputStream resourceStream = resourceUrl.openStream()) {
                final String resourceSystemId = CLASSPATH_URI_PREFIX + classpathLocation;
                return new SimpleLSInput(publicId, resourceSystemId, resourceStream.readAllBytes());
            } catch (IOException e) {
                throw new IllegalStateException("Erro ao carregar XSD do classpath: " + classpathLocation, e);
            }
        }

        /**
         * Converte a referencia recebida do parser para um caminho interno de
         * classpath.
         * <p>
         * O metodo aceita tres formas de entrada:
         * </p>
         * <ul>
         * <li>{@code classpath:/...} explicito;</li>
         * <li>caminho relativo resolvido a partir de um {@code baseURI}
         * {@code classpath:/...};</li>
         * <li>caminho relativo simples, combinado com o diretorio base do schema
         * raiz.</li>
         * </ul>
         * <p>
         * Em todos os casos a URI resultante e normalizada e validada para evitar
         * escape de escopo por {@code ..} ou referencia equivalente.
         * </p>
         */
        private static String toClasspathLocation(String systemId, String baseURI, String fallbackRootDir) {
            final String candidateLocation;
            if (systemId.startsWith(CLASSPATH_URI_PREFIX)) {
                candidateLocation = systemId.substring(CLASSPATH_URI_PREFIX.length());
            } else if (baseURI != null && baseURI.startsWith(CLASSPATH_URI_PREFIX)) {
                final URI base = URI.create(baseURI);
                final URI resolved = base.resolve(systemId).normalize();
                if (!resolved.toString().startsWith(CLASSPATH_URI_PREFIX)) {
                    throw new IllegalArgumentException("Referencia de schema invalida: " + systemId);
                }
                candidateLocation = resolved.toString().substring(CLASSPATH_URI_PREFIX.length());
            } else {
                candidateLocation = normalizeRelativeClasspathLocation(fallbackRootDir + systemId);
            }

            if (!candidateLocation.startsWith(fallbackRootDir)) {
                throw new IllegalArgumentException("Referencia de schema fora do escopo permitido: " + systemId);
            }

            return candidateLocation;
        }

        /**
         * Normaliza um caminho relativo de classpath usando {@link URI} para
         * colapsar segmentos redundantes e padronizar a representacao final.
         */
        private static String normalizeRelativeClasspathLocation(String location) {
            final URI normalized = URI.create(CLASSPATH_URI_PREFIX + location).normalize();
            return normalized.toString().substring(CLASSPATH_URI_PREFIX.length());
        }

        /**
         * Detecta se o identificador recebido representa uma URI absoluta fora do
         * esquema {@code classpath}.
         * <p>
         * Referencias invalidas tambem sao tratadas como nao confiaveis para que
         * nao sejam processadas como caminhos locais ambiguos.
         * </p>
         */
        private static boolean hasExternalScheme(String systemId) {
            try {
                final URI uri = URI.create(systemId);
                return uri.isAbsolute() && !"classpath".equalsIgnoreCase(uri.getScheme());
            } catch (IllegalArgumentException e) {
            	log.warn("Identificador de recurso malformado, tratando como nao confiavel: " + systemId, e);
                return true;
            }
        }
    }

    /**
     * Implementacao enxuta de {@link LSInput} usada para entregar ao parser o
     * conteudo de um XSD resolvido pelo {@link ClasspathResourceResolver}.
     * <p>
     * O conteudo e materializado em memoria no construtor para desacoplar o
     * ciclo de vida do recurso do parser e evitar depender de quando o parser
     * fecha o stream subjacente.
     * </p>
     * <p>
     * Nesta implementacao o parser consome principalmente:
     * </p>
     * <ul>
     * <li>{@link #getByteStream()}: bytes do schema resolvido;</li>
     * <li>{@link #getSystemId()}: identificador logico usado em resolucoes
     * subsequentes;</li>
     * <li>{@link #getPublicId()}: metadado opcional fornecido pelo parser.</li>
     * </ul>
     * <p>
     * Os demais metodos retornam valores neutros porque nao sao necessarios para
     * o fluxo adotado nesta classe.
     * </p>
     */
    private static final class SimpleLSInput implements LSInput {

        private String publicId;
        private String systemId;
        private InputStream byteStream;

        private SimpleLSInput(String publicId, String systemId, byte[] resourceBytes) {
            this.publicId = publicId;
            this.systemId = systemId;
            this.byteStream = new ByteArrayInputStream(resourceBytes);
        }

        @Override
        public Reader getCharacterStream() {
            return null;
        }

        @Override
        public void setCharacterStream(Reader characterStream) {
            // not used
        }

        @Override
        public InputStream getByteStream() {
            return byteStream;
        }

        @Override
        public void setByteStream(InputStream byteStream) {
            this.byteStream = byteStream;
        }

        @Override
        public String getStringData() {
            return null;
        }

        @Override
        public void setStringData(String stringData) {
            // not used
        }

        @Override
        public String getSystemId() {
            return systemId;
        }

        @Override
        public void setSystemId(String systemId) {
            this.systemId = systemId;
        }

        @Override
        public String getPublicId() {
            return publicId;
        }

        @Override
        public void setPublicId(String publicId) {
            this.publicId = publicId;
        }

        @Override
        public String getBaseURI() {
            return null;
        }

        @Override
        public void setBaseURI(String baseURI) {
            // not used
        }

        @Override
        public String getEncoding() {
            return null;
        }

        @Override
        public void setEncoding(String encoding) {
            // not used
        }

        @Override
        public boolean getCertifiedText() {
            return false;
        }

        @Override
        public void setCertifiedText(boolean certifiedText) {
            // not used
        }
    }

}
