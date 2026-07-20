# AGENTS.md

## Contexto

Módulo **investsim** — simulador e comparador de cenários de investimento em renda fixa (Poupança, CDB, Tesouro Selic) para app de planejamento financeiro existente (Java 21 + Spring Boot + Angular 18).

O módulo começa como projeto standalone e será incorporado ao app validado.

## Estrutura

O projeto Maven vive dentro de `investsim/investsim/`, **não** na raiz do repo. Todo comando deve ser executado de `investsim/investsim/` ou usar `-f investsim/investsim/pom.xml`.

```
investsim/investsim/
├── pom.xml
├── mvnw / mvnw.cmd          # Maven wrapper
├── .mvn/wrapper/
├── src/main/java/com/turgho/investsim/
│   └── InvestsimApplication.java
├── src/test/java/com/turgho/investsim/
│   └── InvestsimApplicationTests.java
└── src/main/resources/
    ├── application.properties
    ├── db/migration/         # Flyway (vazio — aguarda migrations)
    ├── static/
    └── templates/
```

## Comandos (cwd = investsim/investsim/)

```bash
./mvnw compile          # compilar
./mvnw test             # rodar testes (Surefire)
./mvnw package          # gerar jar
./mvnw clean test       # limpar + testar
```

Maven wrapper (`mvnw`) já configurado — `mvn` global não é necessário.

## Fatos-chave

- **Spring Boot 4.1.0** (spring-boot-starter-parent)
- **Java 21**
- **JUnit 5** via spring-boot-starter-*-test
- **Flyway** para migrations PostgreSQL (`db/migration/`)
- **Spring Cache** habilitado (sem implementation Caffeine/Redis ainda)
- Config: `application.properties` (não YAML)
- Sem CI, linters ou formatters configurados
- Sem Angular neste repo — frontend fica no repo do app principal

### Dependências ativas no pom.xml

| Starter | Finalidade |
|---|---|
| `spring-boot-starter-webmvc` | REST controllers |
| `spring-boot-starter-data-jpa` | JPA / Hibernate |
| `spring-boot-starter-flyway` + `flyway-database-postgresql` | Migrations |
| `spring-boot-starter-cache` | Cache de taxas |
| `spring-boot-starter-validation` | Bean validation |
| `postgresql` (runtime) | JDBC driver |

### O que ainda falta configurar

- Datasource PostgreSQL (host, port, db, user, password)
- Cache manager (Caffeine ou Redis)
- API SGS URLs (Selic, CDI, IPCA)
- Timeout de HTTP client

## Fase 1 — Auditoria (somente leitura)

Antes de escrever código, o agente deve:

1. Ler estrutura do projeto (pastas, módulos, convenções de nomenclatura de pacotes)
2. Identificar padrão de camadas do app existente (controller/service/repository ou domain/application/infrastructure) — seguir o padrão real, não o diagrama do usuário se divergir
3. Verificar versão exata do Spring Boot, Java e dependências no pom.xml (JPA, Lombok, validação, etc.)
4. Checar schema de banco (PostgreSQL) e estratégia de migrations (Flyway? Liquibase? JPA ddl-auto?)
5. Verificar convenções de teste (JUnit5, Mockito, Testcontainers?)
6. **Reportar tudo antes de tocar em qualquer arquivo**. Não presumir — perguntar se algo não estiver claro

## Fase 2 — Escopo do módulo

### Camadas

- **Domain**: regras de cálculo de rendimento (juros compostos, tabela IR regressivo: 22,5% ≤180d, 20% ≤360d, 17,5% ≤720d, 15% >720d). Regras Poupança (0,5%/mês ou 70% da Selic quando Selic ≤ 8,5% a.a.), CDB (% do CDI), Tesouro Selic. Esta camada **não depende de Spring** — testável com JUnit puro
- **Application**: casos de uso orquestrando busca de taxas + cálculo (ex: `CompararCenariosUseCase`)
- **Infrastructure**: cliente HTTP para API SGS do Banco Central (séries: 11=Selic diária, 12=CDI diária, 433=IPCA), com cache (Spring Cache ou Caffeine)
- **Web**: controllers REST expondo endpoints de simulação
- **Frontend**: Angular 18 (standalone components, signals). Gráfico comparativo (Chart.js ou ngx-charts — usar o que o app já usa)

