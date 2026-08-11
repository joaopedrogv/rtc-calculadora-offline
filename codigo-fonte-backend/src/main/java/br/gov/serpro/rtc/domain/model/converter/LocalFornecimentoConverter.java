package br.gov.serpro.rtc.domain.model.converter;
import br.gov.serpro.rtc.domain.model.enumeration.LocalFornecimento;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Conversor JPA que persiste {@link LocalFornecimento} como código inteiro no
 * banco de dados e reconstitui o enum na leitura da entidade.
 */
@Converter(autoApply = true)
public class LocalFornecimentoConverter implements AttributeConverter<LocalFornecimento, Integer> {

    @Override
    public Integer convertToDatabaseColumn(LocalFornecimento attribute) {
        return attribute != null ? attribute.getCodigo() : null;
    }

    @Override
    public LocalFornecimento convertToEntityAttribute(Integer dbData) {
        return dbData != null ? LocalFornecimento.fromCodigo(dbData) : null;
    }
}