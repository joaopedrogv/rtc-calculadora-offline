package br.gov.serpro.rtc.testesintegracao.calculoscorretos.comprasgovernamentais;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;

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
import org.springframework.util.StreamUtils;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-testes.yml")
@ActiveProfiles("testes")
class CompraGovControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Value("classpath:entradas/calculoscorretos/compras-governamentais/entrada.json")
    private Resource entradaJson;

    @Test
    void deveCalcularTributosCompraGovViaEndpoint() throws Exception {
        final String json = StreamUtils.copyToString(entradaJson.getInputStream(), StandardCharsets.UTF_8);

        mockMvc.perform(post("/calculadora/regime-geral")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isOk())

            // oper.gCompraGov
            .andExpect(jsonPath("$.oper.gCompraGov.tpEnteGov").value(2))
            .andExpect(jsonPath("$.oper.gCompraGov.pRedutor").value("50.00"))
            .andExpect(jsonPath("$.oper.gCompraGov.tpOperGov").value(1))

            // objetos[0]
            .andExpect(jsonPath("$.objetos[0].nObj").value(1))

            // objetos[0].tribCalc.IS
            .andExpect(jsonPath("$.objetos[0].tribCalc.IS.vBCIS").value("200.00"))
            .andExpect(jsonPath("$.objetos[0].tribCalc.IS.pIS").value("14.00"))
            .andExpect(jsonPath("$.objetos[0].tribCalc.IS.pISEspec").value("21.30"))
            .andExpect(jsonPath("$.objetos[0].tribCalc.IS.qTrib").value("1"))
            .andExpect(jsonPath("$.objetos[0].tribCalc.IS.vIS").value("49.30"))

            // objetos[0].tribCalc.IBSCBS.gIBSCBS
            .andExpect(jsonPath("$.objetos[0].tribCalc.IBSCBS.gIBSCBS.vBC").value("249.30"))

            // gIBSUF
            .andExpect(jsonPath("$.objetos[0].tribCalc.IBSCBS.gIBSCBS.gIBSUF.pIBSUF").value("0.00"))
            .andExpect(jsonPath("$.objetos[0].tribCalc.IBSCBS.gIBSCBS.gIBSUF.gRed.pRedAliq").value("0.00"))
            .andExpect(jsonPath("$.objetos[0].tribCalc.IBSCBS.gIBSCBS.gIBSUF.gRed.pAliqEfet").value("0.00"))
            .andExpect(jsonPath("$.objetos[0].tribCalc.IBSCBS.gIBSCBS.gIBSUF.vIBSUF").value("0.00"))

            // gIBSMun
            .andExpect(jsonPath("$.objetos[0].tribCalc.IBSCBS.gIBSCBS.gIBSMun.pIBSMun").value("0.00"))
            .andExpect(jsonPath("$.objetos[0].tribCalc.IBSCBS.gIBSCBS.gIBSMun.gRed.pRedAliq").value("0.00"))
            .andExpect(jsonPath("$.objetos[0].tribCalc.IBSCBS.gIBSCBS.gIBSMun.gRed.pAliqEfet").value("0.00"))
            .andExpect(jsonPath("$.objetos[0].tribCalc.IBSCBS.gIBSCBS.gIBSMun.vIBSMun").value("0.00"))

            .andExpect(jsonPath("$.objetos[0].tribCalc.IBSCBS.gIBSCBS.vIBS").value("0.00"))

            // gCBS
            .andExpect(jsonPath("$.objetos[0].tribCalc.IBSCBS.gIBSCBS.gCBS.pCBS").value("0.00"))
            .andExpect(jsonPath("$.objetos[0].tribCalc.IBSCBS.gIBSCBS.gCBS.gRed.pRedAliq").value("0.00"))
            .andExpect(jsonPath("$.objetos[0].tribCalc.IBSCBS.gIBSCBS.gCBS.gRed.pAliqEfet").value("0.00"))
            .andExpect(jsonPath("$.objetos[0].tribCalc.IBSCBS.gIBSCBS.gCBS.vCBS").value("0.00"))

            // gTribRegular
            .andExpect(jsonPath("$.objetos[0].tribCalc.IBSCBS.gIBSCBS.gTribRegular.cClassTribReg").value("200032"))
            .andExpect(jsonPath("$.objetos[0].tribCalc.IBSCBS.gIBSCBS.gTribRegular.pAliqEfetRegIBSUF").value("0.54"))
            .andExpect(jsonPath("$.objetos[0].tribCalc.IBSCBS.gIBSCBS.gTribRegular.vTribRegIBSUF").value("1.35"))
            .andExpect(jsonPath("$.objetos[0].tribCalc.IBSCBS.gIBSCBS.gTribRegular.pAliqEfetRegIBSMun").value("0.00"))
            .andExpect(jsonPath("$.objetos[0].tribCalc.IBSCBS.gIBSCBS.gTribRegular.vTribRegIBSMun").value("0.00"))
            .andExpect(jsonPath("$.objetos[0].tribCalc.IBSCBS.gIBSCBS.gTribRegular.pAliqEfetRegCBS").value("1.53"))
            .andExpect(jsonPath("$.objetos[0].tribCalc.IBSCBS.gIBSCBS.gTribRegular.vTribRegCBS").value("3.81"))

            // gTribCompraGov
            .andExpect(jsonPath("$.objetos[0].tribCalc.IBSCBS.gIBSCBS.gTribCompraGov.pAliqIBSUF").value("0.00"))
            .andExpect(jsonPath("$.objetos[0].tribCalc.IBSCBS.gIBSCBS.gTribCompraGov.vTribIBSUF").value("0.00"))
            .andExpect(jsonPath("$.objetos[0].tribCalc.IBSCBS.gIBSCBS.gTribCompraGov.pAliqIBSMun").value("0.00"))
            .andExpect(jsonPath("$.objetos[0].tribCalc.IBSCBS.gIBSCBS.gTribCompraGov.vTribIBSMun").value("0.00"))
            .andExpect(jsonPath("$.objetos[0].tribCalc.IBSCBS.gIBSCBS.gTribCompraGov.pAliqCBS").value("0.00"))
            .andExpect(jsonPath("$.objetos[0].tribCalc.IBSCBS.gIBSCBS.gTribCompraGov.vTribCBS").value("0.00"))

            // total.tribCalc.ISTot
            .andExpect(jsonPath("$.total.tribCalc.ISTot.vIS").value("49.30"))

            // total.tribCalc.IBSCBSTot
            .andExpect(jsonPath("$.total.tribCalc.IBSCBSTot.vBCIBSCBS").value("249.30"))

            .andExpect(jsonPath("$.total.tribCalc.IBSCBSTot.gIBS.gIBSUF.vDif").value("0.00"))
            .andExpect(jsonPath("$.total.tribCalc.IBSCBSTot.gIBS.gIBSUF.vDevTrib").value("0.00"))
            .andExpect(jsonPath("$.total.tribCalc.IBSCBSTot.gIBS.gIBSUF.vIBSUF").value("0.00"))
            .andExpect(jsonPath("$.total.tribCalc.IBSCBSTot.gIBS.gIBSMun.vDif").value("0.00"))
            .andExpect(jsonPath("$.total.tribCalc.IBSCBSTot.gIBS.gIBSMun.vDevTrib").value("0.00"))
            .andExpect(jsonPath("$.total.tribCalc.IBSCBSTot.gIBS.gIBSMun.vIBSMun").value("0.00"))
            .andExpect(jsonPath("$.total.tribCalc.IBSCBSTot.gIBS.vIBS").value("0.00"))
            .andExpect(jsonPath("$.total.tribCalc.IBSCBSTot.gIBS.vCredPres").value("0.00"))
            .andExpect(jsonPath("$.total.tribCalc.IBSCBSTot.gIBS.vCredPresCondSus").value("0.00"))

            .andExpect(jsonPath("$.total.tribCalc.IBSCBSTot.gCBS.vDif").value("0.00"))
            .andExpect(jsonPath("$.total.tribCalc.IBSCBSTot.gCBS.vDevTrib").value("0.00"))
            .andExpect(jsonPath("$.total.tribCalc.IBSCBSTot.gCBS.vCBS").value("0.00"))
            .andExpect(jsonPath("$.total.tribCalc.IBSCBSTot.gCBS.vCredPres").value("0.00"))
            .andExpect(jsonPath("$.total.tribCalc.IBSCBSTot.gCBS.vCredPresCondSus").value("0.00"))

            .andExpect(jsonPath("$.total.tribCalc.IBSCBSTot.gMono.vIBSMono").value("0.00"))
            .andExpect(jsonPath("$.total.tribCalc.IBSCBSTot.gMono.vCBSMono").value("0.00"))
            .andExpect(jsonPath("$.total.tribCalc.IBSCBSTot.gMono.vIBSMonoReten").value("0.00"))
            .andExpect(jsonPath("$.total.tribCalc.IBSCBSTot.gMono.vCBSMonoReten").value("0.00"))
            .andExpect(jsonPath("$.total.tribCalc.IBSCBSTot.gMono.vIBSMonoRet").value("0.00"))
            .andExpect(jsonPath("$.total.tribCalc.IBSCBSTot.gMono.vCBSMonoRet").value("0.00"));
    }
}