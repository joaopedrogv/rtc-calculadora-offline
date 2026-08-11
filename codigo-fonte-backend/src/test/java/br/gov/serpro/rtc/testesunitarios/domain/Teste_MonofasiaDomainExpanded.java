package br.gov.serpro.rtc.testesunitarios.domain;

import static java.math.BigDecimal.ZERO;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import br.gov.serpro.rtc.api.model.output.CbsIbsOutput;
import br.gov.serpro.rtc.api.model.output.GrupoDiferimentoMonofasiaOutput;
import br.gov.serpro.rtc.api.model.output.GrupoEtapaMonofasiaOutput;
import br.gov.serpro.rtc.api.model.output.GrupoMonofasiaOutput;
import br.gov.serpro.rtc.api.model.roc.MonofasiaDiferimentoDomain;
import br.gov.serpro.rtc.api.model.roc.MonofasiaPadraoDomain;
import br.gov.serpro.rtc.api.model.roc.MonofasiaRetencaoDomain;
import br.gov.serpro.rtc.api.model.roc.MonofasiaRetidoAnteriormenteDomain;

@ExtendWith(MockitoExtension.class)
class Teste_MonofasiaDomainExpanded {

    @Test
    void teste_builderMonofasia_inicializaComZeros() {
        MonofasiaPadraoDomain padrao = MonofasiaPadraoDomain.builder().build();
        MonofasiaRetencaoDomain retencao = MonofasiaRetencaoDomain.builder().build();
        MonofasiaRetidoAnteriormenteDomain retido = MonofasiaRetidoAnteriormenteDomain.builder().build();
        MonofasiaDiferimentoDomain diferimento = MonofasiaDiferimentoDomain.builder().build();

        assertThat(padrao.getQBCMono()).isEqualByComparingTo(ZERO);
        assertThat(padrao.getAdRemIBS()).isEqualByComparingTo(ZERO);
        assertThat(padrao.getAdRemCBS()).isEqualByComparingTo(ZERO);
        assertThat(padrao.getVIBSMono()).isEqualByComparingTo(ZERO);
        assertThat(padrao.getVCBSMono()).isEqualByComparingTo(ZERO);

        assertThat(retencao.getQBCMonoReten()).isEqualByComparingTo(ZERO);
        assertThat(retencao.getAdRemIBSReten()).isEqualByComparingTo(ZERO);
        assertThat(retencao.getAdRemCBSReten()).isEqualByComparingTo(ZERO);
        assertThat(retencao.getVIBSMonoReten()).isEqualByComparingTo(ZERO);
        assertThat(retencao.getVCBSMonoReten()).isEqualByComparingTo(ZERO);

        assertThat(retido.getQBCMonoRet()).isEqualByComparingTo(ZERO);
        assertThat(retido.getAdRemIBSRet()).isEqualByComparingTo(ZERO);
        assertThat(retido.getAdRemCBSRet()).isEqualByComparingTo(ZERO);
        assertThat(retido.getVIBSMonoRet()).isEqualByComparingTo(ZERO);
        assertThat(retido.getVCBSMonoRet()).isEqualByComparingTo(ZERO);

        assertThat(diferimento.getPDifIBS()).isEqualByComparingTo(ZERO);
        assertThat(diferimento.getVIBSMonoDif()).isEqualByComparingTo(ZERO);
        assertThat(diferimento.getPDifCBS()).isEqualByComparingTo(ZERO);
        assertThat(diferimento.getVCBSMonoDif()).isEqualByComparingTo(ZERO);
    }

    @Test
    void teste_criacaoMonofasicaComNull_retornaNull() {
        assertThat(MonofasiaPadraoDomain.create(null, null, null)).isNull();
        assertThat(MonofasiaRetencaoDomain.create(null, null, null)).isNull();
        assertThat(MonofasiaRetidoAnteriormenteDomain.create(null, null, null)).isNull();
        assertThat(MonofasiaDiferimentoDomain.create(null, null, null)).isNull();
    }

