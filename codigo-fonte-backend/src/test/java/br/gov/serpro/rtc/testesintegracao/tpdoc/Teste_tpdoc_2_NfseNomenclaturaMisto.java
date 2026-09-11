/*
* Versão de Homologação/Testes
*/
package br.gov.serpro.rtc.testesintegracao.tpdoc;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.gov.serpro.rtc.api.model.input.OperacaoInput;
import br.gov.serpro.rtc.domain.service.CalculadoraService;
import br.gov.serpro.rtc.domain.service.exception.NbsNaoVinculadaException;
import br.gov.serpro.rtc.domain.service.exception.NomenclaturaObrigatoriaNaoInformadaException;

/**
 * Caso de aceitação 2: cClassTrib 200043 (locação; anexo com NCM e NBS) com
 * tpDoc 91 (NFS-e) — nomenclatura MISTO: exige NCM completo OU NBS completa,
 * qualquer um dos dois, válido no anexo.
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-testes.yml")
@ActiveProfiles("testes")
class Teste_tpdoc_2_NfseNomenclaturaMisto {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CalculadoraService calculadoraService;

    private OperacaoInput operacao;

    @BeforeEach
    void beforeEach(final @Value("classpath:entradas/tpdoc/tpdoc_nfse_200043.json") Resource resourceFile)
            throws IOException {
        operacao = objectMapper.readValue(resourceFile.getInputStream(), OperacaoInput.class);
    }

    @Test
    @DisplayName("NFS-e + 200043 (MISTO): NBS válida do anexo passa")
    void teste_nbsValidaDoAnexoPassa() {
        assertThatCode(() -> calculadoraService.calcularTributos(operacao))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("NFS-e + 200043 (MISTO): NCM válido do anexo passa")
    void teste_ncmValidoDoAnexoPassa() {
        operacao.getItens().get(0).setNbs(null);
        operacao.getItens().get(0).setNcm("87100000");
        assertThatCode(() -> calculadoraService.calcularTributos(operacao))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("NFS-e + 200043 (MISTO): nenhuma nomenclatura informada gera erro")
    void teste_nenhumaNomenclaturaErro() {
        operacao.getItens().get(0).setNbs(null);
        assertThatThrownBy(() -> calculadoraService.calcularTributos(operacao))
                .isExactlyInstanceOf(NomenclaturaObrigatoriaNaoInformadaException.class);
    }

    @Test
    @DisplayName("NFS-e + 200043 (MISTO): NBS fora do anexo gera erro")
    void teste_nbsForaDoAnexoErro() {
        operacao.getItens().get(0).setNbs("109052100");
        assertThatThrownBy(() -> calculadoraService.calcularTributos(operacao))
                .isExactlyInstanceOf(NbsNaoVinculadaException.class);
    }

}
