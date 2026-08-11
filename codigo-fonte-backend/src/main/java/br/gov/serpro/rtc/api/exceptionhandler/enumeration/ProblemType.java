/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.api.exceptionhandler.enumeration;

import java.net.URI;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import br.gov.serpro.rtc.api.exceptionhandler.dto.CodigoErro;
import br.gov.serpro.rtc.api.util.HttpUtils;
import br.gov.serpro.rtc.domain.model.enumeration.EstadoItemEnum;
import br.gov.serpro.rtc.domain.service.exception.AliquotaAdRemNaoEncontradaException;
import br.gov.serpro.rtc.domain.service.exception.AliquotaNaoEncontradaException;
import br.gov.serpro.rtc.domain.service.exception.AliquotaNegativaException;
import br.gov.serpro.rtc.domain.service.exception.AliquotaPadraoNaoEncontradaException;
import br.gov.serpro.rtc.domain.service.exception.AliquotaReferenciaNaoEncontradaException;
import br.gov.serpro.rtc.domain.service.exception.CampoInvalidoException;
import br.gov.serpro.rtc.domain.service.exception.CaptchaException;
import br.gov.serpro.rtc.domain.service.exception.ClassificacaoTributariaNaoEncontradaException;
import br.gov.serpro.rtc.domain.service.exception.ClassificacaoTributariaNaoVinculadaSituacaoTributariaException;
import br.gov.serpro.rtc.domain.service.exception.DataFatoGeradorNaoInformadaException;
import br.gov.serpro.rtc.domain.service.exception.ErroAvaliadorExpressaoAritmeticaException;
import br.gov.serpro.rtc.domain.service.exception.ErroFaltaImplementacaoException;
import br.gov.serpro.rtc.domain.service.exception.ErroGenericoValidacaoException;
import br.gov.serpro.rtc.domain.service.exception.ErroInternoSistemaException;
import br.gov.serpro.rtc.domain.service.exception.ErroXmlException;
import br.gov.serpro.rtc.domain.service.exception.FormaAplicacaoNaoDefinidaException;
import br.gov.serpro.rtc.domain.service.exception.FundamentacaoClassificacaoNaoEncontradaException;
import br.gov.serpro.rtc.domain.service.exception.ImpostoSeletivoInformadoIndevidamenteException;
import br.gov.serpro.rtc.domain.service.exception.ImpostoSeletivoNaoInformadoException;
import br.gov.serpro.rtc.domain.service.exception.IncompatibilidadeSuspensaoException;
import br.gov.serpro.rtc.domain.service.exception.MunicipioNaoEncontradoException;
import br.gov.serpro.rtc.domain.service.exception.MunicipioNaoPertencenteException;
import br.gov.serpro.rtc.domain.service.exception.NbsCompletoNaoInformadoException;
import br.gov.serpro.rtc.domain.service.exception.NbsNaoEncontradaException;
import br.gov.serpro.rtc.domain.service.exception.NbsNaoVinculadaException;
import br.gov.serpro.rtc.domain.service.exception.NcmCompletoNaoInformadoException;
import br.gov.serpro.rtc.domain.service.exception.NcmNaoEncontradaException;
import br.gov.serpro.rtc.domain.service.exception.NcmNaoVinculadaException;
import br.gov.serpro.rtc.domain.service.exception.NcmNbsSimultaneasException;
import br.gov.serpro.rtc.domain.service.exception.NegocioException;
import br.gov.serpro.rtc.domain.service.exception.NomenclaturaException;
import br.gov.serpro.rtc.domain.service.exception.PercentualReducaoNaoEncontradoException;
import br.gov.serpro.rtc.domain.service.exception.SiglaDFeNaoEncontradaException;
import br.gov.serpro.rtc.domain.service.exception.SituacaoTributariaNaoEncontradaException;
import br.gov.serpro.rtc.domain.service.exception.TipoAliquotaDesconhecidoException;
import br.gov.serpro.rtc.domain.service.exception.TratamentoClassificacaoNaoEncontradoException;
import br.gov.serpro.rtc.domain.service.exception.TratamentoTributarioNaoEncontradoException;
import br.gov.serpro.rtc.domain.service.exception.TributacaoRegularInformadaIndevidamenteException;
import br.gov.serpro.rtc.domain.service.exception.TributacaoRegularNaoInformadaException;
import br.gov.serpro.rtc.domain.service.exception.TributoSituacaoTributariaNaoEncontradoException;
import br.gov.serpro.rtc.domain.service.exception.UfNaoEncontradaException;
import lombok.Getter;
import lombok.NonNull;

