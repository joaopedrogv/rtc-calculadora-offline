package br.gov.serpro.rtc.domain.service.basecalculo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import br.gov.serpro.rtc.api.model.input.basecalculo.BaseCalculoUnificadaNfseInput;
import br.gov.serpro.rtc.api.model.input.basecalculo.DocumentoAjusteNfseInput;
import br.gov.serpro.rtc.api.model.input.basecalculo.LocacaoNfseInput;
import br.gov.serpro.rtc.api.model.input.basecalculo.enumeration.OpcaoSimplesNacional;
import br.gov.serpro.rtc.api.model.input.basecalculo.enumeration.RegimeApuracaoIbsCbsSN;
import br.gov.serpro.rtc.api.model.input.basecalculo.enumeration.SituacaoOperacaoNfse;
import br.gov.serpro.rtc.api.model.input.basecalculo.enumeration.TipoAjusteNfse;
import br.gov.serpro.rtc.api.model.output.basecalculo.BaseCalculoUnificadaNfseOutput;
import br.gov.serpro.rtc.api.model.output.basecalculo.ErroBaseCalculoNfse;
import br.gov.serpro.rtc.core.util.ArredondamentoUtils;
import br.gov.serpro.rtc.domain.service.exception.CampoInvalidoException;

/**
 * Serviço responsável pelo cálculo unificado das bases de cálculo NFS-e (IBS/CBS + Simples Nacional).
 * Implementa o algoritmo completo conforme especificação (seções 4.0 a 4.10).
 */
@Service
public class BaseCalculoUnificadaNfseService {

    private static final int ANO_EXTINCAO_PIS_COFINS  = 2027;
    private static final int ANO_EXTINCAO_ISSQN        = 2033;

    public BaseCalculoUnificadaNfseOutput calcular(BaseCalculoUnificadaNfseInput input) {
        List<String> avisos = new ArrayList<>();
        List<ErroBaseCalculoNfse> erros = new ArrayList<>();
        List<DocumentoAjusteNfseInput> documentos = nullToEmpty(input.getDocumentos());

        // Validação estrutural
        validarEstruturaLocacao(input);

        // Validações de negócio
        validarNegocio(input, erros);

        // Varredura dos documentos — retorna lista filtrada (exclui inválidos)
        List<DocumentoAjusteNfseInput> documentosValidos = scanDocumentos(documentos, avisos, erros);

        // MEI — retorno antecipado
        if (input.getOpSimpNac().isMei()) {
            avisos.add("E1302: Operação MEI: bases de cálculo não aplicáveis");
            if (!documentos.isEmpty()) {
                erros.add(new ErroBaseCalculoNfse("E0436",
                    "Documentos de ajuste não são permitidos para MEI"));
            }
            return BaseCalculoUnificadaNfseOutput.builder()
                .baseRG(null)
                .baseSN(null)
                .avisos(avisos)
                .erros(erros)
                .build();
        }

        // Agregações (só com documentos válidos)
        BigDecimal vCalcAjusteBCIBSCBS = calcularSomaIbsCbs(documentosValidos, input.getSituacao());
        BigDecimal somaAjustesProfissionalParceiro = calcularSomaAjustesProfissionalParceiro(documentosValidos, input.getSituacao());

        // E1534
        BigDecimal vServ = nullToZero(input.getVServ());
        if (vCalcAjusteBCIBSCBS.compareTo(BigDecimal.ZERO) > 0
                && vCalcAjusteBCIBSCBS.compareTo(vServ) >= 0) {
            erros.add(new ErroBaseCalculoNfse("E1534",
                String.format("Soma dos ajustes (%s) excede ou iguala o valor do serviço (%s)",
                    ArredondamentoUtils.formatarMoeda(vCalcAjusteBCIBSCBS),
                    ArredondamentoUtils.formatarMoeda(vServ))));
        }

        // Avisos defensivos de contexto
        aplicarRegrasDefensivas(input, documentos, avisos);

        // Cálculo das bases
        BigDecimal baseRG = calcularBaseRG(input, vCalcAjusteBCIBSCBS);
        BigDecimal baseSN = calcularBaseSN(input, vCalcAjusteBCIBSCBS, somaAjustesProfissionalParceiro);

        // Optante pendente
        if (input.getOpSimpNac().isPendente()) {
            avisos.add("Optante pendente: cálculo realizado com ressalva");
        }

        BigDecimal baseRGFinal = baseRG != null ? baseRG.setScale(2, java.math.RoundingMode.HALF_EVEN) : null;
        BigDecimal baseSNFinal = baseSN != null ? baseSN.setScale(2, java.math.RoundingMode.HALF_EVEN) : null;

        if (baseRGFinal != null && baseRGFinal.compareTo(BigDecimal.ZERO) < 0) {
            erros.add(new ErroBaseCalculoNfse("BASE-NEGATIVA",
                String.format("Base de cálculo RG negativa: %s",
                    ArredondamentoUtils.formatarMoeda(baseRGFinal))));
        }
        if (baseSNFinal != null && baseSNFinal.compareTo(BigDecimal.ZERO) < 0) {
            erros.add(new ErroBaseCalculoNfse("BASE-NEGATIVA",
                String.format("Base de cálculo SN negativa: %s",
                    ArredondamentoUtils.formatarMoeda(baseSNFinal))));
        }

        return BaseCalculoUnificadaNfseOutput.builder()
            .baseRG(baseRGFinal)
            .baseSN(baseSNFinal)
            .avisos(avisos)
            .erros(erros)
            .build();
    }

