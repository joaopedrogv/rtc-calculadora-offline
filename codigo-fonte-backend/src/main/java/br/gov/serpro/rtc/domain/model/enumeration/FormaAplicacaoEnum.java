/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.model.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enumera as formas de aplicação de uma alíquota padrão sobre a alíquota de
 * referência.
 *
 * Valores: {@code SUBSTITUICAO}, {@code ACRESCIMO} e {@code DECRESCIMO}.
 */
@Getter
@RequiredArgsConstructor
public enum FormaAplicacaoEnum {
    SUBSTITUICAO("Substitui a alíquota de referência pelo percentual informado"),
    ACRESCIMO("Acrescenta os pontos percentuais informados à alíquota de referência"),
    DECRESCIMO("Diminui os pontos percentuais informados à alíquota de referência");

    private final String descricao;
}
