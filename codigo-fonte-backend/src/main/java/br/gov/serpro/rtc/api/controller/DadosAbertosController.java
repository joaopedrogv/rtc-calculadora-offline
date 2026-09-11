/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.api.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.gov.serpro.rtc.api.model.output.dadosabertos.AliquotaDadosAbertosOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.ClassificacaoTributariaDadosAbertosOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.SituacaoClassificacaoAninhadoOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.FundamentacaoClassificacaoDadosAbertosOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.GrupoDfeDadosAbertosOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.GrupoAtorDadosAbertosOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.MunicipioDadosAbertosOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.NbsDadosAbertosOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.NbsListaDadosAbertosOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.NcmDadosAbertosOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.NomenclaturaDadosAbertosOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.RedutorCompraGovernamentalDadosAbertosOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.SituacaoTributariaDadosAbertosOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.TransferenciaCBSDadosAbertosOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.TransferenciaIBSDadosAbertosOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.UfDadosAbertosOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.ValidadeDfeClassificacaoTributariaDadosAbertosOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.VersaoOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.NbsAplicavelOutput;
import br.gov.serpro.rtc.api.model.output.dadosabertos.NcmAplicavelOutput;
import br.gov.serpro.rtc.api.openapi.controller.DadosAbertosControllerOpenApi;
import br.gov.serpro.rtc.domain.model.enumeration.TipoWarningDadosSimulados;
import br.gov.serpro.rtc.domain.model.enumeration.PapelAtorEnum;
import br.gov.serpro.rtc.domain.service.VersaoBaseDadosService;
import br.gov.serpro.rtc.domain.service.dadosabertos.DadosAbertosService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador REST de consultas de dados abertos da calculadora, como UFs,
 * municípios, classificações tributárias, alíquotas, versões e tabelas
 * auxiliares.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(
    value = "calculadora/dados-abertos",
    produces = APPLICATION_JSON_VALUE
)
public class DadosAbertosController implements DadosAbertosControllerOpenApi {

    private static final Duration CACHE_PUBLIC_MAX_AGE = Duration.ofHours(1);
    private static final String HEADER_WARNING_DADOS_SIMULADOS = "x-warning-dados-simulados";

    @Value("${info.app.version:unknown}")
    private String versaoAplicacao;
    private final Environment environment;
    private final VersaoBaseDadosService versaoBaseDadosService;
    private final DadosAbertosService dadosAbertosService;

    @Override
    @GetMapping("/ufs")
    public ResponseEntity<List<UfDadosAbertosOutput>> consultarUfs() {
        return ResponseEntity.ok()
                .cacheControl(cacheControlPublicoUmaHora())
                .body(dadosAbertosService.consultarUfs());
    }

    @Override
    @GetMapping("/ufs/municipios")
    public ResponseEntity<List<MunicipioDadosAbertosOutput>> consultarMunicipiosPorSiglaUf(
            @RequestParam String siglaUf) {
        return ResponseEntity.ok()
                .cacheControl(cacheControlPublicoUmaHora())
                .body(dadosAbertosService.consultarMunicipiosPorSiglaUf(siglaUf));
    }

    @Override
    @GetMapping("/situacoes-tributarias/cbs-ibs")
    public ResponseEntity<List<SituacaoClassificacaoAninhadoOutput>> consultarCstsComClassificacoesCbsIbs(
            @RequestParam(required = false) String siglaDfe, @RequestParam LocalDate data) {
        return ResponseEntity.ok()
                .cacheControl(cacheControlPublicoUmaHora())
                .body(dadosAbertosService.consultarCstsComClassificacoesCbsIbs(siglaDfe, data));
    }

	/**
	 * Consulta de classificações tributárias por ID de situação tributária.
	 * 
	 * @deprecated Recomendada a utilização dos endpoints específicos para CBS/IBS
	 *             {@link #consultarClassificacoesTributariasCbsIbs(LocalDate)} e
	 *             Imposto Seletivo
	 *             {@link #consultarClassificacoesTributariasImpostoSeletivo(LocalDate)}.
	 *             Este endpoint será descontinuado em breve.
	 */
    @Override
    @GetMapping("/classificacoes-tributarias/{idSituacaoTributaria}")
    @Deprecated(since = "2026-01-13", forRemoval = true)
    public ResponseEntity<List<ClassificacaoTributariaDadosAbertosOutput>> consultarClassificacoesTributariasPorIdSituacaoTributaria(
        @PathVariable Long idSituacaoTributaria, @RequestParam LocalDate data) {
        return ResponseEntity
                .ok(dadosAbertosService.consultarClassificacoesTributariasPorIdSituacaoTributaria(idSituacaoTributaria, data));
    }