    @Test
    void teste_monofasiaPadraoComCBSIBS_retornaCBSIBS() {
        MonofasiaPadraoDomain resultado = MonofasiaPadraoDomain.create(
                criarCbsComTributoMonofasico("10.0000", "1.10", "11.00"),
                criarIbsComTributoMonofasico("20.0000", "2.20", "22.00"),
                criarIbsComTributoMonofasico("99.0000", "9.99", "99.00"));

        assertThat(resultado.getQBCMono()).isEqualByComparingTo("20.0000");
        assertThat(resultado.getAdRemIBS()).isEqualByComparingTo("2.20");
        assertThat(resultado.getVIBSMono()).isEqualByComparingTo("22.00");
        assertThat(resultado.getAdRemCBS()).isEqualByComparingTo("1.10");
        assertThat(resultado.getVCBSMono()).isEqualByComparingTo("11.00");
    }

    @Test
    void teste_monofasiaPadraoCriadaComCBSNull_retornaCBSZero() {
        MonofasiaPadraoDomain resultado = MonofasiaPadraoDomain.create(
                null,
                criarIbsComTributoMonofasico("30.0000", "3.30", "33.00"),
                criarIbsComTributoMonofasico("88.0000", "8.80", "88.00"));

        assertThat(resultado.getQBCMono()).isEqualByComparingTo("30.0000");
        assertThat(resultado.getAdRemIBS()).isEqualByComparingTo("3.30");
        assertThat(resultado.getVIBSMono()).isEqualByComparingTo("33.00");
        assertThat(resultado.getAdRemCBS()).isEqualByComparingTo(ZERO);
        assertThat(resultado.getVCBSMono()).isEqualByComparingTo(ZERO);
    }

    @Test
    void teste_monofasiaRetencaoComCBSIBS_retornaCBSIBS() {
        MonofasiaRetencaoDomain resultado = MonofasiaRetencaoDomain.create(
                criarCbsComTributoSujeitoRetencao("40.0000", "4.40", "44.00"),
                criarIbsComTributoSujeitoRetencao("50.0000", "5.50", "55.00"),
                criarIbsComTributoSujeitoRetencao("77.0000", "7.70", "77.00"));

        assertThat(resultado.getQBCMonoReten()).isEqualByComparingTo("50.0000");
        assertThat(resultado.getAdRemIBSReten()).isEqualByComparingTo("5.50");
        assertThat(resultado.getVIBSMonoReten()).isEqualByComparingTo("55.00");
        assertThat(resultado.getAdRemCBSReten()).isEqualByComparingTo("4.40");
        assertThat(resultado.getVCBSMonoReten()).isEqualByComparingTo("44.00");
    }

    @Test
    void teste_monofasiaRetencaoCriadaComCBSNull_retornaCBSZero() {
        MonofasiaRetencaoDomain resultado = MonofasiaRetencaoDomain.create(
                null,
                criarIbsComTributoSujeitoRetencao("60.0000", "6.60", "66.00"),
                criarIbsComTributoSujeitoRetencao("76.0000", "7.60", "76.00"));

        assertThat(resultado.getQBCMonoReten()).isEqualByComparingTo("60.0000");
        assertThat(resultado.getAdRemIBSReten()).isEqualByComparingTo("6.60");
        assertThat(resultado.getVIBSMonoReten()).isEqualByComparingTo("66.00");
        assertThat(resultado.getAdRemCBSReten()).isEqualByComparingTo(ZERO);
        assertThat(resultado.getVCBSMonoReten()).isEqualByComparingTo(ZERO);
    }

    @Test
    void teste_monofasiaRetidaComCBSIBS_retornaCBSIBS() {
        MonofasiaRetidoAnteriormenteDomain resultado = MonofasiaRetidoAnteriormenteDomain.create(
                criarCbsComTributoRetido("70.0000", "7.70", "77.00"),
                criarIbsComTributoRetido("80.0000", "8.80", "88.00"),
                criarIbsComTributoRetido("66.0000", "6.60", "66.00"));

        assertThat(resultado.getQBCMonoRet()).isEqualByComparingTo("80.0000");
        assertThat(resultado.getAdRemIBSRet()).isEqualByComparingTo("8.80");
        assertThat(resultado.getVIBSMonoRet()).isEqualByComparingTo("88.00");
        assertThat(resultado.getAdRemCBSRet()).isEqualByComparingTo("7.70");
        assertThat(resultado.getVCBSMonoRet()).isEqualByComparingTo("77.00");
    }

