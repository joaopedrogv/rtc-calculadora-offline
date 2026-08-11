/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service.token;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Serviço responsável por cachear e reutilizar instâncias de {@code TextToken}
 * durante a tokenização de templates. Reduz recriação de objetos e melhora o
 * desempenho na montagem de memórias de cálculo e outros textos parametrizados.
 */
@Service
public class TokenCacheService {
    
    @Cacheable("TokenCacheService.get")
    public TextToken get(String text, String key) {
        return new TextToken(text, key);
    }
}
