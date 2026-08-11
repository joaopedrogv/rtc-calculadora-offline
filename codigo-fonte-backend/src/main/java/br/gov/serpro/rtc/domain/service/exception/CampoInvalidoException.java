/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service.exception;

/**
 * Exceção lançada quando um campo da requisição contém valor inválido ou
 * incompatível com as regras de negócio da calculadora.
 */
public class CampoInvalidoException extends NegocioException {

    private static final long serialVersionUID = 2582687430572466747L;

    public CampoInvalidoException(String msg) {
        super(msg);
    }

}