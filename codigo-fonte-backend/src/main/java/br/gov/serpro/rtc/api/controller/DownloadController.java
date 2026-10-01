package br.gov.serpro.rtc.api.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.gov.serpro.rtc.api.model.output.DownloadUrlOutput;
import br.gov.serpro.rtc.domain.service.DownloadPlatformResolution;
import br.gov.serpro.rtc.domain.service.DownloadService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST oculto da documentação OpenAPI que retorna a URL de download
 * da aplicação offline e registra métricas por plataforma. As plataformas
 * aceitas são definidas dinamicamente via configuração (ver
 * {@code application.download.platforms}).
 */
@RestController
@RequestMapping(
    value = "calculadora/download",
    produces = APPLICATION_JSON_VALUE
)
@RequiredArgsConstructor
@Hidden
public class DownloadController {

    private final DownloadService downloadService;
    private final MeterRegistry meterRegistry;

    @GetMapping("/url")
    public ResponseEntity<DownloadUrlOutput> getDownloadUrl(
            @RequestParam(value = "platform") String platform) {

        DownloadPlatformResolution resolution = downloadService.resolveDownloadUrl(platform);
        String downloadUrl = resolution.downloadUrl();

        if (downloadUrl == null || downloadUrl.isBlank()) {
            return ResponseEntity.noContent().build();
        }

        // Incrementa o contador com tag da plataforma resolvida (evita cardinalidade não controlada)
        Counter.builder("api_calc_downloads_total")
                .description("Total number of download URL requests")
                .tag("endpoint", "download-url")
                .tag("platform", resolution.platform())
                .register(meterRegistry)
                .increment();

        return ResponseEntity.ok(new DownloadUrlOutput(downloadUrl));
    }
}
