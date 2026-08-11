/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.core.util;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import lombok.NonNull;

/**
 * Classe utilitária para formatação de datas usadas pela aplicação. Centraliza
 * a conversão de {@link java.time.LocalDate} para o formato brasileiro e a
 * serialização padrão de {@link java.time.OffsetDateTime}.
 */
public final class DateTimeUtils {
    private static final DateTimeFormatter PT_BR_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private DateTimeUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static final String toString(@NonNull LocalDate data) {
        return PT_BR_FORMATTER.format(data);
    }

    public static final String toString(@NonNull OffsetDateTime data) {
        return data.toString();
    }
}
