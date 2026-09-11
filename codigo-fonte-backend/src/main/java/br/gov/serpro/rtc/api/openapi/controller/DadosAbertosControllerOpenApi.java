/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.api.openapi.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import br.gov.serpro.rtc.api.model.output.dadosabertos.AliquotaDadosAbertosOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.ClassificacaoTributariaDadosAbertosOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.SituacaoClassificacaoAninhadoOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.FundamentacaoClassificacaoDadosAbertosOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.GrupoDfeDadosAbertosOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.GrupoAtorDadosAbertosOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.MunicipioDadosAbertosOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.NbsAplicavelOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.NbsDadosAbertosOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.NbsListaDadosAbertosOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.NcmAplicavelOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.NcmDadosAbertosOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.NomenclaturaDadosAbertosOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.RedutorCompraGovernamentalDadosAbertosOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.SituacaoTributariaDadosAbertosOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.TransferenciaCBSDadosAbertosOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.TransferenciaIBSDadosAbertosOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.UfDadosAbertosOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.ValidadeDfeClassificacaoTributariaDadosAbertosOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.VersaoOutput;
import br.gov.serpro.rtc.domain.model.enumeration.PapelAtorEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;

/**
 * Contrato OpenAPI das consultas de dados abertos da calculadora, cobrindo
 * tabelas de referência, classificações tributárias, alíquotas e metadados de
 * versão.
 */
@Tag(name = "Dados Abertos - VERSÃO BETA", description = "Consultas para os Dados Abertos")
public interface DadosAbertosControllerOpenApi {

