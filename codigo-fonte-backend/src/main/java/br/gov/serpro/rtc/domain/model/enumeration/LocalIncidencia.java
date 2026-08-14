package br.gov.serpro.rtc.domain.model.enumeration;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LocalIncidencia {

    LOCAL_1(1, "Endereço do Destinatário"),
    LOCAL_2(2, "Endereço do Tomador/Adquirente"),
    LOCAL_3(3, "Estabelecimento do Prestador/Fornecedor"),
    LOCAL_4(4, "Local da Prestação"),
    LOCAL_5(5, "Local do Evento"),
    LOCAL_6(6, "Localidade do Imóvel"),
    LOCAL_7(7, "Localização do Trecho");

    private final int codigo;
    private final String descricao;
    
    @JsonValue
    public int getCodigo() {
        return codigo;
    }
    
    public static LocalIncidencia fromCodigo(int codigo) {
        for (LocalIncidencia li : values()) {
            if (li.codigo == codigo) {
                return li;
            }
        }
        throw new IllegalArgumentException("Código inválido: " + codigo);
    }

}
