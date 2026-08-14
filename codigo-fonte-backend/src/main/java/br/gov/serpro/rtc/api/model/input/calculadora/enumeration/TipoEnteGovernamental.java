package br.gov.serpro.rtc.api.model.input.calculadora.enumeration;

import java.util.Objects;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonValue;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;

/**
 * Enumeração dos tipos de ente governamentais aceitos nas operações de compra
 * governamental.
 */
@RequiredArgsConstructor
@Schema(type = "integer", 
		description = """
				Tipo de ente governamental.<p>
				Valores:<p>
				<ul>
				<li>1 = União
				<li>2 = Estado
				<li>3 = Distrito Federal
				<li>4 = Município
				<li>5 = Consórcio Público
				<li>6 = Comitê Gestor do IBS
				</ul>
				""")
public enum TipoEnteGovernamental {
    UNIAO(1, "União") {
		@Override
		public TipoEnteGovernamental tipoEnteEquivalente() {
			return this;
		}
	},
    ESTADO(2, "Estado") {
		@Override
		public TipoEnteGovernamental tipoEnteEquivalente() {
			return this;
		}
	},
    DISTRITO_FEDERAL(3, "Distrito Federal") {
		@Override
		public TipoEnteGovernamental tipoEnteEquivalente() {
			return ESTADO;
		}
	},
    MUNICIPIO(4, "Município") {
		@Override
		public TipoEnteGovernamental tipoEnteEquivalente() {
			return this;
		}
	},
    CONSORCIO_PUBLICO(5, "Consórcio Público") {
		@Override
		public TipoEnteGovernamental tipoEnteEquivalente() {
			return MUNICIPIO;
		}
	},
    COMITE_GESTOR_IBS(6, "Comitê Gestor do IBS") {
		@Override
		public TipoEnteGovernamental tipoEnteEquivalente() {
			return MUNICIPIO;
		}
	};   

    private final Integer codigo;
    private final String descricao;
    
    @JsonValue
    public Integer getCodigo() {
        return codigo;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public static TipoEnteGovernamental fromCodigo(Integer codigo) {
        return Stream.of(values()).filter(t -> Objects.equals(t.getCodigo(), codigo)).findFirst()
                .orElseThrow(() -> new RuntimeException("Tipo de ente governamental não mapeado"));
    }
    
	/**
	 * Retorna o tipo de ente governamental equivalente para fins de cálculo do IBS
	 * e da CBS.
	 * 
	 * @return
	 */
    protected abstract TipoEnteGovernamental tipoEnteEquivalente();
    
    public boolean isUniao() {
		return this.tipoEnteEquivalente() == UNIAO;
	}
    
    public boolean isEstado() {
		return this.tipoEnteEquivalente() == ESTADO;
	}
	
	public boolean isMunicipio() {
		return this.tipoEnteEquivalente() == MUNICIPIO;
	}

}
