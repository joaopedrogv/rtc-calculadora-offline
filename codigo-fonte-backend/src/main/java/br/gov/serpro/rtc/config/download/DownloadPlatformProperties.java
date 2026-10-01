package br.gov.serpro.rtc.config.download;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Mapeia, a partir do prefixo {@code application.download}, as plataformas de
 * download disponíveis e suas respectivas URLs, permitindo que novas
 * plataformas sejam adicionadas via configuração sem alteração de código.
 */
@Slf4j
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "application.download")
public class DownloadPlatformProperties {

    private Map<String, String> platforms = new LinkedHashMap<>();

    @PostConstruct
    void validar() {
        if (platforms.isEmpty()) {
            log.warn("Nenhuma plataforma de download configurada em 'application.download.platforms'");
            return;
        }
        platforms.forEach((nome, url) -> {
            if (nome == null || nome.isBlank()) {
                throw new IllegalStateException("Plataforma de download com nome nulo ou vazio na configuração 'application.download.platforms'");
            }
            if (url == null || url.isBlank()) {
                log.info("Plataforma de download '{}' sem URL configurada", nome);
            }
        });
        log.info("Plataformas de download configuradas: {}", platforms.keySet());
    }
}