/**
 * Catálogo de erros REG, CAL e VAL exposto pela API, relacionando exceções,
 * códigos, estados e descrições usados em observabilidade.
 */
@Getter
public enum ProblemType {

    // === REG ===

    CAMPO_INVALIDO(CampoInvalidoException.class,
            "Campo inválido", "campo-invalido",
            new CodigoErro("REG-024", null, EstadoItemEnum.INCONSISTENCIA_ENTRADA),
            "O valor informado para o campo não é válido conforme as regras de negócio."),

    NCM_E_NBS_SIMULTANEAS(NcmNbsSimultaneasException.class,
            "NCM e NBS informadas simultaneamente", "ncm-nbs-simultaneas",
            new CodigoErro("REG-001", null, EstadoItemEnum.INCONSISTENCIA_ENTRADA),
            "Apenas uma nomenclatura (NCM ou NBS) deve ser informada por item. A presença simultânea impede a classificação."),

    NCM_NAO_ENCONTRADO(NcmNaoEncontradaException.class,
            "NCM não encontrada", "ncm-nao-encontrada",
            new CodigoErro("REG-002", "ncm", EstadoItemEnum.INCONSISTENCIA_ENTRADA),
            "O código NCM informado não foi localizado na base de nomenclaturas vigente para a data do fato gerador."),

    NBS_NAO_ENCONTRADO(NbsNaoEncontradaException.class,
            "NBS não encontrada", "nbs-nao-encontrada",
            new CodigoErro("REG-003", "nbs", EstadoItemEnum.INCONSISTENCIA_ENTRADA),
            "O código NBS informado não foi localizado na base de nomenclaturas vigente para a data do fato gerador."),

    SITUACAO_TRIBUTARIA_NAO_ENCONTRADA(SituacaoTributariaNaoEncontradaException.class,
            "Situação tributária não encontrada", "situacao-tributaria-nao-encontrada",
            new CodigoErro("REG-004", "cst", EstadoItemEnum.INCONSISTENCIA_ENTRADA),
            "A situação tributária (CST) informada não existe na base de dados para o tributo e data do fato gerador."),

    CLASSIFICACAO_TRIBUTARIA_NAO_ENCONTRADA(ClassificacaoTributariaNaoEncontradaException.class,
            "Classificação tributária não encontrada", "classificacao-tributaria-nao-encontrada",
            new CodigoErro("REG-005", "cClassTrib", EstadoItemEnum.INCONSISTENCIA_ENTRADA),
            "A classificação tributária informada não foi localizada na base vigente para a data do fato gerador."),

    CLASSIFICACAO_TRIBUTARIA_NAO_VINCULADA_SITUACAO_TRIBUTARIA(
            ClassificacaoTributariaNaoVinculadaSituacaoTributariaException.class,
            "Classificação tributária não vinculada à situação tributária",
            "classificacao-tributaria-nao-vinculada-situacao-tributaria",
            new CodigoErro("REG-006", "cClassTrib", EstadoItemEnum.INCONSISTENCIA_ENTRADA),
            "A classificação tributária informada não está vinculada à situação tributária selecionada."),

    ERRO_NOMENCLATURA(NomenclaturaException.class,
            "Erro de nomenclatura", "erro-nomenclatura",
            new CodigoErro("REG-007", "ncm", EstadoItemEnum.INCONSISTENCIA_ENTRADA),
            "A nomenclatura informada apresenta inconsistência com as regras de classificação tributária vigentes."),

    TRATAMENTO_CLASSIFICACAO_NAO_ENCONTRADO(TratamentoClassificacaoNaoEncontradoException.class,
            "Tratamento de classificação não encontrado", "tratamento-classificacao-nao-encontrado",
            new CodigoErro("REG-008", "cClassTrib", EstadoItemEnum.INCONSISTENCIA_ENTRADA),
            "Não foi encontrado tratamento de classificação tributária para os parâmetros informados."),

