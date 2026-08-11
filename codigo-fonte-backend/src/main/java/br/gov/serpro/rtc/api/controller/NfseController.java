/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.api.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.gov.serpro.rtc.api.model.input.nfse.NfseBaseCalculoInput;
import br.gov.serpro.rtc.api.model.input.nfse.NfseValidacaoIndicadorOperacaoInput;
import br.gov.serpro.rtc.api.model.output.nfse.NfseBaseCalculoOutput;
import br.gov.serpro.rtc.api.model.output.nfse.NfseSituacaoClassificacaoOutput;
import br.gov.serpro.rtc.api.model.output.nfse.NfseIndicadorOperacaoOutput;
import br.gov.serpro.rtc.api.model.output.nfse.NfseLocalOperacaoOutput;
import br.gov.serpro.rtc.api.model.output.nfse.NfseValidacaoIndicadorOperacaoOutput;
import br.gov.serpro.rtc.api.openapi.controller.NfseControllerOpenApi;
import br.gov.serpro.rtc.domain.service.nfse.NfseService;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para cálculo de base de cálculo, validação de indicador de
 * operação e consultas auxiliares de NFS-e.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("calculadora/nfse")
public class NfseController implements NfseControllerOpenApi {

    private final NfseService nfseService;

    @Override
    @PostMapping(
        value = "base-calculo",
        consumes = APPLICATION_JSON_VALUE,
        produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<NfseBaseCalculoOutput> calcularBaseCalculo(@RequestBody NfseBaseCalculoInput input) {
        return ResponseEntity.ok(nfseService.calcularBaseCalculo(input));
    }

    @Override
    @PostMapping(
        value = "indicador-operacao/validate",
        consumes = APPLICATION_JSON_VALUE,
        produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<NfseValidacaoIndicadorOperacaoOutput> validarIndicadorOperacao(@RequestBody NfseValidacaoIndicadorOperacaoInput input) {
        NfseValidacaoIndicadorOperacaoOutput output = nfseService.validarIndicadorOperacao(input);
        return ResponseEntity.ok(output);
    }

    @Override
    @GetMapping(
        value = "local-operacao",
        produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<NfseLocalOperacaoOutput> consultarLocalOperacao(
            @RequestParam String cIndOp,
            @RequestParam LocalDate dataOcorrenciaFatoGerador) {
        return ResponseEntity.ok(nfseService.consultarLocalOperacao(cIndOp, dataOcorrenciaFatoGerador));
    }
    
    @Override
    @GetMapping(value = "indicador-operacao", produces = APPLICATION_JSON_VALUE)
    public List<NfseIndicadorOperacaoOutput> consultarIndicadoresOperacao(
            @RequestParam LocalDate dataOcorrenciaFatoGerador,
            @RequestParam(required = false) String nbs) {
        return nfseService.consultarIndicadorOperacao(dataOcorrenciaFatoGerador, nbs);
    }
    
    @Override
    @GetMapping(
        value = "situacoes-classificacoes-tributarias",
        produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<NfseSituacaoClassificacaoOutput>> consultarSituacoesClassificacoesPorNbs(
            @RequestParam String nbs,
            @RequestParam LocalDate data) {
        
        List<NfseSituacaoClassificacaoOutput> resultado = 
            nfseService.consultarSituacoesClassificacoesPorNbs(nbs, data);
        
        return ResponseEntity.ok(resultado);
    }
}
