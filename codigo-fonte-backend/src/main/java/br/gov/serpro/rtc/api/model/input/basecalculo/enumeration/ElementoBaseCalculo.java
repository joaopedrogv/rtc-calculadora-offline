/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.api.model.input.basecalculo.enumeration;

import java.util.Optional;
import java.util.Set;

import lombok.Getter;

import static br.gov.serpro.rtc.api.model.input.basecalculo.enumeration.BaseCalculoOperacao.SOMA;
import static br.gov.serpro.rtc.api.model.input.basecalculo.enumeration.BaseCalculoOperacao.SUBTRAI;
import static br.gov.serpro.rtc.api.model.input.basecalculo.enumeration.BaseCalculoBase.RG;
import static br.gov.serpro.rtc.api.model.input.basecalculo.enumeration.BaseCalculoBase.IS;
import static br.gov.serpro.rtc.api.model.input.basecalculo.enumeration.BaseCalculoBase.SN;
import static br.gov.serpro.rtc.api.model.input.basecalculo.enumeration.BaseCalculoNatureza.BEM;
import static br.gov.serpro.rtc.api.model.input.basecalculo.enumeration.BaseCalculoNatureza.SERVICO;


/**
 * Enum que define todos os elementos de base de cálculo com suas regras de vigência,
 * natureza aplicável, bases de cálculo e rejeição por modelo.
 * 
 * Baseado no catálogo completo do endpoint de base unificada.
 */
@Getter
public enum ElementoBaseCalculo {

    V_PROD("vProd", SOMA, Set.of(RG, IS, SN), null, 2026, 2033, Set.of(55, 65)),
    V_FRETE("vFrete", SOMA, Set.of(RG, IS, SN), null, 2026, 2033, Set.of(55, 65)),
    V_SEG("vSeg", SOMA, Set.of(RG, IS, SN), null, 2026, 2033, Set.of(55, 65)),
    V_OUTRO("vOutro", SOMA, Set.of(RG, IS, SN), null, 2026, 2033, Set.of(55, 65)),
    V_II("vII", SOMA, Set.of(RG, IS), BEM, 2026, 2033, Set.of(55)),
    V_IS("vIS", SOMA, Set.of(RG, SN), null, 2027, 2033, Set.of(55, 65)),
    V_DESC("vDesc", SUBTRAI, Set.of(RG, IS, SN), null, 2026, 2033, Set.of(55, 65)),
    
    V_PIS("vPIS", SUBTRAI, Set.of(RG, IS), null, 2026, 2026, Set.of(55, 65)),
    V_COFINS("vCOFINS", SUBTRAI, Set.of(RG, IS), null, 2026, 2026, Set.of(55, 65)),
    V_ICMS("vICMS", SUBTRAI, Set.of(RG, IS), BEM, 2026, 2032, Set.of(55, 65)),
    V_ICMS_UF_DEST("vICMSUFDest", SUBTRAI, Set.of(RG, IS), BEM, 2026, 2032, Set.of(55)),
    V_FCP("vFCP", SUBTRAI, Set.of(RG, IS), BEM, 2026, 2032, Set.of(55, 65)),
    V_FCP_UF_DEST("vFCPUFDest", SUBTRAI, Set.of(RG, IS), BEM, 2026, 2032, Set.of(55)),
    V_ICMS_MONO("vICMSMono", SUBTRAI, Set.of(RG, IS), BEM, 2026, 2032, Set.of(55, 65)),
    V_ISSQN("vISSQN", SUBTRAI, Set.of(RG, IS), SERVICO, 2026, 2032, Set.of(55, 65));

    /**
     * Tag/nome do campo (ex: "vProd", "vFrete")
     */
    private final String tag;
    
    /**
     * Grupo do campo: SOMA ou SUBTRAI
     */
    private final BaseCalculoOperacao operacao;
    
    /**
     * Bases de cálculo às quais este campo se aplica (RG, IS, SN)
     */
    private final Set<BaseCalculoBase> bases;
    
    /**
     * Natureza à qual este campo se aplica (BEM, SERVICO ou null para ambos)
     */
    private final BaseCalculoNatureza natureza;
    
