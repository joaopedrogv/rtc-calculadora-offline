/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.model.enumeration;

import java.util.Objects;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enumera os tipos de alíquota aceitos nas classificações tributárias.
 *
 * Valores: {@code PROPRIA}, {@code REFERENCIA}, {@code SETOR_FIXA} e {@code
 * SEM_DESTAQUE_TRIBUTO}.
 */
@Getter
@RequiredArgsConstructor
public enum TipoAliquotaEnum {
    PROPRIA("Própria"), 
    REFERENCIA("Referência"), 
    SETOR_FIXA("Por setor e fixa"),
    SEM_DESTAQUE_TRIBUTO("Sem destaque de tributo");

    private final String nome;

    public static TipoAliquotaEnum get(String nome) {
        return Stream.of(values()).filter(t -> Objects.equals(nome, t.getNome())).findFirst()
                .orElseThrow(() -> new RuntimeException("Tipo de aliquota não mapeado"));
    }
}
