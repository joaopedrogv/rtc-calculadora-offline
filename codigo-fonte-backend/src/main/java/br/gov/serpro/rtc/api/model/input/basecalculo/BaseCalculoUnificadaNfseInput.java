package br.gov.serpro.rtc.api.model.input.basecalculo;

import java.math.BigDecimal;
import java.util.List;

import br.gov.serpro.rtc.api.model.SerializationVisibility;
import br.gov.serpro.rtc.api.model.input.basecalculo.enumeration.OpcaoSimplesNacional;
import br.gov.serpro.rtc.api.model.input.basecalculo.enumeration.RegimeApuracaoIbsCbsSN;
import br.gov.serpro.rtc.api.model.input.basecalculo.enumeration.SituacaoOperacaoNfse;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Input DTO para cálculo da base de cálculo unificada NFS-e (IBS/CBS + Simples Nacional).
 * Contém campos obrigatórios para identificação do fato gerador, opção pelo Simples Nacional,
 * situação da operação, valores monetários opcionais e documentos de ajuste.
 */
@Getter
@Setter
@NoArgsConstructor
public class BaseCalculoUnificadaNfseInput implements SerializationVisibility {

    @NotNull
    @Schema(description = "Ano do fato gerador", example = "2026")
    private Integer anoFatoGerador;

    @NotNull
    @Schema(
        description = "Opção pelo Simples Nacional: 1=Não Optante, 2=MEI, 3=Optante ME/EPP, 4=Optante Pendente",
        allowableValues = {"1", "2", "3", "4"},
        example = "1"
    )
    private OpcaoSimplesNacional opSimpNac;

    @NotNull
    @Schema(description = "Situação da operação NFS-e", example = "comum")
    private SituacaoOperacaoNfse situacao;

    @Schema(description = "Valor do serviço (null → zero)", example = "1000.00")
    private BigDecimal vServ;

    @Schema(description = "Valor do desconto incondicional (null → zero)", example = "50.00")
    private BigDecimal vDescIncond;

    @Schema(description = "Valor do ISSQN destacado (null → zero)", example = "30.00")
    private BigDecimal vISSQN;

    @Schema(description = "Valor do PIS (null → zero)", example = "9.25")
    private BigDecimal vPIS;

    @Schema(description = "Valor da COFINS (null → zero)", example = "46.25")
    private BigDecimal vCOFINS;

    @Schema(
        description = "Regime de apuração IBS/CBS no SN: 1=IBS+CBS pelo SN, 2=CBS pelo SN + IBS regular, 3=Ambos regulares. Obrigatório para optantes.",
        allowableValues = {"1", "2", "3"},
        example = "1",
        nullable = true
    )
    private RegimeApuracaoIbsCbsSN regApIBSCBSSN;

    @Valid
    @Schema(description = "Documentos de ajuste à base de cálculo (null → lista vazia)")
    private List<DocumentoAjusteNfseInput> documentos;

    @Valid
    @Schema(description = "Dados de locação de imóveis. Obrigatório quando situacao=locacao9903", nullable = true)
    private LocacaoNfseInput locacao;
}
