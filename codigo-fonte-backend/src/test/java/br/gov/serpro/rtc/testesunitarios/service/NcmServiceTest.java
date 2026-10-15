/*
 * Versão de Homologação/Testes
 */
package br.gov.serpro.rtc.testesunitarios.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.gov.serpro.rtc.api.model.output.dadosabertos.NcmVigenciaOutput;
import br.gov.serpro.rtc.domain.repository.NcmRepository;
import br.gov.serpro.rtc.domain.service.NcmService;

@ExtendWith(MockitoExtension.class)
class NcmServiceTest {

    private static final LocalDate DATA = LocalDate.parse("2026-01-01");

    @Mock
    private NcmRepository repository;

    @InjectMocks
    private NcmService service;
    
    @Test
    void deveRetornarTrueQuandoNcmExistirNaData() {
        String ncm = "23099070";
        when(repository.existeNcm(eq(ncm), eq(DATA))).thenReturn(true);

        boolean resultado = service.existeNcm(ncm, DATA);

        verify(repository).existeNcm(eq(ncm), eq(DATA));
        assertEquals(true, resultado);
    }

    @Test
    void deveRetornarFalseQuandoNcmNaoExistirNaData() {
        String ncm = "99999999";
        when(repository.existeNcm(eq(ncm), eq(DATA))).thenReturn(false);

        boolean resultado = service.existeNcm(ncm, DATA);

        verify(repository).existeNcm(eq(ncm), eq(DATA));
        assertEquals(false, resultado);
    }

    @Test
    void deveRetornarListaDeNcmsVigentes() {
        List<NcmVigenciaOutput> esperado = List.of(
                output("01012100", "ANIMAIS VIVOS"),
                output("01012900", "CAVALOS")
        );
        when(repository.buscarTodosNcmsVigentes(eq(DATA))).thenReturn(esperado);

        List<NcmVigenciaOutput> resultado = service.consultarNcmsVigentes(DATA);

        verify(repository).buscarTodosNcmsVigentes(eq(DATA));
        assertEquals(esperado, resultado);
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremNcmsVigentes() {
        when(repository.buscarTodosNcmsVigentes(eq(DATA))).thenReturn(List.of());

        List<NcmVigenciaOutput> resultado = service.consultarNcmsVigentes(DATA);

        verify(repository).buscarTodosNcmsVigentes(eq(DATA));
        assertEquals(List.of(), resultado);
    }

    @Test
    void deveRetornarListaConformeDadosDoRepositorio() {
        when(repository.buscarTodosNcmsVigentes(eq(DATA))).thenReturn(
                Arrays.asList(
                        output("01012100", "A"),
                        output("ABC12100", "B"),
                        output("01012", "C"),
                        output("0101210X", "D"),
                        output("01012900", "E")
                ));

        List<NcmVigenciaOutput> resultado = service.consultarNcmsVigentes(DATA);

        verify(repository).buscarTodosNcmsVigentes(eq(DATA));
        assertEquals(
                List.of(
                        output("01012100", "A"),
                        output("ABC12100", "B"),
                        output("01012", "C"),
                        output("0101210X", "D"),
                        output("01012900", "E")
                ),
                resultado
        );
    }

    private static NcmVigenciaOutput output(String ncm, String descricao) {
        return new NcmVigenciaOutput(ncm, descricao);
    }
}
