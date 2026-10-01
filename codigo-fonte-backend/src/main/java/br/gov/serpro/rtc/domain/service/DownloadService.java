package br.gov.serpro.rtc.domain.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import br.gov.serpro.rtc.config.download.DownloadPlatformProperties;
import br.gov.serpro.rtc.domain.service.exception.PlataformaDownloadNaoEncontradaException;
import lombok.RequiredArgsConstructor;

/**
 * Resolve a URL de download associada a uma plataforma, com base nas
 * plataformas configuradas em {@code application.download.platforms}.
 */
@Service
@RequiredArgsConstructor
public class DownloadService {

    private final DownloadPlatformProperties downloadPlatformProperties;

    public DownloadPlatformResolution resolveDownloadUrl(String platform) {
        if (platform == null || platform.isBlank()) {
            throw new PlataformaDownloadNaoEncontradaException(platform, plataformasDisponiveis());
        }

        String platformNormalizado = platform.trim().toLowerCase();

        return downloadPlatformProperties.getPlatforms().entrySet().stream()
                .filter(entry -> entry.getKey().equalsIgnoreCase(platformNormalizado))
                .findFirst()
                .map(entry -> new DownloadPlatformResolution(entry.getKey(), entry.getValue()))
                .orElseThrow(() -> new PlataformaDownloadNaoEncontradaException(platform, plataformasDisponiveis()));
    }

    private Iterable<String> plataformasDisponiveis() {
        Map<String, String> platforms = downloadPlatformProperties.getPlatforms();
        return platforms.keySet();
    }
}
