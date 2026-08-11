package br.gov.serpro.rtc.domain.service.collector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import br.gov.serpro.rtc.api.exceptionhandler.dto.ErroDetalhe;

/**
 * Classe responsável por acumular erros globais e os coletores de itens usados
 * na resposta de observabilidade.
 */
public class GlobalCollector {

    private final ConcurrentLinkedQueue<ErroDetalhe> errosGlobais = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<ItemCollector> itemCollectors = new ConcurrentLinkedQueue<>();

    public void addErroGlobal(String type, String title, String code, String pointer, String detail) {
        errosGlobais.add(new ErroDetalhe(type, title, code, pointer, detail));
    }

    public void addItemCollector(ItemCollector collector) {
        itemCollectors.add(collector);
    }

    public List<ErroDetalhe> getErrosGlobais() {
        return Collections.unmodifiableList(new ArrayList<>(errosGlobais));
    }

    public List<ItemCollector> getItemCollectors() {
        return Collections.unmodifiableList(new ArrayList<>(itemCollectors));
    }

    public boolean hasErrosGlobais() {
        return !errosGlobais.isEmpty();
    }
}
