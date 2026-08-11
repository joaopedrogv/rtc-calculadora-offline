package br.gov.serpro.rtc.api.exceptionhandler;

import java.net.URI;
import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import br.gov.serpro.rtc.api.exceptionhandler.dto.ErroDetalhe;
import br.gov.serpro.rtc.domain.service.collector.ErrosCalculoException;
import lombok.extern.slf4j.Slf4j;

/**
 * Handler do cálculo com observabilidade que converte {@code
 * ErrosCalculoException} em {@link ProblemDetail} com a lista detalhada de
 * erros por item.
 */
@Slf4j
@RestControllerAdvice
public class ObservabilidadeExceptionHandler {

    @ExceptionHandler(ErrosCalculoException.class)
    ResponseEntity<ProblemDetail> handleErrosCalculoException(ErrosCalculoException ex, WebRequest request) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        List<ErroDetalhe> erros = ex.getErros();

        log.debug("Erros de observabilidade: {} erro(s) | URI: {}",
                erros.size(), request.getDescription(false));

        ProblemDetail problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setTitle("Erros no processamento do cálculo");
        problemDetail.setDetail(String.format("O cálculo contém %d erro(s).", erros.size()));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("errors", erros);
        return ResponseEntity.status(status).body(problemDetail);
    }
}