    @Override
    @GetMapping("/classificacoes-tributarias/imposto-seletivo/{cst}")
    public ResponseEntity<List<ClassificacaoTributariaDadosAbertosOutput>> listarPorCstImpostoSeletivo(
        @PathVariable String cst,
        @RequestParam LocalDate data) {
        List<ClassificacaoTributariaDadosAbertosOutput> result = dadosAbertosService
            .consultarClassificacoesTributariasPorCstETributoTipo(cst, List.of("IS"), data);
        return ResponseEntity.ok(result);
    }

    @Override
    @GetMapping("/classificacoes-tributarias/cbs-ibs/{cst}")
    public ResponseEntity<List<ClassificacaoTributariaDadosAbertosOutput>> listarPorCstCbsIbs(
        @PathVariable String cst,
        @RequestParam LocalDate data) {
        List<ClassificacaoTributariaDadosAbertosOutput> result = dadosAbertosService
            .consultarClassificacoesTributariasPorCstETributoTipo(cst, List.of("CBS", "IBSUF", "IBSMun"), data);
        return ResponseEntity.ok(result);
    }

    @Override
    @GetMapping("/classificacoes-tributarias/cbs-ibs/class-trib/{cClassTrib}")
    public ResponseEntity<ClassificacaoTributariaDadosAbertosOutput> consultarClassificacaoTributariaCbsIbsPorCodigo(
        @PathVariable String cClassTrib,
        @RequestParam LocalDate data) {
        ClassificacaoTributariaDadosAbertosOutput result = dadosAbertosService
            .consultarClassificacaoTributariaCbsIbsPorCodigo(cClassTrib, data);
        return ResponseEntity.ok(result);
    }

    @Override
    @GetMapping("/classificacoes-tributarias/is/class-trib/{cClassTrib}")
    public ResponseEntity<ClassificacaoTributariaDadosAbertosOutput> consultarClassificacaoTributariaIsPorCodigo(
        @PathVariable String cClassTrib,
        @RequestParam LocalDate data) {
        ClassificacaoTributariaDadosAbertosOutput result = dadosAbertosService
            .consultarClassificacaoTributariaIsPorCodigo(cClassTrib, data);
        return ResponseEntity.ok(result);
    }

    @Override
    @GetMapping("/classificacoes-tributarias/cbs-ibs")
    public ResponseEntity<List<ClassificacaoTributariaDadosAbertosOutput>> consultarClassificacoesTributariasCbsIbs(
        @RequestParam LocalDate data) {
        return ResponseEntity
                .ok(dadosAbertosService.consultarClassificacoesTributariasCbsIbs(data));
    }

    @Override
    @GetMapping("/classificacoes-tributarias/cbs-ibs/por-atores")
    public ResponseEntity<List<ClassificacaoTributariaDadosAbertosOutput>> consultarClassificacoesTributariasCbsIbsPorAtores(
        @RequestParam LocalDate data,
        @RequestParam(required = false) Long fornecedor,
        @RequestParam(required = false) Long adquirente,
        @RequestParam(required = false) String siglaDfe) {
        return ResponseEntity.ok()
                .cacheControl(cacheControlPublicoUmaHora())
                .body(dadosAbertosService.consultarClassificacoesTributariasCbsIbsPorAtores(data, fornecedor, adquirente, siglaDfe));
    }

    @Override
    @GetMapping("/classificacoes-tributarias/imposto-seletivo")
    public ResponseEntity<List<ClassificacaoTributariaDadosAbertosOutput>> consultarClassificacoesTributariasImpostoSeletivo(
        @RequestParam LocalDate data) {
        List<ClassificacaoTributariaDadosAbertosOutput> resultado = dadosAbertosService.consultarClassificacoesTributariasImpostoSeletivo(data);
        return okComWarningDadosSimulados(resultado, data);
    }
    
    @Override
    @GetMapping("/classificacoes-tributarias/nbs")
    public ResponseEntity<List<String>> listarClassificacoesTributariasPorNbs(
        @RequestParam String nbs,
        @RequestParam LocalDate data) {
        return ResponseEntity.ok(dadosAbertosService.listarClassificacaoAplicavelPorNbs(nbs, data));
    }

    @Override
    @GetMapping("/situacoes-tributarias/imposto-seletivo")
    public ResponseEntity<List<SituacaoTributariaDadosAbertosOutput>> consultarSituacoesTributariasImpostoSeletivo(
            @RequestParam LocalDate data) {
        return ResponseEntity.ok(dadosAbertosService.consultarSituacoesTributarias(1L, data));
    }

    @Override
    @GetMapping("/ncm")
    public ResponseEntity<NcmDadosAbertosOutput> consultarNcm(
        @RequestParam String ncm, @RequestParam LocalDate data) {
        NcmDadosAbertosOutput resultado = dadosAbertosService.consultarNcm(ncm, data);
        return resultado.isTributadoPeloImpostoSeletivo() 
            ? okComWarningDadosSimulados(resultado, data) 
            : ResponseEntity.ok(resultado);
    }