    @Test
    void teste_monofasiaRetidaCriadaComCBSNull_retornaCBSZero() {
        MonofasiaRetidoAnteriormenteDomain resultado = MonofasiaRetidoAnteriormenteDomain.create(
                null,
                criarIbsComTributoRetido("90.0000", "9.90", "99.00"),
                criarIbsComTributoRetido("55.0000", "5.50", "55.00"));

        assertThat(resultado.getQBCMonoRet()).isEqualByComparingTo("90.0000");
        assertThat(resultado.getAdRemIBSRet()).isEqualByComparingTo("9.90");
        assertThat(resultado.getVIBSMonoRet()).isEqualByComparingTo("99.00");
        assertThat(resultado.getAdRemCBSRet()).isEqualByComparingTo(ZERO);
        assertThat(resultado.getVCBSMonoRet()).isEqualByComparingTo(ZERO);
    }

    @Test
    void teste_monofasiaDiferimentoComCBSIBS_retornaCBSIBS() {
        MonofasiaDiferimentoDomain resultado = MonofasiaDiferimentoDomain.create(
                criarCbsComTributoDiferido("10.00", "11.00"),
                criarIbsComTributoDiferido("20.00", "22.00"),
                criarIbsComTributoDiferido("99.00", "99.00"));

        assertThat(resultado.getPDifIBS()).isEqualByComparingTo("20.00");
        assertThat(resultado.getVIBSMonoDif()).isEqualByComparingTo("22.00");
        assertThat(resultado.getPDifCBS()).isEqualByComparingTo("10.00");
        assertThat(resultado.getVCBSMonoDif()).isEqualByComparingTo("11.00");
    }

    @Test
    void teste_monofasiaDiferimentoCriadaComCBSNull_retornaCBSZero() {
        MonofasiaDiferimentoDomain resultado = MonofasiaDiferimentoDomain.create(
                null,
                criarIbsComTributoDiferido("30.00", "33.00"),
                criarIbsComTributoDiferido("88.00", "88.00"));

        assertThat(resultado.getPDifIBS()).isEqualByComparingTo("30.00");
        assertThat(resultado.getVIBSMonoDif()).isEqualByComparingTo("33.00");
        assertThat(resultado.getPDifCBS()).isEqualByComparingTo(ZERO);
        assertThat(resultado.getVCBSMonoDif()).isEqualByComparingTo(ZERO);
    }

    @Test
    void teste_mergeGrupoEtapaMonofasiaExistenteComNull_retornaGrupoExistente() {
        GrupoEtapaMonofasiaOutput grupo = GrupoEtapaMonofasiaOutput.builder()
                .quantidade(decimal("1.0000"))
                .aliquotaAdRem(decimal("1.10"))
                .valor(decimal("11.00"))
                .build();

        assertThat(GrupoEtapaMonofasiaOutput.merge(null, grupo)).isSameAs(grupo);
        assertThat(GrupoEtapaMonofasiaOutput.merge(grupo, null)).isSameAs(grupo);
    }

    @Test
    void teste_mergeGrupoEtapaMonofasia_retornaSomandoValoresECompletandoNulls() {
        GrupoEtapaMonofasiaOutput grupo1 = GrupoEtapaMonofasiaOutput.builder()
                .valor(decimal("1.25"))
                .build();
        GrupoEtapaMonofasiaOutput grupo2 = GrupoEtapaMonofasiaOutput.builder()
                .quantidade(decimal("2.0000"))
                .aliquotaAdRem(decimal("2.20"))
                .valor(decimal("2.75"))
                .build();

        GrupoEtapaMonofasiaOutput resultado = GrupoEtapaMonofasiaOutput.merge(grupo1, grupo2);

        assertThat(resultado.getQuantidade()).isEqualByComparingTo("2.0000");
        assertThat(resultado.getAliquotaAdRem()).isEqualByComparingTo("2.20");
        assertThat(resultado.getValor()).isEqualByComparingTo("4.00");
    }