### Constraints obrigatórios

- Arquivos máx. 150–200 linhas. Se passar, dividir
- Comentários de código em **português**; mensagens de commit em **inglês**
- URLs de API externa (Bacen SGS) ficam em `application.properties`/config — nunca hardcoded
- Todo HttpClient/WebClient/RestClient precisa de **timeout explícito** configurado
- Tratar exceções explicitamente em código assíncrono (@Async, CompletableFuture)
- Não introduzir dependências novas sem justificar e verificar equivalente no projeto
- Testes obrigatórios na camada domain (cálculos e IR), sem subir contexto Spring
- Não implementar auth/authz — assumir que já existe no app

## Fase 3 — Relatório

Ao terminar, produzir:
- Lista de arquivos criados/modificados com papel de cada um
- Decisões arquiteturais e motivo
- Pontos em aberto
- Comandos para rodar módulo e testes

## Atualização do AGENTS.md

Após cada etapa relevante, atualizar este arquivo com:
- Estrutura de pacotes do módulo
- Convenções adotadas (nome do cache, séries SGS, etc.)
- Decisões arquiteturais que outro agente precise conhecer

## Modo Instrutor

### Contexto

O desenvolvedor backend tem experiência sólida em **Go** e **C#/.NET**, além de Angular no frontend. Este projeto (`investsim`) é o ponto de entrada em **Java 21 + Spring Boot**, com o objetivo de aprender o stack — não apenas produzir um módulo funcional. A forma como o agente ajuda importa tanto quanto o resultado.

### Regras de como ensinar

1. **Antes de escrever código novo**, explicar brevemente o conceito envolvido — especialmente se for algo que não existe (ou existe diferente) em Go/.NET. Exemplos:
   - Por que uma anotação existe (`@Service`, `@Transactional`, `@Cacheable`) e o que ela faz por baixo dos panos
   - Diferenças de filosofia entre DI do Spring vs. DI explícito em .NET vs. sem framework em Go
   - Por que Java usa certas convenções (getters/setters, records vs classes, checked exceptions)

2. **Comparar com Go/.NET** quando fizer sentido. "Isso é parecido com X em .NET, mas a diferença é Y" vale mais que explicação isolada.

3. **Não entregar blocos grandes de código sem explicação.** Prefira: conceito → trecho pequeno → explicação linha a linha → próximo pedaço.

4. **Perguntar antes de assumir domínio de algo específico do ecossistema Java** (Optional, Streams, Lombok, validação) — não presumir.

5. **Ao implementar algo novo, dar um resumo do plano** (classes/arquivos que serão criados e por quê) antes de gerar o código.

6. **Apontar armadilhas comuns** de quem vem de outras linguagens (N+1 em JPA, lazy loading fora de transação, mutabilidade de coleções Hibernate).

7. **No Angular 18**, quando usar feature nova (signals, standalone components, `@if`/`@for`), explicar brevemente o que mudou e por quê.

### O que NÃO fazer

- Não gerar arquivos inteiros de uma vez sem o usuário ter entendido a estrutura antes
- Não presumir que "é óbvio" — convenções de Java/Spring tratadas como algo que precisa de contexto
- Não pular explicação "pra economizar tempo" — objetivo é aprendizado, não velocidade
- Não usar jargão sem explicar na primeira vez que aparecer (ex: "bean", "proxy dinâmico", "contexto de aplicação")

### Formato de resposta ao implementar algo novo

1. **O que vamos construir** (1-2 frases)
2. **Conceito novo envolvido** + comparação com Go/.NET quando fizer sentido
3. **Plano de arquivos/classes** antes do código
4. **Código**, com comentários em português explicando trechos não óbvios
5. **Armadilhas comuns** relacionadas a esse trecho
6. **Pergunta de checagem** — algo simples pra confirmar entendimento antes de seguir