    GRUPO_TRIBUTACAO_REGULAR_NAO_INFORMADO(TributacaoRegularNaoInformadaException.class,
            "Grupo de tributação regular não informado", "grupo-tributacao-regular-nao-informado",
            new CodigoErro("REG-009", "tributacaoRegular", EstadoItemEnum.INCONSISTENCIA_ENTRADA),
            "O grupo de tributação regular é obrigatório para a situação tributária selecionada e não foi informado."),

    INCOMPATIBILIDADE_SUSPENSAO(IncompatibilidadeSuspensaoException.class,
            "Incompatibilidade com suspensão", "incompatibilidade-suspensao",
            new CodigoErro("REG-010", "tributacaoRegular/cClassTrib", EstadoItemEnum.INCONSISTENCIA_ENTRADA),
            "A classificação tributária informada é incompatível com a situação de suspensão aplicada ao item."),

    NCM_NAO_VINCULADA(NcmNaoVinculadaException.class,
            "NCM não vinculada", "ncm-nao-vinculada",
            new CodigoErro("REG-011", "ncm", EstadoItemEnum.INCONSISTENCIA_ENTRADA),
            "O código NCM informado não está vinculado a nenhuma classificação tributária vigente."),

    NBS_NAO_VINCULADA(NbsNaoVinculadaException.class,
            "NBS não vinculada", "nbs-nao-vinculada",
            new CodigoErro("REG-012", "nbs", EstadoItemEnum.INCONSISTENCIA_ENTRADA),
            "O código NBS informado não está vinculado a nenhuma classificação tributária vigente."),

    IMPOSTO_SELETIVO_NAO_INFORMADO(ImpostoSeletivoNaoInformadoException.class,
            "Dados do Imposto Seletivo não informados", "dados-imposto-seletivo-nao-informados",
            new CodigoErro("REG-013", "impostoSeletivo", EstadoItemEnum.INCONSISTENCIA_ENTRADA),
            "Os dados do Imposto Seletivo são obrigatórios para a classificação tributária selecionada e não foram informados."),

    IMPOSTO_SELETIVO_INFORMADO_INDEVIDAMENTE(ImpostoSeletivoInformadoIndevidamenteException.class,
            "Dados do Imposto Seletivo informados indevidamente", "dados-imposto-seletivo-informados-indevidamente",
            new CodigoErro("REG-014", "impostoSeletivo", EstadoItemEnum.INCONSISTENCIA_ENTRADA),
            "Os dados do Imposto Seletivo foram informados, mas a classificação tributária selecionada não prevê incidência."),

    NCM_COMPLETO_NAO_INFORMADO(NcmCompletoNaoInformadoException.class,
            "NCM completo não informado", "ncm-completo-nao-informado",
            new CodigoErro("REG-015", "ncm", EstadoItemEnum.INCONSISTENCIA_ENTRADA),
            "O código NCM deve ser informado com todos os dígitos (completo) para a operação solicitada."),

    NBS_COMPLETO_NAO_INFORMADO(NbsCompletoNaoInformadoException.class,
            "NBS completo não informado", "nbs-completo-nao-informado",
            new CodigoErro("REG-016", "nbs", EstadoItemEnum.INCONSISTENCIA_ENTRADA),
            "O código NBS deve ser informado com todos os dígitos (completo) para a operação solicitada."),

    MUNICIPIO_NAO_PERTENCE_UF(MunicipioNaoPertencenteException.class,
            "Município não pertencente à UF", "municipio-nao-pertencente-uf",
            new CodigoErro("REG-018", null, EstadoItemEnum.INCONSISTENCIA_ENTRADA),
            "O município informado não pertence à UF indicada na operação."),

    MUNICIPIO_NAO_ENCONTRADO(MunicipioNaoEncontradoException.class,
            "Município não encontrado", "municipio-nao-encontrado",
            new CodigoErro("REG-019", null, EstadoItemEnum.INCONSISTENCIA_ENTRADA),
            "O código do município informado não foi localizado na base de dados."),

    UF_NAO_ENCONTRADA(UfNaoEncontradaException.class,
            "UF não encontrada", "uf-nao-encontrada",
            new CodigoErro("REG-020", null, EstadoItemEnum.INCONSISTENCIA_ENTRADA),
            "A UF informada para a operação não foi encontrada na base de dados."),

    DATA_FATO_GERADOR_NAO_INFORMADA(DataFatoGeradorNaoInformadaException.class,
            "Data do fato gerador não informada", "fato-gerador-nao-informada",
            new CodigoErro("REG-021", null, EstadoItemEnum.INCONSISTENCIA_ENTRADA),
            "A data do fato gerador é obrigatória e não foi informada na requisição."),

