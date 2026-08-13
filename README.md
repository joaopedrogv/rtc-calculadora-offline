# 🧮 Sincronizador da Calculadora de Tributos - Reforma Tributária Brasil

## Visão Geral

Este projeto implementa uma solução de automação para manter sincronizada a versão mais recente da **Calculadora Oficial de Tributos** lançada pela Receita Federal, voltada para os novos modelos de impostos sobre consumo (CBS, IBS e Imposto Seletivo).

A ferramenta funciona como um **intermediário inteligente** entre o servidor oficial da Receita Federal e este repositório, garantindo que desenvolvedores e empresas tenham sempre acesso à versão mais atualizada do código-fonte para integração em seus sistemas ERP.

---

## 🚀 Funcionalidade Principal

Um sistema automatizado que:
- **Verifica diariamente** se existe uma nova versão da calculadora
- **Extrai e processa** os arquivos quando atualizações são detectadas
- **Valida as mudanças** através de comparação de integridade (MD5)
- **Publica as atualizações** automaticamente no GitHub com releases versionadas

---

## 🔧 Como Tudo Funciona

### Etapa 1: Acionamento
```
Execução automática: Todos os dias à meia-noite (UTC)
Execução manual: Via GitHub Actions (button "Run workflow")
```

### Etapa 2: Consulta da API Oficial
Conecta-se ao endpoint da Receita Federal para obter o link de download atual:
```
https://piloto-cbs.tributos.gov.br/servico/calculadora-consumo/api/calculadora/download/url?platform=default
```

### Etapa 3: Aquisição do Pacote
Realiza o download do arquivo compactado (calculadora.zip) usando a URL dinâmica obtida.

### Etapa 4: Análise de Mudanças
- Extrai e calcula o hash MD5 do `codigo-fonte-backend.zip`
- Compara com o hash anterior salvo no repositório
- Determina se houve alterações no código-fonte

### Etapa 5: Atualização do Repositório
Apenas se mudanças forem detectadas:
- Descompacta os arquivos na pasta `codigo-fonte-backend`
- Atualiza o checksum MD5 para a próxima comparação
- Realiza commit com timestamp da atualização
- Sincroniza tudo com o GitHub

### Etapa 6: Liberação de Versão
Quando há atualização:
- Gera uma nova release (versão)
- Anexa o pacote do código-fonte
- Marca como "latest" (mais recente)

---

## 📚 Recursos Úteis

| Recurso | Link |
|---------|------|
| **Calculadora Web** | https://piloto-cbs.tributos.gov.br/servico/calculadora-consumo/calculadora/calculadora-offline |
| **Endpoint de Download** | https://piloto-cbs.tributos.gov.br/servico/calculadora-consumo/api/calculadora/download/url?platform=default |

---

## ▶️ Disparando Execução Manual

Precisa atualizar agora? Siga estes passos:

1. Acesse a seção **Actions** neste repositório
2. Procure pelo workflow **"Download and Update Código Fonte da Calculadora da Reforma Tributária"**
3. Clique no botão **"Run workflow"**
4. Escolha a branch `main` como destino
5. Confirme a execução

O processo será executado imediatamente e você acompanhará o progresso em tempo real.

---

## 🎯 Propósito

Facilitar o acesso e integração da Calculadora de Tributos em:
- Sistemas ERP empresariais
- Plataformas de simulação tributária
- Ferramentas de conformidade fiscal
- Soluções de cálculo automático de impostos
