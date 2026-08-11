package br.gov.serpro.rtc.api.model.output.versaooffline;

import java.util.regex.Pattern;

import br.gov.serpro.rtc.api.model.output.dadosabertos.VersaoOutput;
import lombok.Getter;

/**
 * Saída com o comparativo de versões da aplicação e do banco de dados local em
 * relação às versões remotas para uso offline.
 *
 * <p>Recebe as versões local e remota no construtor e calcula internamente se
 * cada componente está atualizado.</p>
 */
@Getter
public class VersaoOfflineOutput {

    private static final Pattern SEMVER_PATTERN =
            Pattern.compile("^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)");

    private final boolean aplicacaoAtualizada;
    private final boolean dbAtualizada;
    private final boolean dataDbAtualizada;
    private final String versaoAplicacaoLocal;
    private final String versaoAplicacaoRemota;
    private final String versaoDbLocal;
    private final String versaoDbRemota;
    private final String dataDbLocal;
    private final String dataDbRemota;

    public VersaoOfflineOutput(VersaoOutput local, VersaoOutput remota) {
        this.versaoAplicacaoLocal = local.getVersaoApp();
        this.versaoDbLocal = local.getVersaoDb();
        this.dataDbLocal = local.getDataVersaoDb();

        this.versaoAplicacaoRemota = remota != null ? remota.getVersaoApp() : null;
        this.versaoDbRemota = remota != null ? remota.getVersaoDb() : null;
        this.dataDbRemota = remota != null ? remota.getDataVersaoDb() : null;

        String semverLocal = extrairSemver(this.versaoAplicacaoLocal);
        String semverRemota = extrairSemver(this.versaoAplicacaoRemota);
        this.aplicacaoAtualizada = semverLocal != null && semverLocal.equals(semverRemota);
        this.dbAtualizada = this.versaoDbLocal != null && this.versaoDbLocal.equals(this.versaoDbRemota);
        this.dataDbAtualizada = this.dataDbLocal != null && this.dataDbLocal.equals(this.dataDbRemota);
    }

    private static String extrairSemver(String versao) {
        if (versao == null) return null;
        var matcher = SEMVER_PATTERN.matcher(versao);
        return matcher.find() ? matcher.group() : null;
    }
}
