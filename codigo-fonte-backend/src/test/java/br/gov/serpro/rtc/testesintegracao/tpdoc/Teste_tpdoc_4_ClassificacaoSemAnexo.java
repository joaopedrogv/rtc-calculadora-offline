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
import br.gov.serpro.rtc.domain.service.exception.NomenclaturaIncompativelTipoDfeException;

/**
 * Caso de aceitação 4: classificação sem anexo (000001 em 2026): nada é
 * exigido; NCM/NBS informado é aceito sem crítica contra lista, com e sem
 * tpDoc. Dentro do trio NF-e/NFC-e/NFS-e a regra permanece rígida: NBS em
 * NF-e gera erro.
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-testes.yml")
@ActiveProfiles("testes")
class Teste_tpdoc_4_ClassificacaoSemAnexo {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CalculadoraService calculadoraService;

    private OperacaoInput operacao;

    @BeforeEach
    void beforeEach(final @Value("classpath:entradas/tpdoc/tpdoc_sem_anexo_000001.json") Resource resourceFile)
            throws IOException {
        operacao = objectMapper.readValue(resourceFile.getInputStream(), OperacaoInput.class);
    }

    @Test
    @DisplayName("NF-e + 000001 (sem anexo): NCM informado aceito sem crítica")
    void teste_comTpDoc_ncmAceitoSemCritica() {
        assertThatCode(() -> calculadoraService.calcularTributos(operacao))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("NF-e + 000001 (sem anexo): nada informado passa")
    void teste_comTpDoc_semNomenclaturaPassa() {
        operacao.getItens().get(0).setNcm(null);
        assertThatCode(() -> calculadoraService.calcularTributos(operacao))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("NF-e + 000001 (sem anexo): NBS informada gera erro (trio rígido)")
    void teste_comTpDoc_nbsEmNfeErro() {
        operacao.getItens().get(0).setNcm(null);
        operacao.getItens().get(0).setNbs("109052100");
        assertThatThrownBy(() -> calculadoraService.calcularTributos(operacao))
                .isExactlyInstanceOf(NomenclaturaIncompativelTipoDfeException.class);
    }

    @Test
    @DisplayName("NFS-e + 000001 (sem anexo): NBS informada aceita sem crítica")
    void teste_comTpDocNfse_nbsAceita() {
        operacao.setTpDoc(91);
        operacao.getItens().get(0).setNcm(null);
        operacao.getItens().get(0).setNbs("109052100");
        assertThatCode(() -> calculadoraService.calcularTributos(operacao))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Sem tpDoc + 000001 (sem anexo): NCM informado aceito (compatibilidade)")
    void teste_semTpDoc_ncmAceito() {
        operacao.setTpDoc(null);
        assertThatCode(() -> calculadoraService.calcularTributos(operacao))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Sem tpDoc + 000001 (sem anexo): nada informado passa (compatibilidade)")
    void teste_semTpDoc_semNomenclaturaPassa() {
        operacao.setTpDoc(null);
        operacao.getItens().get(0).setNcm(null);
        assertThatCode(() -> calculadoraService.calcularTributos(operacao))
                .doesNotThrowAnyException();
    }

}
