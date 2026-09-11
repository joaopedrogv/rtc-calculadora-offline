/*
* Versão de Homologação/Testes
*/
package br.gov.serpro.rtc.testesintegracao.validacoes.negocio;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.gov.serpro.rtc.api.model.input.AliquotasNominaisImpostoSeletivo;
import br.gov.serpro.rtc.api.model.input.AliquotasNominaisInput;
import br.gov.serpro.rtc.api.model.input.ImpostoSeletivoInput;
import br.gov.serpro.rtc.api.model.input.ItemOperacaoInput;
import br.gov.serpro.rtc.api.model.input.OperacaoInput;
import br.gov.serpro.rtc.domain.service.CalculadoraService;
import br.gov.serpro.rtc.domain.service.exception.AliquotaImpostoSeletivoNaoInformadaException;
import br.gov.serpro.rtc.domain.service.exception.AliquotasNominaisInformadasIndevidamenteException;
import br.gov.serpro.rtc.domain.service.exception.AliquotasNominaisNaoInformadasException;

/**
 * Validações das alíquotas nominais por ano do fato gerador:
 * - até 31/12/2026 o grupo aliquotasNominais é proibido (a calculadora resolve
 *   as alíquotas vigentes em lei), incluindo o subgrupo impostoSeletivo;
 * - a partir de 01/01/2027 as alíquotas nominais (cbs, ibsEstadual e
 *   ibsMunicipal) são obrigatórias quando o CST exige o grupo IBS/CBS;
 * - a incidência do Imposto Seletivo passa a ser determinada pela existência do
 *   registro de alíquota (mesmo com valor NULL): com o grupo impostoSeletivo
 *   presente e sem valor resolvível, a alíquota deve ser informada pelo
 *   usuário.
 *
 * A massa base (Teste_validacao_15) tem fato gerador em 2027, NCM 24021000
 * (tributada pelo IS, com valor ainda não definido em lei no banco) e
 * alíquotas nominais de CBS/IBS preenchidas.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-testes.yml")
@ActiveProfiles("testes")
class Teste_validacao_AliquotasNominaisPorAnoFatoGerador {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CalculadoraService calculadoraService;

    private OperacaoInput operacao;

    @BeforeEach
    void beforeEach(
            final @Value("classpath:entradas/validacoes/Teste_validacao_15_ImpostoSeletivoNaoInformadoException.json") Resource resourceFile)
            throws Exception {
        operacao = objectMapper.readValue(resourceFile.getInputStream(), OperacaoInput.class);
    }

    private ItemOperacaoInput item() {
        return operacao.getItens().get(0);
    }

    private void mudarParaFatoGerador2026() {
        operacao.setDataHoraEmissao(OffsetDateTime.parse("2026-08-30T03:00:00-03:00"));
    }

    private void informarGrupoImpostoSeletivo() {
        ImpostoSeletivoInput impostoSeletivo = new ImpostoSeletivoInput();
        impostoSeletivo.setCst("000");
        impostoSeletivo.setCClassTrib("000001");
        impostoSeletivo.setBaseCalculo(new BigDecimal("100"));
        impostoSeletivo.setQuantidade(BigDecimal.ONE);
        impostoSeletivo.setUnidade("UN");
        item().setImpostoSeletivo(impostoSeletivo);
    }

    @Test
    void deveCriticarAliquotasNominaisInformadasAte2026() {
        mudarParaFatoGerador2026();

        assertThatThrownBy(() -> calculadoraService.calcularTributos(operacao))
                .isExactlyInstanceOf(AliquotasNominaisInformadasIndevidamenteException.class);
    }

    @Test
    void deveCriticarQualquerCampoDoGrupoAliquotasNominaisAte2026() {
        // A proibição vale para qualquer campo do grupo, incluindo o subgrupo
        // impostoSeletivo isoladamente.
        mudarParaFatoGerador2026();
        AliquotasNominaisImpostoSeletivo impostoSeletivo = new AliquotasNominaisImpostoSeletivo();
        impostoSeletivo.setAdValorem(new BigDecimal("10.0"));
        AliquotasNominaisInput aliquotas = new AliquotasNominaisInput();
        aliquotas.setImpostoSeletivo(impostoSeletivo);
        item().setAliquotasNominais(aliquotas);

        assertThatThrownBy(() -> calculadoraService.calcularTributos(operacao))
                .isExactlyInstanceOf(AliquotasNominaisInformadasIndevidamenteException.class);
    }

    @Test
    void deveCriticarAliquotasNominaisInformadasAte2026ViaController() throws Exception {
        mudarParaFatoGerador2026();

        mockMvc.perform(post("/calculadora/regime-geral")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(operacao)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.title").value("Alíquotas nominais informadas indevidamente"));
    }

    @Test
    void deveCalcularNormalmenteEm2026SemAliquotasNominais() throws Exception {
        // Regressão zero para 2026: sem alíquotas nominais, o cálculo segue
        // resolvendo as alíquotas vigentes em lei.
        mudarParaFatoGerador2026();
        item().setAliquotasNominais(null);
        informarGrupoImpostoSeletivo();

        mockMvc.perform(post("/calculadora/regime-geral")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(operacao)))
                .andExpect(status().isOk());
    }

    @Test
    void deveCriticarAusenciaDeAliquotasNominaisAPartirDe2027() {
        item().setAliquotasNominais(null);

        assertThatThrownBy(() -> calculadoraService.calcularTributos(operacao))
                .isExactlyInstanceOf(AliquotasNominaisNaoInformadasException.class);
    }

    @Test
    void deveCriticarPreenchimentoParcialDasAliquotasNominaisAPartirDe2027() {
        item().getAliquotasNominais().setIbsEstadual(null);
        item().getAliquotasNominais().setIbsMunicipal(null);

        assertThatThrownBy(() -> calculadoraService.calcularTributos(operacao))
                .isExactlyInstanceOf(AliquotasNominaisNaoInformadasException.class);
    }

    @Test
    void deveCriticarAliquotaImpostoSeletivoNaoResolvivelAPartirDe2027() {
        // NCM com incidência do IS na data (registro existe, valor NULL no
        // banco), grupo impostoSeletivo presente e alíquota não informada.
        informarGrupoImpostoSeletivo();

        assertThatThrownBy(() -> calculadoraService.calcularTributos(operacao))
                .isExactlyInstanceOf(AliquotaImpostoSeletivoNaoInformadaException.class);
    }

    @Test
    void deveCriticarAliquotaImpostoSeletivoNaoResolvivelViaController() throws Exception {
        informarGrupoImpostoSeletivo();

        mockMvc.perform(post("/calculadora/regime-geral")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(operacao)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.title").value("Alíquota do Imposto Seletivo não informada"));
    }

    @Test
    void deveCalcularComAliquotaImpostoSeletivoInformadaAPartirDe2027() throws Exception {
        informarGrupoImpostoSeletivo();
        AliquotasNominaisImpostoSeletivo impostoSeletivo = new AliquotasNominaisImpostoSeletivo();
        impostoSeletivo.setAdValorem(new BigDecimal("13.0"));
        item().getAliquotasNominais().setImpostoSeletivo(impostoSeletivo);

        mockMvc.perform(post("/calculadora/regime-geral")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(operacao)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.objetos[0].calculoSimulado").value(true))
                .andExpect(jsonPath("$.total").exists());
    }
}
