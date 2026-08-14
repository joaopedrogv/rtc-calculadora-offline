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

import br.gov.serpro.rtc.api.model.input.nfse.NfseBaseCalculoInput;
import br.gov.serpro.rtc.api.model.input.nfse.NfseValidacaoIndicadorOperacaoInput;
import br.gov.serpro.rtc.api.model.output.nfse.NfseBaseCalculoOutput;
import br.gov.serpro.rtc.api.model.output.nfse.NfseSituacaoClassificacaoOutput;
import br.gov.serpro.rtc.api.model.output.nfse.NfseCodigoDescricaoOutput;
import br.gov.serpro.rtc.api.model.output.nfse.NfseIndicadorOperacaoOutput;
import br.gov.serpro.rtc.api.model.output.nfse.NfseLocalOperacaoOutput;
import br.gov.serpro.rtc.api.model.output.nfse.NfseValidacaoIndicadorOperacaoOutput;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;


/**
 * Contrato OpenAPI dos endpoints de NFS-e para cálculo de base, validação de
 * indicador de operação e consultas auxiliares.
 */
@Tag(name = "NFS-e - VERSÃO BETA", description = "Serviço para cálculo de Base de Cálculo de NFS-e")
public interface NfseControllerOpenApi {

    @Operation(summary = "Base de Cálculo NFS-e", description = "Afere a Base de Cálculo de uma NFS-e no período de transição. "
            + "ATENÇÃO: Os campos PIS e COFINS não podem ser informados a partir de 2027. "
            + "O campo ISS não pode ser informado a partir de 2033.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cálculo realizado com sucesso", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = NfseBaseCalculoOutput.class)) }),
            @ApiResponse(responseCode = "400", description = "Estrutura e/ou dados informados em formato não reconhecido ou campos incompatíveis com o ano do fato gerador informado (PIS e COFINS a partir de 2027; ISS a partir de 2033)", content = {
                    @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }),
            @ApiResponse(responseCode = "404", description = "Erro na URL da requisição", content = {
                    @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }),
            @ApiResponse(responseCode = "422", description = "Erro de validação", content = {
                    @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }),
            @ApiResponse(responseCode = "500", description = "Erro interno na API", content = {
                    @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }) })
    ResponseEntity<NfseBaseCalculoOutput> calcularBaseCalculo(
            @Valid
            @RequestBody(
                    description = "Dados da base de cálculo da NFS-e", 
                    required = true, 
                    content = @Content(
                            mediaType = APPLICATION_JSON_VALUE, 
                            schema = @Schema(implementation = NfseBaseCalculoInput.class))
                    ) NfseBaseCalculoInput baseCalculo);

    @Operation(summary = "Validação de Indicador de Operação", 
               description = "Valida se o cIndOp é compatível com cClassTrib, NBS e cTribNac (opcional)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Validação realizada com sucesso", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE, 
                            schema = @Schema(implementation = NfseValidacaoIndicadorOperacaoOutput.class)) }),
            @ApiResponse(responseCode = "400", description = "Requisição com problema", content = {
                    @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, 
                            schema = @Schema(implementation = ProblemDetail.class)) }),
            @ApiResponse(responseCode = "404", description = "Erro na URL da requisição", content = {
                    @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, 
                            schema = @Schema(implementation = ProblemDetail.class)) }),
            @ApiResponse(responseCode = "500", description = "Erro interno na API", content = {
                    @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, 
                            schema = @Schema(implementation = ProblemDetail.class)) }) })
    ResponseEntity<NfseValidacaoIndicadorOperacaoOutput> validarIndicadorOperacao(
            @Valid
            @RequestBody(
                    description = "Dados para validação: cIndOp, cClassTrib, NBS e cTribNac (opcional)", 
                    required = true, 
                    content = @Content(
                            mediaType = APPLICATION_JSON_VALUE, 
                            schema = @Schema(implementation = NfseValidacaoIndicadorOperacaoInput.class))
                    ) 
            NfseValidacaoIndicadorOperacaoInput input);

    @Operation(summary = "Local da Operação", 
               description = "Consulta o local da operação baseado no código do Indicador de Operação (cIndOp) e data de ocorrência do fato gerador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE, 
                            schema = @Schema(implementation = NfseLocalOperacaoOutput.class),
                            examples = @ExampleObject(
                                name = "Local Operação Example",
                                value = """
                                {
                                  "cIndOp": "100301",
                                  "dataOcorrenciaFatoGerador": "2026-01-01",
                                  "codigoLocalFornecimento": 11,
                                  "localFornecimento": "Local do domicílio principal do adquirente residente ou domiciliado no País. Nas aquisições indicadas no art. 11, §4º, II, considera-se domicílio principal do adquirente o estabelecimentro matriz",
                                  "codigoLocalIncidencia": 2,
                                  "localIncidencia": "Endereço do Tomador/Adquirente"
                                }
                                """
                            )) }),
            @ApiResponse(responseCode = "400", description = "Requisição com problema", content = {
                    @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, 
                            schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(
                                name = "Bad Request Example",
                                value = """
                                {
                                  "type": "about:blank",
                                  "title": "Bad Request",
                                  "status": 400,
                                  "detail": "Required parameter 'cIndOp' is not present.",
                                  "instance": "/api/calculadora/nfse/local-operacao"
                                }
                                """
                            )) }),
            @ApiResponse(responseCode = "404", description = "Erro na URL da requisição", content = {
                    @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, 
                            schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(
                                name = "Not Found Example",
                                value = """
                                {
                                  "type": "about:blank",
                                  "title": "Not Found",
                                  "status": 404,
                                  "detail": "No static resource calculadora/nfse/local-operacao.",
                                  "instance": "/api/calculadora/nfse/local-operacao-invalido"
                                }
                                """
                            )) }),
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
                                  "instance": "/api/calculadora/nfse/local-operacao"
                                }
                                """
                            )) }) })
    ResponseEntity<NfseLocalOperacaoOutput> consultarLocalOperacao(
            @Parameter(description = "Código do Indicador de Operação (6 dígitos)", example = "100301", required = true)
            @NotBlank(message = "O campo cIndOp é obrigatório")
            @NotNull(message = "O campo cIndOp não pode ser nulo")
            @Pattern(regexp = "^\\d{6}$", message = "O campo cIndOp deve conter exatamente 6 dígitos numéricos")
            String cIndOp,
            @Parameter(description = "Data de ocorrência do fato gerador no padrão ISO 8601 (yyyy-MM-dd)", example = "2026-01-01", required = true)
            @NotNull(message = "O campo dataOcorrenciaFatoGerador é obrigatório")
            LocalDate dataOcorrenciaFatoGerador);
    
    
    @Operation(summary = "Indicador de Operação", 
            description = "Consulta os indicadores de operação baseado na data de ocorrência do fato gerador")
    @ApiResponses(value = {
         @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso", content = {
                 @Content(mediaType = APPLICATION_JSON_VALUE, 
                         schema = @Schema(implementation = NfseIndicadorOperacaoOutput.class),
                         examples = {
                                @ExampleObject(
                             name = "Indicador Operação Example",
                             value = """
                                    [
                                        {
                                        "cIndOp": "020101",
                                        "tipoOperacao": "Operação com bem imóvel, bem imaterial, inclusive direito,  relacionada a bem imóvel",
                                        "codigoLocalFornecimento": 14,
                                        "localFornecimento": "Localidade do imóvel",
                                        "codigoLocalIncidencia": 6,
                                        "localIncidencia": "Localidade do Imóvel"
                                        },
                                        {
                                        "cIndOp": "020201",
                                        "tipoOperacao": "Serviço prestado fisicamente sobre bem imóvel",
                                        "codigoLocalFornecimento": 14,
                                        "localFornecimento": "Localidade do imóvel",
                                        "codigoLocalIncidencia": 6,
                                        "localIncidencia": "Localidade do Imóvel"
                                        },
                                        {
                                        "cIndOp": "020301",
                                        "tipoOperacao": "Serviço de administração e intermediação de bem imóvel",
                                        "codigoLocalFornecimento": 14,
                                        "localFornecimento": "Localidade do imóvel",
                                        "codigoLocalIncidencia": 6,
                                        "localIncidencia": "Localidade do Imóvel"
                                        }
                                    ]
                             """
                         ),
                         @ExampleObject(
                             name = "Indicador Operação com NBS Example",
                             value = """
                                     [
                                        {
                                        "cIndOp": "100301",
                                        "tipoOperacao": "Demais serviços, em operações onerosas",
                                        "codigoLocalFornecimento": 11,
                                        "localFornecimento": "Local do domicílio principal do adquirente residente ou domiciliado no País. Nas aquisições indicadas no art. 11, §4º, II, considera-se domicílio principal do adquirente o estabelecimentro matriz",
                                        "codigoLocalIncidencia": 2,
                                        "localIncidencia": "Endereço do Tomador/Adquirente",
                                        "prestacaoServicoOnerosa": true,
                                        "adquirenteExterior": false
                                        }
                                     ]
                             """
                         )
                         }) }),
         @ApiResponse(responseCode = "400", description = "Requisição com problema", content = {
                 @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, 
                         schema = @Schema(implementation = ProblemDetail.class),
                         examples = @ExampleObject(
                             name = "Bad Request Example",
                             value = """
                             {
                               "type": "about:blank",
                               "title": "Bad Request",
                               "status": 400,
                               "detail": "Required parameter 'dataOcorrenciaFatoGerador' is not present.",
                               "instance": "/api/calculadora/nfse/indicador-operacao"
                             }
                             """
                         )) }),
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
                               "instance": "/api/calculadora/nfse/indicador-operacao"
                             }
                             """
                         )) }) })
    List<NfseIndicadorOperacaoOutput> consultarIndicadoresOperacao(
            @Parameter(description = "Data de ocorrência do fato gerador no padrão ISO 8601 (yyyy-MM-dd)", example = "2026-01-01", required = true)
            @NotNull(message = "O campo dataOcorrenciaFatoGerador é obrigatório")
            LocalDate dataOcorrenciaFatoGerador,
            @Parameter(description = "Código NBS (9 dígitos numéricos) - quando informado, retorna campos adicionais prestacaoServicoOnerosa e adquirenteExterior", example = "123456789", required = false)
            @Pattern(regexp = "^\\d{9}$", message = "O campo nbs deve conter exatamente 9 dígitos numéricos")
            String nbs);
    
    @Operation(
        summary = "Consultar Classificações e Situações Tributárias por NBS",
        description = "Retorna todas as combinações de Classificações Tributárias e Situações Tributárias " +
                      "associadas a um código NBS em uma data específica"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Consulta realizada com sucesso",
            content = @Content(
                mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = NfseSituacaoClassificacaoOutput.class),
                examples = @ExampleObject(
                    name = "Classificações e Situações Example",
                    value = """
                    [
                      {
                        "codigoClassificacaoTributaria": "200045",
                        "descricaoClassificacaoTributaria": "Operações relacionadas a projetos de reabilitação urbana de zonas históricas e de áreas críticas de recuperação e reconversão urbanística",
                        "codigoSituacaoTributaria": "200",
                        "descricaoSituacaoTributaria": "Alíquota reduzida"
                      },
                      {
                        "codigoClassificacaoTributaria": "200046",
                        "descricaoClassificacaoTributaria": "Operações com bens imóveis",
                        "codigoSituacaoTributaria": "200",
                        "descricaoSituacaoTributaria": "Alíquota reduzida"
                      }
                    ]
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Parâmetros inválidos",
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
                      "detail": "NBS deve conter exatamente 9 dígitos numéricos",
                      "instance": "/api/calculadora/nfse/classificacoes-situacoes-tributarias"
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
                schema = @Schema(implementation = ProblemDetail.class)
            )
        )
    })
    ResponseEntity<List<NfseSituacaoClassificacaoOutput>> consultarSituacoesClassificacoesPorNbs(
        @Parameter(description = "Código NBS sem formatação", example = "101011100", required = true)
        @Pattern(regexp = "^\\d{9}$", message = "O código NBS deve conter exatamente 9 dígitos numéricos")
        String nbs,
        
        @Parameter(description = "Data no padrão ISO 8601 (yyyy-MM-dd)", example = "2026-06-01", required = true)
        @NotNull(message = "O campo data é obrigatório")
        LocalDate data
    );

    @Operation(summary = "Locais de Fornecimento", 
               description = "Retorna a lista de códigos e descrições dos locais de fornecimento. Se o parâmetro codigo for informado, retorna apenas o valor correspondente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = NfseCodigoDescricaoOutput.class)) }),
            @ApiResponse(responseCode = "400", description = "Código inválido", content = {
                    @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class)) }),
            @ApiResponse(responseCode = "500", description = "Erro interno na API", content = {
                    @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class)) }) })
    List<NfseCodigoDescricaoOutput> consultarLocaisFornecimento(
            @Parameter(description = "Código do local de fornecimento (opcional)", example = "5", required = false)
            Integer codigo);

    @Operation(summary = "Locais de Incidência", 
               description = "Retorna a lista de códigos e descrições dos locais de incidência. Se o parâmetro codigo for informado, retorna apenas o valor correspondente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = NfseCodigoDescricaoOutput.class)) }),
            @ApiResponse(responseCode = "400", description = "Código inválido", content = {
                    @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class)) }),
            @ApiResponse(responseCode = "500", description = "Erro interno na API", content = {
                    @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class)) }) })
    List<NfseCodigoDescricaoOutput> consultarLocaisIncidencia(
            @Parameter(description = "Código do local de incidência (opcional)", example = "3", required = false)
            Integer codigo);
}
