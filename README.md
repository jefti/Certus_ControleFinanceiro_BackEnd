# Certus Controle Financeiro — Back-end

[![Java](https://img.shields.io/badge/Java-17-007396?logo=java&logoColor=white)](https://openjdk.org/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791?logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-ready-2496ED?logo=docker&logoColor=white)](https://www.docker.com/)
[![Status](https://img.shields.io/badge/status-em%20desenvolvimento-yellow)]()

API REST do projeto **Certus Controle Financeiro**, responsável pelas regras de negócio, autenticação, persistência de dados e exposição dos endpoints consumidos pelo front-end.

> Este repositório contém apenas o **back-end**. O front-end está em [Certus Controle Financeiro — Front-end](https://github.com/jefti/Certus_ControleFinanceiro_FrontEnd).

---

## Sumário

- [Visão Geral](#visão-geral)
- [Tecnologias](#tecnologias)
- [Arquitetura](#arquitetura)
- [Domínios e Funcionalidades](#domínios-e-funcionalidades)
- [Pré-requisitos](#pré-requisitos)
- [Configuração](#configuração)
- [Execução Local](#execução-local)
- [Execução com Docker Compose](#execução-com-docker-compose)
- [Observabilidade](#observabilidade)
- [Documentação da API](#documentação-da-api)
- [Deploy](#deploy)
- [Integração com o Front-end](#integração-com-o-front-end)
- [Autores](#autores)

---

## Visão Geral

O back-end foi planejado para suportar o fluxo principal da aplicação financeira:

- Login e sessão de usuário
- Cadastro de usuário
- Recuperação de senha (via e-mail transacional)
- Gerenciamento de dados do usuário
- Cadastros das tabelas de domínio
- Dashboard interativo com dados financeiros consolidados

**Status:** projeto em desenvolvimento ativo. A API está estruturada em camadas para manter a separação entre entidades, regras de negócio, persistência e exposição dos endpoints.

---

## Tecnologias

| Categoria          | Stack                                                            |
| ------------------ | ---------------------------------------------------------------- |
| Linguagem          | Java 17                                                          |
| Framework          | Spring Boot 3.5 (Web, Data JPA, Security, Actuator, DevTools)    |
| Segurança          | Spring Security + JWT (jjwt 0.13)                                |
| Persistência       | PostgreSQL 16, Flyway (migrations)                               |
| Mapeamento         | ModelMapper, Lombok                                              |
| Documentação       | Springdoc OpenAPI / Swagger UI                                   |
| Observabilidade    | Spring Boot Actuator, Micrometer, Prometheus, Grafana            |
| Build              | Maven, JaCoCo (cobertura de testes)                              |
| Containerização    | Docker, Docker Compose                                           |
| Configuração       | spring-dotenv (`.env`)                                           |

---

## Arquitetura

API em camadas, com responsabilidades bem separadas:

```
src/main/java/com/projeto/financeiro
├── controller     # Exposição dos endpoints REST
├── service        # Regras de negócio
├── repository     # Acesso a dados (Spring Data JPA)
├── entity         # Modelo de domínio (JPA)
├── dto            # Requests, responses e mappers
├── security       # Configuração de segurança, filtros JWT, OpenAPI
├── exception      # Exceções de negócio
├── handler        # Tratamento global de exceções
└── FinanceiroApplication.java
```

Esse modelo facilita manutenção, testes e evolução do sistema.

---

## Domínios e Funcionalidades

**Domínios principais**

- Usuários
- Autenticação
- Títulos financeiros
- Centros de custo
- Dashboard

**Funcionalidades previstas**

- Autenticação e autorização com JWT
- Cadastro e manutenção de usuários
- Recuperação de senha com expiração configurável
- Controle de títulos financeiros
- Controle de centros de custo
- Dashboard financeiro interativo
- Documentação interativa via Swagger/OpenAPI

---

## Pré-requisitos

- Java 17+
- Maven 3.8+
- PostgreSQL 13+ (ou Docker, para uso via Compose)
- (Opcional) Docker e Docker Compose para execução completa do stack

---

## Configuração

### Banco de dados local

```sql
CREATE DATABASE certus;
```

### Variáveis de ambiente

Crie um arquivo `.env` na raiz do projeto (não versionado) com as seguintes variáveis:

```env
# Banco de dados
DATABASE_URL=jdbc:postgresql://localhost:5432/certus
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=sua_senha

# JWT
JWT_SECRET_KEY=sua_chave_com_pelo_menos_32_caracteres

# Recuperação de senha
PASSWORD_RECOVERY_EXPIRATION_MINUTES=15

# Envio de e-mail (Resend)
MAIL_FROM=onboarding@resend.dev
RESEND_API_KEY=re_sua_chave_da_resend
```

> O arquivo `application.yaml` é versionado, mas valores sensíveis devem ser fornecidos exclusivamente via variáveis de ambiente.

---

## Execução Local

```bash
# Compilar
mvn clean install

# Executar
mvn spring-boot:run
```

A API ficará disponível em:

```
http://localhost:8080
```

A porta também pode ser sobrescrita pela variável de ambiente `PORT` (útil em provedores de deploy).

---

## Execução com Docker Compose

O `docker-compose.yml` provisiona toda a stack: aplicação, PostgreSQL, Prometheus e Grafana.

```bash
docker compose up --build
```

| Serviço     | URL                          | Credenciais         |
| ----------- | ---------------------------- | ------------------- |
| API         | http://localhost:8080        | —                   |
| PostgreSQL  | localhost:5432 (db `certus`) | via `.env`          |
| Prometheus  | http://localhost:9090        | —                   |
| Grafana     | http://localhost:3000        | `admin` / `admin`   |

Para construir/rodar apenas a imagem da aplicação:

```bash
docker build -t certus-financeiro-backend .
docker run --env-file .env -p 8080:8080 certus-financeiro-backend
```

---

## Observabilidade

Endpoints expostos pelo Spring Boot Actuator:

- `GET /actuator/health` — health check da aplicação
- `GET /actuator/info`   — metadados
- `GET /actuator/prometheus` — métricas no formato Prometheus

Métricas são coletadas pelo Prometheus (configuração em `monitoring/prometheus/prometheus.yml`) e podem ser visualizadas no Grafana já provisionado pelo Compose.

---

## Documentação da API

Após subir a aplicação, a documentação interativa fica disponível em:

```
http://localhost:8080/swagger-ui.html
http://localhost:8080/v3/api-docs
```

Grupos principais de endpoints:

- Autenticação
- Usuários
- Títulos
- Centros de Custo
- Dashboard

---

## Deploy

O projeto inclui um `render.yaml` para deploy no [Render](https://render.com/). Em qualquer provedor, basta configurar as variáveis de ambiente listadas em [Configuração](#configuração) com os valores reais de banco, JWT, e-mail e recuperação de senha.

---

## Integração com o Front-end

Esta API é consumida pelo front-end React + TypeScript do projeto:

- [Front-end React + TypeScript](https://github.com/jefti/Certus_ControleFinanceiro_FrontEnd)

O objetivo é oferecer endpoints para:

- Login e sessão de usuário
- Cadastro de usuários
- Operações cadastrais do sistema
- Consulta de dados consolidados no dashboard

---

## Autores

- Marcelo Pinotti — [GitHub](https://github.com/marcelopinotti)
- Jefti Meira — [GitHub](https://github.com/jefti)
