/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.api.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.gov.serpro.rtc.api.model.input.basecalculo.BaseCalculoCibsInput;
import br.gov.serpro.rtc.api.model.input.basecalculo.BaseCalculoISMercadoriasInput;
import br.gov.serpro.rtc.api.model.input.basecalculo.BaseCalculoUnificadaInput;
import br.gov.serpro.rtc.api.model.input.basecalculo.BaseCalculoUnificadaNfseInput;
import br.gov.serpro.rtc.api.model.output.basecalculo.BaseCalculoCibsModel;
import br.gov.serpro.rtc.api.model.output.basecalculo.BaseCalculoISMercadoriasModel;
import br.gov.serpro.rtc.api.model.output.basecalculo.BaseCalculoUnificadaOutput;
import br.gov.serpro.rtc.api.model.output.basecalculo.BaseCalculoUnificadaNfseOutput;
import br.gov.serpro.rtc.domain.service.basecalculo.BaseCalculoService;
import br.gov.serpro.rtc.domain.service.basecalculo.BaseCalculoUnificadaService;
import br.gov.serpro.rtc.domain.service.basecalculo.BaseCalculoUnificadaNfseService;
import br.gov.serpro.rtc.api.openapi.controller.BaseCalculoControllerOpenApi;
import jakarta.validation.Valid;

/**
 * Controlador REST que expõe os cálculos de base de cálculo de CBS/IBS e
 * Imposto Seletivo.
 */
@RestController
@RequestMapping("calculadora/base-calculo")
public class BaseCalculoController implements BaseCalculoControllerOpenApi {

    private final BaseCalculoService baseCalculoService;
    private final BaseCalculoUnificadaService baseCalculoUnificadaService;
    private final BaseCalculoUnificadaNfseService baseCalculoUnificadaNfseService;
    
    public BaseCalculoController(BaseCalculoService baseCalculoService,
                                 BaseCalculoUnificadaService baseCalculoUnificadaService,
                                 BaseCalculoUnificadaNfseService baseCalculoUnificadaNfseService) {
        this.baseCalculoService = baseCalculoService;
        this.baseCalculoUnificadaService = baseCalculoUnificadaService;
        this.baseCalculoUnificadaNfseService = baseCalculoUnificadaNfseService;
    }
    
    @Override
    @PostMapping(
        value = "base-calculo-unificada",
        consumes = APPLICATION_JSON_VALUE,
        produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<BaseCalculoUnificadaOutput> calcularBaseUnificada(@RequestBody @Valid BaseCalculoUnificadaInput input) {
        return ResponseEntity.ok(baseCalculoUnificadaService.calcular(input));
    }

    @Override
    @PostMapping(
        value = "is-mercadorias",
        consumes = APPLICATION_JSON_VALUE,
        produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<BaseCalculoISMercadoriasModel> calcularISMercadorias(
            @RequestBody @Valid BaseCalculoISMercadoriasInput input) {
        return ResponseEntity.ok(baseCalculoService.calcularISMercadorias(input));
    }

    @Override
    @PostMapping(
        value = "cbs-ibs-mercadorias",
        consumes = APPLICATION_JSON_VALUE,
        produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<BaseCalculoCibsModel> calcularCibs(@RequestBody @Valid BaseCalculoCibsInput input) {
        return ResponseEntity.ok(baseCalculoService.calcularCibs(input));
    }

    @Override
    @PostMapping(
        value = "base-calculo-unificada-nfse",
        consumes = APPLICATION_JSON_VALUE,
        produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<BaseCalculoUnificadaNfseOutput> calcularBaseUnificadaNfse(
            @RequestBody @Valid BaseCalculoUnificadaNfseInput input) {
        return ResponseEntity.ok(baseCalculoUnificadaNfseService.calcular(input));
    }

}
