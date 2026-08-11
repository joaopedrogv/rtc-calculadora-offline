/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service.token;

/**
 * Record que representa um trecho literal ou um placeholder identificado
 * durante a tokenização de templates.
 */
public final record TextToken (String text, String key){
}