    @Operation(summary = "Unidade Federativa", description = "Obtém a lista das unidades federativas cadastradas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso", content = {
            @Content(
                mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = UfDadosAbertosOutput.class),
                examples = @ExampleObject(
                    name = "UFs Example",
                    value = """
                    [
                      { "sigla": "RO", "nome": "RONDÔNIA", "codigo": 11 },
                      { "sigla": "AC", "nome": "ACRE", "codigo": 12 }
                    ]
                    """
                )
            )
        }),
        @ApiResponse(responseCode = "400", description = "Requisição com problema",
            content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Bad Request Example",
                    value = """
                    {
                      "type": "about:blank",
                      "title": "Bad Request",
                      "status": 400,
                      "detail": "Parâmetro inválido ou ausente.",
                      "instance": "/api/calculadora/dados-abertos/ufs"
                    }
                    """
                )
            )
        ),
        @ApiResponse(responseCode = "404", description = "Erro na URL da requisição",
            content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Not Found Example",
                    value = """
                    {
                        "type": "about:blank",
                        "title": "Not Found",
                        "status": 404,
                        "detail": "No static resource calculadora/dados-abertos/ufs/1.",
                        "instance": "/api/calculadora/dados-abertos/ufs/1"
                    }
                    """
                )
            )
        ),
        @ApiResponse(responseCode = "500", description = "Erro interno na API",
            content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Internal Server Error Example",
                    value = """
                    {
                      "type": "http://url-ambiente/errors/erro-interno",
                      "title": "Erro interno na API",
                      "status": 500,
                      "detail": "Falha ao processar a requisição.",
                      "instance": "/api/calculadora/dados-abertos/ufs"
                    }
                    """
                )
            )
        )
    })
    ResponseEntity<List<UfDadosAbertosOutput>> consultarUfs();

    @Operation(summary = "Município", description = "Obtém a lista dos municípios cadastrados com base na sigla de uma unidade federativa")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso", content = {
            @Content(
                mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = MunicipioDadosAbertosOutput.class),
                examples = @ExampleObject(
                    name = "Municípios Example",
                    value = """
                    [
                      {
                        "codigo": 4314902,
                        "nome": "Porto Alegre"
                      },
                      {
                        "codigo": 4305108,
                        "nome": "Caxias do Sul"
                      }
                    ]
                    """
                )
            )
        }),
        @ApiResponse(responseCode = "500", description = "Erro interno na API", content = {
            @Content(
                mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Internal Server Error Example",
                    value = """
                    {
                      "type": "http://url-ambiente/errors/erro-interno",
                      "title": "Erro interno na API",
                      "status": 500,
                      "detail": "Falha ao processar a requisição.",
                      "instance": "/api/calculadora/dados-abertos/municipios/RS"
                    }
                    """
                )
            )
        })
    })
    ResponseEntity<List<MunicipioDadosAbertosOutput>> consultarMunicipiosPorSiglaUf(
        @Parameter(description = "Sigla da unidade federativa", example = "RS", required = true) String siglaUf);

    @Deprecated(since = "2026-01-13", forRemoval = true)
    @Operation(
        summary = "Classificação Tributária (cClassTrib) - DEPRECATED", 
        description = "Obtém a lista das classificações tributárias (cClassTrib) cadastradas com base no ID da situação tributária. " +
        "DEPRECATED: Use os novos endpoints /classificacoes-tributarias/imposto-seletivo/{cst} ou " +
        "/classificacoes-tributarias/cbs-ibs/{cst} que trabalham com CST ao invés de IDs.",
        deprecated = true
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso", content = {
            @Content(
                mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ClassificacaoTributariaDadosAbertosOutput.class),
                examples = @ExampleObject(
                    name = "Classificações Tributárias Example",
                    value = """
                    [
                        {
                            "codigo": "000003",
                            "descricao": "Regime automotivo - projetos incentivados (art. 311)",
                            "tipoAliquota": "Padrão",
                            "nomenclatura": "NCM",
                            "descricaoTratamentoTributario": "Tributação integral",
                            "incompativelComSuspensao": false,
                            "exigeGrupoDesoneracao": false,
                            "possuiPercentualReducao": true,
                            "indicaApropriacaoCreditoAdquirenteCbs": false,
                            "indicaApropriacaoCreditoAdquirenteIbs": false,
                            "indicaCreditoPresumidoFornecedor": false,
                            "indicaCreditoPresumidoAdquirente": false,
                            "tiposDfeClassificacao": [
                                {
                                    "tipo": 55,
                                    "sigla": "NFe",
                                    "descricao": "Nota Fiscal Eletrônica"
                                }
                            ],
                            "dataAtualizacao": "2025-12-15"
                        },
                        {
                            "codigo": "000004",
                            "descricao": "Regime automotivo - projetos incentivados (art. 312)",
                            "tipoAliquota": "Padrão",
                            "nomenclatura": "NCM",
                            "descricaoTratamentoTributario": "Tributação integral",
                            "incompativelComSuspensao": false,
                            "exigeGrupoDesoneracao": false,
                            "possuiPercentualReducao": true,
                            "indicaApropriacaoCreditoAdquirenteCbs": false,
                            "indicaApropriacaoCreditoAdquirenteIbs": false,
                            "indicaCreditoPresumidoFornecedor": false,
                            "indicaCreditoPresumidoAdquirente": false,
                            "tiposDfeClassificacao": [
                                {
                                    "tipo": 55,
                                    "sigla": "NFe",
                                    "descricao": "Nota Fiscal Eletrônica"
                                }
                            ],
                            "dataAtualizacao": "2025-12-15"
                        }
                    ]
                    """
                )
            )
        }),
        @ApiResponse(responseCode = "400", description = "Requisição com problema",
            content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Bad Request Example",
                    value = """
                    {
                        "type": "about:blank",
                        "title": "Bad Request",
                        "status": 400,
                        "detail": "Required parameter 'data' is not present.",
                        "instance": "/api/calculadora/dados-abertos/classificacoes-tributarias/3244253"
                    }
                    """
                )
            )
        ),
        @ApiResponse(responseCode = "500", description = "Erro interno na API",
            content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Internal Server Error Example",
                    value = """
                    {
                      "type": "http://url-ambiente/errors/erro-interno",
                      "title": "Erro interno na API",
                      "status": 500,
                      "detail": "Falha ao processar a requisição.",
                      "instance": "/api/calculadora/dados-abertos/classificacoes-tributarias/1"
                    }
                    """
                )
            )
        )
    })
    ResponseEntity<List<ClassificacaoTributariaDadosAbertosOutput>> consultarClassificacoesTributariasPorIdSituacaoTributaria(
        @Parameter(description = "Id da Situação Tributária (CST)", example = "1", required = true) Long idSituacaoTributaria,
        @Parameter(description = "Data no padrão ISO 8601 (yyyy-MM-dd)", example = "2026-01-01", required = true) LocalDate data);

    @Operation(
        summary = "Classificações Tributárias por CST - Imposto Seletivo",
        description = "Obtém a lista das classificações tributárias por CST para Imposto Seletivo"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso", content = {
            @Content(
                mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ClassificacaoTributariaDadosAbertosOutput.class),
                examples = @ExampleObject(
                    name = "Classificações IS Example",
                    value = """
                    [
                      {
                        "codigo": "000001",
                        "descricao": "Primeiro fornecimento a qualquer título de bem",
                        "tipoAliquota": "Alíquotas Combinadas (Ad Valorem e Ad Rem)",
                        "nomenclatura": "NBS ou NCM",
                        "descricaoTratamentoTributario": "Tributação pelo Imposto Seletivo - Com Cálculo",
                        "incompativelComSuspensao": false,
                        "exigeGrupoDesoneracao": false,
                        "possuiPercentualReducao": true,
                        "indicaApropriacaoCreditoAdquirenteCbs": false,
                        "indicaApropriacaoCreditoAdquirenteIbs": false,
                        "indicaCreditoPresumidoFornecedor": false,
                        "indicaCreditoPresumidoAdquirente": false,
                        "tiposDfeClassificacao": []
                      }
                    ]
                    """
                )
            )
        }),
        @ApiResponse(responseCode = "400", description = "Requisição com problema",
            content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class)
            )
        ),
        @ApiResponse(responseCode = "500", description = "Erro interno na API",
            content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class)
            )
        )
    })
    ResponseEntity<List<br.gov.serpro.rtc.api.model.output.dadosabertos.ClassificacaoTributariaDadosAbertosOutput>> listarPorCstImpostoSeletivo(
        @Parameter(description = "Código da Situação Tributária (CST)", example = "000", required = true) String cst,
        @Parameter(description = "Data no padrão ISO 8601 (yyyy-MM-dd)", example = "2027-01-01", required = true) LocalDate data);

    @Operation(
        summary = "Classificações Tributárias por CST - CBS/IBS", 
        description = "Obtém a lista das classificações tributárias por CST para CBS/IBS"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso", content = {
            @Content(
                mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ClassificacaoTributariaDadosAbertosOutput.class),
                examples = @ExampleObject(
                    name = "Classificações CBS/IBS Example",
                    value = """
                    [
                      {
                        "codigo": "000001",
                        "descricao": "Situações tributadas integralmente pelo IBS e CBS.",
                        "tipoAliquota": "Padrão",
                        "nomenclatura": "NBS ou NCM",
                        "descricaoTratamentoTributario": "Tributação integral",
                                                "dataAtualizacao": "2025-12-15",
                        "incompativelComSuspensao": false,
                        "exigeGrupoDesoneracao": false,
                        "possuiPercentualReducao": true,
                        "indicaApropriacaoCreditoAdquirenteCbs": true,
                        "indicaApropriacaoCreditoAdquirenteIbs": true,
                        "indicaCreditoPresumidoFornecedor": false,
                        "indicaCreditoPresumidoAdquirente": false,
                        "tiposDfeClassificacao": []
                      }
                    ]
                    """
                )
            )
        }),
        @ApiResponse(responseCode = "400", description = "Requisição com problema",
            content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class)
            )
        ),
        @ApiResponse(responseCode = "500", description = "Erro interno na API",
            content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class)
            )
        )
    })
    ResponseEntity<List<br.gov.serpro.rtc.api.model.output.dadosabertos.ClassificacaoTributariaDadosAbertosOutput>> listarPorCstCbsIbs(
        @Parameter(description = "Código da Situação Tributária (CST)", example = "000", required = true) String cst,
        @Parameter(description = "Data no padrão ISO 8601 (yyyy-MM-dd)", example = "2025-01-01", required = true) LocalDate data);

    @Operation(
        summary = "Situação Tributária (CST)",
        description = "Obtém a lista das situações tributárias cadastradas para Imposto Seletivo"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso", content = {
            @Content(
                mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = SituacaoTributariaDadosAbertosOutput.class),
                examples = @ExampleObject(
                    name = "Situações Tributárias Example",
                    value = """
                    [
                      {
                        "id": 19,
                        "codigo": "000",
                        "descricao": "Tributado com Imposto Seletivo"
                      },
                      {
                        "id": 20,
                        "codigo": "100",
                        "descricao": "Imunidade"
                      }
                    ]
                    """
                )
            )
        }),
        @ApiResponse(responseCode = "400", description = "Requisição com problema",
            content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Bad Request Example",
                    value = """
                    {
                      "type": "about:blank",
                      "title": "Bad Request",
                      "status": 400,
                      "detail": "Required parameter 'data' is not present.",
                      "instance": "/api/calculadora/dados-abertos/situacoes-tributarias/imposto-seletivo"
                    }
                    """
                )
            )
        ),
        @ApiResponse(responseCode = "500", description = "Erro interno na API",
            content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Internal Server Error Example",
                    value = """
                    {
                      "type": "http://url-ambiente/errors/erro-interno",
                      "title": "Erro interno na API",
                      "status": 500,
                      "detail": "Falha ao processar a requisição.",
                      "instance": "/api/calculadora/dados-abertos/situacoes-tributarias/imposto-seletivo"
                    }
                    """
                )
            )
        )
    })
    ResponseEntity<List<SituacaoTributariaDadosAbertosOutput>> consultarSituacoesTributariasImpostoSeletivo(
        @Parameter(description = "Data no padrão ISO 8601 (yyyy-MM-dd)", example = "2027-01-01", required = true) LocalDate data);


    @Operation(summary = "Nomenclatura Comum do Mercosul (NCM)", description = "Obtém informações sobre a NCM em relação ao Imposto Seletivo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso", headers = @Header(name = "x-warning-dados-simulados", description = "Indica que os dados são simulados. Valores possíveis: 1 (alíquotas do Imposto Seletivo ainda não definidas em lei).", schema = @Schema(type = "boolean", example = "true")), content = {
            @Content(
                mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = NcmDadosAbertosOutput.class),
                examples = @ExampleObject(
                    name = "NCM Example",
                    value = """
                    {
                        "tributadoPeloImpostoSeletivo": true,
                        "temAliquotaAdValorem": true,
                        "temAliquotaAdRem": true,
                        "capitulo": "Tabaco e seus sucedâneos manufaturados; produtos, mesmo com nicotina, destinados à inalação sem combustão; outros produtos que contenham nicotina destinados à absorção da nicotina pelo corpo humano.",
                        "posicao": "Charutos, cigarrilhas e cigarros, de tabaco ou dos seus sucedâneos.",
                        "subitem": "Charutos e cigarrilhas, que contenham tabaco",
                        "unidade": "VN"
                    }
                    """
                )
            )
        }),
        @ApiResponse(responseCode = "400", description = "Requisição com problema",
            content = @Content(
                mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Bad Request Example",
                    value = """
                    {
                        "type": "about:blank",
                        "title": "Bad Request",
                        "status": 400,
                        "detail": "Required parameter 'data' is not present.",
                        "instance": "/api/calculadora/dados-abertos/ncm/24021000"
                    }
                    """
                )
            )
        ),
        @ApiResponse(responseCode = "404", description = "NCM não encontrada na data especificada",
            content = @Content(
                mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Not Found Example",
                    value = """
                    {
                        "type": "http://url-ambiente/errors/ncm-nao-encontrada",
                        "title": "NCM não encontrada",
                        "status": 404,
                        "detail": "NCM de código 24020009 não encontrada para a data 2026-08-25",
                        "instance": "/api/calculadora/dados-abertos/ncm"
                    }
                    """
                )
            )
        ),
        @ApiResponse(responseCode = "500", description = "Erro interno na API",
            content = @Content(
                mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Internal Server Error Example",
                    value = """
                    {
                        "type": "http://url-ambiente/errors/erro-interno",
                        "title": "Erro interno na API",
                        "status": 500,
                        "detail": "Falha ao processar a requisição.",
                        "instance": "/api/calculadora/dados-abertos/ncm/24021000"
                    }
                    """
                )
            )
        )
    })
    ResponseEntity<NcmDadosAbertosOutput> consultarNcm(
        @Parameter(description = "Código NCM sem formatação", example = "24021000", required = true) String ncm,
        @Parameter(description = "Data no padrão ISO 8601 (yyyy-MM-dd)", example = "2027-01-01", required = true) LocalDate data);

    @Operation(summary = "Nomenclatura Brasileira de Serviços (NBS)", description = "Obtém informações sobre a NBS em relação ao Imposto Seletivo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso", headers = @Header(name = "x-warning-dados-simulados", description = "Indica que os dados são simulados. Valores possíveis: 1 (alíquotas do Imposto Seletivo ainda não definidas em lei).", schema = @Schema(type = "boolean", example = "true")), content = {
            @Content(
                mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = NbsDadosAbertosOutput.class),
                examples = @ExampleObject(
                    name = "NBS Example",
                    value = """
                    {
                      "tributadoPeloImpostoSeletivo": false,
                      "temAliquotaAdValorem": false,
                      "capitulo": "Serviços veterinários",
                      "posicao": "Serviços veterinários para animais de corte",
                      "item": "Serviços de atendimento, assistência ou tratamento para animais de corte"
                    }
                    """
                )
            )
        }),
        @ApiResponse(responseCode = "400", description = "Requisição com problema",
            content = @Content(
                mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Bad Request Example",
                    value = """
                    {
                      "type": "about:blank",
                      "title": "Bad Request",
                      "status": 400,
                      "detail": "Required parameter 'data' is not present.",
                      "instance": "/api/calculadora/dados-abertos/nbs/114052200"
                    }
                    """
                )
            )
        ),
        @ApiResponse(responseCode = "404", description = "NBS não encontrada na data especificada",
            content = @Content(
                mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Not Found Example",
                    value = """
                    {
                      "type": "http://url-ambiente/errors/nbs-nao-encontrada",
                      "title": "NBS não encontrada",
                      "status": 404,
                      "detail": "NBS de código 999999999 não encontrada para a data 2026-08-25",
                      "instance": "/api/calculadora/dados-abertos/nbs"
                    }
                    """
                )
            )
        ),
        @ApiResponse(responseCode = "500", description = "Erro interno na API",
            content = @Content(
                mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Internal Server Error Example",
                    value = """
                    {
                      "type": "http://url-ambiente/errors/erro-interno",
                      "title": "Erro interno na API",
                      "status": 500,
                      "detail": "Falha ao processar a requisição.",
                      "instance": "/api/calculadora/dados-abertos/nbs/114052200"
                    }
                    """
                )
            )
        )
    })
    ResponseEntity<NbsDadosAbertosOutput> consultarNbs(
        @Parameter(description = "Código NBS sem formatação", example = "114052200", required = true) String nbs,
        @Parameter(description = "Data no padrão ISO 8601 (yyyy-MM-dd)", example = "2027-01-01", required = true) LocalDate data);

    @Operation(summary = "Lista de Nomenclatura Brasileira de Serviços (NBS)", description = "Obtém a lista de todas as NBS válidas em uma data, incluindo todos os níveis hierárquicos (capítulo, posição, subposições e itens)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso", content = {
            @Content(
                mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = NbsListaDadosAbertosOutput.class),
                examples = @ExampleObject(
                    name = "Lista NBS Example",
                    value = """
                    [
                      {
                        "codigo": "101",
                        "descricao": "Serviços de construção"
                      },
                      {
                        "codigo": "10101",
                        "descricao": "Serviços de construção de edificações"
                      },
                      {
                        "codigo": "101011",
                        "descricao": "Serviços de construção de edificações residenciais"
                      },
                      {
                        "codigo": "101011100",
                        "descricao": "Serviços de construção de edificações residenciais de um e dois pavimentos"
                      },
                      {
                        "codigo": "101011200",
                        "descricao": "Serviços de construção de edificações residenciais com mais de dois pavimentos"
                      },
                      {
                        "codigo": "101012",
                        "descricao": "Serviços de construção de edificações não residenciais"
                      },
                      {
                        "codigo": "101012100",
                        "descricao": "Serviços de construção de edificações industriais"
                      },
                      {
                        "codigo": "101012200",
                        "descricao": "Serviços de construção de edificações comerciais"
                      },
                      {
                        "codigo": "101012900",
                        "descricao": "Serviços de construção de edificações não residenciais não classificados em subposições anteriores"
                      }
                    ]
                    """
                )
            )
        }),
        @ApiResponse(responseCode = "400", description = "Requisição com problema",
            content = @Content(
                mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Bad Request Example",
                    value = """
                    {
                      "type": "about:blank",
                      "title": "Bad Request",
                      "status": 400,
                      "detail": "Required parameter 'data' is not present.",
                      "instance": "/api/calculadora/dados-abertos/nbs"
                    }
                    """
                )
            )
        ),
        @ApiResponse(responseCode = "500", description = "Erro interno na API",
            content = @Content(
                mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Internal Server Error Example",
                    value = """
                    {
                      "type": "http://url-ambiente/errors/erro-interno",
                      "title": "Erro interno na API",
                      "status": 500,
                      "detail": "Falha ao processar a requisição.",
                      "instance": "/api/calculadora/dados-abertos/nbs"
                    }
                    """
                )
            )
        )
    })
    ResponseEntity<List<NbsListaDadosAbertosOutput>> listarNbs(
        @Parameter(description = "Data no padrão ISO 8601 (yyyy-MM-dd)", example = "2027-01-01", required = true) LocalDate data);

    @Operation(
        summary = "Lista de NBS aplicáveis por Classificação Tributária",
        description = "Obtém a lista de NBS aplicáveis a um cClassTrib em uma determinada data. Se não houver NBS associados e a nomenclatura for aplicável a serviços (NBS ou 'NBS ou NCM'), retorna todos os NBS vigentes. Se não for aplicável a serviços (apenas NCM), retorna lista vazia."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso", content = {
            @Content(
                mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = NbsListaDadosAbertosOutput.class),
                examples = {
                        @ExampleObject(
                                name = "Lista NBS Aplicáveis Example",
                                value = """
                                [
                                    {
                                        "codigo": "114052200",
                                        "descricao": "Serviços de atendimento, assistência ou tratamento para animais de corte"
                                    },
                                    {
                                        "codigo": "114052300",
                                        "descricao": "Serviços de atendimento, assistência ou tratamento para animais de produção"
                                    }
                                ]
                                """
                        ),
                        @ExampleObject(
                                name = "Lista NBS Aplicáveis Vazia Example",
                                value = """
                                []
                                """
                        )
                }
            )
        }),
        @ApiResponse(responseCode = "400", description = "Requisição com problema",
            content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Bad Request Example",
                    value = """
                    {
                      "type": "about:blank",
                      "title": "Bad Request",
                      "status": 400,
                      "detail": "Required parameter 'data' is not present.",
                      "instance": "/api/calculadora/dados-abertos/nbs/aplicaveis-por-classificacao"
                    }
                    """
                )
            )
        ),
        @ApiResponse(responseCode = "404", description = "Classificação tributária não encontrada",
            content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Not Found Example",
                    value = """
                    {
                      "type": "about:blank",
                      "title": "Not Found",
                      "status": 404,
                      "detail": "Classificação tributária não encontrada para código 999999 e data 2026-01-01 (CBS/IBS)",
                      "instance": "/api/calculadora/dados-abertos/nbs/aplicaveis-por-classificacao"
                    }
                    """
                )
            )
        ),
        @ApiResponse(responseCode = "500", description = "Erro interno na API",
            content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Internal Server Error Example",
                    value = """
                    {
                      "type": "http://url-ambiente/errors/erro-interno",
                      "title": "Erro interno na API",
                      "status": 500,
                      "detail": "Falha ao processar a requisição.",
                      "instance": "/api/calculadora/dados-abertos/nbs/aplicaveis-por-classificacao"
                    }
                    """
                )
            )
        )
    })
    ResponseEntity<List<NbsListaDadosAbertosOutput>> listarNbsAplicaveisPorClassificacao(
        @Parameter(description = "Código da Classificação Tributária (cClassTrib)", example = "000001", required = true) 
        @Pattern(regexp = "^\\d{6}$", message = "O código da Classificação Tributária deve conter exatamente 6 dígitos")
        String cClassTrib,
        @Parameter(description = "Data no padrão ISO 8601 (yyyy-MM-dd)", example = "2027-01-01", required = true) 
        LocalDate data);


    @Operation(summary = "Fundamentação Legal", description = "Obtém informações sobre as fundamentações legais")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso", content = {
            @Content(
                mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = FundamentacaoClassificacaoDadosAbertosOutput.class),
                examples = @ExampleObject(
                    name = "Fundamentações Legais Example",
                    value = """
                    [
                      {
                        "codigoClassificacaoTributaria": "000001",
                        "descricaoClassificacaoTributaria": "Situações tributadas integralmente pelo IBS e CBS.",
                        "codigoSituacaoTributaria": "000",
                        "descricaoSituacaoTributaria": "Tributação integral",
                        "conjuntoTributo": "CBS e IBS",
                        "texto": "",
                        "textoCurto": "",
                        "referenciaNormativa": "LIVRO I \\n\\nDO IMPOSTO SOBRE BENS E SERVIÇOS (IBS) E DA CONTRIBUIÇÃO SOCIAL SOBRE BENS E SERVIÇOS (CBS) \\n\\nTÍTULO I \\n\\nDAS NORMAS GERAIS DO IBS E DA CBS \\n\\nCAPÍTULO I \\n\\nDISPOSIÇÕES PRELIMINARES"
                      },
                      {
                        "codigoClassificacaoTributaria": "000002",
                        "descricaoClassificacaoTributaria": "Exploração de via",
                        "codigoSituacaoTributaria": "000",
                        "descricaoSituacaoTributaria": "Tributação integral",
                        "conjuntoTributo": "CBS e IBS",
                        "texto": "Art. 11. Considera-se local da operação com:\\nVIII - serviço de exploração de via, mediante cobrança de valor a qualquer título, incluindo tarifas, pedágios e quaisquer outras formas de cobrança, o território de cada Município e Estado, ou do Distrito Federal, proporcionalmente à correspondente extensão da via explorada;",
                        "textoCurto": "Art. 11, VIII",
                        "referenciaNormativa": "LIVRO I \\n\\nDO IMPOSTO SOBRE BENS E SERVIÇOS (IBS) E DA CONTRIBUIÇÃO SOCIAL SOBRE BENS E SERVIÇOS (CBS) \\n\\nTÍTULO I \\n\\nDAS NORMAS GERAIS DO IBS E DA CBS \\n\\nCAPÍTULO II\\n\\nDO IBS E DA CBS SOBRE OPERAÇÕES COM BENS E SERVIÇOS\\n\\nSeção IV\\n\\nDo Local da Operação"
                      }
                    ]
                    """
                )
            )
        }),
        @ApiResponse(responseCode = "400", description = "Requisição com problema",
            content = @Content(
                mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Bad Request Example",
                    value = """
                    {
                      "type": "about:blank",
                      "title": "Bad Request",
                      "status": 400,
                      "detail": "Required parameter 'data' is not present.",
                      "instance": "/api/calculadora/dados-abertos/fundamentacoes-legais"
                    }
                    """
                )
            )
        ),
        @ApiResponse(responseCode = "500", description = "Erro interno na API",
            content = @Content(
                mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Internal Server Error Example",
                    value = """
                    {
                      "type": "http://url-ambiente/errors/erro-interno",
                      "title": "Erro interno na API",
                      "status": 500,
                      "detail": "Falha ao processar a requisição.",
                      "instance": "/api/calculadora/dados-abertos/fundamentacoes-legais"
                    }
                    """
                )
            )
        )
    })
    ResponseEntity<List<FundamentacaoClassificacaoDadosAbertosOutput>> consultarFundamentacoesLegais(
        @Parameter(description = "Data no padrão ISO 8601 (yyyy-MM-dd)", example = "2027-01-01", required = true) LocalDate data);

    @Operation(summary = "Classificação Tributária (cClassTrib)", description = "Obtém a lista das classificações tributárias (cClassTrib) para CBS e IBS")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso", content = {
            @Content(
                mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ClassificacaoTributariaDadosAbertosOutput.class),
                examples = @ExampleObject(
                    name = "Classificações Tributárias Example",
                    value = """
                    [
                        {
                        "codigo": "000004",
                        "descricao": "Regime automotivo - projetos incentivados (art. 312)",
                        "tipoAliquota": "Padrão",
                        "nomenclatura": "NCM",
                        "descricaoTratamentoTributario": "Tributação integral",
                        "incompativelComSuspensao": false,
                        "exigeGrupoDesoneracao": false,
                        "possuiPercentualReducao": true,
                        "indicaApropriacaoCreditoAdquirenteCbs": true,
                        "indicaApropriacaoCreditoAdquirenteIbs": true,
                        "indicaCreditoPresumidoFornecedor": true,
                        "indicaCreditoPresumidoAdquirente": false,
                        "creditoOperacaoAntecedente": "Manutenção",
                        "percentualReducaoCbs": 0,
                        "percentualReducaoIbsUf": 0,
                        "percentualReducaoIbsMun": 0,
                        "tiposDfeClassificacao": [
                            {
                            "tipo": 55,
                            "sigla": "NFe",
                            "descricao": "Nota Fiscal Eletrônica"
                            }
                        ]
                        },
                        {
                        "codigo": "010001",
                        "descricao": "Operações do FGTS não realizadas pela Caixa Econômica Federal",
                        "tipoAliquota": "Uniforme setorial",
                        "nomenclatura": "NBS",
                        "descricaoTratamentoTributario": "Tributação com alíquotas uniformes - operações do FGTS",
                        "incompativelComSuspensao": false,
                        "exigeGrupoDesoneracao": false,
                        "possuiPercentualReducao": true,
                        "indicaApropriacaoCreditoAdquirenteCbs": true,
                        "indicaApropriacaoCreditoAdquirenteIbs": true,
                        "indicaCreditoPresumidoFornecedor": false,
                        "indicaCreditoPresumidoAdquirente": false,
                        "creditoOperacaoAntecedente": "Manutenção",
                        "percentualReducaoCbs": 0,
                        "percentualReducaoIbsUf": 0,
                        "percentualReducaoIbsMun": 0,
                        "tiposDfeClassificacao": [
                            {
                            "tipo": 91,
                            "sigla": "NFSe",
                            "descricao": "Nota Fiscal de Serviços Eletrônica"
                            },
                            {
                            "tipo": 94,
                            "sigla": "DERE",
                            "descricao": "Declaração Eletrônica de Regimes Específicos"
                            }
                        ]
                        }
                    ]
                    """
                )
            )
        }),
        @ApiResponse(responseCode = "400", description = "Requisição com problema",
            content = @Content(
                mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Bad Request Example",
                    value = """
                    {
                        "type": "about:blank",
                        "title": "Bad Request",
                        "status": 400,
                        "detail": "Required parameter 'data' is not present.",
                        "instance": "/api/calculadora/dados-abertos/classificacoes-tributarias/cbs-ibs"
                    }
                    """
                )
            )
        ),
        @ApiResponse(responseCode = "500", description = "Erro interno na API",
            content = @Content(
                mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Internal Server Error Example",
                    value = """
                    {
                        "type": "http://url-ambiente/errors/erro-interno",
                        "title": "Erro interno na API",
                        "status": 500,
                        "detail": "Falha ao processar a requisição.",
                        "instance": "/api/calculadora/dados-abertos/classificacoes-tributarias/cbs-ibs"
                    }
                    """
                )
            )
        )
    })
    ResponseEntity<List<ClassificacaoTributariaDadosAbertosOutput>> consultarClassificacoesTributariasCbsIbs(
        @Parameter(description = "Data no padrão ISO 8601 (yyyy-MM-dd)", example = "2026-01-01", required = true) LocalDate data);

    @Operation(
        summary = "Classificação Tributária (cClassTrib) por Atores - CBS/IBS",
        description = """
            Obtém a lista das classificações tributárias (cClassTrib) para CBS e IBS aplicáveis \
            à combinação de atores da operação. Uma classificação sem vínculo de ator vigente em \
            um papel (fornecedor ou adquirente) é aplicável a qualquer ator naquele papel e é \
            sempre retornada; quando fornecedor e adquirente são informados juntos, as duas \
            condições devem ser satisfeitas simultaneamente. Sem filtros, o resultado é idêntico \
            ao do endpoint de classificações tributárias CBS/IBS."""
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso",
            headers = @Header(name = "Cache-Control", description = "public, max-age=3600"),
            content = {
                @Content(
                    mediaType = APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ClassificacaoTributariaDadosAbertosOutput.class),
                    examples = @ExampleObject(
                        name = "Classificações Tributárias por Atores Example",
                        value = """
                        [
                            {
                            "codigo": "200054",
                            "descricao": "Fornecimento de bens e serviços por agente financeiro do FGTS",
                            "tipoAliquota": "Padrão",
                            "nomenclatura": "NBS",
                            "descricaoTratamentoTributario": "Alíquota zero",
                            "incompativelComSuspensao": false,
                            "exigeGrupoDesoneracao": false,
                            "possuiPercentualReducao": true,
                            "indicaApropriacaoCreditoAdquirenteCbs": true,
                            "indicaApropriacaoCreditoAdquirenteIbs": true,
                            "indicaCreditoPresumidoFornecedor": false,
                            "indicaCreditoPresumidoAdquirente": false,
                            "creditoOperacaoAntecedente": "Manutenção",
                            "percentualReducaoCbs": 100,
                            "percentualReducaoIbsUf": 100,
                            "percentualReducaoIbsMun": 100,
                            "tipoReceitaBrutaSimplesNacional": 1,
                            "tiposDfeClassificacao": [
                                {
                                "tipo": 91,
                                "sigla": "NFSe",
                                "descricao": "Nota Fiscal de Serviços Eletrônica"
                                }
                            ],
                            "dataAtualizacao": "2025-12-15"
                            }
                        ]
                        """
                    )
                )
            }
        ),
        @ApiResponse(responseCode = "400", description = "Requisição com problema",
            content = @Content(
                mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Bad Request Example",
                    value = """
                    {
                        "type": "about:blank",
                        "title": "Bad Request",
                        "status": 400,
                        "detail": "Required parameter 'data' is not present.",
                        "instance": "/api/calculadora/dados-abertos/classificacoes-tributarias/cbs-ibs/por-atores"
                    }
                    """
                )
            )
        ),
        @ApiResponse(responseCode = "500", description = "Erro interno na API",
            content = @Content(
                mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Internal Server Error Example",
                    value = """
                    {
                        "type": "http://url-ambiente/errors/erro-interno",
                        "title": "Erro interno na API",
                        "status": 500,
                        "detail": "Falha ao processar a requisição.",
                        "instance": "/api/calculadora/dados-abertos/classificacoes-tributarias/cbs-ibs/por-atores"
                    }
                    """
                )
            )
        )
    })
    ResponseEntity<List<ClassificacaoTributariaDadosAbertosOutput>> consultarClassificacoesTributariasCbsIbsPorAtores(
        @Parameter(description = "Data no padrão ISO 8601 (yyyy-MM-dd)", example = "2027-01-01", required = true) LocalDate data,
        @Parameter(description = "ATOR_ID do ator no papel de fornecedor da operação (opcional)", example = "27") Long fornecedor,
        @Parameter(description = "ATOR_ID do ator no papel de adquirente da operação (opcional)", example = "28") Long adquirente,
        @Parameter(description = "Sigla do tipo de DF-e (opcional), em qualquer formato (ex.: NFe, NF-e, nfe)", example = "NFSe") String siglaDfe);

    @Operation(
        summary = "Classificação Tributária (cClassTrib) por código - CBS/IBS", 
        description = "Obtém uma classificação tributária específica por seu código (cClassTrib) para CBS/IBS"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso", content = {
            @Content(
                mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ClassificacaoTributariaDadosAbertosOutput.class),
                examples = @ExampleObject(
                    name = "Classificação CBS/IBS por código Example",
                    value = """
                    {
                        "codigo": "000001",
                        "descricao": "Situações tributadas integralmente pelo IBS e CBS.",
                        "tipoAliquota": "Padrão",
                        "nomenclatura": "NBS ou NCM",
                        "descricaoTratamentoTributario": "Tributação integral",
                        "incompativelComSuspensao": false,
                        "exigeGrupoDesoneracao": false,
                        "possuiPercentualReducao": true,
                        "indicaApropriacaoCreditoAdquirenteCbs": true,
                        "indicaApropriacaoCreditoAdquirenteIbs": true,
                        "indicaCreditoPresumidoFornecedor": false,
                        "indicaCreditoPresumidoAdquirente": false,
                        "creditoOperacaoAntecedente": "Manutenção",
                        "percentualReducaoCbs": 0,
                        "percentualReducaoIbsUf": 0,
                        "percentualReducaoIbsMun": 0,
                        "tipoReceitaBrutaSimplesNacional": 1,
                        "tiposDfeClassificacao": [
                            {
                            "tipo": 55,
                            "sigla": "NFe",
                            "descricao": "Nota Fiscal Eletrônica"
                            },
                            {
                            "tipo": 65,
                            "sigla": "NFCe",
                            "descricao": "Nota Fiscal de Consumidor Eletrônica"
                            }
                        ],
                        "dataAtualizacao": "2025-12-15"
                    }
                    """
                )
            )
        }),
        @ApiResponse(responseCode = "400", description = "Requisição com problema",
            content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class)
            )
        ),
        @ApiResponse(responseCode = "404", description = "Classificação não encontrada",
            content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class)
            )
        ),
        @ApiResponse(responseCode = "500", description = "Erro interno na API",
            content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class)
            )
        )
    })
    ResponseEntity<ClassificacaoTributariaDadosAbertosOutput> consultarClassificacaoTributariaCbsIbsPorCodigo(
        @Parameter(description = "Código da Classificação Tributária (cClassTrib)", example = "000001", required = true) String cClassTrib,
        @Parameter(description = "Data no padrão ISO 8601 (yyyy-MM-dd)", example = "2027-01-01", required = true) LocalDate data);

    @Operation(
        summary = "Classificação Tributária (cClassTrib) por código - IS",
        description = "Obtém uma classificação tributária específica por seu código (cClassTrib) para o Imposto Seletivo (IS)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso", content = {
            @Content(
                mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ClassificacaoTributariaDadosAbertosOutput.class),
                examples = @ExampleObject(
                    name = "Classificação IS por código Example",
                    value = """
                    {
                        "codigo": "000001",
                        "descricao": "Primeiro fornecimento a qualquer título de bem",
                        "tipoAliquota": "Alíquotas Combinadas (Ad Valorem e Ad Rem)",
                        "nomenclatura": "NBS ou NCM",
                        "descricaoTratamentoTributario": "Tributação com Imposto Seletivo",
                        "incompativelComSuspensao": false,
                        "exigeGrupoDesoneracao": false,
                        "possuiPercentualReducao": false,
                        "tiposDfeClassificacao": [
                            {
                            "tipo": 55,
                            "sigla": "NFe",
                            "descricao": "Nota Fiscal Eletrônica"
                            }
                        ],
                        "dataAtualizacao": "2025-12-15"
                    }
                    """
                )
            )
        }),
        @ApiResponse(responseCode = "400", description = "Requisição com problema",
            content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class)
            )
        ),
        @ApiResponse(responseCode = "404", description = "Classificação não encontrada",
            content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class)
            )
        ),
        @ApiResponse(responseCode = "500", description = "Erro interno na API",
            content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class)
            )
        )
    })
    ResponseEntity<ClassificacaoTributariaDadosAbertosOutput> consultarClassificacaoTributariaIsPorCodigo(
        @Parameter(description = "Código da Classificação Tributária (cClassTrib)", example = "000001", required = true) String cClassTrib,
        @Parameter(description = "Data no padrão ISO 8601 (yyyy-MM-dd)", example = "2027-01-01", required = true) LocalDate data);

    @Operation(
        summary = "Classificação Tributária (cClassTrib) para Imposto Seletivo",
        description = "Obtém a lista das classificações tributárias (cClassTrib) para Imposto Seletivo"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Consulta realizada com sucesso",
            headers = @Header(
                name = "x-warning-dados-simulados",
                description = "Indica que os dados são simulados. Valores possíveis: " +
                             "1 (alíquotas da CBS e do IBS ainda não definidas em lei).",
                schema = @Schema(type = "integer", example = "1")
            ),
            content = {
            @Content(
                mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ClassificacaoTributariaDadosAbertosOutput.class),
                examples = {
                    @ExampleObject(
                        name = "Classificações Tributárias Imposto Seletivo Example",
                        value = """
                        [
                            {
                            "codigo": "000001",
                            "descricao": "Primeiro fornecimento a qualquer título de bem",
                            "tipoAliquota": "Alíquotas Combinadas (Ad Valorem e Ad Rem)",
                            "nomenclatura": "NBS ou NCM",
                            "descricaoTratamentoTributario": "Tributação pelo Imposto Seletivo - Com Cálculo",
                            "incompativelComSuspensao": false,
                            "exigeGrupoDesoneracao": false,
                            "possuiPercentualReducao": true,
                            "indicaApropriacaoCreditoAdquirenteCbs": false,
                            "indicaApropriacaoCreditoAdquirenteIbs": false,
                            "indicaCreditoPresumidoFornecedor": false,
                            "indicaCreditoPresumidoAdquirente": false
                            },
                            {
                            "codigo": "000002",
                            "descricao": "Arrematação em leilão público",
                            "tipoAliquota": "Alíquotas Combinadas (Ad Valorem e Ad Rem)",
                            "nomenclatura": "NBS ou NCM",
                            "descricaoTratamentoTributario": "Tributação pelo Imposto Seletivo - Com Cálculo",
                            "incompativelComSuspensao": false,
                            "exigeGrupoDesoneracao": false,
                            "possuiPercentualReducao": true,
                            "indicaApropriacaoCreditoAdquirenteCbs": false,
                            "indicaApropriacaoCreditoAdquirenteIbs": false,
                            "indicaCreditoPresumidoFornecedor": false,
                            "indicaCreditoPresumidoAdquirente": false
                            }
                        ]
                        """
                    ),
                    @ExampleObject(
                        name = "Classificações Tributárias Imposto Seletivo Vazia Example",
                        value = """
                        []
                        """
                    )
                }
            )
        }),
        @ApiResponse(responseCode = "400", description = "Requisição com problema",
            content = @Content(
                mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Bad Request Example",
                    value = """
                    {
                        "type": "about:blank",
                        "title": "Bad Request",
                        "status": 400,
                        "detail": "Required parameter 'data' is not present.",
                        "instance": "/api/calculadora/dados-abertos/classificacoes-tributarias/imposto-seletivo"
                    }
                    """
                )
            )
        ),
        @ApiResponse(responseCode = "500", description = "Erro interno na API",
            content = @Content(
                mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Internal Server Error Example",
                    value = """
                    {
                        "type": "http://url-ambiente/errors/erro-interno",
                        "title": "Erro interno na API",
                        "status": 500,
                        "detail": "Falha ao processar a requisição.",
                        "instance": "/api/calculadora/dados-abertos/classificacoes-tributarias/imposto-seletivo"
                    }
                    """
                )
            )
        )
    })
    ResponseEntity<List<ClassificacaoTributariaDadosAbertosOutput>> consultarClassificacoesTributariasImpostoSeletivo(
        @Parameter(description = "Data no padrão ISO 8601 (yyyy-MM-dd)", example = "2027-01-01", required = true) LocalDate data);

    @Operation(
        summary = "Alíquota Padrão ou de Referência",
        description = "Obtém a alíquota padrão ou de referência para CBS"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Consulta realizada com sucesso",
            headers = @Header(
                name = "x-warning-dados-simulados",
                description = "Indica que os dados são simulados. Valores possíveis: " +
                             "1 (Classificações Tributárias para o Imposto Seletivo ainda não foram estabelecidas em lei).",
                schema = @Schema(type = "integer", example = "1")
            ),
            content = @Content(
                mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = AliquotaDadosAbertosOutput.class),
                examples = @ExampleObject(
                    name = "Alíquota União Example",
                    value = """
                    {
                        "aliquotaReferencia": 0.9,
                        "aliquotaPropria": 0.9
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Requisição com problema",
            content = @Content(
                mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Bad Request Example",
                    value = """
                    {
                        "type": "about:blank",
                        "title": "Bad Request",
                        "status": 400,
                        "detail": "Required parameter 'data' is not present.",
                        "instance": "/api/calculadora/dados-abertos/aliquota-uniao"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Erro interno na API",
            content = @Content(
                mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Internal Server Error Example",
                    value = """
                    {
                        "type": "http://url-ambiente/errors/erro-interno",
                        "title": "Erro interno na API",
                        "status": 500,
                        "detail": "Falha ao processar a requisição.",
                        "instance": "/api/calculadora/dados-abertos/aliquota-uniao"
                    }
                    """
                )
            )
        )
    })
    ResponseEntity<AliquotaDadosAbertosOutput> consultarAliquotaUniao(
        @Parameter(description = "Data no padrão ISO 8601 (yyyy-MM-dd)", example = "2026-01-01", required = true) LocalDate data);

    @Operation(
        summary = "Classificações Tributárias aplicáveis por NBS",
        description = "Obtém a lista dos códigos cClassTrib aplicáveis a um NBS em uma determinada data, considerando vínculos e regras de exceção. Também inclui classificações de serviço vigentes não vinculadas a nenhum NBS."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso", content = {
            @Content(
                mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = String.class),
                examples = @ExampleObject(
                    name = "cClassTrib por NBS Example",
                    value = """
                    ["000001", "000002", "000003"]
                    """
                )
            )
        }),
        @ApiResponse(responseCode = "400", description = "Requisição com problema",
            content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Bad Request Example",
                    value = """
                    {
                      "type": "about:blank",
                      "title": "Bad Request",
                      "status": 400,
                      "detail": "Parâmetro inválido ou ausente.",
                      "instance": "/api/calculadora/dados-abertos/classificacoes-tributarias/nbs"
                    }
                    """
                )
            )
        ),
        @ApiResponse(responseCode = "404", description = "NBS não encontrado na data especificada",
            content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Not Found Example",
                    value = """
                    {
                      "type": "about:blank",
                      "title": "Not Found",
                      "status": 404,
                      "detail": "Nenhum resultado foi encontrado para a NBS: 114052200 na data: 2026-01-01",
                      "instance": "/api/calculadora/dados-abertos/classificacoes-tributarias/nbs"
                    }
                    """
                )
            )
        ),
        @ApiResponse(responseCode = "500", description = "Erro interno na API",
            content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Internal Server Error Example",
                    value = """
                    {
                      "type": "http://url-ambiente/errors/erro-interno",
                      "title": "Erro interno na API",
                      "status": 500,
                      "detail": "Falha ao processar a requisição.",
                      "instance": "/api/calculadora/dados-abertos/classificacoes-tributarias/nbs"
                    }
                    """
                )
            )
        )
    })
    ResponseEntity<List<String>> listarClassificacoesTributariasPorNbs(
        @Parameter(description = "Código NBS sem formatação", example = "114052200", required = true)
        @Pattern(regexp = "^\\d{9}$", message = "O código NBS deve conter exatamente 9 dígitos") 
        String nbs,
        @Parameter(description = "Data no padrão ISO 8601 (yyyy-MM-dd)", example = "2026-01-01", required = true) 
        LocalDate data);
        
    @Operation(
        summary = "Alíquota Padrão ou de Referência para IBS Estadual",
        description = "Obtém a alíquota padrão ou de referência para IBS Estadual"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Consulta realizada com sucesso",
            headers = @Header(
                name = "x-warning-dados-simulados",
                description = "Indica que os dados são simulados. Valores possíveis: " +
                             "1 (alíquotas da CBS ainda não definidas em lei).",
                schema = @Schema(type = "integer", example = "1")
            ),
            content = @Content(
                mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = AliquotaDadosAbertosOutput.class),
                examples = @ExampleObject(
                    name = "Alíquota UF Example",
                    value = """
                    {
                        "aliquotaReferencia": 0.9,
                        "aliquotaPropria": 0.9
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Requisição com problema",
            content = @Content(
                mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Bad Request Example",
                    value = """
                    {
                      "type": "about:blank",
                      "title": "Bad Request",
                      "status": 400,
                      "detail": "Required parameter 'data' is not present.",
                      "instance": "/api/calculadora/dados-abertos/aliquota-uf"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Erro interno na API",
            content = @Content(
                mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Internal Server Error Example",
                    value = """
                    {
                      "type": "http://url-ambiente/errors/erro-interno",
                      "title": "Erro interno na API",
                      "status": 500,
                      "detail": "Falha ao processar a requisição.",
                      "instance": "/api/calculadora/dados-abertos/aliquota-uf"
                    }
                    """
                )
            )
        )
    })
    ResponseEntity<AliquotaDadosAbertosOutput> consultarAliquotaUf(
        @Parameter(description = "Código da UF", example = "43", required = true) Long codigoUf,
        @Parameter(description = "Data no padrão ISO 8601 (yyyy-MM-dd)", example = "2026-01-01", required = true) LocalDate data);


    @Operation(
        summary = "Alíquota Padrão ou de Referência para IBS Municipal",
        description = "Obtém a alíquota padrão ou de referência para IBS Municipal"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Consulta realizada com sucesso",
            headers = @Header(
                name = "x-warning-dados-simulados",
                description = "Indica que os dados são simulados. Valores possíveis: " +
                             "1 (alíquotas da CBS e do IBS ainda não definidas em lei).",
                schema = @Schema(type = "integer", example = "1")
            ),
            content = @Content(
                mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = AliquotaDadosAbertosOutput.class),
                examples = @ExampleObject(
                    name = "Alíquota Município Example",
                    value = """
                    {
                        "aliquotaReferencia": 0.9,
                        "aliquotaPropria": 0.9
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Requisição com problema",
            content = @Content(
                mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Bad Request Example",
                    value = """
                    {
                      "type": "about:blank",
                      "title": "Bad Request",
                      "status": 400,
                      "detail": "Required parameter 'data' is not present.",
                      "instance": "/api/calculadora/dados-abertos/aliquota-municipio"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Erro interno na API",
            content = @Content(
                mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Internal Server Error Example",
                    value = """
                    {
                      "type": "http://url-ambiente/errors/erro-interno",
                      "title": "Erro interno na API",
                      "status": 500,
                      "detail": "Falha ao processar a requisição.",
                      "instance": "/api/calculadora/dados-abertos/aliquota-municipio"
                    }
                    """
                )
            )
        )
    })
    ResponseEntity<AliquotaDadosAbertosOutput> consultarAliquotaMunicipio(
        @Parameter(description = "Código do Município (Tabela IBGE)", example = "4314902", required = true) Long codigoMunicipio,
        @Parameter(description = "Data no padrão ISO 8601 (yyyy-MM-dd)", example = "2026-01-01", required = true) LocalDate data);

    @Operation(summary = "Classificação Tributária CBS/IBS por DFe e cClassTrib", description = "Obtém a classificação tributária CBS/IBS com base na sigla do DFe e código da classificação")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso",
            content = @Content(mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ValidadeDfeClassificacaoTributariaDadosAbertosOutput.class))),
        @ApiResponse(responseCode = "400", description = "Requisição com problema",
            content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Bad Request Example",
                    value = """
                        {
                            "type": "about:blank",
                            "title": "Bad Request",
                            "status": 400,
                            "detail": "Required parameter 'data' is not present.",
                            "instance": "/api/calculadora/dados-abertos/classificacoes-tributarias/cbs-ibs/NFE/000001"
                        }
                        """
                ))),
        @ApiResponse(responseCode = "404", description = "Classificação tributária não encontrada",
            content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Not Found Example",
                    value = """
                        {
                            "type": "https://url-ambiente/errors/classificacao-tributaria-nao-encontrada",
                            "title": "Classificação Tributária Não Encontrada",
                            "status": 404,
                            "detail": "Classificação tributária não encontrada para DFe 'NFSE' e código '000001' na data '2026-01-01'",
                            "instance": "/api/calculadora/dados-abertos/classificacoes-tributarias/cbs-ibs/NFSE/000001",
                            "timestamp": 1760724111.1784844
                        }
                        """
                ))),
            @ApiResponse(responseCode = "422", description = "Erro de validação nos parâmetros",
                content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(
                        name = "Validation Error Example",
                        value = """
                            {
                                "type": "http://url-ambiente/errors/sigla-dfe-nao-reconhecida",
                                "title": "Sigla DFe não reconhecida",
                                "status": 422,
                                "detail": "Sigla DFe inválida: NFEJ",
                                "instance": "/api/calculadora/dados-abertos/classificacoes-tributarias/cbs-ibs/NFEe/620002",
                                "timestamp": 1760729074.1335235
                            }
                            """
                    ))),
                @ApiResponse(responseCode = "500", description = "Erro interno na API",
                    content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                        schema = @Schema(implementation = ProblemDetail.class),
                        examples = @ExampleObject(
                            name = "Internal Server Error Example",
                            value = """
                                {
                                  "type": "http://url-ambiente/errors/erro-de-sistema-nao-previsto",
                                  "title": "Erro de sistema não previsto",
                                  "status": 500,
                                  "detail": "Falha ao acessar o banco de dados",
                                  "instance": "/api/calculadora/dados-abertos/classificacoes-tributarias/cbs-ibs/NFE/620001",
                                  "timestamp": 1760730286.2395124
                                }
                                """
                        )
                    )
                )
    })
    ResponseEntity<ValidadeDfeClassificacaoTributariaDadosAbertosOutput> consultarValidadeDfeClassificacaoTributaria(
        @Parameter(description = "Sigla do tipo de Documento Fiscal Eletrônico", example = "NFSE", required = true) String siglaDfe,
        @Parameter(description = "Código da classificação tributária", example = "000001", required = true) String cClassTrib,
        @Parameter(description = "Data do fato gerador (yyyy-MM-dd)", example = "2026-01-01", required = true) LocalDate data);

    @Operation(summary = "Situações Tributárias (CSTs) com Classificações Tributárias - CBS/IBS",
               description = "Obtém a lista de situações tributárias (CSTs) com suas classificações tributárias aplicáveis para CBS/IBS em uma data informada. "
                   + "Quando o parâmetro siglaDfe é informado, retorna apenas as CSTs e classificações aplicáveis ao tipo de DFe; "
                   + "quando omitido, retorna todas as situações sem filtro de DFe.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso", content = {
            @Content(
                mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = SituacaoClassificacaoAninhadoOutput.class),
                examples = @ExampleObject(
                    name = "CSTs CBS/IBS with Classifications Example",
                    value = """
                    [
                      {
                        "id": 1,
                        "codigo": "000",
                        "descricao": "Tributação Integral",
                        "classificacoesTributarias": [
                          {
                            "codigo": "000001",
                            "descricao": "Situações tributadas integralmente pelo IBS e CBS."
                          },
                          {
                            "codigo": "000002",
                            "descricao": "Operação Interestadual"
                          }
                        ]
                      },
                      {
                        "id": 5,
                        "codigo": "060",
                        "descricao": "Diferimento",
                        "classificacoesTributarias": [
                          {
                            "codigo": "060001",
                            "descricao": "Operação com Diferimento"
                          }
                        ]
                      }
                    ]
                    """
                )
            )
        }),
        @ApiResponse(responseCode = "400", description = "Requisição inválida",
            content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Bad Request Example",
                    value = """
                    {
                      "type": "about:blank",
                      "title": "Bad Request",
                      "status": 400,
                      "detail": "Parâmetro inválido ou ausente",
                      "instance": "/api/calculadora/dados-abertos/situacoes-tributarias/cbs-ibs"
                    }
                    """
                )
            )
        ),
        @ApiResponse(responseCode = "422", description = "Erro de validação nos parâmetros",
            content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Validation Error Example",
                    value = """
                    {
                      "type": "http://url-ambiente/errors/sigla-dfe-nao-reconhecida",
                      "title": "Sigla DFe não reconhecida",
                      "status": 422,
                      "detail": "Sigla DFe inválida: NFEJ",
                      "instance": "/api/calculadora/dados-abertos/situacoes-tributarias/cbs-ibs",
                      "timestamp": 1760729074.1335235
                    }
                    """
                )
            )
        ),
        @ApiResponse(responseCode = "500", description = "Erro interno na API",
            content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Internal Server Error Example",
                    value = """
                    {
                      "type": "http://url-ambiente/errors/erro-de-sistema-nao-previsto",
                      "title": "Erro de sistema não previsto",
                      "status": 500,
                      "detail": "Falha ao acessar o banco de dados",
                      "instance": "/api/calculadora/dados-abertos/situacoes-tributarias/cbs-ibs",
                      "timestamp": 1760730286.2395124
                    }
                    """
                )
            )
        )
    })
    ResponseEntity<List<SituacaoClassificacaoAninhadoOutput>> consultarCstsComClassificacoesCbsIbs(
        @Parameter(description = "Sigla do tipo de Documento Fiscal Eletrônico (opcional); quando omitida, retorna todas as situações sem filtro de DFe", example = "NFE") String siglaDfe,
        @Parameter(description = "Data do fato gerador (yyyy-MM-dd)", example = "2026-01-01", required = true) LocalDate data);

    @Operation(summary = "Situações Tributárias (CSTs) com Classificações Tributárias - IS",
               description = "Obtém a lista de situações tributárias (CSTs) com suas classificações tributárias aplicáveis para o Imposto Seletivo (IS) em uma data informada. "
                   + "O parâmetro siglaDfe é opcional; quando informado e o Imposto Seletivo não for aplicável ao tipo de DFe, retorna lista vazia.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso", content = {
            @Content(
                mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = SituacaoClassificacaoAninhadoOutput.class),
                examples = @ExampleObject(
                    name = "CSTs IS with Classifications Example",
                    value = """
                    [
                      {
                        "codigo": "000",
                        "descricao": "Tributado com Imposto Seletivo",
                        "classificacoesTributarias": [
                          {
                            "codigo": "000001",
                            "descricao": "Primeiro fornecimento a qualquer título de bem"
                          }
                        ]
                      },
                      {
                        "codigo": "100",
                        "descricao": "Imunidade",
                        "classificacoesTributarias": [
                          {
                            "codigo": "100001",
                            "descricao": "Exportação"
                          }
                        ]
                      }
                    ]
                    """
                )
            )
        }),
        @ApiResponse(responseCode = "400", description = "Requisição inválida",
            content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Bad Request Example",
                    value = """
                    {
                      "type": "about:blank",
                      "title": "Bad Request",
                      "status": 400,
                      "detail": "Parâmetro inválido ou ausente",
                      "instance": "/api/calculadora/dados-abertos/situacoes-tributarias/is"
                    }
                    """
                )
            )
        ),
        @ApiResponse(responseCode = "422", description = "Erro de validação nos parâmetros",
            content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Validation Error Example",
                    value = """
                    {
                      "type": "http://url-ambiente/errors/sigla-dfe-nao-reconhecida",
                      "title": "Sigla DFe não reconhecida",
                      "status": 422,
                      "detail": "Sigla DFe inválida: NFEJ",
                      "instance": "/api/calculadora/dados-abertos/situacoes-tributarias/is",
                      "timestamp": 1760729074.1335235
                    }
                    """
                )
            )
        ),
        @ApiResponse(responseCode = "500", description = "Erro interno na API",
            content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Internal Server Error Example",
                    value = """
                    {
                      "type": "http://url-ambiente/errors/erro-de-sistema-nao-previsto",
                      "title": "Erro de sistema não previsto",
                      "status": 500,
                      "detail": "Falha ao acessar o banco de dados",
                      "instance": "/api/calculadora/dados-abertos/situacoes-tributarias/is",
                      "timestamp": 1760730286.2395124
                    }
                    """
                )
            )
        )
    })
    ResponseEntity<List<SituacaoClassificacaoAninhadoOutput>> consultarCstsComClassificacoesIs(
        @Parameter(description = "Sigla do tipo de Documento Fiscal Eletrônico (opcional); quando omitida, retorna todas as situações sem filtro de DFe", example = "NFE") String siglaDfe,
        @Parameter(description = "Data do fato gerador (yyyy-MM-dd)", example = "2027-01-01", required = true) LocalDate data);

    @Operation(summary = "Versão do Aplicativo e do Banco de Dados", description = "Obtém a versão do aplicativo e do banco de dados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso", content = {
            @Content(
                mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = VersaoOutput.class),
                examples = @ExampleObject(
                    name = "Versão Example",
                    value = """
                    {
                        "versaoApp": "0.0.0-SNAPSHOT",
                        "versaoDb": "v0011",
                        "descricaoVersaoDb": "Nova carga de classificação tributária e novos campos em CLASSIFICACAO_TRIBUTARIA e SITUACAO_TRIBUTARIA.",
                        "dataVersaoDb": "2025-10-16",
                        "ambiente": "online"
                    }
                    """
                )
            )
        }),
        @ApiResponse(responseCode = "500", description = "Erro interno na API", content = {
            @Content(
                mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Internal Server Error Example",
                    value = """
                    {
                      "type": "http://url-ambiente/errors/erro-interno",
                      "title": "Erro interno na API",
                      "status": 500,
                      "detail": "Falha ao processar a requisição.",
                      "instance": "/api/calculadora/dados-abertos/versao"
                    }
                    """
                )
            )
        })
    })
    ResponseEntity<VersaoOutput> consultarVersao();

    @Operation(summary = "Redutor de Compra Governamental", description = "Obtém a lista de todos os redutores de compra governamental cadastrados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso", content = {
            @Content(
                mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = RedutorCompraGovernamentalDadosAbertosOutput.class),
                examples = @ExampleObject(
                    name = "Redutores Example",
                    value = """
                    [
                        {
                            "valor": 0,
                            "inicioVigencia": "2026-01-01",
                            "fimVigencia": "2026-12-31"
                        },
                        {
                            "valor": 50,
                            "inicioVigencia": "2027-01-01"
                        }
                    ]
                    """
                )
            )
        }),
        @ApiResponse(responseCode = "500", description = "Erro interno na API",
            content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Internal Server Error Example",
                    value = """
                    {
                      "type": "http://url-ambiente/errors/erro-interno",
                      "title": "Erro interno na API",
                      "status": 500,
                      "detail": "Falha ao processar a requisição.",
                      "instance": "/api/calculadora/dados-abertos/redutores-compra-governamental"
                    }
                    """
                )
            )
        )
    })
    ResponseEntity<List<RedutorCompraGovernamentalDadosAbertosOutput>> consultarRedutoresCompraGovernamental();

    @Operation(summary = "Transferência CBS para Entes Governamentais", description = "Obtém a lista de todos os percentuais de transferência CBS cadastrados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso", content = {
            @Content(
                mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = TransferenciaCBSDadosAbertosOutput.class),
                examples = @ExampleObject(
                    name = "Transferências CBS Example",
                    value = """
                    [
                        {
                            "valor": 0.15,
                            "inicioVigencia": "2026-01-01"
                        },
                        {
                            "valor": 0.10,
                            "inicioVigencia": "2025-01-01",
                            "fimVigencia": "2025-12-31"
                        }
                    ]
                    """
                )
            )
        }),
        @ApiResponse(responseCode = "500", description = "Erro interno na API",
            content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Internal Server Error Example",
                    value = """
                    {
                      "type": "http://url-ambiente/errors/erro-interno",
                      "title": "Erro interno na API",
                      "status": 500,
                      "detail": "Falha ao processar a requisição.",
                      "instance": "/api/calculadora/dados-abertos/transferencias-cbs"
                    }
                    """
                )
            )
        )
    })
    ResponseEntity<List<TransferenciaCBSDadosAbertosOutput>> consultarTransferenciasCBS();
    
        @Operation(summary = "Transferência IBS para Entes Governamentais", description = "Obtém a lista de todos os percentuais de transferência IBS cadastrados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso", content = {
            @Content(
                mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = TransferenciaIBSDadosAbertosOutput.class),
                examples = @ExampleObject(
                    name = "Transferências IBS Example",
                    value = """
                    [
                        {
                            "valor": 0,
                            "inicioVigencia": "2026-01-01",
                            "fimVigencia": "2026-12-31"
                        },
                        {
                            "valor": 100,
                            "inicioVigencia": "2027-01-01"
                        }
                    ]
                    """
                )
            )
        }),
        @ApiResponse(responseCode = "500", description = "Erro interno na API",
            content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Internal Server Error Example",
                    value = """
                    {
                      "type": "http://url-ambiente/errors/erro-interno",
                      "title": "Erro interno na API",
                      "status": 500,
                      "detail": "Falha ao processar a requisição.",
                      "instance": "/api/calculadora/dados-abertos/transferencias-ibs"
                    }
                    """
                )
            )
        )
    })
    ResponseEntity<List<TransferenciaIBSDadosAbertosOutput>> consultarTransferenciasIBS();

    @Operation(summary = "Validar NBS Aplicável", description = "Valida se um NBS é aplicável para uma classificação tributária relacionada ao CBS e IBS em determinada data.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Validação realizada com sucesso", content = {
            @Content(
                mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = NbsAplicavelOutput.class),
                examples = @ExampleObject(
                    name = "NBS Aplicável Example",
                    value = """
                    {
                      "cClassTrib": "000001",
                      "nbs": "114052200",
                      "dataOcorrenciaFatoGerador": "2026-01-01",
                      "valido": true
                    }
                    """
                )
            )
        }),
        @ApiResponse(responseCode = "400", description = "Parâmetro inválido", content = {
            @Content(
                mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Bad Request Example",
                    value = """
                    {
                      "type": "http://url-ambiente/errors/parametro-invalido",
                      "title": "Parâmetro inválido",
                      "status": 400,
                      "detail": "NBS ou cClassTrib inválido.",
                      "instance": "/api/calculadora/dados-abertos/classificacoes-tributarias/nbs-aplicavel"
                    }
                    """
                )
            )
        }),
        @ApiResponse(responseCode = "500", description = "Erro interno na API", content = {
            @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Internal Server Error Example",
                    value = """
                    {
                      "type": "http://url-ambiente/errors/erro-interno",
                      "title": "Erro interno na API",
                      "status": 500,
                      "detail": "Falha ao processar a requisição.",
                      "instance": "/api/calculadora/dados-abertos/classificacoes-tributarias/nbs-aplicavel"
                    }
                    """
                )
            )
        })
    })
    ResponseEntity<NbsAplicavelOutput> validarNbsAplicavel(
        @Parameter(description = "Código da classificação tributária", required = true, example = "000001")
        @RequestParam String cClassTrib,
        @Parameter(description = "NBS", required = true, example = "114052200")
        @RequestParam String nbs,
        @Parameter(description = "Data de ocorrência do fato gerador", required = true, example = "2026-01-01")
        @RequestParam LocalDate dataOcorrenciaFatoGerador
    );

    @Operation(summary = "Validar NCM Aplicável", description = "Valida se um NCM é aplicável para uma classificação tributária relacionada ao CBS e IBS em determinada data.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Validação realizada com sucesso", content = {
            @Content(
                mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = NcmAplicavelOutput.class),
                examples = @ExampleObject(
                    name = "NCM Aplicável Example",
                    value = """
                    {
                      "cClassTrib": "000001",
                      "ncm": "09024000",
                      "dataOcorrenciaFatoGerador": "2026-01-01",
                      "valido": true
                    }
                    """
                )
            )
        }),
        @ApiResponse(responseCode = "400", description = "Parâmetro inválido", content = {
            @Content(
                mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Bad Request Example",
                    value = """
                    {
                      "type": "http://url-ambiente/errors/parametro-invalido",
                      "title": "Parâmetro inválido",
                      "status": 400,
                      "detail": "NCM ou cClassTrib inválido.",
                      "instance": "/api/calculadora/dados-abertos/classificacoes-tributarias/ncm-aplicavel"
                    }
                    """
                )
            )
        }),
        @ApiResponse(responseCode = "500", description = "Erro interno na API", content = {
            @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Internal Server Error Example",
                    value = """
                    {
                      "type": "http://url-ambiente/errors/erro-interno",
                      "title": "Erro interno na API",
                      "status": 500,
                      "detail": "Falha ao processar a requisição.",
                      "instance": "/api/calculadora/dados-abertos/classificacoes-tributarias/ncm-aplicavel"
                    }
                    """
                )
            )
        })
    })
    ResponseEntity<NcmAplicavelOutput> validarNcmAplicavel(
        @Parameter(description = "Código da classificação tributária CBS/IBS", required = true, example = "000001")
        @RequestParam String cClassTrib,
        @Parameter(description = "NCM", required = true, example = "09024000")
        @RequestParam String ncm,
        @Parameter(description = "Data de ocorrência do fato gerador", required = true, example = "2026-01-01")
        @RequestParam LocalDate dataOcorrenciaFatoGerador
    );

    @Operation(
    summary = "Nomenclatura Aplicável",
    description = "Determina qual nomenclatura (NCM, NBS, MISTO,SEM, EXCECAO_NCM ou EXCECAO_NBS) é aplicável para uma combinação de tipo de documento fiscal eletrônico, classificação tributária e data. "
        + "Devolve também os sinais de obrigatoriedade: obrigatorio (true para NCM, NBS, MISTO, EXCECAO_NCM e EXCECAO_NBS; false para SEM) e, "
        + "para desambiguar o resultado SEM, ncmOpcional (true quando SEM e o DFe é NF-e/NFC-e) e nbsOpcional (true quando SEM e o DFe é NFS-e)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso",
            content = @Content(mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = NomenclaturaDadosAbertosOutput.class),
                examples = {
                    @ExampleObject(name = "NCM Aplicável", value = """
                        {"siglaDfe":"NFe","cClassTrib":"000001","data":"2026-01-01","nomenclatura":"NCM","obrigatorio":true,"ncmOpcional":false,"nbsOpcional":false}
                    """),
                    @ExampleObject(name = "NBS Aplicável", value = """
                        {"siglaDfe":"NFSe","cClassTrib":"000001","data":"2026-01-01","nomenclatura":"NBS","obrigatorio":true,"ncmOpcional":false,"nbsOpcional":false}
                    """),
                    @ExampleObject(name = "Nenhuma Aplicável (NCM opcional)", value = """
                        {"siglaDfe":"NFe","cClassTrib":"200001","data":"2026-01-01","nomenclatura":"SEM","obrigatorio":false,"ncmOpcional":true,"nbsOpcional":false}
                    """),
                    @ExampleObject(name = "Nenhuma Aplicável", value = """
                        {"siglaDfe":"BPe","cClassTrib":"000001","data":"2026-01-01","nomenclatura":"SEM","obrigatorio":false,"ncmOpcional":false,"nbsOpcional":false}
                    """)
                }
            )),
        @ApiResponse(responseCode = "400", description = "Requisição com problema",
            content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Bad Request Example",
                    value = """
                    {
                        "type": "about:blank",
                        "title": "Bad Request",
                        "status": 400,
                        "detail": "Required parameter 'data' is not present.",
                        "instance": "/api/calculadora/dados-abertos/nomenclatura/NFe/000001"
                    }
                    """
                ))),
        @ApiResponse(responseCode = "404", description = "Classificação tributária ou siglaDfe não encontrada",
            content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Not Found Example",
                    value = """
                    {
                        "type": "https://url-ambiente/errors/classificacao-tributaria-nao-encontrada",
                        "title": "Classificação Tributária Não Encontrada",
                        "status": 404,
                        "detail": "Classificação tributária não encontrada para código 999999 e data 2026-01-01 (CBS/IBS)",
                        "instance": "/api/calculadora/dados-abertos/nomenclatura/NFe/999999"
                    }
                    """
                ))),
        @ApiResponse(responseCode = "422", description = "Erro de validação nos parâmetros",
            content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Validation Error Example",
                    value = """
                    {
                        "type": "http://url-ambiente/errors/sigla-dfe-nao-reconhecida",
                        "title": "Sigla DFe não reconhecida",
                        "status": 422,
                        "detail": "Sigla DFe inválida: NFEJ",
                        "instance": "/api/calculadora/dados-abertos/nomenclatura/NFEJ/000001"
                    }
                    """
                ))),
        @ApiResponse(responseCode = "500", description = "Erro interno na API",
            content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Internal Server Error Example",
                    value = """
                    {
                        "type": "http://url-ambiente/errors/erro-de-sistema-nao-previsto",
                        "title": "Erro de sistema não previsto",
                        "status": 500,
                        "detail": "Falha ao acessar o banco de dados",
                        "instance": "/api/calculadora/dados-abertos/nomenclatura/NFe/000001"
                    }
                    """
                )
            )
        )
    })
    ResponseEntity<NomenclaturaDadosAbertosOutput> consultarNomenclatura(
        @Parameter(description = "Sigla do tipo de Documento Fiscal Eletrônico", example = "NFe", required = true) String siglaDfe,
        @Parameter(description = "Código da classificação tributária", example = "000001", required = true) String cClassTrib,
        @Parameter(description = "Data do fato gerador (yyyy-MM-dd)", example = "2026-01-01", required = true) LocalDate data);

    @Operation(summary = "Tipos de DFe agrupados",
        description = "Obtém os tipos de Documento Fiscal Eletrônico (DFe) vigentes, agrupados pela sua categoria, "
            + "para a seleção do tipo de documento. O campo codigo de cada tipo é a sigla usada como siglaDfe nos demais endpoints")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso", content = {
            @Content(
                mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = GrupoDfeDadosAbertosOutput.class),
                examples = @ExampleObject(
                    name = "Grupos DFe Example",
                    value = """
                    [
                      {
                        "id": "1",
                        "titulo": "MERCADORIAS",
                        "ordem": 1,
                        "tipos": [
                          { "codigo": "NFE", "titulo": "Nota Fiscal Eletrônica", "modelo": "55", "ordem": 1 },
                          { "codigo": "NFCE", "titulo": "Nota Fiscal de Consumidor Eletrônica", "modelo": "65", "ordem": 2 }
                        ]
                      }
                    ]
                    """
                )
            )
        }),
        @ApiResponse(responseCode = "400", description = "Requisição com problema",
            content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Bad Request Example",
                    value = """
                    {
                      "type": "about:blank",
                      "title": "Bad Request",
                      "status": 400,
                      "detail": "Parâmetro inválido ou ausente.",
                      "instance": "/api/calculadora/dados-abertos/dfe/grupos"
                    }
                    """
                )
            )
        ),
        @ApiResponse(responseCode = "500", description = "Erro interno na API",
            content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Internal Server Error Example",
                    value = """
                    {
                      "type": "http://url-ambiente/errors/erro-interno",
                      "title": "Erro interno na API",
                      "status": 500,
                      "detail": "Falha ao processar a requisição.",
                      "instance": "/api/calculadora/dados-abertos/dfe/grupos"
                    }
                    """
                )
            )
        )
    })
    ResponseEntity<List<GrupoDfeDadosAbertosOutput>> consultarTiposDfeAgrupados(
        @Parameter(description = "Data de vigência (yyyy-MM-dd); ausente usa a data atual", example = "2026-01-01") LocalDate data);

    @Operation(
        summary = "Atores agrupados",
        description = "Obtém a lista de atores vigentes agrupados, com filtro opcional por papel."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso",
            headers = @Header(name = "Cache-Control", description = "public, max-age=3600"),
            content = {
                @Content(
                    mediaType = APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = GrupoAtorDadosAbertosOutput.class),
                    examples = @ExampleObject(
                        name = "Atores Agrupados Example",
                        value = """
                        [
                          {
                            "id": 1,
                            "descricao": "Regime regular / contribuinte padrão (arts. 21 ss.)",
                            "ordem": 1,
                            "atores": [
                              { "id": 22, "descricao": "Contribuinte sujeito ao regime regular do IBS e da CBS", "ordem": 1 }
                            ]
                          },
                          {
                            "id": 7,
                            "descricao": "Serviços financeiros, seguros, FGTS e consórcios (arts. 181-233)",
                            "ordem": 7,
                            "atores": [
                              { "id": 4, "descricao": "Agente financeiro do FGTS (exceto CEF)", "ordem": 1 },
                              { "id": 5, "descricao": "Estabelecimento bancário (exceto CEF)", "ordem": 2 }
                            ]
                          }
                        ]
                        """
                    )
                )
            }
        ),
        @ApiResponse(responseCode = "400", description = "Requisição com problema",
            content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Bad Request Example",
                    value = """
                    {
                      "type": "about:blank",
                      "title": "Bad Request",
                      "status": 400,
                      "detail": "Required parameter 'data' is not present.",
                      "instance": "/api/calculadora/dados-abertos/ator/grupos"
                    }
                    """
                )
            )
        ),
        @ApiResponse(responseCode = "500", description = "Erro interno na API",
            content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "Internal Server Error Example",
                    value = """
                    {
                      "type": "http://url-ambiente/errors/erro-interno",
                      "title": "Erro interno na API",
                      "status": 500,
                      "detail": "Falha ao processar a requisição.",
                      "instance": "/api/calculadora/dados-abertos/ator/grupos"
                    }
                    """
                )
            )
        )
    })
    ResponseEntity<List<GrupoAtorDadosAbertosOutput>> consultarAtoresAgrupados(
        @Parameter(description = "Data no padrão ISO 8601 (yyyy-MM-dd)", example = "2027-01-01", required = true) LocalDate data,
        @Parameter(description = "Papel do ator (opcional)", example = "FORNECEDOR",
            schema = @Schema(implementation = PapelAtorEnum.class)) PapelAtorEnum papel);

}
