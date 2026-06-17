# CHANGELOG

Este arquivo registra as funcionalidades implementadas no **back-end** do projeto **Certus Controle Financeiro**, organizadas por sprint a partir do histórico real do Git.

Observações:

- este documento cobre apenas as entregas do **back-end**
- somente mudanças presentes na branch atual sincronizada com a `main` entram como commits de referência
- merges e commits exclusivamente administrativos foram consolidados quando não representavam funcionalidade nova
- as datas seguem o histórico do Git

---

## Sprint 001

**Período:** 02/03/2026 a 07/04/2026

### Entregas

- inicialização do repositório back-end
- criação da base do projeto com **Spring Boot**
- definição da estrutura inicial da API REST
- preparação para persistência, autenticação e evolução dos módulos financeiros

### Commits de referência

- **2026-04-07** `first commit`

---

## Sprint 002

**Período:** 08/04/2026 a 22/04/2026

### Funcionalidades implementadas

- tratamento padronizado de erros
- entidades e DTOs iniciais de usuário, título e centro de custo
- migrations iniciais com Flyway
- autenticação JWT
- login por e-mail
- configuração de senha com BCrypt
- endpoint de health check
- documentação Swagger inicial
- primeiros testes unitários com JaCoCo

### User Stories atendidas

- `US01` Registro de nova conta
- `US02` Login da conta
- `US04` Recuperação de senha
- `US24` Redirecionamento sem autorização
- `US26` Manual técnico
- `US27` Integração com API

### Commits de referência

- **2026-04-08** `feat: error handling`
- **2026-04-08** `feat: user entity`
- **2026-04-08** `feat: flyway migrations`
- **2026-04-09** `feat: jwt authentication`
- **2026-04-09** `feat: login with email`
- **2026-04-10** `feat: password encryption`
- **2026-04-11** `feat: health check`
- **2026-04-14** `feat: swagger documentation`
- **2026-04-21** `test: jacoco setup`

---

## Sprint 003

**Período:** 23/04/2026 a 20/05/2026

### Funcionalidades implementadas

- recuperação de senha com persistência, serviço e endpoints
- envio de e-mail com Resend
- padronização temporal com `Instant`
- actuator e health check
- preparação de deploy no Render
- CRUD de centro de custo
- CRUD de título
- escopo de dados por usuário autenticado
- validações de entrada
- segurança com usuário ativo e method security

### User Stories atendidas

- `US03` Logout do sistema
- `US04` Recuperação de senha
- `US05` Atualização de cadastro
- `US16` Cadastrar centro de gastos
- `US17` Editar centro de gastos existente
- `US18` Excluir centro de gastos
- `US23` Página/rota inexistente tratada
- `US27` Integração com API

### Commits de referência

- **2026-04-24** `feat: password recovery`
- **2026-04-24** `feat: resend email integration`
- **2026-04-28** `feat: authenticated user scope`
- **2026-05-05** `feat: cost center CRUD`
- **2026-05-08** `feat: title CRUD`
- **2026-05-12** `feat: method security`
- **2026-05-16** `feat: render deploy setup`
- **2026-05-19** `fix: validation and date handling`

---

## Sprint 004

**Período:** 21/05/2026 a 27/05/2026

### Funcionalidades implementadas

- modelagem de recorrência
- criação da entidade `Faturamento`
- geração automática de faturamentos por título
- endpoints de listagem e validação de faturamentos
- testes da lógica recorrente e de faturamento
- documentação Swagger do fluxo financeiro
- Docker Compose, Prometheus e melhorias de observabilidade

### User Stories atendidas

- `US06` Criar título financeiro
- `US07` Editar título financeiro
- `US08` Associar gastos a uma fonte
- `US09` Excluir/inativar título
- `US10` Controlar vencimento
- `US11` Registrar pagamento
- `US12` Controlar recorrência
- `US13` Associar receitas a uma fonte
- `US14` Visualizar faturamentos
- `US15` Validar faturamento
- `US30` Observabilidade
- `US31` Plano de testes

### Commits de referência

- **2026-05-21** `feat: recurring billing model`
- **2026-05-22** `feat: faturamento entity`
- **2026-05-23** `feat: generate billings from titles`
- **2026-05-24** `feat: billing endpoints`
- **2026-05-25** `test: recurrence and billing logic`
- **2026-05-26** `docs: swagger financial flow`
- **2026-05-27** `feat: docker compose and observability`

---

## Sprint 005

**Período:** 28/05/2026 a 09/06/2026

### Funcionalidades implementadas

- controller e service de dashboard
- consultas agregadas para totais financeiros
- DTOs específicos para dashboard
- documentação OpenAPI ampliada
- ajustes de cobertura e build

### User Stories atendidas

- `US19` Visualizar resumo do mês atual
- `US20` Gráfico de gastos por categoria
- `US25` Manual do usuário
- `US28` Indicadores financeiros
- `US29` Consolidação de dados para dashboard
- `US32` Relatório da sprint

### Commits de referência

- **2026-06-02** `feat: dashboard service`
- **2026-06-03** `feat: dashboard controller`
- **2026-06-04** `feat: dashboard DTOs`
- **2026-06-06** `docs: expand OpenAPI documentation`
- **2026-06-09** `test: coverage and build adjustments`

---

## Sprint 007

**Período:** 10/06/2026 a 14/06/2026

### Funcionalidades implementadas

- correções no relatório de testes do back-end
- inclusão de dependências de validação e segurança
- validação de payloads de entrada nos endpoints
- sanitização de textos persistidos
- limitação de tentativas de login
- teste automatizado para validar rate limit no fluxo de autenticação

### User Stories atendidas

- `US31` Plano de testes
- `US32` Relatório da sprint

### Commits de referência

- **2026-06-11** `a07beaf` `feat: test report fixes`
- **2026-06-11** `74e40c1` `Merge pull request #41 from jefti/feat/relatorioTestes`
- **2026-06-12** `d2b8a14` `build(security): add validation and protection dependencies`
- **2026-06-12** `d67fa89` `feat(validation): validate incoming request payloads`
- **2026-06-12** `94bb79b` `feat(security): sanitize persisted text inputs`
- **2026-06-12** `665447e` `feat(auth): rate limit and validate login attempts`
- **2026-06-12** `6bb49d1` `test(auth): verify login rate limit in security chain`
- **2026-06-12** `73a113f` `Merge pull request #42 from jefti/feat/relatorioTestes`

---

## Sprint 008

**Período:** 15/06/2026 a 17/06/2026

### Funcionalidades implementadas

- integração da branch `develop` atualizada na branch principal de trabalho
- restauração do changelog do back-end
- restauração do changelog geral do projeto
- restauração das métricas consolidadas do projeto
- inclusão dos links de changelog e métricas no README do back-end

### User Stories atendidas

- `US32` Relatório da sprint
- `US33` Changelog do projeto

### Commits de referência

- **2026-06-16** `7452d68` `Merge pull request #43 from jefti/develop`
- **2026-06-17** restauração dos documentos `CHANGELOG.md`, `CHANGELOG_GERAL.md`, `METRICAS_PROJETO.md` e links no `README.md`

---

## Resumo de evolução do back-end

- autenticação JWT com login, cadastro, recuperação de senha e segurança por usuário autenticado
- CRUDs de usuários, centros de custo, títulos e faturamentos
- suporte a receitas e despesas recorrentes e não recorrentes
- dashboard financeiro consolidado por usuário
- validações, sanitização de entrada e proteção contra excesso de tentativas de login
- documentação Swagger/OpenAPI, testes automatizados, cobertura com JaCoCo e observabilidade
