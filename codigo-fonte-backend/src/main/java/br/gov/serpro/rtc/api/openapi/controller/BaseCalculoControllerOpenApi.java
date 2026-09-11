/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.api.openapi.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;

import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import br.gov.serpro.rtc.api.model.input.basecalculo.BaseCalculoCibsInput;
import br.gov.serpro.rtc.api.model.input.basecalculo.BaseCalculoISMercadoriasInput;
import br.gov.serpro.rtc.api.model.input.basecalculo.BaseCalculoUnificadaInput;
import br.gov.serpro.rtc.api.model.input.basecalculo.BaseCalculoUnificadaNfseInput;
import br.gov.serpro.rtc.api.model.output.basecalculo.BaseCalculoCibsModel;
import br.gov.serpro.rtc.api.model.output.basecalculo.BaseCalculoISMercadoriasModel;
import br.gov.serpro.rtc.api.model.output.basecalculo.BaseCalculoUnificadaOutput;
import br.gov.serpro.rtc.api.model.output.basecalculo.BaseCalculoUnificadaNfseOutput;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Contrato OpenAPI dos endpoints de base de cálculo para Imposto Seletivo e
 * CBS/IBS.
 */
@Tag(name = "Base de Cálculo - VERSÃO BETA", description = "Serviço para Base de Cálculo")
public interface BaseCalculoControllerOpenApi {

    @Operation(summary = "Imposto Seletivo", description = "Afere a Base de Cálculo do Imposto Seletivo de uma operação de consumo. "
            + "ATENÇÃO: Os campos ICMS e ISS não podem ser informados a partir de 2033.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cálculo realizado com sucesso", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = BaseCalculoISMercadoriasModel.class)) }),
            @ApiResponse(responseCode = "400", description = "Estrutura e/ou dados informados em formato não reconhecido ou campos incompatíveis com o ano do fato gerador informado (ICMS e ISS a partir de 2033)", content = {
                    @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }),
            @ApiResponse(responseCode = "404", description = "Erro na URL da requisição", content = {
                    @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }),
            @ApiResponse(responseCode = "422", description = "Erro de validação", content = {
                    @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }),
            @ApiResponse(responseCode = "500", description = "Erro interno na API", content = {
                    @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }) })
    ResponseEntity<BaseCalculoISMercadoriasModel> calcularISMercadorias(
            @RequestBody(
                    description = "Dados da base de cálculo do Imposto Seletivo", 
                    required = true, 
                    content = @Content(
                            mediaType = APPLICATION_JSON_VALUE, 
                            schema = @Schema(implementation = BaseCalculoISMercadoriasInput.class))
                    ) BaseCalculoISMercadoriasInput baseCalculo);

    @Operation(summary = "CIBS", description = "Afere a Base de Cálculo da CBS/IBS de uma operação de consumo. "
            + "ATENÇÃO: Os campos PIS, COFINS, PIS Importação e COFINS Importação não podem ser informados a partir de 2027. "
            + "Os campos ICMS e ISS não podem ser informados a partir de 2033.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cálculo realizado com sucesso", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = BaseCalculoCibsModel.class)) }),
            @ApiResponse(responseCode = "400", description = "Estrutura e/ou dados informados em formato não reconhecido ou campos incompatíveis com o ano do fato gerador informado (PIS, COFINS, PIS Importação e COFINS a partir de 2027; ICMS e ISS a partir de 2033)", content = {
                    @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }),
            @ApiResponse(responseCode = "404", description = "Erro na URL da requisição", content = {
                    @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }),
            @ApiResponse(responseCode = "422", description = "Erro de validação", content = {
                    @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) }),
            @ApiResponse(responseCode = "500", description = "Erro interno na API", content = {
                    @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)) })
    })
    ResponseEntity<BaseCalculoCibsModel> calcularCibs(
            @RequestBody(
                    description = "Dados da base de cálculo do CIBS", 
                    required = true, 
                    content = @Content(
                            mediaType = APPLICATION_JSON_VALUE, 
                            schema = @Schema(implementation = BaseCalculoCibsInput.class))
                    ) BaseCalculoCibsInput baseCalculo);

    @Operation(
        summary = "Cálculo Base Unificada",
        description = "Calcula as três bases de cálculo (RG/IBS-CBS, IS, SN) para um único item. " +
            "Recebe todos os campos possíveis e aplica as regras de vigência conforme ano, natureza e modelo. " +
            "Campos informados com valor diferente de zero que não compõem o contexto (fora de vigência, " +
            "natureza incompatível ou modelo de documento inválido) são listados em 'avisos' com o motivo — " +
            "comportamento informativo, não bloqueante: as bases retornadas já desconsideram esses campos. "
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cálculo realizado com sucesso",
            content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = BaseCalculoUnificadaOutput.class))),
        @ApiResponse(responseCode = "400", description = "Dados inconsistentes ou ano/modelo/natureza inválidos",
            content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "422", description = "Erro de validação de campos",
            content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)))
    })
    ResponseEntity<BaseCalculoUnificadaOutput> calcularBaseUnificada(
        @RequestBody(
            description = "Dados completos para cálculo das 3 bases.", 
            required = true, 
            content = @Content(mediaType = APPLICATION_JSON_VALUE, 
                schema = @Schema(implementation = BaseCalculoUnificadaInput.class, example = """
                    {
                      "anoFatoGerador": 2026,
                      "tipoDocumento": 55,
                      "natureza": "BEM",
                      "vProd": 1000.00,
                      "vFrete": 50.00,
                      "vSeg": 20.00,
                      "vOutro": 10.00,
                      "vII": 150.00,
                      "vIS": 30.00,
                      "vDesc": 50.00,
                      "vPIS": 9.25,
                      "vCOFINS": 46.25,
                      "vICMS": 180.00,
                      "vICMSUFDest": 20.00,
                      "vICMSMono": 100.00,
                      "vFCP": 10.00,
                      "vFCPUFDest": 5.00,
                      "vISSQN": 30.00
                    }"""))) BaseCalculoUnificadaInput input
    );
    
    @Operation(
        summary = "[EXPERIMENTAL] Base de Cálculo Unificada NFS-e",
        description = "Calcula as bases de cálculo de IBS/CBS (RG) e Simples Nacional (SN) "
            + "para operações de NFS-e. Retorna bases, avisos e erros de negócio na mesma resposta."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cálculo realizado com sucesso",
            content = @Content(mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = BaseCalculoUnificadaNfseOutput.class))),
        @ApiResponse(responseCode = "400", description = "Campos obrigatórios ausentes ou valores inválidos",
            content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class)))
    })
    ResponseEntity<BaseCalculoUnificadaNfseOutput> calcularBaseUnificadaNfse(
        @RequestBody(
            description = "Dados da operação NFS-e para cálculo das bases de cálculo unificadas.",
            required = true,
            content = @Content(mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = BaseCalculoUnificadaNfseInput.class))
        ) BaseCalculoUnificadaNfseInput input);

}
