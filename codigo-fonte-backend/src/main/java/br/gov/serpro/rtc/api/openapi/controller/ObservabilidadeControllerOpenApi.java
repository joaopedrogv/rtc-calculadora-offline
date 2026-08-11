package br.gov.serpro.rtc.api.openapi.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;

import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import br.gov.serpro.rtc.api.exceptionhandler.dto.ErroDescricao;
import br.gov.serpro.rtc.api.model.input.OperacaoInput;
import br.gov.serpro.rtc.api.model.roc.ObservabilidadeROCDomain;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Contrato OpenAPI dos endpoints de observabilidade, incluindo cálculo com
 * erros por item e consulta ao catálogo de códigos de erro.
 */
@Tag(name = "Observabilidade - Calculadora", description = "Endpoint de observabilidade da Calculadora de Tributos")
public interface ObservabilidadeControllerOpenApi {

    @Operation(summary = "Cálculo com observabilidade", description = "Processa todos os itens acumulando erros individuais. Retorna resultado parcial com estado de cada item.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Processamento realizado (itens podem ter erros individuais)", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ObservabilidadeROCDomain.class)) }),
            @ApiResponse(responseCode = "400", description = "Estrutura e/ou dados informados em formato não reconhecido", content = {
                    @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }),
            @ApiResponse(responseCode = "422", description = "Erro de validação global (UF, Município, data do fato gerador)", content = {
                    @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }),
            @ApiResponse(responseCode = "500", description = "Erro interno na API", content = {
                    @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }) })
    ResponseEntity<ObservabilidadeROCDomain> calcularTributosObservabilidade(
            @RequestBody(description = "Dados de uma operação de consumo", required = true, content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = OperacaoInput.class))) OperacaoInput operacao);

    @Operation(summary = "Consultar descrição de erro", description = "Retorna título e descrição em PT-BR para o código de erro informado (REG, CAL ou VAL).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Descrição do erro encontrada", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErroDescricao.class)) }),
            @ApiResponse(responseCode = "404", description = "Código de erro não encontrado") })
    ResponseEntity<ErroDescricao> buscarErro(
            @Parameter(description = "Código do erro (ex: REG-020, CAL-001, VAL-001)", required = true) String codigo);
}
