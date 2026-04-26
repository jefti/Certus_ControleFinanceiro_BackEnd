# Certus Controle Financeiro - Back-end

API REST do projeto **Certus Controle Financeiro**, responsável pelas regras de negócio, autenticação, persistência de dados e exposição dos endpoints consumidos pelo front-end.

Este repositório contém apenas o back-end. O front-end da aplicação está disponível em:

- [Certus Controle Financeiro - Front-end](https://github.com/jefti/Certus_ControleFinanceiro_FrontEnd)

## Visão Geral

O back-end foi planejado para suportar o fluxo principal da aplicação financeira:

- login
- cadastro de usuário
- recuperação de senha
- gerenciamento de dados do usuário
- cadastros das tabelas de domínio
- dashboard interativo com dados financeiros

## Status do Projeto

Projeto em desenvolvimento.

A API está sendo estruturada em camadas para manter a separação entre entidades, regras de negócio, persistência e exposição dos endpoints.

## Tecnologias Utilizadas

- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- JWT
- PostgreSQL
- Flyway
- Lombok
- ModelMapper
- Springdoc OpenAPI
- Maven

## Arquitetura da API

O projeto segue a ideia de API em camadas, com responsabilidades separadas entre:

- controllers
- services
- repositories
- entities
- DTOs
- tratamento global de exceções
- segurança e autenticação

Esse modelo facilita manutenção, testes e evolução do sistema ao longo do desenvolvimento.

## Funcionalidades Previstas

- autenticação e autorização com JWT
- cadastro e manutenção de usuários
- recuperação de senha
- controle de títulos financeiros
- controle de centros de custo
- dashboard financeiro interativo
- documentação de endpoints com Swagger/OpenAPI

## Domínios Principais

Os principais módulos planejados para a API são:

- usuários
- autenticação
- títulos
- centros de custo
- dashboard

## Integração com o Front-end

Esta API será consumida pelo front-end React do projeto:

- [Front-end React + TypeScript](https://github.com/jefti/Certus_ControleFinanceiro_FrontEnd)

O objetivo é oferecer os endpoints necessários para:

- login e sessão de usuário
- cadastro de usuários
- operações cadastrais do sistema
- consulta de dados consolidados no dashboard

## Como Executar Localmente

### Pré-requisitos

- Java 17+
- Maven 3.8+
- PostgreSQL

### Banco de Dados

Crie um banco local para a aplicação. Exemplo:

```sql
CREATE DATABASE certus;
```

### Variáveis de Ambiente

O projeto utiliza configuração externa para dados sensíveis e conexão com o banco.

Exemplo:

```env
DATABASE_URL=jdbc:postgresql://localhost:5432/certus
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=sua_senha
JWT_SECRET_KEY=sua_chave_com_pelo_menos_32_caracteres
MAIL_FROM=onboarding@resend.dev
RESEND_API_KEY=re_sua_chave_da_resend
PASSWORD_RECOVERY_EXPIRATION_MINUTES=15
```

Observação:

- o arquivo `application.properties` pode ficar versionado
- os valores reais sensíveis não devem ser enviados ao repositório

### Instalação e Execução

```bash
mvn clean install
```

```bash
mvn spring-boot:run
```

Por padrão, a API será disponibilizada localmente em:

```text
http://localhost:8080
```

Em ambientes de deploy, a aplicação também aceita a porta definida pela variável de ambiente `PORT`.

Documentação Swagger/OpenAPI:

```text
http://localhost:8080/swagger-ui.html
http://localhost:8080/v3/api-docs
```

### Deploy com Docker

Para gerar a imagem localmente:

```bash
docker build -t certus-financeiro-backend .
```

Para executar o container com variáveis de ambiente:

```bash
docker run --env-file .env -p 8080:8080 certus-financeiro-backend
```

Para deploy em provedores como Render, configure as variáveis de ambiente do serviço com os valores de banco, JWT, email e recuperação de senha.

## Configuração da Aplicação

A configuração da API fica centralizada em `application.properties` ou `application.yaml`, com leitura de valores via ambiente para:

- conexão com PostgreSQL
- segurança JWT
- envio de email via API HTTP
- configurações de execução

## Endpoints Planejados

Os grupos principais de endpoints previstos para a API incluem:

- autenticação
- usuários
- títulos
- centros de custo
- dashboard

Conforme a API evoluir, a documentação detalhada dos endpoints poderá ser consultada também via Swagger/OpenAPI.

## Documentação Complementar

Para acompanhar a camada de interface e a integração completa do sistema, consulte:

- [Repositório do Front-end](https://github.com/jefti/Certus_ControleFinanceiro_FrontEnd)

## Autores

Projeto desenvolvido para trabalho acadêmico.
