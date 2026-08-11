package br.gov.serpro.rtc.config.retry;

import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

/**
 * Habilita o mecanismo de retentativas automáticas em métodos anotados com
 * {@code @Retryable}. Dá resiliência a operações que podem falhar
 * temporariamente durante o processamento da aplicação.
 */
@Configuration
@EnableRetry
public class RetryConfig {
}