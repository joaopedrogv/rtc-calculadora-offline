/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import br.gov.serpro.rtc.api.model.output.dadosabertos.NcmVigenciaOutput;
import br.gov.serpro.rtc.domain.repository.NcmRepository;
import lombok.RequiredArgsConstructor;

/**
 * Serviço de domínio para consulta de NCMs (Nomenclatura Comum do Mercosul).
 * <p>
 * Responsabilidades:
 * <ul>
 *   <li>Verificar existência e vigência de um NCM em uma data específica.</li>
 *   <li>Listar todos os NCMs vigentes e completos (8 dígitos) em uma data.</li>
 * </ul>
 * <p>
 * Delega a lógica de consulta para {@link NcmRepository}, que aplica filtros de vigência
 * e tamanho de código via JPQL.
 * 
 */
@RequiredArgsConstructor
@Service
public class NcmService {

    private final NcmRepository repository;

    /**
     * Verifica se um NCM específico existe e está vigente na data informada.
     * <p>
     * Critério: data deve estar entre {@code inicioVigencia} e {@code fimVigencia}
     * (inclusivo; quando {@code fimVigencia} é nulo, considera vigente indefinidamente).
     * 
     * @param ncm código do NCM a verificar
     * @param data data de referência para validar vigência
     * @return {@code true} se NCM existe e está vigente; {@code false} caso contrário
     */
    public boolean existeNcm(String ncm, LocalDate data) {
        return repository.existeNcm(ncm, data);
    }

    /**
     * Retorna todos os NCMs vigentes na data informada com código completo (8 dígitos).
     * <p>
     * Caso de uso: popular dropdown/autocomplete de NCMs válidos em um período,
     * ou validar NCMs em lote contra a tabela de vigências.
     * <p>
     * Nota: a lista retornada pode estar vazia (sem erros), conforme especificação.
     * 
     * @param data data de referência para filtrar vigência
     * @return lista (possivelmente vazia) de {@link NcmVigenciaOutput} ordenada por código
     * 
     */
    public List<NcmVigenciaOutput> consultarNcmsVigentes(LocalDate data) {
        return repository.buscarTodosNcmsVigentes(data);
    }

}
