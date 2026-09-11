/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.api.model.output.basecalculo;

/**
 * Erro de negócio embutido na resposta HTTP 200.
 * Contém codigo (identificador estável) e mensagem (legível por humanos).
 */
public record ErroBaseCalculoNfse(String codigo, String mensagem) {}
