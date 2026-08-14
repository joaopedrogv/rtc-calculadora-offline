package br.gov.serpro.rtc.api.model.input.calculadora.enumeration;

import java.util.Objects;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonValue;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;

/**
 * Enumeração dos tipos de operação com ente governamental aceitos na API.
 */
@RequiredArgsConstructor
@Schema(type = "integer", 
		description = """
				Tipo de operação com o ente governamental.<p>
				Valores:<p>
				<ul>
				<li>1 = Fornecimento com pagamento posterior
				<li>2 = Recebimento do pagamento com fornecimento já realizado
				<li>3 = Fornecimento com pagamento já realizado
				<li>4 = Recebimento do pagamento com fornecimento posterior
				<li>5 = Fornecimento e recebimento do pagamento concomitantes
				</ul>
				""")
public enum TipoOperacaoGovernamental {    
    FORNECIMENTO_PAGAMENTO_POSTERIOR(1, "Fornecimento com pagamento posterior"),
    RECEBIMENTO_PAGAMENTO_FORNECIMENTO_REALIZADO(2, "Recebimento do pagamento com fornecimento já realizado"), 
    FORNECIMENTO_PAGAMENTO_REALIZADO(3, "Fornecimento com pagamento já realizado"),
    RECEBIMENTO_PAGAMENTO_FORNECIMENTO_POSTERIOR(4, "Recebimento do pagamento com fornecimento posterior"),
    FORNECIMENTO_RECEBIMENTO_CONCOMITANTES(5, "Fornecimento e recebimento do pagamento concomitantes");
    
    private final Integer codigo;
    private final String descricao;
    
    @JsonValue
    public Integer getCodigo() {
        return codigo;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public static TipoOperacaoGovernamental fromCodigo(Integer codigo) {
        return Stream.of(values()).filter(t -> Objects.equals(t.getCodigo(), codigo)).findFirst()
                .orElseThrow(() -> new RuntimeException("Tipo de operação com ente governamental não mapeado"));
    }
    
}
