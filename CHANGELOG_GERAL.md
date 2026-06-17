# Changelog Geral

Este changelog consolida as entregas do **Certus Controle Financeiro** considerando os repositórios de front-end e back-end em conjunto. A revisão atual usa as branches `feat/changelog` dos dois repositórios, sincronizadas com a `main`, conforme orientação da equipe.

Arquivos relacionados:

- [Changelog do back-end](./CHANGELOG.md)
- [Métricas consolidadas do projeto](./METRICAS_PROJETO.md)
- Changelog do front-end no repositório `Certus_ControleFinanceiro_FrontEnd`

## Sprint 001

**Período:** 02/03/2026 a 07/04/2026

### Entregas consolidadas

- definição do tema do projeto, stack e organização inicial
- criação dos repositórios do front-end e back-end
- inicialização do front-end com React + Vite
- inicialização do back-end com Spring Boot

### Resultado

- projeto estruturado para evolução em duas camadas
- base inicial do produto criada no Git

## Sprint 002

**Janela de implementação no Git:** 08/04/2026 a 22/04/2026

### Front-end

- comunicação inicial com a API
- contexto global de autenticação
- rotas públicas e privadas
- layouts público e autenticado
- tela de login
- tela de cadastro
- recuperação de senha
- homepage pública inicial

### Back-end

- tratamento padronizado de erros
- entidades e DTOs iniciais de usuário, título e centro de custo
- migrations iniciais com Flyway
- autenticação JWT
- login por e-mail
- configuração de senha com BCrypt
- endpoint de health check
- documentação Swagger inicial
- primeiros testes unitários com JaCoCo

### User Stories

- `US01`, `US02`, `US04`, `US21`, `US22`, `US24`

## Sprint 003

**Período:** 23/04/2026 a 20/05/2026

### Front-end

- landing page
- organização de rotas
- página de centros de custo
- página de títulos
- home autenticada com atalhos
- refinamentos de navegação e header
- integração mais madura dos fluxos de autenticação

### Back-end

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

### User Stories

- `US03`, `US05`, `US16`, `US17`, `US18`, `US23`, `US26`, `US27`

## Sprint 004

**Período:** 21/05/2026 a 27/05/2026

### Front-end

- liberação da página de centros de custo no fluxo autenticado
- cadastro e serviços de centro de custo
- integração de requisições com o back-end
- melhorias de responsividade
- primeiros ajustes para dashboard

### Back-end

- modelagem de recorrência
- criação da entidade `Faturamento`
- geração automática de faturamentos por título
- endpoints de listagem e validação de faturamentos
- testes da lógica recorrente e de faturamento
- documentação Swagger do fluxo financeiro
- Docker Compose, Prometheus e melhorias de observabilidade

### User Stories

- `US06`, `US07`, `US08`, `US09`, `US10`, `US11`, `US12`, `US13`, `US14`, `US15`, `US30`, `US31`

## Sprint 005

**Período:** 28/05/2026 a 09/06/2026

### Front-end

- implementação da tela de dashboard
- indicadores visuais de receitas, despesas e saldo
- suporte a fluxo de usuário expirado

### Back-end

- controller e service de dashboard
- consultas agregadas para totais financeiros
- DTOs específicos para dashboard
- documentação OpenAPI ampliada
- ajustes de cobertura e build

### User Stories

- `US19`, `US20`, `US25`, `US28`, `US29`, `US32`

## Sprint 007

**Período:** 10/06/2026 a 14/06/2026

### Front-end

- estruturação dos testes do front-end

### Back-end

- correções no relatório de testes do back-end
- inclusão de dependências de validação e segurança
- validação de payloads de entrada nos endpoints
- sanitização de textos persistidos
- limitação de tentativas de login
- teste automatizado para validar rate limit no fluxo de autenticação

### User Stories

- `US31`, `US32`

### Commits de referência

**Front-end**

- **2026-06-11** `tests: tests setup`
- **2026-06-11** `Merge pull request #33 from jefti/feat/testes`

**Back-end**

- **2026-06-11** `feat: test report fixes`
- **2026-06-11** `Merge pull request #41 from jefti/feat/relatorioTestes`
- **2026-06-12** `build(security): add validation and protection dependencies`
- **2026-06-12** `feat(validation): validate incoming request payloads`
- **2026-06-12** `feat(security): sanitize persisted text inputs`
- **2026-06-12** `feat(auth): rate limit and validate login attempts`
- **2026-06-12** `test(auth): verify login rate limit in security chain`
- **2026-06-12** `Merge pull request #42 from jefti/feat/relatorioTestes`

## Sprint 008

**Período:** 15/06/2026 a 17/06/2026

### Front-end

- atualização da documentação do README
- revisão textual de informações desatualizadas
- criação e revisão do changelog do front-end
- criação do changelog geral do projeto
- criação das métricas consolidadas do projeto
- inclusão dos links de documentação no README

### Back-end

- integração da branch `develop` atualizada na branch principal de trabalho
- restauração dos documentos do projeto no repositório back-end
- inclusão de links para changelog e métricas no README do back-end

### Documentação do projeto

- criação e revisão do changelog do front-end
- criação e revisão do changelog do back-end
- criação do changelog geral do projeto
- criação das métricas consolidadas do projeto
- inclusão dos links de changelog e métricas nos READMEs

### User Stories

- `US32`, `US33`

### Commits de referência

**Front-end**

- **2026-06-16** `docs: atualiza README com documentação completa do front-end`
- **2026-06-16** `Merge pull request #34 from jefti/feat/readme`
- **2026-06-16** `Merge pull request #35 from jefti/develop`
- **2026-06-16** `fix: exchange outdated text information`
- **2026-06-16** `Merge pull request #36 from jefti/fix/textErrors`
- **2026-06-17** `Doc: changelogs e metrics`

**Back-end**

- **2026-06-16** `Merge pull request #43 from jefti/develop`
- **2026-06-17** restauração dos documentos `CHANGELOG.md`, `CHANGELOG_GERAL.md`, `METRICAS_PROJETO.md` e links no `README.md`

## Resultado Geral

- 33/33 user stories concluídas
- autenticação, cadastro, recuperação de senha e navegação protegida
- CRUD de centros de custo, títulos e faturamentos
- suporte a receitas e despesas recorrentes e não recorrentes
- dashboard financeiro consolidado
- documentação técnica, Swagger/OpenAPI, changelogs e métricas
- testes unitários, integração, componentes e E2E
- observabilidade, deploy e melhorias de segurança
