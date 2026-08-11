package br.gov.serpro.rtc.api.model.input.calculadora.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import br.gov.serpro.rtc.api.model.input.calculadora.enumeration.TipoEnteGovernamental;

/**
 * Conversor Spring que transforma códigos numéricos recebidos pela API no enum
 * {@link TipoEnteGovernamental}. Permite a conversão automática de parâmetros 
 * HTTP para o tipo de ente governamental antes do processamento da operação.
 */
@Component
public class TipoEnteGovernamentalConverter implements Converter<Integer, TipoEnteGovernamental> {
    @Override
    public TipoEnteGovernamental convert(@NonNull Integer codigo) {
        return TipoEnteGovernamental.fromCodigo(codigo);
    }
}