# Métricas do Projeto

Este documento consolida as métricas dos repositórios **Certus Controle Financeiro Front-end** e **Certus Controle Financeiro Back-end**.

- **Data da medição:** 17/06/2026
- **Front-end analisado:** branch `feat/changelog`, commit `4fc240a`
- **Back-end analisado:** branch `feat/changelog`, commit `7452d68`
- **Critério:** branches atuais usadas porque foram sincronizadas com a `main`, conforme orientação da equipe
- **Período do Git analisado:** 07/04/2026 a 17/06/2026
- **Backlog:** 7 épicos e **33/33 user stories concluídas**

> Observação: a conclusão das 33 user stories foi considerada a partir da validação da equipe, mesmo que algum export antigo do backlog não reflita o estado final.

## Metodologia

- Commits totais incluem merge commits.
- Commits de implementação excluem merge commits.
- Autores foram normalizados para evitar duplicidade de pessoas.
- Linhas de código consideram arquivos versionados de aplicação, testes e configuração.
- Foram excluídos diretórios de build, relatórios locais, logs, binários e lockfiles.
- Horas estimadas usam dias-pessoa ativos no Git: 1 pessoa com commit em 1 dia = 1 dia-pessoa.
- Cenário base de esforço: 4h por dia-pessoa ativo.
- Os documentos `CHANGELOG.md`, `CHANGELOG_GERAL.md`, `METRICAS_PROJETO.md` e links nos READMEs entram como entregas da Sprint 008.

## Resumo Executivo

| Métrica | Valor |
| --- | ---: |
| Repositórios analisados | 2 |
| Contribuidores únicos normalizados | 5 |
| Épicos do backlog | 7 |
| User stories concluídas | 33/33 |
| Commits totais | 255 |
| Commits de implementação, sem merges | 178 |
| Merge commits | 77 |
| Pull requests integrados por merge | 63 |
| Arquivos versionados | 201 |
| Arquivos de código/teste/configuração contados | 177 |
| Linhas de código, testes e configuração | 14.353 |
| Linhas de aplicação | 11.473 |
| Linhas de testes | 2.467 |
| Linhas de configuração | 413 |
| Linhas adicionadas no histórico, sem merges | 21.487 |
| Linhas removidas no histórico, sem merges | 4.788 |
| Dias-pessoa ativos no Git | 52 |
| Horas estimadas, 3h/dia | 156h |
| Horas estimadas, 4h/dia | 208h |
| Horas estimadas, 6h/dia | 312h |

## Métricas por Repositório

| Métrica | Front-end `feat/changelog` | Back-end `feat/changelog` |
| --- | ---: | ---: |
| Primeiro commit | 07/04/2026 | 07/04/2026 |
| Último commit analisado | 17/06/2026 | 16/06/2026 |
| Commits totais | 78 | 177 |
| Commits sem merge | 49 | 129 |
| Merge commits | 29 | 48 |
| Pull requests integrados por merge | 28 | 35 |
| Arquivos versionados | 91 | 110 |
| Arquivos de código/teste/configuração contados | 75 | 102 |
| Linhas totais contadas | 8.170 | 6.183 |
| Linhas de aplicação | 7.774 | 3.699 |
| Linhas de testes | 314 | 2.153 |
| Linhas de configuração | 82 | 331 |
| Linhas de documentação Markdown | 945 | 312 |
| Linhas adicionadas, sem merges | 12.901 | 8.586 |
| Linhas removidas, sem merges | 3.677 | 1.111 |

## Contribuições por Autor

| Autor normalizado | Commits totais | Commits sem merge |
| --- | ---: | ---: |
| Jefti Meira | 161 | 94 |
| Marcelo Pinotti | 83 | 73 |
| Luis Leal | 8 | 8 |
| Hugo Felipe | 2 | 2 |
| Neemias Borges | 1 | 1 |

## Linhas por Tipo de Arquivo

### Front-end

| Tipo | Arquivos | Linhas |
| --- | ---: | ---: |
| `.css` | 15 | 3.627 |
| `.tsx` | 27 | 3.576 |
| `.ts` | 31 | 931 |
| `.js` | 1 | 23 |
| `.html` | 1 | 13 |

### Back-end

| Tipo | Arquivos | Linhas |
| --- | ---: | ---: |
| `.java` | 90 | 5.633 |
| `.xml` | 1 | 249 |
| `.yml` | 3 | 120 |
| `.sql` | 5 | 105 |
| `.yaml` | 2 | 73 |
| `.properties` | 1 | 3 |