    GRUPO_TRIBUTACAO_REGULAR_INFORMADO_INDEVIDAMENTE(TributacaoRegularInformadaIndevidamenteException.class,
            "Grupo de tributação regular informado indevidamente", "grupo-tributacao-regular-informado-indevidamente",
            new CodigoErro("REG-022", "tributacaoRegular", EstadoItemEnum.INCONSISTENCIA_ENTRADA),
            "O grupo de tributação regular foi informado, mas a situação tributária selecionada não o exige."),

    ERRO_GENERICO_VALIDACAO(ErroGenericoValidacaoException.class,
            "Erro genérico de validação", "erro-generico-validacao",
            new CodigoErro("REG-023", null, EstadoItemEnum.INCONSISTENCIA_ENTRADA),
            "Erro de validação que não se enquadra em nenhuma categoria específica."),

    // === CAL ===

    ALIQUOTA_NAO_ENCONTRADA(AliquotaNaoEncontradaException.class,
            "Alíquota não encontrada", "aliquota-nao-encontrada",
            new CodigoErro("CAL-001", null, EstadoItemEnum.FALHA_TECNICA),
            "Não foi possível localizar a alíquota aplicável para os parâmetros tributários informados."),

    ALIQUOTA_PADRAO_NAO_ENCONTRADA(AliquotaPadraoNaoEncontradaException.class,
            "Alíquota padrão não encontrada", "aliquota-padrao-nao-encontrada",
            new CodigoErro("CAL-002", null, EstadoItemEnum.FALHA_TECNICA),
            "A alíquota padrão (de referência) não foi encontrada na base para o tributo e período informados."),

    ALIQUOTA_REFERENCIA_NAO_ENCONTRADA(AliquotaReferenciaNaoEncontradaException.class,
            "Alíquota de referência não encontrada", "aliquota-referencia-nao-encontrada",
            new CodigoErro("CAL-003", null, EstadoItemEnum.FALHA_TECNICA),
            "A alíquota de referência necessária para o cálculo não está disponível na base de dados."),

    ALIQUOTA_AD_REM_NAO_ENCONTRADA(AliquotaAdRemNaoEncontradaException.class,
            "Alíquota ad rem não encontrada", "aliquota-ad-rem-nao-encontrada",
            new CodigoErro("CAL-004", null, EstadoItemEnum.FALHA_TECNICA),
            "A alíquota ad rem (valor fixo por unidade) não foi localizada para o produto e período informados."),

    ALIQUOTA_NEGATIVA(AliquotaNegativaException.class,
            "Alíquota negativa", "aliquota-negativa",
            new CodigoErro("CAL-005", null, EstadoItemEnum.FALHA_TECNICA),
            "O cálculo resultou em uma alíquota negativa, o que indica inconsistência nos parâmetros tributários."),

    TIPO_ALIQUOTA_DESCONHECIDO(TipoAliquotaDesconhecidoException.class,
            "Tipo de alíquota desconhecido", "tipo-aliquota-desconhecido",
            new CodigoErro("CAL-006", null, EstadoItemEnum.FALHA_TECNICA),
            "O tipo de alíquota retornado pela base de dados não é reconhecido pelo sistema."),

    ERRO_FALTA_IMPLEMENTACAO(ErroFaltaImplementacaoException.class,
            "Classificação tributária em desenvolvimento", "erro-falta-implementacao",
            new CodigoErro("CAL-007", "cClassTrib", EstadoItemEnum.NAO_IMPLEMENTADO),
            "A classificação tributária informada ainda não possui implementação completa no sistema."),

    ERRO_AVALIADOR_EXPRESSAO(ErroAvaliadorExpressaoAritmeticaException.class,
            "Erro no avaliador de expressão aritmética", "erro-avaliador-expressao",
            new CodigoErro("CAL-008", null, EstadoItemEnum.FALHA_TECNICA),
            "Ocorreu um erro ao avaliar a expressão aritmética utilizada no cálculo do tributo."),