    /**
     * Ano inicio de vigência (inclusive)
     */
    private final int anoInicioVigencia;
    
    /**
     * Ano fim de vigência (inclusive)
     */
    private final int anoFimVigencia;
    
    /**
     * Lista de modelos de documento válidos para este campo
     */
    private final Set<Integer> tiposDocumentoValidos;

    ElementoBaseCalculo(String tag, BaseCalculoOperacao grupo, Set<BaseCalculoBase> bases, BaseCalculoNatureza apenas,
                       int anoDesde, int anoAte, Set<Integer> tiposValidos) {
        this.tag = tag;
        this.operacao = grupo;
        this.bases = bases;
        this.natureza = apenas;
        this.anoInicioVigencia = anoDesde;
        this.anoFimVigencia = anoAte;
        this.tiposDocumentoValidos = tiposValidos;
    }

    /**
      * Verifica se o tipo de documento é válido para este campo
      * @param tipoDocumento o tipo de documento (ex: 55, 65)
      * @return true se o tipo está na lista de tipos válidos
      */
    public boolean isValidoParaTipoDocumento(int tipoDocumento) {
        return tiposDocumentoValidos.contains(tipoDocumento);
    }

    /**
     * Verifica se este campo está ativo (exigido) para um contexto específico.
     * 
     * @param ano o ano do fato gerador (2026-2033)
     * @param natureza a natureza da operação ("bem" ou "servico")
     * @param tipoDocumento o tipo de documento (55 ou 65)
     * @return true se o campo está ativo/exigido, false caso contrário
     */
    public boolean campoCompoeBase(int ano, BaseCalculoNatureza naturezaParam, int tipoDocumento) {
        boolean anoValido = ano >= anoInicioVigencia && ano <= anoFimVigencia;
        boolean naturezaValida = (this.natureza == null) || (naturezaParam != null && this.natureza == naturezaParam);
        boolean modeloValido = tiposDocumentoValidos.contains(tipoDocumento);
        return anoValido && naturezaValida && modeloValido;
    }

    /**
     * Retorna o motivo pelo qual este campo não compõe as bases no contexto informado,
     * reutilizando as mesmas condições de {@link #campoCompoeBase(int, BaseCalculoNatureza, int)}.
     * Se mais de uma condição falhar, reporta a primeira na ordem: vigência, natureza, modelo.
     *
     * @param ano o ano do fato gerador
     * @param naturezaParam a natureza da operação
     * @param tipoDocumento o tipo de documento (55 ou 65)
     * @return o motivo da não composição, ou {@link Optional#empty()} se o campo compõe as bases
     */
    public Optional<String> motivoNaoComposicao(int ano, BaseCalculoNatureza naturezaParam, int tipoDocumento) {
        boolean anoValido = ano >= anoInicioVigencia && ano <= anoFimVigencia;
        if (!anoValido) {
            return Optional.of("Campo %s não compõe as bases para o ano %d (vigência %d–%d) e foi ignorado no cálculo"
                    .formatted(tag, ano, anoInicioVigencia, anoFimVigencia));
        }
        boolean naturezaValida = (this.natureza == null) || (naturezaParam != null && this.natureza == naturezaParam);
        if (!naturezaValida) {
            return Optional.of("Campo %s não compõe as bases para a natureza %s (aplicável apenas a %s) e foi ignorado no cálculo"
                    .formatted(tag, naturezaParam, this.natureza));
        }
        boolean modeloValido = tiposDocumentoValidos.contains(tipoDocumento);
        if (!modeloValido) {
            return Optional.of("Campo %s não compõe as bases para o documento modelo %d e foi ignorado no cálculo"
                    .formatted(tag, tipoDocumento));
        }
        return Optional.empty();
    }


    /**
     * Verifica se uma dada base de cálculo está presente nos campos aplicáveis
     * 
     * @param base a base a verificar (RG, IS ou SN)
     * @return true se a base aplica este campo
     */
    public boolean aplicaBase(BaseCalculoBase base) {
        return bases.contains(base);
    }

    /**
     * Obtém todos os campos do catálogo como Set para iteração
     */
    public static Set<ElementoBaseCalculo> todos() {
        return Set.of(values());
    }

}
