# Estrutura de Pacotes e Design Patterns

## Visão Geral das Camadas

```
Web → Application → Domain ← Infrastructure
```

- **Web**: controllers REST, DTOs, exception handler
- **Application**: use cases orquestrando lógica
- **Domain**: regras de negócio puras (sem Spring)
- **Infrastructure**: SGS client, cache, repositories

## Diagrama de Classes (agrupado por camada)

```mermaid
classDiagram
    direction TB

    namespace Web {
        class SimulacaoController {
            +simular(request) ResponseEntity
        }
        class SimulacaoRequest {
            +valorInicial: BigDecimal
            +mesesAplicacao: int
            +taxaCdiPercentual: BigDecimal
        }
        class SimulacaoResponse {
            +cenarioPoupanca: CenarioResponse
            +cenarioCdb: CenarioResponse
            +cenarioTesouroSelic: CenarioResponse
        }
        class CenarioResponse {
            +nomeInvestimento: String
            +montanteBruto: BigDecimal
            +impostoRenda: BigDecimal
            +montanteLiquido: BigDecimal
            +rentabilidadeLiquida: BigDecimal
        }
        class GlobalExceptionHandler {
            +tratarExcecao() ResponseEntity
        }
    }

    namespace Application {
        class CompararCenariosUseCase {
            -taxaService: TaxaService
            -simuladorService: SimuladorService
            +executar(request) SimulacaoResponse
        }
    }

    namespace Domain {
        class SimuladorService {
            +calcularMontanteBruto(valor, taxa, meses) BigDecimal
        }
        class CalculoIR {
            +aplicar(montante, dias) BigDecimal
            +buscarAliquota(dias) BigDecimal
        }
        class TabelaIRRegressivo {
            +buscarAliquota(dias) BigDecimal
        }
        class RegraPoupanca {
            +calcular(valor, selicAnual) BigDecimal
        }
        class RegraCdb {
            +calcular(valor, cdi, meses) BigDecimal
        }
        class RegraTesouroSelic {
            +calcular(valor, selic, meses) BigDecimal
        }
        class TaxaRepository {
            <<interface>>
            +buscarSelic(periodo) List
            +buscarCdi(periodo) List
        }
    }

    namespace Infrastructure {
        class BacenSgsClient {
            +buscarSelicDiaria(data) BigDecimal
            +buscarCdiDiario(data) BigDecimal
            +buscarIpca(data) BigDecimal
        }
        class TaxaRepositoryImpl {
            +buscarSelic(periodo) List
            +buscarCdi(periodo) List
        }
        class CacheConfig {
            +cacheManager() CacheManager
        }
    }

    SimulacaoController --> CompararCenariosUseCase : delega
    SimulacaoController ..> SimulacaoRequest : recebe
    SimulacaoController ..> SimulacaoResponse : retorna
    GlobalExceptionHandler ..> SimulacaoController : intercepta erros

    CompararCenariosUseCase --> SimuladorService : usa
    CompararCenariosUseCase --> CalculoIR : usa
    CompararCenariosUseCase --> TaxaRepository : busca taxas
    CompararCenariosUseCase ..> CenarioResponse : monta

    SimuladorService ..> RegraPoupanca : aplica
    SimuladorService ..> RegraCdb : aplica
    SimuladorService ..> RegraTesouroSelic : aplica
    CalculoIR --> TabelaIRRegressivo : implementa

    TaxaRepositoryImpl ..|> TaxaRepository : implementa
    TaxaRepositoryImpl --> BacenSgsClient : consome API

    SimulacaoResponse *-- CenarioResponse : contém
```

> A camada **Domain** não depende de nenhuma outra — nem de `Infrastructure`, nem de `Spring`. A dependência corre sempre em direção ao domínio (`Web → Application → Domain`), e `Infrastructure` implementa as interfaces que o `Domain` define (`TaxaRepository`), nunca o contrário. Isso é o que mantém as regras de cálculo testáveis com JUnit puro.

## Padrões Aplicados

| Padrão | Onde | Por quê |
|---|---|---|
| **Use Case** | `CompararCenariosUseCase` | Orquestração isolada de regras |
| **Strategy** | `RegraPoupanca`, `RegraCdb`, `RegraTesouroSelic` | Cada investimento tem sua lógica, fácil de adicionar novo |
| **Repository** | `TaxaRepository` + impl | Abstrai fonte de dados (SGS/BD) |
| **Adapter** | `BacenSgsClient` | Anti-corruption layer para API externa |
| **DTO** | `SimulacaoRequest/Response` | Separação entre API e domínio |
| **Fallback** | `ErrService` + cache | Resiliência quando SGS cai |
| **Global Exception Handler** | `GlobalExceptionHandler` | Tratamento centralizado de erros |