## Qualidade e Testes

| Métrica | Valor |
| --- | ---: |
| Arquivos de teste mapeados | 22 |
| Cenários/casos de teste mapeados | 89 |
| Execuções de teste validadas nesta revisão | 93 |
| Testes unitários/componentes do front-end | 13/13 passando |
| Testes E2E do front-end | 6/6 passando |
| Testes do back-end | 74/74 passando |
| Build back-end | Sucesso |
| Cobertura front-end, statements | 50,54% |
| Cobertura front-end, branches | 58,33% |
| Cobertura front-end, functions | 31,57% |
| Cobertura front-end, lines | 56,79% |
| Cobertura geral back-end, instructions | 82,33% |
| Cobertura geral back-end, branches | 60,77% |
| JaCoCo check configurado no back-end | Aprovado |

Observação: a validação final passou com as suítes de front-end, E2E e back-end executadas com sucesso.

## Métricas de Arquitetura

### Front-end

| Item | Quantidade |
| --- | ---: |
| Páginas React | 11 |
| Componentes reutilizáveis | 6 |
| Serviços de API | 10 |
| Arquivos de rotas | 4 |
| Arquivos de tipos | 6 |
| Arquivos de validação | 4 |
| Cenários E2E | 2 |
| Navegadores no E2E | Chromium, Firefox, WebKit |

### Back-end

| Item | Quantidade |
| --- | ---: |
| Controllers | 7 |
| Services | 16 |
| Repositories | 5 |
| Entidades/enums de domínio | 8 |
| Arquivos de DTO/mappers | 24 |
| Migrations Flyway | 5 |
| Anotações de mapeamento HTTP | 28 |
| Classes analisadas pelo JaCoCo | 58 |

## Velocity por Sprint

| Sprint | Período | Commits totais | Commits sem merge | User stories concluídas | Pontos estimados | Horas estimadas |
| --- | --- | ---: | ---: | ---: | ---: | ---: |
| Sprint 001 | 02/03 a 07/04 | 3 | 3 | 0 | 0 | 4h |
| Sprint 002 | 08/04 a 22/04 | 94 | 71 | 6 | 28 | 88h |
| Sprint 003 | 23/04 a 20/05 | 63 | 38 | 8 | 20 | 36h |
| Sprint 004 | 21/05 a 27/05 | 53 | 41 | 12 | 44 | 40h |
| Sprint 005 | 28/05 a 09/06 | 25 | 15 | 5 | 9 | 20h |
| Sprint 007 | 10/06 a 14/06 | 10 | 7 | 1 | 1 | 8h |
| Sprint 008 | 15/06 a 17/06 | 7 | 3 | 1 | 1 | 12h |

## Commits da Sprint 007

### Front-end

- **2026-06-11** `9d3feae` `tests: tests setup`
- **2026-06-11** `5dd8e51` `Merge pull request #33 from jefti/feat/testes`

### Back-end

- **2026-06-11** `a07beaf` `feat: test report fixes`
- **2026-06-11** `74e40c1` `Merge pull request #41 from jefti/feat/relatorioTestes`
- **2026-06-12** `d2b8a14` `build(security): add validation and protection dependencies`
- **2026-06-12** `d67fa89` `feat(validation): validate incoming request payloads`
- **2026-06-12** `94bb79b` `feat(security): sanitize persisted text inputs`
- **2026-06-12** `665447e` `feat(auth): rate limit and validate login attempts`
- **2026-06-12** `6bb49d1` `test(auth): verify login rate limit in security chain`
- **2026-06-12** `73a113f` `Merge pull request #42 from jefti/feat/relatorioTestes`

## Commits da Sprint 008

### Front-end

- **2026-06-16** `83a53f4` `docs: atualiza README com documentação completa do front-end`
- **2026-06-16** `3465dba` `Merge pull request #34 from jefti/feat/readme`
- **2026-06-16** `ece6862` `Merge pull request #35 from jefti/develop`
- **2026-06-16** `020b817` `fix: exchange outdated text information`
- **2026-06-16** `b264eb4` `Merge pull request #36 from jefti/fix/textErrors`
- **2026-06-17** `4fc240a` `Doc: changelogs e metrics`

### Back-end

- **2026-06-16** `7452d68` `Merge pull request #43 from jefti/develop`
- **2026-06-17** restauração dos documentos `CHANGELOG.md`, `CHANGELOG_GERAL.md`, `METRICAS_PROJETO.md` e links no `README.md`
