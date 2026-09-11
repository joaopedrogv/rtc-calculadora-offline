/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.model.enumeration;

/**
 * Modo de validação da nomenclatura (NCM/NBS) para documentos fiscais fora do
 * trio NF-e/NFC-e/NFS-e, nos quais a nomenclatura resolvida é {@code SEM} e o
 * preenchimento de NCM/NBS não é exigido.
 *
 * Dentro do trio NF-e/NFC-e/NFS-e a regra é sempre rígida, independentemente
 * do modo: nomenclatura incompatível com o documento gera erro.
 *
 * - {@link #PERMISSIVO}: NCM/NBS informado em documento fora do trio não
 *   bloqueia; se a classificação possui anexo, o valor é criticado contra o
 *   anexo; sem anexo, é aceito em silêncio.
 * - {@link #RIGIDO}: NCM/NBS informado em documento fora do trio gera erro
 *   (proibição).
 */
public enum ModoValidacaoNomenclatura {
    PERMISSIVO,
    RIGIDO
}
