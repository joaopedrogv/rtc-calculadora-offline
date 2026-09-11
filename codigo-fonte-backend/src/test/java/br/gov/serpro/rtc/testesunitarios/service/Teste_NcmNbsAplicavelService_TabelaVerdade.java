/*
* Versão de Homologação/Testes
*/
package br.gov.serpro.rtc.testesunitarios.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.gov.serpro.rtc.domain.repository.NbsAplicavelRepository;
import br.gov.serpro.rtc.domain.repository.NcmAplicavelRepository;
import br.gov.serpro.rtc.domain.service.NbsAplicavelService;
import br.gov.serpro.rtc.domain.service.NcmAplicavelService;
import br.gov.serpro.rtc.domain.service.exception.NbsCompletoNaoInformadoException;
import br.gov.serpro.rtc.domain.service.exception.NbsNaoVinculadaException;
import br.gov.serpro.rtc.domain.service.exception.NcmCompletoNaoInformadoException;
import br.gov.serpro.rtc.domain.service.exception.NcmNaoVinculadaException;

/**
 * Tabela-verdade da validação de NCM/NBS-aplicável (Requisito 6.2).
 *
 * Predicados: T = tem (classificação possui anexo vigente),
 * A = temAplicavel (vínculo vigente cobre o código),
 * E = temExcecao (exceção vigente cobre o código),
 * S = temAplicavelSemExcecao (vínculo vigente cobrindo o código sem nenhuma
 * exceção vigente associada). Regra: com anexo (T=1) e código completo,
 * rejeitar quando !A — inclusive a combinação (A=0, E=1), vínculo
 * expirado/futuro com exceção vigente, que antes passava em silêncio — ou
 * quando E e !S (excepcionado sem outro vínculo livre de exceções).
 */
class Teste_NcmNbsAplicavelService_TabelaVerdade {

    private static final LocalDate DATA = LocalDate.of(2026, 1, 1);
    private static final Long ID = 1L;
    private static final String CODIGO = "200003";
    private static final String TRIBUTOS = "CBS e IBS";

    private NcmAplicavelRepository ncmRepository;
    private NbsAplicavelRepository nbsRepository;
    private NcmAplicavelService ncmService;
    private NbsAplicavelService nbsService;

    @BeforeEach
    void setUp() {
        ncmRepository = mock(NcmAplicavelRepository.class);
        nbsRepository = mock(NbsAplicavelRepository.class);
        ncmService = new NcmAplicavelService(ncmRepository);
        nbsService = new NbsAplicavelService(nbsRepository);
    }

