package br.gov.serpro.rtc.testesunitarios.collector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import br.gov.serpro.rtc.api.exceptionhandler.dto.ErroDetalhe;
import br.gov.serpro.rtc.domain.service.collector.GlobalCollector;
import br.gov.serpro.rtc.domain.service.collector.ItemCollector;

class Teste_GlobalCollector {

    @Test
    void novoCollectorNaoTemErros() {
        var collector = new GlobalCollector();

        assertThat(collector.hasErrosGlobais()).isFalse();
        assertThat(collector.getErrosGlobais()).isEmpty();
        assertThat(collector.getItemCollectors()).isEmpty();
    }

    @Test
    void addErroGlobalMarcaHasErrosTrue() {
        var collector = new GlobalCollector();

        collector.addErroGlobal("type", "title", "code", "#/campo", "detail");

        assertThat(collector.hasErrosGlobais()).isTrue();
        assertThat(collector.getErrosGlobais()).hasSize(1);
        ErroDetalhe erro = collector.getErrosGlobais().get(0);
        assertThat(erro.type()).isEqualTo("type");
        assertThat(erro.title()).isEqualTo("title");
        assertThat(erro.code()).isEqualTo("code");
        assertThat(erro.pointer()).isEqualTo("#/campo");
        assertThat(erro.detail()).isEqualTo("detail");
    }

    @Test
    void getErrosGlobaisRetornaListaImutavel() {
        var collector = new GlobalCollector();

        collector.addErroGlobal("t", "t", "c", "#", "d");

        assertThatThrownBy(() -> collector.getErrosGlobais().add(new ErroDetalhe("a", "b", "c", "d", "e")))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void addItemCollectorFunciona() {
        var collector = new GlobalCollector();
        var item = new ItemCollector(0, "http://localhost");

        collector.addItemCollector(item);

        assertThat(collector.getItemCollectors()).hasSize(1);
    }

    @Test
    void addMultiplosItemCollectors() {
        var collector = new GlobalCollector();

        collector.addItemCollector(new ItemCollector(0, "http://localhost"));
        collector.addItemCollector(new ItemCollector(1, "http://localhost"));
        collector.addItemCollector(new ItemCollector(2, "http://localhost"));

        assertThat(collector.getItemCollectors()).hasSize(3);
    }

    @Test
    void getItemCollectorsRetornaListaImutavel() {
        var collector = new GlobalCollector();

        assertThatThrownBy(() -> collector.getItemCollectors().add(new ItemCollector(0, "")))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void multiplosErrosGlobais() {
        var collector = new GlobalCollector();

        collector.addErroGlobal("t1", "title1", "c1", "#/1", "d1");
        collector.addErroGlobal("t2", "title2", "c2", "#/2", "d2");
        collector.addErroGlobal("t3", "title3", "c3", "#/3", "d3");

        assertThat(collector.getErrosGlobais()).hasSize(3);
    }

    @Test
    void errosGlobaisPreservamOrdem() {
        var collector = new GlobalCollector();

        collector.addErroGlobal("t1", "title1", "CODE-1", "#/1", "d1");
        collector.addErroGlobal("t2", "title2", "CODE-2", "#/2", "d2");

        assertThat(collector.getErrosGlobais())
                .extracting(ErroDetalhe::code)
                .containsExactly("CODE-1", "CODE-2");
    }

    @Test
    void collectorComItensEErrosGlobaisSimultaneos() {
        var collector = new GlobalCollector();

        collector.addErroGlobal("t", "title", "c", "#", "d");
        collector.addItemCollector(new ItemCollector(0, "http://localhost"));

        assertThat(collector.hasErrosGlobais()).isTrue();
        assertThat(collector.getItemCollectors()).hasSize(1);
    }
}
