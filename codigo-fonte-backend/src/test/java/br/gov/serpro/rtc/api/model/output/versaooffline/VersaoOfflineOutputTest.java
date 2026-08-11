package br.gov.serpro.rtc.api.model.output.versaooffline;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import br.gov.serpro.rtc.api.model.output.dadosabertos.VersaoOutput;

class VersaoOfflineOutputTest {

    @Test
    void deveRetornarTudoAtualizadoQuandoVersoesIguais() {
        VersaoOutput local = versao("1.2.3", "1.0.0", "2026-01-01");
        VersaoOutput remota = versao("1.2.3", "1.0.0", "2026-01-01");

        VersaoOfflineOutput output = new VersaoOfflineOutput(local, remota);

        assertThat(output.isAplicacaoAtualizada()).isTrue();
        assertThat(output.isDbAtualizada()).isTrue();
        assertThat(output.isDataDbAtualizada()).isTrue();
    }

    @Test
    void deveRetornarDesatualizadoQuandoVersoesDiferentes() {
        VersaoOutput local = versao("1.0.0", "1.0.0", "2026-01-01");
        VersaoOutput remota = versao("2.0.0", "2.0.0", "2026-06-01");

        VersaoOfflineOutput output = new VersaoOfflineOutput(local, remota);

        assertThat(output.isAplicacaoAtualizada()).isFalse();
        assertThat(output.isDbAtualizada()).isFalse();
        assertThat(output.isDataDbAtualizada()).isFalse();
    }

    @Test
    void deveRetornarTudoDesatualizadoQuandoRemotaNula() {
        VersaoOutput local = versao("1.0.0", "1.0.0", "2026-01-01");

        VersaoOfflineOutput output = new VersaoOfflineOutput(local, null);

        assertThat(output.isAplicacaoAtualizada()).isFalse();
        assertThat(output.isDbAtualizada()).isFalse();
        assertThat(output.isDataDbAtualizada()).isFalse();
        assertThat(output.getVersaoAplicacaoRemota()).isNull();
        assertThat(output.getVersaoDbRemota()).isNull();
        assertThat(output.getDataDbRemota()).isNull();
    }

    @Test
    void deveRetornarNaoAtualizadaQuandoVersaoAppLocalNull() {
        VersaoOutput local = versao(null, "1.0.0", "2026-01-01");
        VersaoOutput remota = versao("1.0.0", "1.0.0", "2026-01-01");

        VersaoOfflineOutput output = new VersaoOfflineOutput(local, remota);

        assertThat(output.isAplicacaoAtualizada()).isFalse();
    }

    @Test
    void naoDeveGerarFalsePositiveQuandoSemverNullEmAmbosLados() {
        VersaoOutput local = versao("abc", "1.0.0", "2026-01-01");
        VersaoOutput remota = versao("xyz", "1.0.0", "2026-01-01");

        VersaoOfflineOutput output = new VersaoOfflineOutput(local, remota);

        assertThat(output.isAplicacaoAtualizada()).isFalse();
    }

    @ParameterizedTest(name = "versao valida com sufixo: {0}")
    @ValueSource(strings = {
            "1.2.3-SNAPSHOT",
            "1.2.3-prerelease+meta",
            "1.2.3+meta",
            "1.2.3+meta-valid",
            "1.2.3-beta",
            "1.2.3-SNAPSHOT-123",
            "1.2.3----RC-SNAPSHOT.12.9.1--.12+788",
            "1.2.3----R-S.12.9.1--.12+meta",
            "1.2.3----RC-SNAPSHOT.12.9.1--.12"
    })
    void deveExtrairPrefixoIgnorandoSufixo(String versaoComSufixo) {
        VersaoOutput local = versao(versaoComSufixo, "1.0.0", "2026-01-01");
        VersaoOutput remota = versao("1.2.3", "1.0.0", "2026-01-01");

        VersaoOfflineOutput output = new VersaoOfflineOutput(local, remota);

        assertThat(output.isAplicacaoAtualizada())
                .as("'%s' deve extrair '1.2.3' e ser igual à remota", versaoComSufixo)
                .isTrue();
    }

    @ParameterizedTest(name = "versao valida simples: {0}")
    @ValueSource(strings = {
            "0.0.4",
            "1.2.3",
            "10.20.30",
            "1.0.0",
            "2.0.0",
            "1.1.7"
    })
    void deveReconhecerVersoesValidasSimples(String versao) {
        VersaoOutput local = versao(versao, "1.0.0", "2026-01-01");
        VersaoOutput remota = versao(versao, "1.0.0", "2026-01-01");

        VersaoOfflineOutput output = new VersaoOfflineOutput(local, remota);

        assertThat(output.isAplicacaoAtualizada())
                .as("'%s' deve ser reconhecida como SemVer válido", versao)
                .isTrue();
    }

