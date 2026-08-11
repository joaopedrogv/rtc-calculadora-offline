package br.gov.serpro.rtc.api.model.input.calculadora.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import br.gov.serpro.rtc.api.model.input.calculadora.enumeration.TipoOperacaoGovernamental;

/**
 * Conversor Spring que transforma códigos numéricos recebidos pela API no enum
 * {@link TipoOperacaoGovernamental}. Permite a conversão automática do tipo de 
 * operação governamental informado nos parâmetros da requisição.
 */
@Component
public class TipoOperacaoGovernamentalConverter implements Converter<Integer, TipoOperacaoGovernamental> {
    @Override
    public TipoOperacaoGovernamental convert(@NonNull Integer codigo) {
        return TipoOperacaoGovernamental.fromCodigo(codigo);
    }
}