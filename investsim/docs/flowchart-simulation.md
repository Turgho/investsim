# Fluxograma de Simulação

Fluxo detalhado de processamento de uma simulação de investimento, organizado em 4 fases: **entrada/validação**, **busca de taxas**, **cálculo por cenário** e **resposta/persistência**.

```mermaid
flowchart TD
    START(["Início"]) --> RECV["Recebe SimulacaoRequest"]
    RECV --> VALID{"Bean Validation"}
    VALID -->|"inválido"| ERR_400["400 / Erros detalhados"]
    ERR_400 --> END_ERR(["Fim - Erro"])

    %% Fase 2: busca de taxas
    VALID -->|"válido"| FETCH_SELIC{"Busca Selic<br/>(cache/BD)"}
    VALID -->|"válido"| FETCH_CDI{"Busca CDI<br/>(cache/BD)"}

    FETCH_SELIC -->|"miss"| SGS_SELIC["API SGS<br/>timeout=5s"]
    SGS_SELIC -->|"sucesso"| SELIC_OK["Selic atualizada"]
    SGS_SELIC -->|"falha"| SELIC_FB["Fallback: 13,75% a.a."]
    FETCH_SELIC -->|"hit"| SELIC_OK
    SELIC_FB --> SELIC_OK

    FETCH_CDI -->|"miss"| SGS_CDI["API SGS<br/>timeout=5s"]
    SGS_CDI -->|"sucesso"| CDI_OK["CDI atualizado"]
    SGS_CDI -->|"falha"| CDI_FB["Fallback: 100% CDI = Selic"]
    FETCH_CDI -->|"hit"| CDI_OK
    CDI_FB --> CDI_OK

    %% Fase 3: calculo por cenario (paralelo)
    SELIC_OK --> RULE_POUP{"Selic <= 8,5% a.a.?"}
    RULE_POUP -->|"sim"| POUP_BAIXA["Poupança = 70% Selic/mês"]
    RULE_POUP -->|"não"| POUP_ALTA["Poupança = 0,5%/mês"]
    POUP_BAIXA --> BRUTO_POUP["Montante Bruto - Poupança"]
    POUP_ALTA --> BRUTO_POUP

    CDI_OK --> REGRA_CDB["CDB = %CDI contratado x meses"]
    REGRA_CDB --> BRUTO_CDB["Montante Bruto - CDB"]

    SELIC_OK --> REGRA_TSELIC["Tesouro Selic = Selic acumulada"]
    REGRA_TSELIC --> BRUTO_TSELIC["Montante Bruto - Tesouro Selic"]

    %% IR aplicado aos 3 cenarios
    BRUTO_POUP --> CALC_IR{"IR Regressivo<br/>por prazo"}
    BRUTO_CDB --> CALC_IR
    BRUTO_TSELIC --> CALC_IR

    CALC_IR -->|"<= 180 dias"| IR_22["22,5%"]
    CALC_IR -->|"<= 360 dias"| IR_20["20%"]
    CALC_IR -->|"<= 720 dias"| IR_17["17,5%"]
    CALC_IR -->|"> 720 dias"| IR_15["15%"]

    IR_22 --> MONT_LIQ["Montante Líquido = Bruto - IR"]
    IR_20 --> MONT_LIQ
    IR_17 --> MONT_LIQ
    IR_15 --> MONT_LIQ

    %% Fase 4: resposta e persistencia
    MONT_LIQ --> COMPARE["Compara Cenários<br/>Poupança vs CDB vs Tesouro Selic"]
    COMPARE --> RESP["Monta SimulacaoResponse<br/>3 cenários + destaque"]
    RESP --> SAVE["Salva no PostgreSQL<br/>Histórico de simulações"]
    SAVE --> END(["Fim - 200 OK"])
```

> Nota: a Poupança e o Tesouro Selic dependem da taxa Selic; o CDB depende do CDI. As duas buscas rodam em paralelo logo após a validação — não há dependência entre elas.

## Tabela IR Regressivo

| Prazo | Alíquota |
|---|---|
| <= 180 dias | 22,5% |
| <= 360 dias | 20,0% |
| <= 720 dias | 17,5% |
| > 720 dias | 15,0% |

## Regras Poupança

- **Selic > 8,5% a.a.** -> rende **0,5% ao mês**
- **Selic <= 8,5% a.a.** -> rende **70% da Selic** ao mês