package br.gov.serpro.rtc.api.exceptionhandler.enumeration;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;

/**
 * Enumera os códigos VAL associados às anotações de Bean Validation e permite
 * consultas pelo nome da anotação ou pelo código catalogado.
 */
@Getter
public enum CodigoValidacao {

    NOT_NULL        ("NotNull",        "VAL-001",
            "Campo obrigatório não informado",
            "O campo marcado como obrigatório não foi preenchido na requisição."),
    NOT_BLANK       ("NotBlank",       "VAL-002",
            "Campo obrigatório em branco",
            "O campo não pode estar em branco ou conter apenas espaços."),
    NOT_EMPTY       ("NotEmpty",       "VAL-003",
            "Lista ou campo não pode estar vazio",
            "O campo ou lista informado não pode estar vazio."),
    SIZE            ("Size",           "VAL-004",
            "Tamanho fora do intervalo permitido",
            "O valor informado não atende aos limites de tamanho mínimo e/ou máximo definidos."),
    MIN             ("Min",            "VAL-005",
            "Valor abaixo do mínimo permitido",
            "O valor informado é inferior ao mínimo aceito para o campo."),
    MAX             ("Max",            "VAL-006",
            "Valor acima do máximo permitido",
            "O valor informado é superior ao máximo aceito para o campo."),
    DECIMAL_MIN     ("DecimalMin",     "VAL-007",
            "Valor decimal abaixo do mínimo permitido",
            "O valor decimal informado é inferior ao mínimo aceito para o campo."),
    DECIMAL_MAX     ("DecimalMax",     "VAL-008",
            "Valor decimal acima do máximo permitido",
            "O valor decimal informado é superior ao máximo aceito para o campo."),
    PATTERN         ("Pattern",        "VAL-009",
            "Formato inválido",
            "O valor informado não atende ao padrão (formato) exigido para o campo."),
    DIGITS          ("Digits",         "VAL-010",
            "Quantidade de dígitos inválida",
            "O valor informado não possui a quantidade de dígitos inteiros e/ou decimais esperada."),
    EMAIL           ("Email",          "VAL-011",
            "E-mail inválido",
            "O endereço de e-mail informado não é válido."),
    POSITIVE        ("Positive",       "VAL-012",
            "Valor deve ser positivo",
            "O valor informado deve ser um número positivo (maior que zero)."),
    POSITIVE_OR_ZERO("PositiveOrZero", "VAL-013",
            "Valor deve ser positivo ou zero",
            "O valor informado deve ser um número positivo ou zero."),
    ASSERT_TRUE     ("AssertTrue",     "VAL-014",
            "Condição não atendida",
            "A condição de validação (assert true) não foi atendida pelo valor informado."),
    ASSERT_FALSE    ("AssertFalse",    "VAL-015",
            "Condição atendida indevidamente",
            "A condição de validação (assert false) foi atendida quando não deveria."),
    DESCONHECIDO    ("_Desconhecido",  "VAL-000",
            "Erro de validação não categorizado",
            "O tipo de validação aplicado não possui uma categoria específica.");

    private final String annotationCode;
    private final String code;
    private final String titulo;
    private final String descricao;

    private static final Map<String, CodigoValidacao> INDEX =
            Stream.of(values())
                  .collect(Collectors.toUnmodifiableMap(CodigoValidacao::getAnnotationCode, Function.identity()));

    private static final Map<String, CodigoValidacao> CODE_INDEX =
            Stream.of(values())
                  .collect(Collectors.toUnmodifiableMap(CodigoValidacao::getCode, Function.identity()));

    CodigoValidacao(String annotationCode, String code, String titulo, String descricao) {
        this.annotationCode = annotationCode;
        this.code = code;
        this.titulo = titulo;
        this.descricao = descricao;
    }

    public static String codeFrom(String annotationCode) {
        CodigoValidacao cv = INDEX.get(annotationCode);
        return cv != null ? cv.code : "VAL-000";
    }

    public static Optional<CodigoValidacao> fromCodigo(String codigo) {
        if (codigo == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(CODE_INDEX.get(codigo));
    }
}