    TRATAMENTO_TRIBUTARIO_NAO_ENCONTRADO(TratamentoTributarioNaoEncontradoException.class,
            "Tratamento tributário não encontrado", "tratamento-tributario-nao-encontrado",
            new CodigoErro("CAL-009", null, EstadoItemEnum.FALHA_TECNICA),
            "O tratamento tributário aplicável não foi localizado para a combinação de parâmetros informada."),

    PERCENTUAL_REDUCAO_NAO_ENCONTRADO(PercentualReducaoNaoEncontradoException.class,
            "Percentual de redução não encontrado", "percentual-reducao-nao-encontrado",
            new CodigoErro("CAL-010", null, EstadoItemEnum.FALHA_TECNICA),
            "O percentual de redução de alíquota previsto para a classificação não foi encontrado na base."),

    FUNDAMENTACAO_NAO_ENCONTRADA(FundamentacaoClassificacaoNaoEncontradaException.class,
            "Fundamentação não encontrada", "fundamentacao-nao-encontrada",
            new CodigoErro("CAL-011", null, EstadoItemEnum.FALHA_TECNICA),
            "A fundamentação legal para a classificação tributária não foi localizada na base de dados."),

    TRIBUTO_SITUACAO_TRIBUTARIA_NAO_ENCONTRADO(TributoSituacaoTributariaNaoEncontradoException.class,
            "Tributo/situação tributária não encontrado", "tributo-situacao-tributaria-nao-encontrado",
            new CodigoErro("CAL-012", null, EstadoItemEnum.FALHA_TECNICA),
            "A combinação de tributo e situação tributária informada não foi localizada na base."),

    ERRO_INTERNO_SISTEMA(ErroInternoSistemaException.class,
            "Erro interno do sistema", "erro-interno-de-sistema",
            new CodigoErro("CAL-999", null, EstadoItemEnum.FALHA_TECNICA),
            "Erro interno inesperado durante o processamento do cálculo. Contate o suporte se o problema persistir."),

    // === Sem código de erro ===

    FORMA_APLICACAO_NAO_DEFINIDA(FormaAplicacaoNaoDefinidaException.class,
            "Forma de Aplicação de Percentual não definida", "forma-aplicacao-percentual-nao-definida"),

    ERRO_XML(ErroXmlException.class,
            "Erro de validação de XML", "erro-xml"),

    ERRO_NEGOCIO(NegocioException.class,
            "Violação de regra de negócio", "erro-negocio"),

    ERRO_CAPTCHA(CaptchaException.class,
            "Erro de Captcha", "erro-captcha"),

    METHOD_ARGUMENT_TYPE_MISMATCH(MethodArgumentTypeMismatchException.class,
            "Tipo de argumento inválido", "tipo-argumento-invalido"),

    SIGLA_DFE_NAO_ENCONTRADA(SiglaDFeNaoEncontradaException.class,
            "Sigla DFe não reconhecida", "sigla-dfe-nao-reconhecida"),

    ERRO_SISTEMA(Exception.class,
            "Erro de sistema não previsto", "erro-de-sistema-nao-previsto");

    private final Class<? extends Exception> classeErro;
    private final String titulo;
    private final String path;
    private final CodigoErro codigoErro;
    private final String descricao;

    ProblemType(Class<? extends Exception> classeErro, String titulo, String path) {
        this(classeErro, titulo, path, null, null);
    }

    ProblemType(Class<? extends Exception> classeErro, String titulo, String path, CodigoErro codigoErro) {
        this(classeErro, titulo, path, codigoErro, null);
    }

    ProblemType(Class<? extends Exception> classeErro, String titulo, String path, CodigoErro codigoErro, String descricao) {
        this.classeErro = classeErro;
        this.titulo = titulo;
        this.path = path;
        this.codigoErro = codigoErro;
        this.descricao = descricao;
    }

    public URI getURI() {
        return URI.create(String.format("%s/errors/%s", HttpUtils.getBaseURL(), path));
    }

    public static <E extends Exception> ProblemType from(@NonNull final E e) {
        Class<? extends Exception> classeErro = e.getClass();
        return Stream.of(values()).filter(t -> t.getClasseErro().equals(classeErro)).findFirst().orElse(ERRO_SISTEMA);
    }

    public static Optional<ProblemType> fromCodigo(String codigo) {
        if (codigo == null) {
            return Optional.empty();
        }
        return Stream.of(values())
                .filter(t -> t.codigoErro != null && t.codigoErro.codigo().equals(codigo))
                .findFirst();
    }

}