    private void validarEstruturaLocacao(BaseCalculoUnificadaNfseInput input) {
        if (input.getSituacao().isLocacao() && input.getLocacao() == null) {
            throw new CampoInvalidoException(
                "Campo locacao obrigatório quando situacao é locacao9903");
        }
    }

    private void validarNegocio(BaseCalculoUnificadaNfseInput input, List<ErroBaseCalculoNfse> erros) {
        OpcaoSimplesNacional opSN = input.getOpSimpNac();
        RegimeApuracaoIbsCbsSN regAp = input.getRegApIBSCBSSN();

        if (opSN.exigeRegimeApuracao() && regAp == null) {
            erros.add(new ErroBaseCalculoNfse("EXXX-REGAP",
                "Campo regApIBSCBSSN obrigatório para optante do Simples Nacional"));
        }

        if (opSN.proibeRegimeApuracao() && regAp != null) {
            erros.add(new ErroBaseCalculoNfse("EXXX-REGAP",
                "Campo regApIBSCBSSN proibido para não optante do Simples Nacional"));
        }

        if (opSN.vedaLocacao() && input.getSituacao().isLocacao()) {
            erros.add(new ErroBaseCalculoNfse("EXXX-LOC-SN",
                "Locação de imóveis incompatível com optante do Simples Nacional"));
        }
    }

    private List<DocumentoAjusteNfseInput> scanDocumentos(List<DocumentoAjusteNfseInput> documentos,
            List<String> avisos, List<ErroBaseCalculoNfse> erros) {
        List<DocumentoAjusteNfseInput> documentosValidos = new ArrayList<>();

        for (DocumentoAjusteNfseInput doc : documentos) {
            String tipo = doc.getTpAjusteBC();

            // Tipo desconhecido — aviso, não entra nas somas
            if (!TipoAjusteNfse.isConhecido(tipo)) {
                avisos.add(String.format("Tipo de ajuste '%s' desconhecido: documento ignorado", tipo));
                continue;
            }

            // Tipo que requer descrição sem xTpAjusteBC — erro LEIAUTE-XTP, não entra nas somas
            if (TipoAjusteNfse.requerDescricao(tipo) && isBlank(doc.getXTpAjusteBC())) {
                erros.add(new ErroBaseCalculoNfse("LEIAUTE-XTP",
                    String.format("Campo xTpAjusteBC obrigatório para tipo de ajuste %s", TipoAjusteNfse.OUTROS.getCodigo())));
                continue;
            }

            documentosValidos.add(doc);
        }

        return documentosValidos;
    }

