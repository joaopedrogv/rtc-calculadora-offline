/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.hibernate.jpa.HibernateHints;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import br.gov.serpro.rtc.api.model.output.dadosabertos.NcmVigenciaOutput;
import br.gov.serpro.rtc.domain.model.entity.Ncm;
import jakarta.persistence.QueryHint;

/**
 * Repositório Spring Data JPA para acesso a {@link Ncm}, com consultas de
 * descrição e existência de NCM vigentes.
 */
public interface NcmRepository extends JpaRepository<Ncm, String> {

    @Query("""
            SELECT LTRIM(RTRIM(n.descricao, ' '), ' -')
            FROM Ncm n
            WHERE n.codigo = SUBSTRING(:codigo, 1, :limite)
            AND :data BETWEEN n.inicioVigencia AND COALESCE(n.fimVigencia, :data)
            """)
    Optional<String> buscarDescricaoNcm(
            @Param("codigo") String codigo,
            @Param("limite") int limite,
            @Param("data") LocalDate data);

    /*
     * Entradas recomendadas na cache: 21.000
     * Memória estimada: ~2,5 MB
     */
    @Query("""
            SELECT EXISTS (
                SELECT 1
                FROM Ncm n
                WHERE n.codigo = :codigo
                AND :data BETWEEN n.inicioVigencia AND COALESCE(n.fimVigencia, :data)
            )
            """)
    @Cacheable(cacheNames = "NcmRepository.existeNcm")
    boolean existeNcm(
            @Param("codigo") String codigo,
            @Param("data") LocalDate data);

	/**
	 * Retorna todos os NCMs vigentes na data informada com código completo (8 dígitos).
	 * <p>
	 * Critérios de seleção:
	 * <ul>
	 *   <li>Data de referência deve estar entre {@code inicioVigencia} e {@code fimVigencia}
	 *       (inclusivo em ambos os lados; quando {@code fimVigencia} é nulo, usa-se a própria data).</li>
	 *   <li>Código NCM com exatamente 8 dígitos (regra de NCM completo).</li>
	 *   <li>Sem duplicatas no resultado ({@code DISTINCT}).</li>
	 *   <li>Ordenado por código NCM crescente.</li>
	 * </ul>
	 * <p>
	 * Usa constructor expression JPQL para projetar direto em {@link NcmVigenciaOutput} (record),
	 * reduzindo overhead de mapeamento de objetos.
	 * <p>
	 * Marcado como {@code READ_ONLY} para Hibernate otimizar a query (sem sincronização
	 * com mudanças de entidade em memória).
	 * 
	 * @param data data de referência para validar vigência (formato ISO 8601 yyyy-MM-dd)
	 * @return lista (possivelmente vazia) de NCMs vigentes e suas descrições ordenados por código
	 */
    @Query("""
            SELECT DISTINCT new br.gov.serpro.rtc.api.model.output.dadosabertos.NcmVigenciaOutput(
                n.codigo,
                n.descricao
            )
            FROM Ncm n
            WHERE :data BETWEEN n.inicioVigencia AND COALESCE(n.fimVigencia, :data)
            AND LENGTH(n.codigo) = 8
            ORDER BY n.codigo
            """)
	@QueryHints(@QueryHint(name = HibernateHints.HINT_READ_ONLY, value = "true"))
	List<NcmVigenciaOutput> buscarTodosNcmsVigentes(@Param("data") LocalDate data);

}