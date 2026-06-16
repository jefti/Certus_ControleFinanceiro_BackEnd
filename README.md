# 💰 Certus Controle Financeiro — Back-end

#### *API REST para gestão financeira pessoal e corporativa*

[![Java](https://img.shields.io/badge/Java-17-007396?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-ready-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)
[![JWT](https://img.shields.io/badge/JWT-secured-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)](https://jwt.io/)

[![Status](https://img.shields.io/badge/status-ativo-brightgreen?style=flat-square)]()
[![Tests](https://img.shields.io/badge/tests-92%20passing-brightgreen?style=flat-square&logo=junit5&logoColor=white)]()
[![License](https://img.shields.io/badge/license-MIT-blue?style=flat-square)]()
[![Build](https://img.shields.io/badge/build-maven-C71A36?style=flat-square&logo=apachemaven&logoColor=white)]()

---

## ✨ Sobre o projeto

**Certus Controle Financeiro** é uma plataforma de gestão financeira que centraliza o controle de **títulos a pagar e a receber**, **centros de custo**, **faturamentos recorrentes** e oferece um **dashboard consolidado** (com exportação para Excel) da saúde financeira do usuário.

Este repositório contém a **API REST** que sustenta toda a aplicação — autenticação, autorização por papéis, regras de negócio, persistência e exposição dos endpoints consumidos pelo front-end.

> [!NOTE]
> 🎨 O front-end React + TypeScript vive em [**Certus_ControleFinanceiro_FrontEnd**](https://github.com/jefti/Certus_ControleFinanceiro_FrontEnd).

---

## 📑 Sumário

- [Funcionalidades](#-funcionalidades)
- [Stack & Tecnologias](#-stack--tecnologias)
- [Arquitetura](#-arquitetura)
- [Segurança](#-segurança)
- [Modelagem de Dados](#-modelagem-de-dados)
- [Migrations](#-migrations)
- [Referência da API](#-referência-da-api)
- [Pré-requisitos](#-pré-requisitos)
- [Configuração](#-configuração)
- [Execução Local](#-execução-local)
- [Execução com Docker Compose](#-execução-com-docker-compose)
- [Testes & Cobertura](#-testes--cobertura)
- [Observabilidade](#-observabilidade)
- [Documentação da API](#-documentação-da-api)
- [Deploy](#-deploy)
- [Autores](#-autores)

---

## 🚀 Funcionalidades

| Domínio | Recursos |
| --- | --- |
| 🔐 **Autenticação** | Login com JWT, recuperação de senha por e-mail (código de 6 dígitos com limite de tentativas), expiração e revogação configuráveis |
| 👤 **Usuários** | Cadastro público, autogestão da própria conta via `/me` (consulta, atualização, inativação lógica) |
| 🛡️ **Administração** | Papel `ADMIN` para gestão de contas (listar fluxo de usuários, desativar/reativar) — **sem** acesso a dados financeiros de terceiros |
| 💵 **Títulos** | CRUD de títulos a pagar/receber, controle de vencimento, ativação/inativação |
| 🔁 **Recorrência** | Títulos únicos, semanais, mensais, anuais — geração automática de faturamentos |
| 💳 **Faturamentos** | Parcelas/competências geradas a partir do título, consulta por título e validação de pagamento |
| 🏷️ **Centros de Custo** | Categorização de títulos (N:N), observações livres |
| 📊 **Dashboard** | Indicadores financeiros consolidados por usuário, com **exportação para Excel** |
| ❤️ **Health Check** | Endpoint público de liveness para orquestradores |
| 📖 **Documentação** | Swagger UI / OpenAPI 3 interativo |

---

## 🧰 Stack & Tecnologias

| Camada | Tecnologias |
| :---: | :--- |
| **Linguagem** | Java 17 |
| **Framework** | Spring Boot 3.5 — Web, Data JPA, Security, Validation, Actuator |
| **Segurança** | Spring Security 6 · JWT (jjwt 0.13) · BCrypt · Bucket4j (rate limiting) · OWASP Java HTML Sanitizer |
| **Persistência** | PostgreSQL 16 · Hibernate · **Flyway** (migrations versionadas) |
| **Mapeamento** | Mappers dedicados (componentes Spring) · Lombok |
| **E-mail** | Resend API (templates transacionais) |
| **Exportação** | Alibaba EasyExcel (relatórios `.xlsx`) |
| **Documentação** | Springdoc OpenAPI · Swagger UI |
| **Observabilidade** | Spring Actuator · Micrometer · **Prometheus** · **Grafana** |
| **Testes** | JUnit 5 · Mockito · Spring Boot Test · Spring Security Test · H2 |
| **Build & Qualidade** | Maven · JaCoCo (cobertura com mínimo enforçado) |
| **Containerização** | Docker · Docker Compose |
| **Configuração** | spring-dotenv (`.env`) |

### 📦 Principais dependências

```text
spring-boot-starter-web          ┃ spring-boot-starter-data-jpa
spring-boot-starter-security     ┃ spring-boot-starter-validation
spring-boot-starter-actuator     ┃ micrometer-registry-prometheus
flyway-core / flyway-postgresql  ┃ postgresql
jjwt-api / jjwt-impl / jjwt-gson ┃ bucket4j-core
owasp-java-html-sanitizer        ┃ easyexcel
springdoc-openapi-starter-webmvc ┃ spring-dotenv
lombok                           ┃ h2 (test)
```

> [!NOTE]
> `spring-boot-devtools` está presente apenas com escopo `runtime` + `optional`, para não ser empacotado no artefato de produção.

---

## 🏛️ Arquitetura

API estruturada em **camadas**, com responsabilidades claramente separadas e contratos bem definidos entre elas:

```
src/main/java/com/projeto/financeiro
├── 🎯 controller     → Endpoints REST e contratos HTTP
├── 📝 docs           → Interfaces de documentação OpenAPI (Swagger)
├── ⚙️  service        → Regras de negócio (RecorrenciaCalculator, export/)
├── 💾 repository     → Acesso a dados (Spring Data JPA)
├── 🧱 entity         → Modelo de domínio (JPA / Hibernate) + enums
├── 🔁 dto            → Requests, responses e mappers dedicados
├── 🛡️  security       → Filtros JWT, rate limiting, RBAC, sanitização, OpenAPI
├── ❗ exception      → Exceções de negócio tipadas
├── 🧯 handler        → Tratamento global de erros (respostas genéricas)
└── 🚀 FinanceiroApplication.java
```

### 🔐 Fluxo de autenticação

```
Cliente ──► POST /api/auth/login ──► JwtAuthenticationFilter
                                          │
                                          ▼
                                  AuthenticationManager (Spring Security)
                                          │
                                          ▼
                                  UserDetailsSecurityServer ──► UsuarioRepository
                                          │
                                          ▼
                              JWT assinado (claims: sub, iss, aud, tv) ──► LoginResponse

Cliente ──► /api/** (Bearer) ──► JwtAuthorizationFilter ──► Controller
                                       │
                                       ├─ valida assinatura, expiração, issuer/audience
                                       ├─ recarrega o usuário do banco (papel atual)
                                       └─ compara token_version (revogação)
```

> [!IMPORTANT]
> As *authorities* são recarregadas do banco a cada requisição. Por isso, **mudanças de papel (`USER`↔`ADMIN`) e revogações têm efeito imediato** no próximo request — sem necessidade de reemitir o token.

---

## 🛡️ Segurança

A API foi submetida a uma revisão de segurança defensiva (ver [`docs/RELATORIO_SEGURANCA.md`](./docs/RELATORIO_SEGURANCA.md) e [`docs/RELATORIO_CONTROLE_ACESSO.md`](./docs/RELATORIO_CONTROLE_ACESSO.md)). Controles implementados:

| Controle | Implementação |
| --- | --- |
| **Autorização por dono** | Recursos financeiros escopados pelo usuário autenticado (`usuarioAutenticado()` via `SecurityContextHolder`); conta própria apenas via `/me` |
| **RBAC** | Papéis `USER`/`ADMIN` com `@PreAuthorize("hasRole('ADMIN')")` + trava na cadeia HTTP; primeiro admin promovido por `ADMIN_EMAILS` no startup (fora da API) |
| **Senha** | Hash BCrypt; política de tamanho mínimo via Bean Validation |
| **JWT** | Assinatura + expiração + `issuer`/`audience` validados; revogação por `token_version` (invalida tokens ao trocar senha) |
| **Rate limiting** | Bucket4j em `/api/auth/**` e cadastro (login, reset, forgot) → HTTP 429 |
| **Brute force** | Código de recuperação com limite de tentativas e expiração curta |
| **Validação de entrada** | `@Valid` + Hibernate Validator em todos os `@RequestBody` mutáveis |
| **Sanitização** | OWASP HTML Sanitizer em textos persistidos |
| **Headers** | CSP, `X-Frame-Options: DENY`, HSTS, `Referrer-Policy`, `X-Content-Type-Options` |
| **Actuator** | `health` com `show-details: when-authorized`; métricas em porta de management separada |
| **CORS** | Origens e cabeçalhos restritos |

---

## 🗂️ Modelagem de Dados

Diagrama Entidade-Relacionamento das tabelas principais:

```
┌────────────────────┐         ┌──────────────────────┐         ┌────────────────────┐
│      Usuario       │         │        Titulo        │         │   CentroDeCusto    │
├────────────────────┤         ├──────────────────────┤         ├────────────────────┤
│ id            PK   │──┐      │ id               PK  │      ┌──│ id             PK  │
│ nome               │  │      │ descricao            │      │  │ descricao          │
│ email   (unique)   │  │      │ valor                │      │  │ observacao         │
│ senha (BCrypt)     │  ├─────▶│ id_usuario       FK  │      │  │ id_usuario     FK  │
│ celular (unique)   │  │      │ tipo (PAGAR/RECEBER) │      │  └────────────────────┘
│ data_criacao       │  │      │ recorrencia          │      │           ▲
│ data_inativacao    │  │      │ data_vencimento      │      │           │ N:N
│ token_version      │  │      │ data_inicio          │      │           │
│ role (USER/ADMIN)  │  │      │ data_fim             │      │           │
└────────────────────┘  │      │ ativo                │      │           │
         ▲              │      │ criado_em            │      │           │
         │              │      └──────────────────────┘      │           │
         │              │                │  ▲                │           │
         │              │                ▼  │                │           │
         │              │      ┌──────────────────────┐      │           │
         │              │      │ titulo_centro_custo  │      │           │
         │              │      ├──────────────────────┤      │           │
         │              │      │ id_titulo         FK ┼──────┘           │
         │              │      │ id_centro_custo   FK ┼──────────────────┘
         │              │      └──────────────────────┘
         │              │                │  (Titulo 1 ──▶ N Faturamento)
         │              │                ▼
         │              │      ┌──────────────────────┐
         │              │      │      Faturamento     │
         │              │      ├──────────────────────┤
         │              │      │ id               PK  │
         │              │      │ id_titulo        FK  │
         │              │      │ data_vencimento      │
         │              │      │ valor                │
         │              │      │ data_pagamento       │
         │              │      │ observacao           │
         │              │      └──────────────────────┘
         │              └────────────────────▶ Titulo.id_usuario
         │
         │                      ┌──────────────────────────┐
         │                      │     RecuperacaoSenha     │
         │                      ├──────────────────────────┤
         └─────────────────────▶│ id               PK      │
                                │ id_usuario       FK      │
                                │ codigo  (6 dígitos)      │
                                │ tentativas               │
                                │ ativo                    │
                                │ data_criacao             │
                                │ data_expiracao           │
                                │ data_inativacao          │
                                │ data_utilizacao          │
                                └──────────────────────────┘
```

### 🔗 Relacionamentos (FKs)

| De | Para | Tipo |
| --- | --- | :---: |
| `Titulo.id_usuario` | `Usuario.id` | N : 1 |
| `CentroDeCusto.id_usuario` | `Usuario.id` | N : 1 |
| `RecuperacaoSenha.id_usuario` | `Usuario.id` | N : 1 |
| `Faturamento.id_titulo` | `Titulo.id` | N : 1 |
| `titulo_centro_custo` | `Titulo` + `CentroDeCusto` | N : N |

> [!TIP]
> **Multitenant por usuário:** todo recurso (título, centro de custo, faturamento) é **escopado pelo usuário autenticado** na camada de serviço. O papel `ADMIN` gerencia apenas contas — nunca os dados financeiros de outros usuários.

---

## 🧬 Migrations

Schema versionado com **Flyway** (`src/main/resources/db/migration`):

| Versão | Descrição |
| :---: | --- |
| **V1** | Schema inicial (usuario, titulo, centro_de_custo, titulo_centro_custo) |
| **V2** | Campos temporais de `usuario` para `TIMESTAMPTZ` (Instant) |
| **V3** | Tabela `recuperacao_senha` (recuperação de senha por código) |
| **V4** | `faturamento` + campos de recorrência em `titulo` |
| **V5** | Remoção de `data_pagamento` de `titulo` (movido para faturamento) |
| **V6** | `token_version` em `usuario` (revogação de JWT) |
| **V7** | `tentativas` em `recuperacao_senha` (lockout anti-brute-force) |
| **V8** | `role` em `usuario` (controle de acesso por papel) |

---

## 📡 Referência da API

> Salvo indicação, todos os endpoints exigem `Authorization: Bearer <token>`. Recursos financeiros são escopados pelo usuário autenticado.

### 🔐 Autenticação — `/api/auth` *(público)*
| Método | Rota | Descrição |
| :---: | --- | --- |
| `POST` | `/login` | Autentica e retorna o JWT |
| `POST` | `/forgot-password` | Envia código de recuperação por e-mail |
| `POST` | `/reset-password` | Redefine a senha com o código recebido |

### 👤 Usuários — `/api/usuarios`
| Método | Rota | Acesso | Descrição |
| :---: | --- | --- | --- |
| `POST` | `/cadastrar` | público | Cria conta (nasce como `USER`) |
| `GET` | `/me` | autenticado | Perfil da própria conta |
| `PUT` | `/me` | autenticado | Atualiza a própria conta |
| `DELETE` | `/me` | autenticado | Inativa a própria conta |

### 🛡️ Admin — `/api/admin/usuarios` *(ROLE_ADMIN)*
| Método | Rota | Descrição |
| :---: | --- | --- |
| `GET` | `/` | Lista o fluxo de contas |
| `GET` | `/{id}` | Detalha uma conta |
| `PATCH` | `/{id}/desativar` | Desativa uma conta (exceto admins) |
| `PATCH` | `/{id}/reativar` | Reativa uma conta |

### 💵 Títulos — `/api/titulos`
| Método | Rota | Descrição |
| :---: | --- | --- |
| `POST` | `/cadastrar` | Cria título (gera faturamentos se recorrente) |
| `GET` | `/obter` | Lista títulos do usuário |
| `GET` | `/obter/{id}` | Detalha um título |
| `PUT` | `/atualizar/{id}` | Atualiza um título |
| `DELETE` | `/deletar/{id}` | Inativa um título |

### 💳 Faturamentos — `/api/faturamentos`
| Método | Rota | Descrição |
| :---: | --- | --- |
| `GET` | `/titulo/{tituloId}` | Lista faturamentos de um título |
| `GET` | `/{id}` | Detalha um faturamento |
| `PATCH` | `/{id}/validar` | Valida/registra o pagamento |

### 🏷️ Centros de Custo — `/api/centros-de-custo`
| Método | Rota | Descrição |
| :---: | --- | --- |
| `POST` | `/cadastrar` | Cria centro de custo |
| `GET` | `/obter` · `/obter/{id}` | Lista / detalha |
| `PUT` | `/atualizar/{id}` | Atualiza |
| `DELETE` | `/deletar/{id}` | Remove |

### 📊 Dashboard — `/api/dashboard`
| Método | Rota | Descrição |
| :---: | --- | --- |
| `GET` | `/` | Indicadores consolidados do período |
| `GET` | `/export` | Exporta o dashboard em `.xlsx` |

### ❤️ Health
| Método | Rota | Acesso | Descrição |
| :---: | --- | --- | --- |
| `GET` | `/health` | público | Liveness para orquestradores |

---

## ✅ Pré-requisitos

- ☕ **Java 17+**
- 📦 **Maven 3.8+**
- 🐘 **PostgreSQL 13+** *(ou Docker, para usar o Compose)*
- 🐳 **Docker & Docker Compose** *(opcional, recomendado)*

---

## ⚙️ Configuração

Crie um arquivo **`.env`** na raiz do projeto a partir do template fornecido:

```bash
cp .env.example .env
```

Em seguida, preencha os valores reais (banco de dados, segredo JWT, credenciais do provedor de e-mail, expiração da recuperação de senha e, opcionalmente, `ADMIN_EMAILS`).

> [!IMPORTANT]
> `ADMIN_EMAILS` (lista separada por vírgula) define quais contas são promovidas a `ADMIN` no startup. Deixe vazio se não houver administrador. O usuário precisa estar cadastrado para ser promovido.

> [!WARNING]
> O arquivo `.env` **não é versionado**. Consulte o **[`.env.example`](./.env.example)** para a lista completa de variáveis. Em produção, injete as variáveis pelo orquestrador (Render, Kubernetes, ECS, etc.) em vez de manter um arquivo no servidor.

---

## ▶️ Execução Local

```bash
# Compilar e instalar dependências
mvn clean install

# Subir a aplicação
mvn spring-boot:run
```

A API ficará disponível em **http://localhost:8080**.

> A porta pode ser sobrescrita pela variável `PORT` (útil em provedores de deploy gerenciado).

---

## 🐳 Execução com Docker Compose

O `docker-compose.yaml` provisiona toda a stack: **API + PostgreSQL + Prometheus + Grafana**.

```bash
docker compose up --build
```

| Serviço      | URL                            | Credenciais        |
| ------------ | ------------------------------ | ------------------ |
| 🚀 API        | http://localhost:8080          | —                  |
| 🐘 PostgreSQL | `localhost:5432` (db `certus`) | via `.env`         |
| 📈 Prometheus | http://localhost:9090          | —                  |
| 📊 Grafana    | http://localhost:3000          | `admin` / `admin`  |

Para construir e rodar **apenas** a imagem da aplicação:

```bash
docker build -t certus-financeiro-backend .
docker run --env-file .env -p 8080:8080 certus-financeiro-backend
```

---

## 🧪 Testes & Cobertura

```bash
# Executar toda a suíte (unitários + integração)
mvn test

# Relatório de cobertura JaCoCo
mvn verify   # gera target/site/jacoco/index.html
```

A suíte cobre **92 testes** — serviços (regras de negócio, autorização por dono, RBAC, bootstrap de admin), mappers, filtros de rate limiting, validação e fluxos de integração ponta-a-ponta (`@SpringBootTest` + MockMvc sobre H2). A cobertura mínima é **enforçada** pelo JaCoCo no build.

---

## 📡 Observabilidade

Endpoints expostos pelo **Spring Boot Actuator** (em porta de management separada):

| Endpoint | Descrição |
| --- | --- |
| `GET /actuator/health` | Health check (`show-details: when-authorized`) |
| `GET /actuator/info` | Metadados de build |
| `GET /actuator/prometheus` | Métricas no formato Prometheus |

As métricas são coletadas pelo Prometheus (`monitoring/prometheus/prometheus.yml`) e podem ser exploradas no Grafana já provisionado pelo Compose.

---

## 📖 Documentação da API

Após subir a aplicação, a documentação interativa fica disponível em:

- 🧪 **Swagger UI:** http://localhost:8080/swagger-ui.html
- 📜 **OpenAPI JSON:** http://localhost:8080/v3/api-docs

**Grupos de endpoints:**

`Autenticação` · `Usuários` · `Admin` · `Títulos` · `Faturamentos` · `Centros de Custo` · `Dashboard` · `Health`

---

## ☁️ Deploy

O projeto inclui um **`render.yaml`** pronto para deploy no [Render](https://render.com/) (runtime Docker).
Em qualquer provedor, basta configurar as variáveis listadas em [`.env.example`](./.env.example) com os valores reais.

---

## 👥 Autores

<div align="center">

| [<img src="https://github.com/marcelopinotti.png" width="100" style="border-radius:50%"><br/><sub><b>Marcelo Pinotti</b></sub>](https://github.com/marcelopinotti) | [<img src="https://github.com/jefti.png" width="100" style="border-radius:50%"><br/><sub><b>Jefti Meira</b></sub>](https://github.com/jefti) |
| :---: | :---: |
| Back-end | Full-stack |

</div>

---

*Feito com ☕ e Spring Boot.*