    @ParameterizedTest(name = "versao valida complexa: {0}")
    @ValueSource(strings = {
            "1.0.0-alpha",
            "1.0.0-beta",
            "1.0.0-alpha.beta",
            "1.0.0-alpha.beta.1",
            "1.0.0-alpha.1",
            "1.0.0-alpha0.valid",
            "1.0.0-alpha.0valid",
            "1.0.0-alpha-a.b-c-somethinglong+build.1-aef.1-its-okay",
            "1.0.0-rc.1+build.1",
            "2.0.0-rc.1+build.123",
            "10.2.3-DEV-SNAPSHOT",
            "2.0.0+build.1848",
            "2.0.1-alpha.1227",
            "1.0.0-alpha+beta",
            "1.0.0+0.build.1-rc.10000aaa-kk-0.1",
            "1.0.0-0A.is.legal",
            "1.1.2-prerelease+meta",
            "1.1.2+meta",
            "1.1.2+meta-valid"
    })
    void deveReconhecerVersoesValidasComplexas(String versaoCompleta) {
        // Extrai o prefixo X.Y.Z esperado
        String prefixoEsperado = versaoCompleta.split("[\\-+]")[0];
        if (!prefixoEsperado.matches("\\d+\\.\\d+\\.\\d+")) {
            prefixoEsperado = versaoCompleta.replaceAll("^(\\d+\\.\\d+\\.\\d+).*", "$1");
        }

        VersaoOutput local = versao(versaoCompleta, "1.0.0", "2026-01-01");
        VersaoOutput remota = versao(prefixoEsperado, "1.0.0", "2026-01-01");

        VersaoOfflineOutput output = new VersaoOfflineOutput(local, remota);

        assertThat(output.isAplicacaoAtualizada())
                .as("'%s' deve extrair '%s'", versaoCompleta, prefixoEsperado)
                .isTrue();
    }

    @Test
    void deveReconhecerNumerosGrandes() {
        String versaoGrande = "99999999999999999999999.999999999999999999.99999999999999999";
        VersaoOutput local = versao(versaoGrande, "1.0.0", "2026-01-01");
        VersaoOutput remota = versao(versaoGrande, "1.0.0", "2026-01-01");

        VersaoOfflineOutput output = new VersaoOfflineOutput(local, remota);

        assertThat(output.isAplicacaoAtualizada()).isTrue();
    }

    @ParameterizedTest(name = "versao invalida com zeros a esquerda: {0}")
    @ValueSource(strings = {
            "01.1.1",
            "1.01.1",
            "1.1.01"
    })
    void deveRejeitarZerosAEsquerda(String versaoInvalida) {
        VersaoOutput local = versao(versaoInvalida, "1.0.0", "2026-01-01");
        VersaoOutput remota = versao("1.2.3", "1.0.0", "2026-01-01");

        VersaoOfflineOutput output = new VersaoOfflineOutput(local, remota);

        assertThat(output.isAplicacaoAtualizada())
                .as("'%s' nao deve ser reconhecida como SemVer valido", versaoInvalida)
                .isFalse();
    }

    static Stream<String> versoesSemFormatoSemver() {
        return Stream.of(
                "1",
                "1.2",
                "alpha",
                "alpha.beta",
                "alpha.beta.1",
                "alpha.1",
                "alpha+beta",
                "alpha_beta",
                "alpha.",
                "alpha..",
                "beta",
                "+invalid",
                "-invalid",
                "-invalid+invalid",
                "-invalid.01",
                "1.2-SNAPSHOT",
                "1.2-RC-SNAPSHOT",
                "-1.0.3-gamma+b7718",
                "+justmeta",
                "-alpha."
        );
    }

    @ParameterizedTest(name = "versao invalida sem formato: {0}")
    @MethodSource("versoesSemFormatoSemver")
    void deveRejeitarStringsSemFormatoSemver(String versaoInvalida) {
        VersaoOutput local = versao(versaoInvalida, "1.0.0", "2026-01-01");
        VersaoOutput remota = versao("1.0.0", "1.0.0", "2026-01-01");

        VersaoOfflineOutput output = new VersaoOfflineOutput(local, remota);

        assertThat(output.isAplicacaoAtualizada())
                .as("'%s' nao deve ser reconhecida como SemVer valido", versaoInvalida)
                .isFalse();
    }

    // -- helper --

    private static VersaoOutput versao(String app, String db, String dataDb) {
        return VersaoOutput.builder()
                .versaoApp(app)
                .versaoDb(db)
                .dataVersaoDb(dataDb)
                .build();
    }
}
