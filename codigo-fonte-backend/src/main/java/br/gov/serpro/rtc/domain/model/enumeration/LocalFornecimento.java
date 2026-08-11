package br.gov.serpro.rtc.domain.model.enumeration;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enumera os códigos legais de local de fornecimento utilizados nas regras de
 * incidência do IBS/CBS.
 *
 * Os valores {@code LOCAL_1} a {@code LOCAL_28} associam cada código numérico à
 * respectiva descrição normativa do local da operação ou da prestação.
 */
@Getter
@RequiredArgsConstructor
public enum LocalFornecimento {

    LOCAL_1(1, "Endereço diverso do fornecedor, adquirente ou destinatário"), 
    LOCAL_2(2, "Local da prestação do serviço diverso do estabelecimento do fornecedor"),
    LOCAL_3(3, "Endereço do destinatário"),
    LOCAL_4(4, "Endereço fornecido para entrega do bem"),
    LOCAL_5(5, "Estabelecimento do fornecedor como local da prestação"),
    LOCAL_6(6, "Local do porto como local da prestação"),
    LOCAL_7(7, "Local da prestação correspondente à extensão da via explorada, proporcionalmente ao território dos entes tributantes"),
    LOCAL_8(8, "Local da retirada do bem"),
    LOCAL_9(9, "Local de início do transporte"),
    LOCAL_10(10, "Local do domicílio principal do destinatário residente ou domiciliado no País, caso o adquirente não seja residente ou domiciliado no País"),
    LOCAL_11(11, "Local do domicílio principal do adquirente residente ou domiciliado no País. Nas aquisições indicadas no art. 11, §4º, II, considera-se domicílio principal do adquirente o estabelecimentro matriz"),
    LOCAL_12(12, "local do domicílio principal do destinatário residente ou domiciliado no País"),
    LOCAL_13(13, "Local do evento a que se refere o serviço"),
    LOCAL_14(14, "Localidade do imóvel"),
    LOCAL_15(15, "Estabelecimento do fornecedor como o local da entrega ou disponibilização do bem ao destinatário"),
    LOCAL_16(16, "Local da realização da operação e sua retirada"),
    LOCAL_17(17, "Endereço do destinatário fornecido para entrega ou disponibilização (assim considerado o o destino final indicado pelo adquirente)"),
    LOCAL_18(18, "Local onde se encontra o bem móvel material"),
    LOCAL_19(19, "Local onde se encontra o bem móvel material (Local da retirada)"),
    LOCAL_20(20, "Local do domicílio principal do adquirente (estabelecimento matriz)"),
    LOCAL_21(21, "Domicílio principal do destinatário"),
    LOCAL_22(22, "Local de instalação do terminal"),
    LOCAL_23(23, "Local do domicílio principal do adquirente (assim considerada a matriz), nas aquisições realizadas de forma centralizada por contribuinte sujeito ao regime regular do IBS e da CBS que possui mais de um estabelecimento e que não estejam sujeitas a vedação à apropriação de créditos"),
    LOCAL_24(24, "Local da entrega ou disponibilização"),
    LOCAL_25(25, "Local do estabelecimento principal do adquirente (assim considerada a matriz), conforme §4º do art. 11"),
    LOCAL_26(26, "Local do estabelecimento do agente ou de seus representados que figurem na posição devedora da liquidação financeira apurada pela Câmara de Comercialização de Energia Elétrica"),
    LOCAL_27(27, "Local do estabelecimento principal do fornecedor"),
    LOCAL_28(28, "Local do estabelecimento principal do adquirente");

    private final int codigo;
    private final String descricao;
    
    @JsonValue
    public int getCodigo() {
        return codigo;
    }
    
    public static LocalFornecimento fromCodigo(int codigo) {
        for (LocalFornecimento lf : values()) {
            if (lf.codigo == codigo) {
                return lf;
            }
        }
        throw new IllegalArgumentException("Código inválido: " + codigo);
    }

}
