/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.api.util;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

/**
 * Utilitário HTTP que monta a URL base da requisição atual para compor links
 * absolutos usados pela API.
 */
public final class HttpUtils {

    private HttpUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static final String getBaseURL() {
        UriComponents uriComponents = ServletUriComponentsBuilder.fromCurrentRequestUri().build();
        String scheme = uriComponents.getScheme();
        String host = uriComponents.getHost();

        return String.format("%s://%s", scheme, host);
    }

}