    private BigDecimal calcularSomaIbsCbs(List<DocumentoAjusteNfseInput> documentos,
            SituacaoOperacaoNfse situacao) {
        // Em locação, documentos são ignorados (não somam)
        if (situacao.ignoraDocumentos()) {
            return BigDecimal.ZERO;
        }
        return documentos.stream()
            .filter(doc -> TipoAjusteNfse.repercuteIbsCbs(doc.getTpAjusteBC()))
            .map(DocumentoAjusteNfseInput::getVAjusteAplic)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calcularSomaAjustesProfissionalParceiro(List<DocumentoAjusteNfseInput> documentos,
            SituacaoOperacaoNfse situacao) {
        if (situacao.ignoraDocumentos()) {
            return BigDecimal.ZERO;
        }
        return documentos.stream()
            .filter(doc -> TipoAjusteNfse.repercuteSN(doc.getTpAjusteBC()))
            .map(DocumentoAjusteNfseInput::getVAjusteAplic)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void aplicarRegrasDefensivas(BaseCalculoUnificadaNfseInput input,
            List<DocumentoAjusteNfseInput> documentos, List<String> avisos) {
        if (input.getSituacao().isLocacao() && !documentos.isEmpty()) {
            avisos.add("Documentos de ajuste ignorados em contexto de locação de imóveis");
        }
        if (!input.getSituacao().isLocacao() && input.getLocacao() != null) {
            avisos.add("Grupo locacao ignorado fora de contexto de locação de imóveis");
        }
    }

    private BigDecimal calcularBaseRG(BaseCalculoUnificadaNfseInput input,
            BigDecimal vCalcAjusteBCIBSCBS) {
        SituacaoOperacaoNfse situacao = input.getSituacao();

        if (situacao.isLocacao()) {
            return calcularBaseRGLocacao(input);
        }

        BigDecimal vServ = nullToZero(input.getVServ());
        BigDecimal vDescIncond = nullToZero(input.getVDescIncond());
        int ano = input.getAnoFatoGerador();
        BigDecimal deduzISSQN = (ano < ANO_EXTINCAO_ISSQN)
            ? nullToZero(input.getVISSQN()) : BigDecimal.ZERO;
        BigDecimal deduzPIS = (ano < ANO_EXTINCAO_PIS_COFINS) ? nullToZero(input.getVPIS()) : BigDecimal.ZERO;
        BigDecimal deduzCOFINS = (ano < ANO_EXTINCAO_PIS_COFINS) ? nullToZero(input.getVCOFINS()) : BigDecimal.ZERO;

        BigDecimal resultado = vServ;
        resultado = ArredondamentoUtils.subtrair(resultado, vDescIncond);
        resultado = ArredondamentoUtils.subtrair(resultado, vCalcAjusteBCIBSCBS);
        resultado = ArredondamentoUtils.subtrair(resultado, deduzISSQN);
        resultado = ArredondamentoUtils.subtrair(resultado, deduzPIS);
        resultado = ArredondamentoUtils.subtrair(resultado, deduzCOFINS);

        return resultado;
    }

    private BigDecimal calcularBaseRGLocacao(BaseCalculoUnificadaNfseInput input) {
        LocacaoNfseInput loc = input.getLocacao();
        BigDecimal fator = ArredondamentoUtils.dividirPorCem(loc.getPCopropriedade());

        // vServEfetivo = (pCopropriedade/100) × vTotOper
        BigDecimal vServEfetivo = ArredondamentoUtils.multiplicar(fator, loc.getVTotOper());

        // vDescIncondEfetivo = (pCopropriedade/100) × vDescIncondTot
        BigDecimal vDescIncondEfetivo = ArredondamentoUtils.multiplicar(fator, loc.getVDescIncondTot());

        // vCalcAjusteBCLocImoveis = (pCopropriedade/100) × somaAjustesLocImoveis
        BigDecimal vCalcAjusteBCLocImoveis = ArredondamentoUtils.multiplicar(fator, loc.getSomaAjustesLocImoveis());

        int ano = input.getAnoFatoGerador();
        BigDecimal vPIS = (ano < ANO_EXTINCAO_PIS_COFINS) ? nullToZero(input.getVPIS()) : BigDecimal.ZERO;
        BigDecimal vCOFINS = (ano < ANO_EXTINCAO_PIS_COFINS) ? nullToZero(input.getVCOFINS()) : BigDecimal.ZERO;

        // baseRG = vServEfetivo - vDescIncondEfetivo - vCalcAjusteBCLocImoveis - vPIS - vCOFINS
        // (ISSQN não deduz na locação — subitem 99.03 sem incidência de ISSQN)
        BigDecimal resultado = vServEfetivo;
        resultado = ArredondamentoUtils.subtrair(resultado, vDescIncondEfetivo);
        resultado = ArredondamentoUtils.subtrair(resultado, vCalcAjusteBCLocImoveis);
        resultado = ArredondamentoUtils.subtrair(resultado, vPIS);
        resultado = ArredondamentoUtils.subtrair(resultado, vCOFINS);

        return resultado;
    }

    private BigDecimal calcularBaseSN(BaseCalculoUnificadaNfseInput input, BigDecimal vCalcAjusteBCIBSCBS, BigDecimal somaAjustesProfissionalParceiro) {
        OpcaoSimplesNacional opSN = input.getOpSimpNac();
        RegimeApuracaoIbsCbsSN regAp = input.getRegApIBSCBSSN();

        if (opSN.baseSNInaplicavel()) return null;
        if (regAp == null) return null;

        SituacaoOperacaoNfse situacao = input.getSituacao();

        BigDecimal base;
        if (situacao.isLocacao()) {
            base = calcularBaseSNLocacao(input);
        } else {
            BigDecimal vServ = nullToZero(input.getVServ());
            BigDecimal vDescIncond = nullToZero(input.getVDescIncond());
            base = ArredondamentoUtils.subtrair(vServ, vDescIncond);
        }

        // Dedução incondicional da vCalcAjusteBCIBSCBS na baseSN.
        // A filtragem dos tipos (101-105, 199) ocorre em calcularSomaIbsCbs;
        // para o tipo 9 (PROFISSIONAL_PARCEIRO) o valor já chega zerado, pois não repercute em IBS/CBS.
        base = ArredondamentoUtils.subtrair(base, vCalcAjusteBCIBSCBS);

        // Profissional parceiro: só deduz da baseSN se SALÃO PARCEIRO + regAp != AMBOS_REGULARES
        boolean profissionalParceiroReduz = situacao.isSalaoParceiro() && regAp.isCalculaBaseSN();
        if (profissionalParceiroReduz) {
            base = ArredondamentoUtils.subtrair(base, somaAjustesProfissionalParceiro);
        }

        return base;
    }

    private BigDecimal calcularBaseSNLocacao(BaseCalculoUnificadaNfseInput input) {
        LocacaoNfseInput loc = input.getLocacao();
        BigDecimal fator = ArredondamentoUtils.dividirPorCem(loc.getPCopropriedade());

        BigDecimal vServEfetivo = ArredondamentoUtils.multiplicar(fator, loc.getVTotOper());
        BigDecimal vDescIncondEfetivo = ArredondamentoUtils.multiplicar(fator, loc.getVDescIncondTot());

        return ArredondamentoUtils.subtrair(vServEfetivo, vDescIncondEfetivo);
    }

    private static BigDecimal nullToZero(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private static List<DocumentoAjusteNfseInput> nullToEmpty(List<DocumentoAjusteNfseInput> list) {
        return list != null ? list : Collections.emptyList();
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
