package br.gov.serpro.rtc.domain.service;

/**
 * Representa o resultado da resolução de uma plataforma de download,
 * associando o nome canônico da plataforma (conforme configurado) à URL
 * correspondente.
 */
public record DownloadPlatformResolution(String platform, String downloadUrl) {
}
