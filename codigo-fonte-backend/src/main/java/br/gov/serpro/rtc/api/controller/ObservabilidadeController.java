package br.gov.serpro.rtc.api.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.gov.serpro.rtc.api.exceptionhandler.dto.ErroDescricao;
import br.gov.serpro.rtc.api.model.input.OperacaoInput;
import br.gov.serpro.rtc.api.model.roc.ObservabilidadeROCDomain;
import br.gov.serpro.rtc.api.openapi.controller.ObservabilidadeControllerOpenApi;
import br.gov.serpro.rtc.api.util.HttpUtils;
import br.gov.serpro.rtc.domain.model.enumeration.TipoWarningDadosSimulados;
import br.gov.serpro.rtc.domain.service.AvisoDadosSimuladosService;
import br.gov.serpro.rtc.domain.service.ErroService;
import br.gov.serpro.rtc.domain.service.ObservabilidadeService;
import br.gov.serpro.rtc.domain.service.VersaoAplicacaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador REST de observabilidade que processa operações com acumulação de
 * erros por item e consulta descrições catalogadas de erros.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("calculadora/observabilidade")
public class ObservabilidadeController implements ObservabilidadeControllerOpenApi {

    private final ObservabilidadeService observabilidadeService;
    private final VersaoAplicacaoService versaoAplicacaoService;
    private final AvisoDadosSimuladosService avisoDadosSimuladosService;
    private final ErroService erroService;

    @Override
    @PostMapping(value = "regime-geral", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ObservabilidadeROCDomain> calcularTributosObservabilidade(
            @RequestBody OperacaoInput operacao) {
        String baseURL = HttpUtils.getBaseURL();

        log.debug("Observabilidade ROC ID {}", operacao.getId());
        ObservabilidadeROCDomain resultado = observabilidadeService.processarOperacao(operacao, baseURL);

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.ok()
                .headers(versaoAplicacaoService.getHeaders());

        TipoWarningDadosSimulados warningDadosSimulados = avisoDadosSimuladosService.getWarningDadosSimulados(operacao);
        if (warningDadosSimulados != null) {
            responseBuilder.header("x-warning-dados-simulados", String.valueOf(warningDadosSimulados.getValor()));
        }

        return responseBuilder.body(resultado);
    }

    @Override
    @GetMapping(value = "erros", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ErroDescricao> buscarErro(@RequestParam String codigo) {
        return erroService.buscarPorCodigo(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