    @Test
    @DisplayName("NCM: sem anexo (T=0) aceita com e sem NCM, sem crítica")
    void teste_ncm_semAnexoAceita() {
        when(ncmRepository.tem(anyLong(), any())).thenReturn(false);
        assertThatCode(() -> ncmService.validarNcmAplicavel(null, ID, CODIGO, DATA, TRIBUTOS))
                .doesNotThrowAnyException();
        assertThatCode(() -> ncmService.validarNcmAplicavel("24021000", ID, CODIGO, DATA, TRIBUTOS))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("NCM: com anexo (T=1) e NCM incompleto/ausente exige NCM completo")
    void teste_ncm_comAnexoExigeCompleto() {
        when(ncmRepository.tem(anyLong(), any())).thenReturn(true);
        assertThatThrownBy(() -> ncmService.validarNcmAplicavel(null, ID, CODIGO, DATA, TRIBUTOS))
                .isExactlyInstanceOf(NcmCompletoNaoInformadoException.class);
        assertThatThrownBy(() -> ncmService.validarNcmAplicavel("2402", ID, CODIGO, DATA, TRIBUTOS))
                .isExactlyInstanceOf(NcmCompletoNaoInformadoException.class);
    }

    @Test
    @DisplayName("NCM (0,0,0): código fora do anexo é rejeitado")
    void teste_ncm_foraDoAnexoRejeita() {
        when(ncmRepository.tem(anyLong(), any())).thenReturn(true);
        when(ncmRepository.temNcmAplicavel(anyString(), anyLong(), any())).thenReturn(false);
        when(ncmRepository.temExcecaoNcmAplicavel(anyString(), anyLong(), any())).thenReturn(false);
        assertThatThrownBy(() -> ncmService.validarNcmAplicavel("24021000", ID, CODIGO, DATA, TRIBUTOS))
                .isExactlyInstanceOf(NcmNaoVinculadaException.class);
    }

    @Test
    @DisplayName("NCM (0,1,0): vínculo fora de vigência com exceção vigente é rejeitado (antes passava)")
    void teste_ncm_vinculoForaDeVigenciaComExcecaoRejeita() {
        when(ncmRepository.tem(anyLong(), any())).thenReturn(true);
        when(ncmRepository.temNcmAplicavel(anyString(), anyLong(), any())).thenReturn(false);
        when(ncmRepository.temExcecaoNcmAplicavel(anyString(), anyLong(), any())).thenReturn(true);
        assertThatThrownBy(() -> ncmService.validarNcmAplicavel("24021000", ID, CODIGO, DATA, TRIBUTOS))
                .isExactlyInstanceOf(NcmNaoVinculadaException.class);
    }

    @Test
    @DisplayName("NCM (1,0,*): vínculo vigente sem exceção que cubra o código é aceito")
    void teste_ncm_semExcecaoCobrindoAceita() {
        when(ncmRepository.tem(anyLong(), any())).thenReturn(true);
        when(ncmRepository.temNcmAplicavel(anyString(), anyLong(), any())).thenReturn(true);
        when(ncmRepository.temExcecaoNcmAplicavel(anyString(), anyLong(), any())).thenReturn(false);
        when(ncmRepository.temNcmAplicavelSemExcecao(anyString(), anyLong(), any())).thenReturn(false);
        assertThatCode(() -> ncmService.validarNcmAplicavel("24021000", ID, CODIGO, DATA, TRIBUTOS))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("NCM (1,1,0): excepcionado sem vínculo livre de exceções é rejeitado")
    void teste_ncm_excepcionadoRejeita() {
        when(ncmRepository.tem(anyLong(), any())).thenReturn(true);
        when(ncmRepository.temNcmAplicavel(anyString(), anyLong(), any())).thenReturn(true);
        when(ncmRepository.temExcecaoNcmAplicavel(anyString(), anyLong(), any())).thenReturn(true);
        when(ncmRepository.temNcmAplicavelSemExcecao(anyString(), anyLong(), any())).thenReturn(false);
        assertThatThrownBy(() -> ncmService.validarNcmAplicavel("24021000", ID, CODIGO, DATA, TRIBUTOS))
                .isExactlyInstanceOf(NcmNaoVinculadaException.class);
    }

    @Test
    @DisplayName("NCM (1,1,1): excepcionado, mas outro vínculo vigente sem exceções cobre o código — aceito")
    void teste_ncm_excepcionadoComVinculoLivreAceita() {
        when(ncmRepository.tem(anyLong(), any())).thenReturn(true);
        when(ncmRepository.temNcmAplicavel(anyString(), anyLong(), any())).thenReturn(true);
        when(ncmRepository.temExcecaoNcmAplicavel(anyString(), anyLong(), any())).thenReturn(true);
        when(ncmRepository.temNcmAplicavelSemExcecao(anyString(), anyLong(), any())).thenReturn(true);
        assertThatCode(() -> ncmService.validarNcmAplicavel("24021000", ID, CODIGO, DATA, TRIBUTOS))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("NBS: sem anexo (T=0) aceita com e sem NBS, sem crítica")
    void teste_nbs_semAnexoAceita() {
        when(nbsRepository.tem(anyLong(), any())).thenReturn(false);
        assertThatCode(() -> nbsService.validarNbsAplicavel(null, ID, CODIGO, DATA, TRIBUTOS))
                .doesNotThrowAnyException();
        assertThatCode(() -> nbsService.validarNbsAplicavel("115012000", ID, CODIGO, DATA, TRIBUTOS))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("NBS: com anexo (T=1) e NBS incompleta/ausente exige NBS completa")
    void teste_nbs_comAnexoExigeCompleta() {
        when(nbsRepository.tem(anyLong(), any())).thenReturn(true);
        assertThatThrownBy(() -> nbsService.validarNbsAplicavel(null, ID, CODIGO, DATA, TRIBUTOS))
                .isExactlyInstanceOf(NbsCompletoNaoInformadoException.class);
    }

    @Test
    @DisplayName("NBS: tabela-verdade equivalente à do NCM")
    void teste_nbs_tabelaVerdade() {
        when(nbsRepository.tem(anyLong(), any())).thenReturn(true);

        // (1,0,*): aceita
        when(nbsRepository.temNbsAplicavel(anyString(), anyLong(), any())).thenReturn(true);
        when(nbsRepository.temExcecaoNbsAplicavel(anyString(), anyLong(), any())).thenReturn(false);
        assertThatCode(() -> nbsService.validarNbsAplicavel("115012000", ID, CODIGO, DATA, TRIBUTOS))
                .doesNotThrowAnyException();

        // (1,1,0): rejeita
        when(nbsRepository.temExcecaoNbsAplicavel(anyString(), anyLong(), any())).thenReturn(true);
        when(nbsRepository.temNbsAplicavelSemExcecao(anyString(), anyLong(), any())).thenReturn(false);
        assertThatThrownBy(() -> nbsService.validarNbsAplicavel("115012000", ID, CODIGO, DATA, TRIBUTOS))
                .isExactlyInstanceOf(NbsNaoVinculadaException.class);

        // (0,1,0): rejeita (antes passava em silêncio)
        when(nbsRepository.temNbsAplicavel(anyString(), anyLong(), any())).thenReturn(false);
        assertThatThrownBy(() -> nbsService.validarNbsAplicavel("115012000", ID, CODIGO, DATA, TRIBUTOS))
                .isExactlyInstanceOf(NbsNaoVinculadaException.class);
    }

}
