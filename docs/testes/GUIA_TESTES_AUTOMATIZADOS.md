# Testes automatizados — Microserviço ESG

## Escopo

A suíte testa os endpoints REST de gestão de resíduos e reciclagem. Os testes BDD usam **Gherkin em português, Cucumber 8, REST Assured 6 e JSON Schema**; Java 21, Spring Boot 4 e Oracle 23.6 executam dentro do Docker. As dependências do Maven estão no `pom.xml` local da branch de testes.

**Resultado comprovado em execução local em 28/10/2026:** 33 cenários BDD aprovados, 1 teste Spring Boot aprovado, 34 testes no total, nenhuma falha, nenhum erro. Esse resultado não substitui a validação do GitHub Actions nem comprova deploy na Azure.

## Organização

- `src/test/resources/features/`: cenários por domínio (usuários, consultas, autenticação, pontos de coleta, descartes e saúde).
- `src/test/resources/schemas/`: contratos para objetos, listas, erros, token JWT e health check.
- `src/test/java/.../bdd/steps/`: implementações por domínio.
- `src/test/java/.../bdd/support/`: contexto de cenário e chamadas HTTP reutilizáveis.
- `src/test/java/.../bdd/hooks/`: limpeza dos registros criados, respeitando os vínculos.
- `src/test/java/.../bdd/config/`: integração Cucumber/Spring Boot.
- `CucumberTest.java`: executor JUnit Platform Suite.

## Como executar localmente

Requisitos: Docker Desktop/Engine com Compose, disponibilidade para executar Oracle, `bash`, `openssl` e Python 3 para conferir relatórios. A aplicação de teste usa Java 21 no contêiner, independentemente da versão de Java do Mac.

Na raiz do repositório:

```bash
bash deploy/test.sh
python3 deploy/relatorio-bdd.py
```

O comando `deploy/test.sh` cria um ambiente com credenciais aleatórias (`.env.test-<PID>`), sobe Oracle isolado, aplica migrações Flyway, executa a suíte e remove contêineres/volumes ao terminar. Ele não consulta o Oracle acadêmico e não altera a VM da Azure. O cache de dependências Maven em `target/maven-cache/` evita downloads repetidos.

Relatórios: `target/test-evidence/cucumber.html`, `cucumber.json`, `TEST-*.xml` e, após `relatorio-bdd.py`, `RESUMO-EXECUCAO.md`. Todos estão sob `target/` e ficam fora do Git. Para consultar no Mac: `open target/test-evidence/cucumber.html`.

## CI no GitHub Actions

O repositório já possui `.github/workflows/ci.yml`. O workflow roda em `push`, `pull_request` e execução manual; executa `bash deploy/test.sh`, constrói a imagem da aplicação, faz smoke test em ambiente efêmero e publica `target/test-evidence/` como artefato. Não houve necessidade de substituir esse workflow.

**Não declare o CI aprovado até verificar uma execução da branch no GitHub.** Para conferir: abra Actions → CI Oracle → execução da branch → job `verify` → resultado da etapa de testes e artefato `surefire-reports`.

## Critérios de aprovação

- Não haver falhas/erros/ignorados em `TEST-*.xml`.
- Todos os passos dos cenários efetivamente executados, não apenas compilar.
- O JSON atender ao respectivo contrato Schema em 200, 201 e erros JSON, quando aplicável.
- Respostas sem corpo (204 e 404 neste projeto) serem verificadas como vazias.
- O trabalho manter a suíte reprodutível sem depender da VPN ou de credenciais acadêmicas.

Consulte `MATRIZ_COBERTURA.md` e `CHECKLIST_ENTREGA.md`.
