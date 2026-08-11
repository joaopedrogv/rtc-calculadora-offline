/*
* Versão de Homologação/Testes
*
* Teste de regressão: compara a saída completa da calculadora com um gabarito
* previamente validado. Para a política de atualização do gabarito, consulte
* o documento POLITICA_GABARITO.md na raiz do projeto.
*/
package br.gov.serpro.rtc.testesintegracao.calculoscorretos.regressao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import br.gov.serpro.rtc.api.model.input.OperacaoInput;
import br.gov.serpro.rtc.domain.service.ObservabilidadeService;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-testes.yml")
@ActiveProfiles("testes")
@Tag("regressao")
@DisplayName("Teste de Regressão – Vários Itens")
class TesteRegressaoVariosItensComObservabilidade {

    /**
     * Campos que devem ser ignorados na comparação entre o resultado obtido e o
     * gabarito. Esses campos contêm informações voláteis (texto descritivo, ordem
     * de geração etc.) que podem variar sem representar erro de cálculo.
     *
     * Para adicionar um novo campo, basta incluí-lo neste array.
     */
    private static final String[] CAMPOS_EXCLUIDOS = {
            "memoriaCalculo",
            "estadoItem",
    };

    private static final Set<String> CAMPOS_EXCLUIDOS_SET = new HashSet<>(Arrays.asList(CAMPOS_EXCLUIDOS));

    private static final int MAX_DIFERENCAS_EXIBIDAS = 50;

    @Autowired
    private ObservabilidadeService observabilidadeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Saída da calculadora deve corresponder ao gabarito (campos excluídos: memoriaCalculo)")
    void testRegressaoCalculoVariosItens(
            final @Value("classpath:entradas/calculoscorretos/varios_itens/Teste_Varios_Itens.json") Resource inputFile,
            final @Value("classpath:saidas/Teste_Varios_Itens.json") Resource gabaritoFile)
            throws Exception {

    	final var operacao = objectMapper.readValue(inputFile.getInputStream(), OperacaoInput.class);

        final var resultado = observabilidadeService.processarOperacao(operacao, "http://localhost:8080");
        assertThat(resultado).as("Resultado da calculadora não pode ser nulo").isNotNull();

        final JsonNode obtido = objectMapper.valueToTree(resultado);
        final JsonNode esperado = carregarGabarito(gabaritoFile);

        removerCamposRecursivamente(obtido);
        removerCamposRecursivamente(esperado);

        final List<String> diferencas = compararJson("$", esperado, obtido);

        if (!diferencas.isEmpty()) {
            final var mensagem = new StringBuilder();
            mensagem.append(String.format(
                    "Teste de regressão FALHOU: %d diferença(s) encontrada(s) entre o resultado e o gabarito.%n",
                    diferencas.size()));
            mensagem.append(String.format("Campos excluídos da comparação: %s%n%n", Arrays.toString(CAMPOS_EXCLUIDOS)));

            final int exibir = Math.min(diferencas.size(), MAX_DIFERENCAS_EXIBIDAS);
            for (int i = 0; i < exibir; i++) {
                mensagem.append(String.format("  %d) %s%n", i + 1, diferencas.get(i)));
            }
            if (diferencas.size() > MAX_DIFERENCAS_EXIBIDAS) {
                mensagem.append(String.format("%n  ... e mais %d diferença(s) omitida(s).%n",
                        diferencas.size() - MAX_DIFERENCAS_EXIBIDAS));
            }

            mensagem.append(String.format(
                    "%nSe as diferenças são esperadas, atualize o gabarito conforme POLITICA_GABARITO.md.%n"));

            fail(mensagem.toString());
        }
    }

    // -------------------------------------------------------------------------
    // Métodos auxiliares
    // -------------------------------------------------------------------------

    private JsonNode carregarGabarito(final Resource gabaritoFile) throws IOException {
        return objectMapper.readTree(gabaritoFile.getInputStream());
    }

    /**
     * Remove recursivamente todos os campos cujos nomes estão em
     * {@link #CAMPOS_EXCLUIDOS_SET} de qualquer nível da árvore JSON.
     */
    private void removerCamposRecursivamente(final JsonNode node) {
        if (node == null) {
            return;
        }
        if (node.isObject()) {
            final ObjectNode obj = (ObjectNode) node;
            obj.remove(new ArrayList<>(CAMPOS_EXCLUIDOS_SET));
            obj.properties().forEach(entry -> removerCamposRecursivamente(entry.getValue()));
        } else if (node.isArray()) {
            for (final JsonNode elemento : node) {
                removerCamposRecursivamente(elemento);
            }
        }
    }

    /**
     * Compara duas árvores JSON recursivamente e retorna uma lista legível de
     * diferenças, identificadas pelo caminho JSON (ex.: {@code $.objetos[0].tribCalc.IBSCBS.gIBSCBS.vBC}).
     */
    private List<String> compararJson(final String caminho, final JsonNode esperado, final JsonNode obtido) {
        final List<String> diferencas = new ArrayList<>();

        if (esperado == null && obtido == null) {
            return diferencas;
        }
        if (esperado == null) {
            diferencas.add(String.format("[%s] Campo inesperado no resultado. Valor: %s", caminho, resumir(obtido)));
            return diferencas;
        }
        if (obtido == null) {
            diferencas.add(String.format("[%s] Campo ausente no resultado. Esperado: %s", caminho, resumir(esperado)));
            return diferencas;
        }

        if (esperado.getNodeType() != obtido.getNodeType()) {
            diferencas.add(String.format("[%s] Tipo diferente. Esperado: %s (%s), Obtido: %s (%s)",
                    caminho, resumir(esperado), esperado.getNodeType(),
                    resumir(obtido), obtido.getNodeType()));
            return diferencas;
        }

        if (esperado.isObject()) {
            final Set<String> todosCampos = new HashSet<>();
            esperado.fieldNames().forEachRemaining(todosCampos::add);
            obtido.fieldNames().forEachRemaining(todosCampos::add);

            for (final String campo : todosCampos) {
                diferencas.addAll(
                        compararJson(caminho + "." + campo, esperado.get(campo), obtido.get(campo)));
            }

        } else if (esperado.isArray()) {
            if (esperado.size() != obtido.size()) {
                diferencas.add(String.format(
                        "[%s] Tamanho do array diferente. Esperado: %d elemento(s), Obtido: %d elemento(s)",
                        caminho, esperado.size(), obtido.size()));
            }
            final int limiteComparacao = Math.min(esperado.size(), obtido.size());
            for (int i = 0; i < limiteComparacao; i++) {
                diferencas.addAll(
                        compararJson(caminho + "[" + i + "]", esperado.get(i), obtido.get(i)));
            }

        } else {
            if (!esperado.equals(obtido)) {
                diferencas.add(String.format("[%s] Valor diferente. Esperado: %s, Obtido: %s",
                        caminho, esperado.asText(), obtido.asText()));
            }
        }

        return diferencas;
    }

    /**
     * Retorna uma representação resumida de um JsonNode para mensagens de erro.
     */
    private String resumir(final JsonNode node) {
        if (node == null) {
            return "null";
        }
        final String texto = node.toString();
        if (texto.length() <= 80) {
            return texto;
        }
        return texto.substring(0, 77) + "...";
    }
}