    @Override
    @GetMapping("/nbs")
    public ResponseEntity<NbsDadosAbertosOutput> consultarNbs(
        @RequestParam String nbs, @RequestParam LocalDate data) {
        NbsDadosAbertosOutput resultado = dadosAbertosService.consultarNbs(nbs, data);
        return resultado.isTributadoPeloImpostoSeletivo() 
            ? okComWarningDadosSimulados(resultado, data) 
            : ResponseEntity.ok(resultado);
    }

    @Override
    @GetMapping("/nbs/lista")
    public ResponseEntity<List<NbsListaDadosAbertosOutput>> listarNbs(
        @RequestParam LocalDate data) {
        return ResponseEntity.ok(dadosAbertosService.listarNbs(data));
    }

    @Override
    @GetMapping("/nbs-aplicaveis")
    public ResponseEntity<List<NbsListaDadosAbertosOutput>> listarNbsAplicaveisPorClassificacao(
        @RequestParam String cClassTrib, @RequestParam LocalDate data) {
        return ResponseEntity.ok(dadosAbertosService.listarNbsAplicaveisPorClassificacao(cClassTrib, data));
    }

    @Override
    @GetMapping("/fundamentacoes-legais")
    public ResponseEntity<List<FundamentacaoClassificacaoDadosAbertosOutput>> consultarFundamentacoesLegais(
        @RequestParam LocalDate data) {
        return ResponseEntity.ok(dadosAbertosService.consultarFundamentacoesLegais(data));
    }

    @Override
    @GetMapping("/aliquota-uniao")
    public ResponseEntity<AliquotaDadosAbertosOutput> consultarAliquotaUniao(
        @RequestParam LocalDate data) {
        AliquotaDadosAbertosOutput resultado = dadosAbertosService.consultarAliquota(2L, null, null, data);
        return okComWarningDadosSimulados(resultado, data);
    }

    @Override
    @GetMapping("/aliquota-uf")
    public ResponseEntity<AliquotaDadosAbertosOutput> consultarAliquotaUf(
        @RequestParam Long codigoUf, @RequestParam LocalDate data) {
        AliquotaDadosAbertosOutput resultado = dadosAbertosService.consultarAliquota(3L, codigoUf, null, data);
        return okComWarningDadosSimulados(resultado, data);
    }

    @Override
    @GetMapping("/aliquota-municipio")
    public ResponseEntity<AliquotaDadosAbertosOutput> consultarAliquotaMunicipio(
        @RequestParam Long codigoMunicipio, @RequestParam LocalDate data) {
        AliquotaDadosAbertosOutput resultado = dadosAbertosService.consultarAliquota(4L, null, codigoMunicipio, data);
        return okComWarningDadosSimulados(resultado, data);
    }

    @Override
    @GetMapping("/classificacoes-tributarias/cbs-ibs/{siglaDfe}/{cClassTrib}")
    public ResponseEntity<ValidadeDfeClassificacaoTributariaDadosAbertosOutput> consultarValidadeDfeClassificacaoTributaria(
        @PathVariable String siglaDfe, @PathVariable String cClassTrib, @RequestParam LocalDate data) {
        return ResponseEntity.ok(dadosAbertosService.consultarValidadeDfeClassificacaoTributaria(siglaDfe, cClassTrib, data));
    }

    @Override
    @GetMapping("/situacoes-tributarias/is")
    public ResponseEntity<List<SituacaoClassificacaoAninhadoOutput>> consultarCstsComClassificacoesIs(
            @RequestParam(required = false) String siglaDfe, @RequestParam LocalDate data) {
        return ResponseEntity.ok()
                .cacheControl(cacheControlPublicoUmaHora())
                .body(dadosAbertosService.consultarCstsComClassificacoesIs(siglaDfe, data));
    }
    
    @Override
    @GetMapping("/dfe/grupos")
    public ResponseEntity<List<GrupoDfeDadosAbertosOutput>> consultarTiposDfeAgrupados(
            @RequestParam(required = false) LocalDate data) {
        return ResponseEntity.ok()
                .cacheControl(cacheControlPublicoUmaHora())
                .body(dadosAbertosService.consultarTiposDfeAgrupados(data));
    }

