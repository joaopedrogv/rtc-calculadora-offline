/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.model.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Entidade JPA que relaciona NBS, indicador de operação, lista de serviços e
 * classificação tributária para operações de serviços. Essa associação é usada
 * para determinar a tributação aplicável em NFS-e, inclusive em cenários de
 * prestação onerosa e aquisição no exterior.
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@Entity
@Table(name = "CLASSIF_NBS_INDOP_LC")
public class IndicadorOpNbsClassificacao {

    @EqualsAndHashCode.Include
    @Id
    @Column(name = "CNIL_ID")
    private Long id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "CNIL_LSLC_CD")
    private ListaServicosLc116 listaServicos;

    @NotNull
    @Column(name = "CNIL_NBS_CD")
    private String nbsCd;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "CNIL_IOIC_CD")
    private IndicadorOperacao indiceOperacao;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "CNIL_CLTR_ID")
    private ClassificacaoTributaria classificacaoTributaria;

    @NotNull
    @Column(name = "CNIL_IN_PS_ONEROSA")
    private boolean inPrestacaoOnerosa;

    @NotNull
    @Column(name = "CNIL_IN_ADQ_EXTERIOR")
    private boolean inAquisicaoExterior;

    @NotNull
    @Column(name = "CNIL_INICIO_VIGENCIA")
    private LocalDate inicioVigencia;

    @Column(name = "CNIL_FIM_VIGENCIA")
    private LocalDate fimVigencia;
}
