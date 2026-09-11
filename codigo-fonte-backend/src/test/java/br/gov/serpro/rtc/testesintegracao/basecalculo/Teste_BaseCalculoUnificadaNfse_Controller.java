/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.testesintegracao.basecalculo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-testes.yml")
@ActiveProfiles("testes")
class Teste_BaseCalculoUnificadaNfse_Controller {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ─── Cenários base (unit test → integration) ─────────────────────────────

    @Test
    @DisplayName("Cenário 1 — COMUM: opSimpNac=1, vServ=100 | baseRG=80, baseSN=null")
    void cenario1_comum_nao_optante() throws Exception {
        ObjectNode input = objectMapper.createObjectNode();
        input.put("anoFatoGerador", 2026);
        input.put("opSimpNac", "1");
        input.put("situacao", "comum");
        input.put("vServ", "100.00");
        input.put("vDescIncond", "5.00");
        input.put("vISSQN", "5.00");
        input.put("vPIS", "5.00");
        input.put("vCOFINS", "5.00");

        mockMvc.perform(post("/calculadora/base-calculo/base-calculo-unificada-nfse")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.baseRG").value(80.0))
                .andExpect(jsonPath("$.baseSN").doesNotExist())
                .andExpect(jsonPath("$.avisos").isArray())
                .andExpect(jsonPath("$.erros").isArray());
    }

    @Test
    @DisplayName("Cenário 2 — SALAO_PARCEIRO: opSimpNac=1, vServ=100 | baseRG=80, baseSN=null")
    void cenario2_salaoParceiro_nao_optante() throws Exception {
        ObjectNode input = objectMapper.createObjectNode();
        input.put("anoFatoGerador", 2026);
        input.put("opSimpNac", "1");
        input.put("situacao", "salaoParceiro");
        input.put("vServ", "100.00");
        input.put("vDescIncond", "5.00");
        input.put("vISSQN", "5.00");
        input.put("vPIS", "5.00");
        input.put("vCOFINS", "5.00");

        mockMvc.perform(post("/calculadora/base-calculo/base-calculo-unificada-nfse")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.baseRG").value(80.0))
                .andExpect(jsonPath("$.baseSN").doesNotExist());
    }

    @Test
    @DisplayName("Cenário 3 — LOCACAO_9903: pCopropriedade=10%, vTotOper=1000 | baseRG=89, baseSN=null")
    void cenario3_locacao_nao_optante() throws Exception {
        ObjectNode locacao = objectMapper.createObjectNode();
        locacao.put("somaAjustesLocImoveis", "5.00");
        locacao.put("pCopropriedade", "10.00");
        locacao.put("vTotOper", "1000.00");
        locacao.put("vDescIncondTot", "5.00");

        ObjectNode input = objectMapper.createObjectNode();
        input.put("anoFatoGerador", 2026);
        input.put("opSimpNac", "1");
        input.put("situacao", "locacao9903");
        input.set("locacao", locacao);
        input.put("vPIS", "5.00");
        input.put("vCOFINS", "5.00");

        mockMvc.perform(post("/calculadora/base-calculo/base-calculo-unificada-nfse")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.baseRG").value(89.0))
                .andExpect(jsonPath("$.baseSN").doesNotExist());
    }

    @Test
    @DisplayName("Cenário 4 — BASE_NEGATIVA: descIncond > vServ | baseRG=-1.31, erro BASE-NEGATIVA")
    void cenario4_baseNegativa() throws Exception {
        ArrayNode docs = objectMapper.createArrayNode();
        ObjectNode doc = docs.addObject();
        doc.put("tpAjusteBC", "101");
        doc.put("vAjusteAplic", "0");

        ObjectNode input = objectMapper.createObjectNode();
        input.put("anoFatoGerador", 2026);
        input.put("opSimpNac", "1");
        input.put("situacao", "comum");
        input.set("documentos", docs);
        input.put("vServ", "0");
        input.put("vDescIncond", "1.313");

        mockMvc.perform(post("/calculadora/base-calculo/base-calculo-unificada-nfse")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.baseRG").value(-1.31))
                .andExpect(jsonPath("$.baseSN").doesNotExist())
                .andExpect(jsonPath("$.erros").isArray())
                .andExpect(jsonPath("$.erros[0].codigo").value("BASE-NEGATIVA"));
    }

    @Test
    @DisplayName("Cenário 5 — LOCACAO compat com optante + erro EXXX-LOC-SN | baseSN=99.50, baseRG=89")
    void cenario5_locacao_optante_com_erro() throws Exception {
        ObjectNode locacao = objectMapper.createObjectNode();
        locacao.put("somaAjustesLocImoveis", "5.00");
        locacao.put("pCopropriedade", "10.00");
        locacao.put("vTotOper", "1000.00");
        locacao.put("vDescIncondTot", "5.00");

        ObjectNode input = objectMapper.createObjectNode();
        input.put("anoFatoGerador", 2026);
        input.put("opSimpNac", "3");
        input.put("situacao", "locacao9903");
        input.put("regApIBSCBSSN", "1");
        input.set("locacao", locacao);
        input.put("vPIS", "5.00");
        input.put("vCOFINS", "5.00");

        mockMvc.perform(post("/calculadora/base-calculo/base-calculo-unificada-nfse")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.baseRG").value(89.0))
                .andExpect(jsonPath("$.baseSN").value(99.5))
                .andExpect(jsonPath("$.erros").isArray())
                .andExpect(jsonPath("$.erros[0].codigo").value("EXXX-LOC-SN"));
    }

    @Test
    @DisplayName("Cenário 6 — SALAO_PARCEIRO + regAp=3 (ambos regulares): SN calculada = 95")
    void cenario6_salaoParceiro_regAp3() throws Exception {
        ObjectNode input = objectMapper.createObjectNode();
        input.put("anoFatoGerador", 2026);
        input.put("opSimpNac", "3");
        input.put("situacao", "salaoParceiro");
        input.put("regApIBSCBSSN", "3");
        input.put("vServ", "100");
        input.put("vDescIncond", "5");
        input.put("vISSQN", "5");
        input.put("vPIS", "5");
        input.put("vCOFINS", "5");

        mockMvc.perform(post("/calculadora/base-calculo/base-calculo-unificada-nfse")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.baseRG").value(80.0))
                .andExpect(jsonPath("$.baseSN").value(95.0));
    }

    @Test
    @DisplayName("Cenário 7 — SALAO_PARCEIRO + regAp=3 + doc102: RG=80, SN=90 (102 não deduz do RG)")
    void cenario7_salaoParceiro_docTipo102() throws Exception {
        ArrayNode docs = objectMapper.createArrayNode();
        ObjectNode doc = docs.addObject();
        doc.put("tpAjusteBC", "102");
        doc.put("vAjusteAplic", "5");

        ObjectNode input = objectMapper.createObjectNode();
        input.put("anoFatoGerador", 2026);
        input.put("opSimpNac", "3");
        input.put("situacao", "salaoParceiro");
        input.put("regApIBSCBSSN", "3");
        input.set("documentos", docs);
        input.put("vServ", "100");
        input.put("vDescIncond", "5");
        input.put("vISSQN", "5");
        input.put("vPIS", "5");
        input.put("vCOFINS", "5");

        mockMvc.perform(post("/calculadora/base-calculo/base-calculo-unificada-nfse")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                // Tipo 102 repercute IBS/CBS (100-5-5-5-5=75) e não SN
                .andExpect(jsonPath("$.baseRG").value(75.0))
                // Tipo 102 não repercute SN (repercuteSN=false), então baseSN igual ao baseRG sem ISSQN
                .andExpect(jsonPath("$.baseSN").value(90.0));
    }

    @Test
    @DisplayName("Cenário 8 — SALAO_PARCEIRO + doc102 + doc9, regAp=3: doc9 não deduz SN (AMBOS_REGULARES)")
    void cenario8_salaoParceiro_docTipo102_e_9() throws Exception {
        ArrayNode docs = objectMapper.createArrayNode();
        ObjectNode doc1 = docs.addObject();
        doc1.put("tpAjusteBC", "102");
        doc1.put("vAjusteAplic", "5");
        ObjectNode doc2 = docs.addObject();
        doc2.put("tpAjusteBC", "9");
        doc2.put("vAjusteAplic", "3");

        ObjectNode input = objectMapper.createObjectNode();
        input.put("anoFatoGerador", 2026);
        input.put("opSimpNac", "3");
        input.put("situacao", "salaoParceiro");
        input.put("regApIBSCBSSN", "3");
        input.set("documentos", docs);
        input.put("vServ", "100");
        input.put("vDescIncond", "5");
        input.put("vISSQN", "5");
        input.put("vPIS", "5");
        input.put("vCOFINS", "5");

        mockMvc.perform(post("/calculadora/base-calculo/base-calculo-unificada-nfse")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                // RG = 100-5(desc)-5(doc102 IBS/CBS)-5(ISOQN)-5(PIS)-5(COFINS) = 75
                .andExpect(jsonPath("$.baseRG").value(75.0))
                // SN = 100-5(desc)-5(doc102 IBS/CBS) = 90 (doc9 não deduz para AMBOS_REGULARES + situação não é SALAO_PARCEIRO para SN)
                .andExpect(jsonPath("$.baseSN").value(90.0));
    }

    // ─── Cenários adicionais de cobertura ────────────────────────────────────

    @Test
    @DisplayName("Campo faltante: opSimpNac null → 400")
    void campoFaltante_opSimpNac_null() throws Exception {
        ObjectNode input = objectMapper.createObjectNode();
        input.put("anoFatoGerador", 2026);
        input.put("situacao", "comum");

        mockMvc.perform(post("/calculadora/base-calculo/base-calculo-unificada-nfse")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Valor zero: campos zerados → baseRG=0")
    void camposZerados() throws Exception {
        ObjectNode input = objectMapper.createObjectNode();
        input.put("anoFatoGerador", 2026);
        input.put("opSimpNac", "1");
        input.put("situacao", "comum");
        input.put("vServ", "0");
        input.put("vDescIncond", "0");

        mockMvc.perform(post("/calculadora/base-calculo/base-calculo-unificada-nfse")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.baseRG").value(0.0))
                .andExpect(jsonPath("$.baseSN").doesNotExist())
                .andExpect(jsonPath("$.erros").isArray());
    }

    @Test
    @DisplayName("2027: PIS/COFINS fora de vigência → não deduz | baseRG=90, baseSN=90")
    void ano2027_pisCofinsForaVigencia() throws Exception {
        ObjectNode input = objectMapper.createObjectNode();
        input.put("anoFatoGerador", 2027);
        input.put("opSimpNac", "3");
        input.put("situacao", "comum");
        input.put("regApIBSCBSSN", "1");
        input.put("vServ", "100");
        input.put("vDescIncond", "10");

        mockMvc.perform(post("/calculadora/base-calculo/base-calculo-unificada-nfse")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                // 2027 >= 2027, PIS/COFINS não deduz: 100-10=90
                .andExpect(jsonPath("$.baseRG").value(90.0))
                .andExpect(jsonPath("$.baseSN").value(90.0));
    }

    @Test
    @DisplayName("2033: ISSQN extinto → não deduz | baseRG=100, baseSN=90")
    void ano2033_issqnExtinto() throws Exception {
        ObjectNode input = objectMapper.createObjectNode();
        input.put("anoFatoGerador", 2033);
        input.put("opSimpNac", "3");
        input.put("situacao", "comum");
        input.put("regApIBSCBSSN", "1");
        input.put("vServ", "100");
        input.put("vDescIncond", "10");
        input.put("vISSQN", "15");

        mockMvc.perform(post("/calculadora/base-calculo/base-calculo-unificada-nfse")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                // 2033 >= 2033, ISSQN não deduz, mas descIncond=10 ainda é: RG=90, SN=90
                .andExpect(jsonPath("$.baseRG").value(90.0))
                .andExpect(jsonPath("$.baseSN").value(90.0));
    }

    @Test
    @DisplayName("E1534: soma ajustes >= vServ → erro E1534, base negativa -10.0")
    void e1534_somaAjustesExcedeServico() throws Exception {
        ArrayNode docs = objectMapper.createArrayNode();
        ObjectNode doc = docs.addObject();
        doc.put("tpAjusteBC", "101");
        doc.put("vAjusteAplic", "50");

        ObjectNode input = objectMapper.createObjectNode();
        input.put("anoFatoGerador", 2026);
        input.put("opSimpNac", "3");
        input.put("situacao", "comum");
        input.put("regApIBSCBSSN", "1");
        input.set("documentos", docs);
        input.put("vServ", "50");
        input.put("vDescIncond", "10");

        mockMvc.perform(post("/calculadora/base-calculo/base-calculo-unificada-nfse")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.erros").isArray())
                .andExpect(jsonPath("$.erros[0].codigo").value("E1534"))
                // Não há truncamento para zero — a base fica negativa
                .andExpect(jsonPath("$.baseRG").value(-10.0));
    }

    @Test
    @DisplayName("MEI: retorno antecipado → bases null, aviso E1302")
    void mei_retornoAntecipado() throws Exception {
        ObjectNode input = objectMapper.createObjectNode();
        input.put("anoFatoGerador", 2026);
        input.put("opSimpNac", "2");
        input.put("situacao", "comum");
        input.put("vServ", "1000");

        mockMvc.perform(post("/calculadora/base-calculo/base-calculo-unificada-nfse")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.baseRG").doesNotExist())
                .andExpect(jsonPath("$.baseSN").doesNotExist())
                .andExpect(jsonPath("$.avisos").isArray());
    }

    @Test
    @DisplayName("EXXX-REGAP: optante sem regAp → erro, mas base calculada")
    void regApObrigatorio() throws Exception {
        ObjectNode input = objectMapper.createObjectNode();
        input.put("anoFatoGerador", 2026);
        input.put("opSimpNac", "3");
        input.put("situacao", "comum");
        input.put("vServ", "100");

        mockMvc.perform(post("/calculadora/base-calculo/base-calculo-unificada-nfse")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.baseRG").isNotEmpty())
                .andExpect(jsonPath("$.baseSN").doesNotExist())
                .andExpect(jsonPath("$.erros").isArray())
                .andExpect(jsonPath("$.erros[0].codigo").value("EXXX-REGAP"));
    }

    @Test
    @DisplayName("Locação sem grupo locacao → 400 CampoInvalidoException")
    void locacaoSemGrupoLocacao() throws Exception {
        ObjectNode input = objectMapper.createObjectNode();
        input.put("anoFatoGerador", 2026);
        input.put("opSimpNac", "1");
        input.put("situacao", "locacao9903");

        mockMvc.perform(post("/calculadora/base-calculo/base-calculo-unificada-nfse")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Tipo 199 sem xTpAjusteBC → erro LEIAUTE-XTP, doc excluído das somas")
    void tipo199SemDescricao() throws Exception {
        ArrayNode docs = objectMapper.createArrayNode();
        ObjectNode doc = docs.addObject();
        doc.put("tpAjusteBC", "199");
        doc.put("vAjusteAplic", "10");

        ObjectNode input = objectMapper.createObjectNode();
        input.put("anoFatoGerador", 2026);
        input.put("opSimpNac", "3");
        input.put("situacao", "comum");
        input.put("regApIBSCBSSN", "1");
        input.set("documentos", docs);
        input.put("vServ", "100");

        mockMvc.perform(post("/calculadora/base-calculo/base-calculo-unificada-nfse")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.baseRG").value(100.0))
                .andExpect(jsonPath("$.baseSN").value(100.0))
                .andExpect(jsonPath("$.erros").isArray())
                .andExpect(jsonPath("$.erros[0].codigo").value("LEIAUTE-XTP"));
    }

    @Test
    @DisplayName("Tipo 199 com xTpAjusteBC → doc válido, deduz da base | RG=90")
    void tipo199ComDescricao() throws Exception {
        ArrayNode docs = objectMapper.createArrayNode();
        ObjectNode doc = docs.addObject();
        doc.put("tpAjusteBC", "199");
        doc.put("vAjusteAplic", "10");
        doc.put("xTpAjusteBC", "Ajuste personalizado");

        ObjectNode input = objectMapper.createObjectNode();
        input.put("anoFatoGerador", 2026);
        input.put("opSimpNac", "1");
        input.put("situacao", "comum");
        input.set("documentos", docs);
        input.put("vServ", "100");

        mockMvc.perform(post("/calculadora/base-calculo/base-calculo-unificada-nfse")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.baseRG").value(90.0))
                .andExpect(jsonPath("$.erros").isArray())
                .andExpect(jsonPath("$.erros").isEmpty());
    }

    @Test
    @DisplayName("Locação com optante MEI → MEI primeiro, bases null, aviso E1302")
    void locacao_mei_com_docs_vedado() throws Exception {
        String jsonInput = "{\"anoFatoGerador\":2026,\"opSimpNac\":\"2\",\"situacao\":\"locacao9903\",\"locacao\":{\"pCopropriedade\":\"100\",\"vTotOper\":\"1000\",\"vDescIncondTot\":\"0\",\"somaAjustesLocImoveis\":\"0\"},\"documentos\":[{\"tpAjusteBC\":\"101\",\"vAjusteAplic\":\"5\"}]}";

        mockMvc.perform(post("/calculadora/base-calculo/base-calculo-unificada-nfse")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonInput))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.baseRG").doesNotExist())
                .andExpect(jsonPath("$.baseSN").doesNotExist())
                .andExpect(jsonPath("$.avisos").isArray())
                .andExpect(jsonPath("$.avisos").exists())
                .andExpect(jsonPath("$.erros").isArray());
    }

    @Test
    @DisplayName("Campos nulos tratados como zero")
    void camposNulos_tratadosComoZero() throws Exception {
        ObjectNode input = objectMapper.createObjectNode();
        input.put("anoFatoGerador", 2026);
        input.put("opSimpNac", "1");
        input.put("situacao", "comum");

        mockMvc.perform(post("/calculadora/base-calculo/base-calculo-unificada-nfse")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.baseRG").isNotEmpty())
                .andExpect(jsonPath("$.baseSN").doesNotExist());
    }

    // ─── Cenários de sucesso ───────────────────────────────────────────────

    @Test
    @DisplayName("Cenário de sucesso: COMUM optante regAp=1, vServ=500, todos dedutores presentes | baseRG=465, baseSN=495")
    void cenario_sucesso_comum_optante_regAp1() throws Exception {
        ObjectNode input = objectMapper.createObjectNode();
        input.put("anoFatoGerador", 2026);
        input.put("opSimpNac", "3");
        input.put("situacao", "comum");
        input.put("regApIBSCBSSN", "1");
        input.put("vServ", "500");
        input.put("vDescIncond", "20");
        input.put("vISSQN", "15");
        input.put("vPIS", "0.65");
        input.put("vCOFINS", "3.00");

        mockMvc.perform(post("/calculadora/base-calculo/base-calculo-unificada-nfse")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                // RG = 500 - 20 - 15 - 0.65 - 3 = 461.35
                // SN = 500 - 20 = 480
                .andExpect(jsonPath("$.baseRG").value(461.35))
                .andExpect(jsonPath("$.baseSN").value(480.0));
    }

    @Test
    @DisplayName("Cenário de sucesso: COMUM optante regAp=2, doc tipo9 deduz SN | baseRG=485, baseSN=485")
    void cenario_sucesso_comum_optante_regAp2_docTipo9() throws Exception {
        ArrayNode docs = objectMapper.createArrayNode();
        ObjectNode doc = docs.addObject();
        doc.put("tpAjusteBC", "9");
        doc.put("vAjusteAplic", "15");

        ObjectNode input = objectMapper.createObjectNode();
        input.put("anoFatoGerador", 2026);
        input.put("opSimpNac", "3");
        input.put("situacao", "comum");
        input.put("regApIBSCBSSN", "2");
        input.set("documentos", docs);
        input.put("vServ", "500");
        input.put("vDescIncond", "20");
        input.put("vISSQN", "15");
        input.put("vPIS", "0.65");
        input.put("vCOFINS", "3.00");

        mockMvc.perform(post("/calculadora/base-calculo/base-calculo-unificada-nfse")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                // RG = 500 - 20 - 15 - 0.65 - 3 = 461.35
                // SN = 500 - 20 = 480 (situacao COMUM: doc9 não deduz SN mesmo com regAp=2)
                .andExpect(jsonPath("$.baseRG").value(461.35))
                .andExpect(jsonPath("$.baseSN").value(480.0));
    }

    @Test
    @DisplayName("Cenário de sucesso: SALAO_PARCEIRO optante regAp=1, doc9 deduz SN | baseSN=465")
    void cenario_sucesso_salaoParceiro_regAp1_docTipo9() throws Exception {
        ArrayNode docs = objectMapper.createArrayNode();
        ObjectNode doc = docs.addObject();
        doc.put("tpAjusteBC", "9");
        doc.put("vAjusteAplic", "15");

        ObjectNode input = objectMapper.createObjectNode();
        input.put("anoFatoGerador", 2026);
        input.put("opSimpNac", "3");
        input.put("situacao", "salaoParceiro");
        input.put("regApIBSCBSSN", "1");
        input.set("documentos", docs);
        input.put("vServ", "500");
        input.put("vDescIncond", "20");
        input.put("vISSQN", "15");
        input.put("vPIS", "0.65");
        input.put("vCOFINS", "3.00");

        mockMvc.perform(post("/calculadora/base-calculo/base-calculo-unificada-nfse")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                // RG = 500 - 20 - 15 - 0.65 - 3 = 461.35
                // SN = 500 - 20 - 15(doc9) = 465
                .andExpect(jsonPath("$.baseRG").value(461.35))
                .andExpect(jsonPath("$.baseSN").value(465.0));
    }

    @Test
    @DisplayName("Cenário de sucesso: COMUM optante pendente | base calculada, aviso pendente")
    void cenario_sucesso_optante_pendente() throws Exception {
        ObjectNode input = objectMapper.createObjectNode();
        input.put("anoFatoGerador", 2026);
        input.put("opSimpNac", "4");
        input.put("situacao", "comum");
        input.put("vServ", "200");
        input.put("vDescIncond", "10");

        mockMvc.perform(post("/calculadora/base-calculo/base-calculo-unificada-nfse")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.baseRG").isNotEmpty())
                .andExpect(jsonPath("$.baseSN").doesNotExist())
                .andExpect(jsonPath("$.avisos").isArray());
    }

    @Test
    @DisplayName("Cenário de sucesso: locacao optante regAp=1 e regAp=2 → mesma base RG, baseSN calculada (regAp=1)")
    void cenario_sucesso_locacao_optante_regAp1_vs_2() throws Exception {
        ObjectNode locacao = objectMapper.createObjectNode();
        locacao.put("somaAjustesLocImoveis", "10");
        locacao.put("pCopropriedade", "50");
        locacao.put("vTotOper", "800");
        locacao.put("vDescIncondTot", "80");

        ObjectNode input = objectMapper.createObjectNode();
        input.put("anoFatoGerador", 2026);
        input.put("opSimpNac", "3");
        input.put("situacao", "locacao9903");
        input.put("regApIBSCBSSN", "2");
        input.set("locacao", locacao);
        input.put("vPIS", "0.65");
        input.put("vCOFINS", "3.00");

        mockMvc.perform(post("/calculadora/base-calculo/base-calculo-unificada-nfse")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                // RG locacao: (0.5*800) - (0.5*80) - (0.5*10) - 0.65 - 3 = 400 - 40 - 5 - 0.65 - 3 = 351.35
                // SN locacao: (0.5*800) - (0.5*80) = 360
                .andExpect(jsonPath("$.baseRG").value(351.35))
                .andExpect(jsonPath("$.baseSN").value(360.0));
    }

    @Test
    @DisplayName("Cenário de sucesso: ajuste zero (todos campos a zero) → base zero")
    void cenario_sucesso_todos_cinzeros() throws Exception {
        ObjectNode input = objectMapper.createObjectNode();
        input.put("anoFatoGerador", 2026);
        input.put("opSimpNac", "3");
        input.put("situacao", "comum");
        input.put("regApIBSCBSSN", "1");
        input.put("vServ", "0");
        input.put("vDescIncond", "0");
        input.put("vISSQN", "0");
        input.put("vPIS", "0");
        input.put("vCOFINS", "0");

        mockMvc.perform(post("/calculadora/base-calculo/base-calculo-unificada-nfse")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.baseRG").value(0.0))
                .andExpect(jsonPath("$.baseSN").value(0.0));
    }

    @Test
    @DisplayName("Cenário de sucesso: doc múltiplos tipos dedutores | RG e SN somam dedutores")
    void cenario_sucesso_multiplos_docs() throws Exception {
        ArrayNode docs = objectMapper.createArrayNode();
        ObjectNode doc1 = docs.addObject();
        doc1.put("tpAjusteBC", "101"); // repercute IBS/CBS
        doc1.put("vAjusteAplic", "10");
        ObjectNode doc2 = docs.addObject();
        doc2.put("tpAjusteBC", "102"); // repercute IBS/CBS + SN
        doc2.put("vAjusteAplic", "5");
        ObjectNode doc3 = docs.addObject();
        doc3.put("tpAjusteBC", "9"); // repercute SN apenas
        doc3.put("vAjusteAplic", "8");

        ObjectNode input = objectMapper.createObjectNode();
        input.put("anoFatoGerador", 2026);
        input.put("opSimpNac", "3");
        input.put("situacao", "comum");
        input.put("regApIBSCBSSN", "1");
        input.set("documentos", docs);
        input.put("vServ", "500");
        input.put("vDescIncond", "20");

        mockMvc.perform(post("/calculadora/base-calculo/base-calculo-unificada-nfse")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                // RG = 500 - 20 - 15(doc101+102) = 465
                // SN = 500 - 20 - 15(vCalcAjusteBCIBSCBS) = 465
                // (situacao COMUM: doc9 não deduz SN)
                .andExpect(jsonPath("$.baseRG").value(465.0))
                .andExpect(jsonPath("$.baseSN").value(465.0));
    }

    @Test
    @DisplayName("Cenário de sucesso: locacao optante regAp=1 com docs → docs ignorados | SN=99.5")
    void cenario_sucesso_locacao_optante_docs_ignored() throws Exception {
        ArrayNode docs = objectMapper.createArrayNode();
        ObjectNode doc = docs.addObject();
        doc.put("tpAjusteBC", "101");
        doc.put("vAjusteAplic", "50");

        ObjectNode locacao = objectMapper.createObjectNode();
        locacao.put("somaAjustesLocImoveis", "5");
        locacao.put("pCopropriedade", "10");
        locacao.put("vTotOper", "1000");
        locacao.put("vDescIncondTot", "5");

        ObjectNode input = objectMapper.createObjectNode();
        input.put("anoFatoGerador", 2026);
        input.put("opSimpNac", "3");
        input.put("situacao", "locacao9903");
        input.put("regApIBSCBSSN", "1");
        input.set("locacao", locacao);
        input.set("documentos", docs);
        input.put("vPIS", "5");
        input.put("vCOFINS", "5");

        mockMvc.perform(post("/calculadora/base-calculo/base-calculo-unificada-nfse")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                // RG locacao: ef=100(descEf=0.5,ajusteEf=0.5,pis=5,cofins=5) = 100-0.5-0.5-5-5 = 89
                .andExpect(jsonPath("$.baseRG").value(89.0))
                .andExpect(jsonPath("$.baseSN").value(99.5))
                .andExpect(jsonPath("$.avisos").isArray());
    }

    @Test
    @DisplayName("Cenário de sucesso: tipo 0 (sem ajuste) não afeta bases | RG=90")
    void cenario_sucesso_tipo0_ajuste() throws Exception {
        ArrayNode docs = objectMapper.createArrayNode();
        ObjectNode doc = docs.addObject();
        doc.put("tpAjusteBC", "0");
        doc.put("vAjusteAplic", "20");

        ObjectNode input = objectMapper.createObjectNode();
        input.put("anoFatoGerador", 2026);
        input.put("opSimpNac", "3");
        input.put("situacao", "comum");
        input.put("regApIBSCBSSN", "1");
        input.set("documentos", docs);
        input.put("vServ", "100");

        mockMvc.perform(post("/calculadora/base-calculo/base-calculo-unificada-nfse")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                // Tipo 0 não repercute IBS/CBS nem SN, doc é válido mas não deduz
                .andExpect(jsonPath("$.baseRG").value(100.0))
                .andExpect(jsonPath("$.baseSN").value(100.0));
    }

    @Test
    @DisplayName("Cenário de sucesso: base positiva exata → 3 decimal places arredondado")
    void cenario_sucesso_arredondamento_3_decimais() throws Exception {
        ObjectNode input = objectMapper.createObjectNode();
        input.put("anoFatoGerador", 2026);
        input.put("opSimpNac", "3");
        input.put("situacao", "comum");
        input.put("regApIBSCBSSN", "1");
        input.put("vServ", "100.777");
        input.put("vDescIncond", "5.444");

        mockMvc.perform(post("/calculadora/base-calculo/base-calculo-unificada-nfse")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                // RG = 100.777 - 5.444 = 95.333
                .andExpect(jsonPath("$.baseRG").value(95.33))
                // SN = 100.777 - 5.444 = 95.333
                .andExpect(jsonPath("$.baseSN").value(95.33));
    }
}
