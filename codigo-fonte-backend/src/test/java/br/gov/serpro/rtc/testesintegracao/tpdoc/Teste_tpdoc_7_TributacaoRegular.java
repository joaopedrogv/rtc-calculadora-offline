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
import br.gov.serpro.rtc.domain.service.exception.ClassificacaoTributariaNaoVinculadaTipoDfeException;
import br.gov.serpro.rtc.domain.service.exception.NcmNaoVinculadaException;

/**
 * Caso de aceitação 7: os mesmos cenários de vínculo e nomenclatura aplicados
 * ao cClassTrib da tributacaoRegular. Item 550001 (suspensão, exige grupo de
 * tributação regular) com tributacaoRegular 200043: a classificação efetiva
 * passa a ser 200043 — vínculo com o tpDoc e crítica de NCM contra o anexo
 * valem para ela.
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-testes.yml")
@ActiveProfiles("testes")
class Teste_tpdoc_7_TributacaoRegular {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CalculadoraService calculadoraService;

    private OperacaoInput operacao;

    @BeforeEach
    void beforeEach(final @Value("classpath:entradas/tpdoc/tpdoc_tributacao_regular.json") Resource resourceFile)
            throws IOException {
        operacao = objectMapper.readValue(resourceFile.getInputStream(), OperacaoInput.class);
    }

    @Test
    @DisplayName("NF-e + 550001/tributacaoRegular 200043: vínculo e NCM do anexo passam")
    void teste_tributacaoRegularVinculadaPassa() {
        assertThatCode(() -> calculadoraService.calcularTributos(operacao))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("NF-e + 550001/tributacaoRegular 200044: classificação regular sem vínculo gera erro")
    void teste_tributacaoRegularSemVinculoErro() {
        operacao.getItens().get(0).getTributacaoRegular().setCClassTrib("200044");
        assertThatThrownBy(() -> calculadoraService.calcularTributos(operacao))
                .isExactlyInstanceOf(ClassificacaoTributariaNaoVinculadaTipoDfeException.class);
    }

    @Test
    @DisplayName("NF-e + 550001/tributacaoRegular 200043: NCM fora do anexo da classificação regular gera erro")
    void teste_tributacaoRegularNcmForaDoAnexoErro() {
        operacao.getItens().get(0).setNcm("24021000");
        assertThatThrownBy(() -> calculadoraService.calcularTributos(operacao))
                .isExactlyInstanceOf(NcmNaoVinculadaException.class);
    }

    @Test
    @DisplayName("NF-e + tributacaoRegular presente sem exigência: vínculo do cClassTrib regular também é validado")
    void teste_tributacaoRegularNaoExigidaSemVinculoErro() {
        // item 000001 não exige grupo de tributação regular, mas o grupo informado
        // com classificação sem vínculo com o tpDoc gera erro de item
        operacao.getItens().get(0).setCst("000");
        operacao.getItens().get(0).setCClassTrib("000001");
        operacao.getItens().get(0).setNcm("24021000");
        operacao.getItens().get(0).getTributacaoRegular().setCst("200");
        operacao.getItens().get(0).getTributacaoRegular().setCClassTrib("200044");
        assertThatThrownBy(() -> calculadoraService.calcularTributos(operacao))
                .isExactlyInstanceOf(ClassificacaoTributariaNaoVinculadaTipoDfeException.class);
    }

}