    @Test
    void teste_mergeGrupoDiferimentoMonofasiaExistenteComNull_retornaGrupoExistente() {
        GrupoDiferimentoMonofasiaOutput grupo = GrupoDiferimentoMonofasiaOutput.builder()
                .percentualDiferimento(decimal("5.00"))
                .valorDiferimento(decimal("10.00"))
                .build();

        assertThat(GrupoDiferimentoMonofasiaOutput.merge(null, grupo)).isSameAs(grupo);
        assertThat(GrupoDiferimentoMonofasiaOutput.merge(grupo, null)).isSameAs(grupo);
    }

    @Test
    void teste_mergeGrupoDiferimentoMonofasia_retornaSomandoValoresECompletandoPercentual() {
        GrupoDiferimentoMonofasiaOutput grupo1 = GrupoDiferimentoMonofasiaOutput.builder()
                .valorDiferimento(decimal("1.25"))
                .build();
        GrupoDiferimentoMonofasiaOutput grupo2 = GrupoDiferimentoMonofasiaOutput.builder()
                .percentualDiferimento(decimal("7.50"))
                .valorDiferimento(decimal("2.75"))
                .build();

        GrupoDiferimentoMonofasiaOutput resultado = GrupoDiferimentoMonofasiaOutput.merge(grupo1, grupo2);

        assertThat(resultado.getPercentualDiferimento()).isEqualByComparingTo("7.50");
        assertThat(resultado.getValorDiferimento()).isEqualByComparingTo("4.00");
    }

    private static CbsIbsOutput criarCbsComTributoMonofasico(String quantidade, String aliquota, String valor) {
        return CbsIbsOutput.builder()
                .grupoMonofasia(GrupoMonofasiaOutput.builder()
                        .tributoMonofasico(criarGrupoEtapa(quantidade, aliquota, valor))
                        .build())
                .build();
    }

    private static CbsIbsOutput criarIbsComTributoMonofasico(String quantidade, String aliquota, String valor) {
        return criarCbsComTributoMonofasico(quantidade, aliquota, valor);
    }

    private static CbsIbsOutput criarCbsComTributoSujeitoRetencao(String quantidade, String aliquota, String valor) {
        return CbsIbsOutput.builder()
                .grupoMonofasia(GrupoMonofasiaOutput.builder()
                        .tributoSujeitoRetencao(criarGrupoEtapa(quantidade, aliquota, valor))
                        .build())
                .build();
    }

    private static CbsIbsOutput criarIbsComTributoSujeitoRetencao(String quantidade, String aliquota, String valor) {
        return criarCbsComTributoSujeitoRetencao(quantidade, aliquota, valor);
    }

    private static CbsIbsOutput criarCbsComTributoRetido(String quantidade, String aliquota, String valor) {
        return CbsIbsOutput.builder()
                .grupoMonofasia(GrupoMonofasiaOutput.builder()
                        .tributoRetido(criarGrupoEtapa(quantidade, aliquota, valor))
                        .build())
                .build();
    }

    private static CbsIbsOutput criarIbsComTributoRetido(String quantidade, String aliquota, String valor) {
        return criarCbsComTributoRetido(quantidade, aliquota, valor);
    }

    private static CbsIbsOutput criarCbsComTributoDiferido(String percentual, String valor) {
        return CbsIbsOutput.builder()
                .grupoMonofasia(GrupoMonofasiaOutput.builder()
                        .tributoDiferido(GrupoDiferimentoMonofasiaOutput.builder()
                                .percentualDiferimento(decimal(percentual))
                                .valorDiferimento(decimal(valor))
                                .build())
                        .build())
                .build();
    }

    private static CbsIbsOutput criarIbsComTributoDiferido(String percentual, String valor) {
        return criarCbsComTributoDiferido(percentual, valor);
    }

    private static GrupoEtapaMonofasiaOutput criarGrupoEtapa(String quantidade, String aliquota, String valor) {
        return GrupoEtapaMonofasiaOutput.builder()
                .quantidade(decimal(quantidade))
                .aliquotaAdRem(decimal(aliquota))
                .valor(decimal(valor))
                .build();
    }

    private static BigDecimal decimal(String valor) {
        return new BigDecimal(valor);
    }
}