    @Override
    @GetMapping("/versao")
    public ResponseEntity<VersaoOutput> consultarVersao() {
        String[] activeProfiles = environment.getActiveProfiles();
        String ambiente = activeProfiles.length > 0 ? String.join(",", activeProfiles) : "default";
        
        if ("local".equals(ambiente)) {
            ambiente = "online";
        }
        
        VersaoOutput versaoOutput = VersaoOutput.builder()
                .versaoApp(versaoAplicacao)
                .versaoDb(versaoBaseDadosService.getUltimaVersao().getNumeroVersao())
                .descricaoVersaoDb(versaoBaseDadosService.getUltimaVersao().getDescricao())
                .dataVersaoDb(versaoBaseDadosService.getUltimaVersao().getData().toString())
                .ambiente(ambiente)
                .build();
        return ResponseEntity.ok(versaoOutput);
    }

    @Override
    @GetMapping("/redutores-compra-governamental")
    public ResponseEntity<List<RedutorCompraGovernamentalDadosAbertosOutput>> consultarRedutoresCompraGovernamental() {
        return ResponseEntity.ok()
                .cacheControl(cacheControlPublicoUmaHora())
                .body(dadosAbertosService.consultarRedutoresCompraGovernamental());
    }

    @Override
    @GetMapping("/transferencias-cbs")
    public ResponseEntity<List<TransferenciaCBSDadosAbertosOutput>> consultarTransferenciasCBS() {
        return ResponseEntity.ok()
                .cacheControl(cacheControlPublicoUmaHora())
                .body(dadosAbertosService.consultarTransferenciasCBS());
    }

    @Override
    @GetMapping("/transferencias-ibs")
    public ResponseEntity<List<TransferenciaIBSDadosAbertosOutput>> consultarTransferenciasIBS() {
        return ResponseEntity.ok()
                .cacheControl(cacheControlPublicoUmaHora())
                .body(dadosAbertosService.consultarTransferenciasIBS());
    }
    
    @Override
    @GetMapping("/nomenclatura/{siglaDfe}/{cClassTrib}")
    public ResponseEntity<NomenclaturaDadosAbertosOutput> consultarNomenclatura(
        @PathVariable String siglaDfe, @PathVariable String cClassTrib, @RequestParam LocalDate data) {
        return ResponseEntity.ok()
                .cacheControl(cacheControlPublicoUmaHora())
                .body(dadosAbertosService.consultarNomenclatura(siglaDfe, cClassTrib, data));
    }

    @Override
    @GetMapping("/ator/grupos")
    public ResponseEntity<List<GrupoAtorDadosAbertosOutput>> consultarAtoresAgrupados(
            @RequestParam LocalDate data,
            @RequestParam(required = false) PapelAtorEnum papel) {
        return ResponseEntity.ok()
                .cacheControl(cacheControlPublicoUmaHora())
                .body(dadosAbertosService.consultarAtoresAgrupados(data, papel));
    }

    /**
	 * Configura o cache para ser público e expirar após uma hora.
	 * 
	 * @return CacheControl configurado para cache público de uma hora.
	 */
    private static CacheControl cacheControlPublicoUmaHora() {
        return CacheControl.maxAge(CACHE_PUBLIC_MAX_AGE).cachePublic();
    }

    /**
	 * Adiciona um header de warning indicando que os dados são simulados, caso haja um warning configurado para a data da consulta.
	 *
	 * @param body O corpo da resposta a ser retornada.
	 * @param data A data para a qual verificar se há um warning de dados simulados.
	 * @return ResponseEntity com o corpo e, se aplicável, o header de warning.
	 */
    private <T> ResponseEntity<T> okComWarningDadosSimulados(T body, LocalDate data) {
        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.ok();
        TipoWarningDadosSimulados warning = dadosAbertosService.getWarningDadosSimuladosPorData(data);
        if (warning != null) {
            responseBuilder.header(HEADER_WARNING_DADOS_SIMULADOS, String.valueOf(warning.getValor()));
        }
        return responseBuilder.body(body);
    }

    @Override
    @GetMapping("/classificacoes-tributarias/nbs-aplicavel")
    public ResponseEntity<NbsAplicavelOutput> validarNbsAplicavel(
            @RequestParam String cClassTrib,
            @RequestParam String nbs,
            @RequestParam LocalDate dataOcorrenciaFatoGerador) {
        return ResponseEntity.ok()
                .header("Cache-Control", "public, max-age=3600")
                .body(dadosAbertosService.validarNbsAplicavel(cClassTrib, nbs, dataOcorrenciaFatoGerador));
    }

    @Override
    @GetMapping("/classificacoes-tributarias/ncm-aplicavel")
    public ResponseEntity<NcmAplicavelOutput> validarNcmAplicavel(
            @RequestParam String cClassTrib,
            @RequestParam String ncm,
            @RequestParam LocalDate dataOcorrenciaFatoGerador) {
        return ResponseEntity.ok()
                .header("Cache-Control", "public, max-age=3600")
                .body(dadosAbertosService.validarNcmAplicavel(cClassTrib, ncm, dataOcorrenciaFatoGerador));
    }

}
