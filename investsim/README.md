# investsim — Backend

API REST para simulação e comparação de cenários de investimento em renda fixa (Poupança, CDB, Tesouro Selic).

## Tech Stack

| Componente | Versão |
|---|---|
| Java | 21 |
| Spring Boot | 4.1.0 |
| PostgreSQL | 14+ |
| Flyway | (via starter) |
| Maven | 3.9.16 (wrapper incluso) |

## Pré-requisitos

- **Java 21** — `java -version`
- **PostgreSQL 14+** — rodando local ou em container
- Sem necessidade de Maven global (wrapper incluso)

## Setup

### 1. Clonar e entrar no diretório

```bash
git clone <repo-url>
cd investsim/investsim
```

### 2. Criar banco de dados

```sql
CREATE DATABASE investsim;
```

Ou via Docker:

```bash
docker run -d --name investsim-pg \
  -e POSTGRES_DB=investsim \
  -e POSTGRES_USER=investsim \
  -e POSTGRES_PASSWORD=investsim \
  -p 5432:5432 \
  postgres:16-alpine
```

### 3. Configurar datasource

Crie `src/main/resources/application-local.properties` (não committar):

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/investsim
spring.datasource.username=investsim
spring.datasource.password=investsim
```

### 4. Compilar e rodar

```bash
./mvnw compile
./mvnw spring-boot:run
```

Ou com profile local:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

A API sobe em `http://localhost:8080`.

## Comandos

```bash
./mvnw compile          # compilar
./mvnw test             # rodar testes
./mvnw package          # gerar jar
./mvnw clean test       # limpar + testar
```

## Estrutura do Projeto

```
investsim/investsim/
├── src/main/java/com/turgho/investsim/
│   ├── InvestsimApplication.java          # entrypoint
│   ├── web/                               # controllers, DTOs, exception handler
│   ├── application/                       # use cases
│   ├── domain/                            # entidades, regras de cálculo (sem Spring)
│   └── infrastructure/                    # SGS client, cache, repositories
├── src/main/resources/
│   ├── application.properties
│   ├── application-local.properties       # config local (não committar)
│   └── db/migration/                      # Flyway migrations
└── src/test/java/com/turgho/investsim/   # testes
```

## Arquitetura

Camadas seguindo Clean Architecture:

- **Web** — controllers REST, validação, exception handler
- **Application** — use cases orquestrando busca de taxas + cálculo
- **Domain** — regras de negócio puras (juros compostos, IR regressivo), sem dependência de Spring
- **Infrastructure** — cliente HTTP para API SGS do Banco Central, cache, repositories

## API SGS — Banco Central

Dados de taxas via API SGS (Sistema Gerenciador de Séries):

| Série | Descrição |
|---|---|
| 11 | Selic diária |
| 12 | CDI diária |
| 433 | IPCA |

URL base e timeouts configurados em `application.properties`.

## Documentação

| Arquivo | Descrição |
|---|---|
| [docs/flow-request-response.md](docs/flow-request-response.md) | Fluxo geral request → response |
| [docs/structure-packages.md](docs/structure-packages.md) | Estrutura de pacotes e design patterns |
| [docs/flowchart-simulation.md](docs/flowchart-simulation.md) | Fluxograma detalhado de simulação |

## Licença

[MIT](LICENSE)
