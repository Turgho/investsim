# Fluxo Geral — Request → Response

```mermaid
flowchart TB
    subgraph Frontend["Frontend — Angular 18"]
        UI["Formulário de Simulação"]
    end

    subgraph Web["Web Layer — REST Controller"]
        CTRL["SimulacaoController"]
        VALID{"Bean Validation"}
    end

    subgraph App["Application Layer — Use Case"]
        UC["CompararCenariosUseCase"]
    end

    subgraph Infra["Infrastructure Layer"]
        CACHE["Spring Cache / Caffeine"]
        SGS["Bacen SGS Client<br/>(RestClient)"]
        ERR["Fallback + Log<br/>quando SGS falha"]
    end

    subgraph Domain["Domain Layer — Regras de Negócio"]
        SIM["SimuladorService"]
        REGRAS{"Strategy por<br/>TipoInvestimento"}
        POUP["Poupança"]
        CDB["CDB"]
        TSELIC["Tesouro Selic"]
        IR["CalculoIR"]
    end

    subgraph DB["PostgreSQL"]
        HIST["Histórico de Simulações"]
    end

    UI -->|"POST /api/simulacao"| CTRL
    CTRL --> VALID
    VALID -->|"400 Bad Request"| UI
    VALID -->|"parâmetros válidos"| UC

    UC --> CACHE
    CACHE -->|"cache hit"| REGRAS
    CACHE -->|"cache miss"| SGS
    SGS -->|"taxas atualizadas"| CACHE
    SGS -->|"timeout / erro"| ERR
    ERR -->|"taxa fallback"| REGRAS

    REGRAS -->|"poupanca"| POUP
    REGRAS -->|"cdb"| CDB
    REGRAS -->|"tesouro_selic"| TSELIC

    POUP --> SIM
    CDB --> SIM
    TSELIC --> SIM

    SIM --> IR
    IR --> RESP["SimulacaoResponse<br/>(comparação de cenários)"]

    UC --> HIST
    RESP --> UI
```

## Legenda de camadas

| Camada | Responsabilidade |
|---|---|
| **Frontend** | Coleta os parâmetros da simulação e exibe o resultado |
| **Web** | Recebe a requisição, valida o payload, delega ao use case |
| **Application** | Orquestra: busca de taxas, chamada às regras de domínio, persistência |
| **Infrastructure** | Cache local, integração com API externa (SGS), tratamento de falha |
| **Domain** | Regras de cálculo puras — sem dependência de Spring |
| **PostgreSQL** | Histórico de simulações realizadas |