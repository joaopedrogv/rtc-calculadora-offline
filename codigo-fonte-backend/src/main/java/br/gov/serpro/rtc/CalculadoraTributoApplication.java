/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

import lombok.RequiredArgsConstructor;

/**
 * Classe principal que inicializa a aplicação Spring Boot da Calculadora de
 * Tributos.
 */
@RequiredArgsConstructor
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class CalculadoraTributoApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(CalculadoraTributoApplication.class, args);
    }
}
