/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.model.enumeration;

import java.util.Objects;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enumera os entes federativos associados aos tributos do domínio.
 * Valores: {@code UNIAO}, {@code ESTADO} e {@code MUNICIPIO}.
 */
@Getter
@RequiredArgsConstructor
public enum EnteFederativoEnum {
    UNIAO("UNIÃO"), ESTADO("ESTADO"), MUNICIPIO("MUNICÍPIO");
    private final String nome;
    
    public static EnteFederativoEnum get(String nome) {
        return Stream.of(values()).filter(t -> Objects.equals(t.getNome(), nome)).findFirst()
                .orElseThrow(() -> new RuntimeException("Entidade federativa não mapeada"));
    }
}
