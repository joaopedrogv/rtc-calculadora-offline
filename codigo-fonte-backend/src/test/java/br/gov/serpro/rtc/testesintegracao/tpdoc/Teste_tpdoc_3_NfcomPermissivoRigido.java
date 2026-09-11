/*
* Versão de Homologação/Testes
*/
package br.gov.serpro.rtc.testesintegracao.tpdoc;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.time.LocalDate;

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
import br.gov.serpro.rtc.domain.model.dto.ClassificacaoTributariaDTO;
import br.gov.serpro.rtc.domain.model.enumeration.ModoValidacaoNomenclatura;
import br.gov.serpro.rtc.domain.model.enumeration.SiglasDFeEnum;
import br.gov.serpro.rtc.domain.service.CalculadoraService;
import br.gov.serpro.rtc.domain.service.ClassificacaoTributariaService;
import br.gov.serpro.rtc.domain.service.ValidacaoNomenclaturaService;
import br.gov.serpro.rtc.domain.service.exception.NbsNaoVinculadaException;
import br.gov.serpro.rtc.domain.service.exception.NcmNaoVinculadaException;
import br.gov.serpro.rtc.domain.service.exception.NomenclaturaNaoPermitidaTipoDfeException;

/**
 * Caso de aceitação 3: cClassTrib 200044 (vinculada a NFCom, anexo com NCM e
 * NBS) com tpDoc 62 (NFCom) — nomenclatura SEM: nada é exigido; se NCM/NBS é
 * informado, o modo PERMISSIVO critica contra o anexo e o modo RIGIDO proíbe.
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-testes.yml")
@ActiveProfiles("testes")
class Teste_tpdoc_3_NfcomPermissivoRigido {

    private static final LocalDate DATA = LocalDate.of(2026, 1, 1);
    private static final String TRIBUTOS = "CBS e IBS";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CalculadoraService calculadoraService;

    @Autowired
    private ValidacaoNomenclaturaService validacaoNomenclaturaService;

    @Autowired
    private ClassificacaoTributariaService classificacaoTributariaService;

    private OperacaoInput operacao;

    @BeforeEach
    void beforeEach(final @Value("classpath:entradas/tpdoc/tpdoc_nfcom_200044.json") Resource resourceFile)
            throws IOException {
        operacao = objectMapper.readValue(resourceFile.getInputStream(), OperacaoInput.class);
    }

    @Test
    @DisplayName("NFCom + 200044: classificação vinculada passa sem NCM/NBS")
    void teste_semNomenclaturaPassa() {
        assertThatCode(() -> calculadoraService.calcularTributos(operacao))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("NFCom + 200044 (PERMISSIVO): NCM válido do anexo passa")
    void teste_permissivo_ncmValidoPassa() {
        operacao.getItens().get(0).setNcm("87100000");
        assertThatCode(() -> calculadoraService.calcularTributos(operacao))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("NFCom + 200044 (PERMISSIVO): NCM fora do anexo gera erro")
    void teste_permissivo_ncmInvalidoErro() {
        operacao.getItens().get(0).setNcm("24021000");
        assertThatThrownBy(() -> calculadoraService.calcularTributos(operacao))
                .isExactlyInstanceOf(NcmNaoVinculadaException.class);
    }

    @Test
    @DisplayName("NFCom + 200044 (PERMISSIVO): NBS válida do anexo passa")
    void teste_permissivo_nbsValidaPassa() {
        operacao.getItens().get(0).setNbs("115012000");
        assertThatCode(() -> calculadoraService.calcularTributos(operacao))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("NFCom + 200044 (PERMISSIVO): NBS fora do anexo gera erro")
    void teste_permissivo_nbsInvalidaErro() {
        operacao.getItens().get(0).setNbs("109052100");
        assertThatThrownBy(() -> calculadoraService.calcularTributos(operacao))
                .isExactlyInstanceOf(NbsNaoVinculadaException.class);
    }

    @Test
    @DisplayName("NFCom + 200044 (RIGIDO): NCM informado gera erro de proibição")
    void teste_rigido_ncmInformadoErro() {
        ClassificacaoTributariaDTO classificacao = classificacaoTributariaService
                .buscarClassificacaoTributariaCbsIbs("200044", DATA);
        assertThatThrownBy(() -> validacaoNomenclaturaService.validar(SiglasDFeEnum.NFCOM,
                classificacao.id(), classificacao.codigo(), "87100000", null, DATA, TRIBUTOS,
                ModoValidacaoNomenclatura.RIGIDO))
                .isExactlyInstanceOf(NomenclaturaNaoPermitidaTipoDfeException.class);
    }

    @Test
    @DisplayName("NFCom + 200044 (RIGIDO): NBS informada gera erro de proibição")
    void teste_rigido_nbsInformadaErro() {
        ClassificacaoTributariaDTO classificacao = classificacaoTributariaService
                .buscarClassificacaoTributariaCbsIbs("200044", DATA);
        assertThatThrownBy(() -> validacaoNomenclaturaService.validar(SiglasDFeEnum.NFCOM,
                classificacao.id(), classificacao.codigo(), null, "115012000", DATA, TRIBUTOS,
                ModoValidacaoNomenclatura.RIGIDO))
                .isExactlyInstanceOf(NomenclaturaNaoPermitidaTipoDfeException.class);
    }

    @Test
    @DisplayName("NFCom + 200044 (RIGIDO): sem NCM/NBS passa")
    void teste_rigido_semNomenclaturaPassa() {
        ClassificacaoTributariaDTO classificacao = classificacaoTributariaService
                .buscarClassificacaoTributariaCbsIbs("200044", DATA);
        assertThatCode(() -> validacaoNomenclaturaService.validar(SiglasDFeEnum.NFCOM,
                classificacao.id(), classificacao.codigo(), null, null, DATA, TRIBUTOS,
                ModoValidacaoNomenclatura.RIGIDO))
                .doesNotThrowAnyException();
    }

}
