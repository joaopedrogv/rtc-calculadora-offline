package br.gov.serpro.rtc.domain.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import br.gov.serpro.rtc.api.model.input.ItemOperacaoInput;
import br.gov.serpro.rtc.api.model.input.ImpostoSeletivoInput;
import br.gov.serpro.rtc.domain.service.calculotributo.model.AliquotaImpostoSeletivoModel;
import br.gov.serpro.rtc.domain.service.exception.ErroGenericoValidacaoException;

class ProcessamentoItemServiceTest {

    private static ProcessamentoItemService processamentoItemService;

    @BeforeAll
    static void setUp() {
        // No momento não estamos testando dependências, então podem ser nulas
        processamentoItemService = new ProcessamentoItemService(null, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    @Test
    void deveLancarErroQuandoQuantidadeNaoInformada() {
        ItemOperacaoInput item = new ItemOperacaoInput();
        // Para o item (monofasia):
        // item.setQuantidade(null);
        // item.setUnidade("UN");

        ImpostoSeletivoInput impostoSeletivo = new ImpostoSeletivoInput();
        impostoSeletivo.setUnidade("UN");

        item.setImpostoSeletivo(impostoSeletivo);

        AliquotaImpostoSeletivoModel aliquota = AliquotaImpostoSeletivoModel.builder()
                .aliquotaAdRem(BigDecimal.ONE)
                .unidadeMedida("UN")
                .build();

        assertThatThrownBy(() -> processamentoItemService.validarQuantidadeEUnidade(item, aliquota))
                .isInstanceOf(ErroGenericoValidacaoException.class)
                .hasMessageContaining(
                        "A quantidade do Imposto Seletivo deve ser informada para alíquota ad rem");
    }

    @Test
    void deveLancarErroQuandoQuantidadeIgualAZero() {
        ItemOperacaoInput item = new ItemOperacaoInput();
        item.setQuantidade(BigDecimal.ZERO);
        item.setUnidade("UN");

        AliquotaImpostoSeletivoModel aliquota = AliquotaImpostoSeletivoModel.builder()
                .aliquotaAdRem(BigDecimal.ONE)
                .unidadeMedida("UN")
                .build();

        assertThatThrownBy(() -> processamentoItemService.validarQuantidadeEUnidade(item, aliquota))
                .isInstanceOf(ErroGenericoValidacaoException.class)
                .hasMessageContaining("A quantidade do item deve ser maior do que zero");
    }

    @Test
    void deveLancarErroQuandoQuantidadeMenorQueZero() {
        ItemOperacaoInput item = new ItemOperacaoInput();
        item.setQuantidade(new BigDecimal("-1"));
        item.setUnidade("UN");

        AliquotaImpostoSeletivoModel aliquota = AliquotaImpostoSeletivoModel.builder()
                .aliquotaAdRem(BigDecimal.ONE)
                .unidadeMedida("UN")
                .build();

        assertThatThrownBy(() -> processamentoItemService.validarQuantidadeEUnidade(item, aliquota))
                .isInstanceOf(ErroGenericoValidacaoException.class)
                .hasMessageContaining("A quantidade do item deve ser maior do que zero");
    }

    @Test
    void deveLancarErroQuandoUnidadeNaoInformada() {
        ItemOperacaoInput item = new ItemOperacaoInput();
        item.setQuantidade(BigDecimal.ONE);
        // Para o item (monofasia):
        // item.setUnidade(null);

        ImpostoSeletivoInput impostoSeletivo = new ImpostoSeletivoInput();
        impostoSeletivo.setQuantidade(BigDecimal.ONE);

        item.setImpostoSeletivo(impostoSeletivo);

        AliquotaImpostoSeletivoModel aliquota = AliquotaImpostoSeletivoModel.builder()
                .aliquotaAdRem(BigDecimal.ONE)

                // .unidadeMedida("UN")
                .build();

        assertThatThrownBy(() -> processamentoItemService.validarQuantidadeEUnidade(item, aliquota))
                .isInstanceOf(ErroGenericoValidacaoException.class)
                .hasMessageContaining(
                        "A unidade de medida do Imposto Seletivo deve ser informada para alíquota ad rem");
    }

    @Test
    void deveLancarErroQuandoUnidadeDiferenteDaAliquota() {
        ItemOperacaoInput item = new ItemOperacaoInput();
        // Para o item (monofasia):
        // item.setQuantidade(BigDecimal.ONE);
        // item.setUnidade("KG");

        ImpostoSeletivoInput impostoSeletivo = new ImpostoSeletivoInput();
        impostoSeletivo.setQuantidade(BigDecimal.ONE);
        impostoSeletivo.setUnidade("KG");

        item.setImpostoSeletivo(impostoSeletivo);

        AliquotaImpostoSeletivoModel aliquota = AliquotaImpostoSeletivoModel.builder()
                .aliquotaAdRem(BigDecimal.ONE)
                .unidadeMedida("UN")
                .build();

        assertThatThrownBy(() -> processamentoItemService.validarQuantidadeEUnidade(item, aliquota))
                .isInstanceOf(ErroGenericoValidacaoException.class)
                .hasMessageContaining("é diferente da unidade de medida da alíquota");
    }

    @Test
    void naoDeveLancarErroQuandoNaoHouverAliquotaAdRem() {
        ItemOperacaoInput item = new ItemOperacaoInput();
        AliquotaImpostoSeletivoModel aliquota = AliquotaImpostoSeletivoModel.builder()
                .build();

        // assertThatCode garante que nenhuma exceção é lançada
        assertThatCode(() -> processamentoItemService.validarQuantidadeEUnidade(item, aliquota))
                .doesNotThrowAnyException();
    }

    @Test
    void naoDeveLancarErroQuandoTudoValido() {
        ItemOperacaoInput item = new ItemOperacaoInput();
        item.setQuantidade(BigDecimal.TEN);
        item.setUnidade("UN");

        AliquotaImpostoSeletivoModel aliquota = AliquotaImpostoSeletivoModel.builder()
                .aliquotaAdRem(BigDecimal.ONE)
                .unidadeMedida("UN")
                .build();

        // assertThatCode garante que nenhuma exceção é lançada
        assertThatCode(() -> processamentoItemService.validarQuantidadeEUnidade(item, aliquota))
                .doesNotThrowAnyException();
    }

}