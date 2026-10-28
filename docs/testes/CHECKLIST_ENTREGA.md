# Checklist de finalização — Microserviço ESG

## Antes de publicar a branch

- [ ] `git branch --show-current` confirma `feature/testes-automatizados` (não `main`).
- [ ] `python3 deploy/revisar-estrutura-testes.py` encontra todos os arquivos.
- [ ] `python3 deploy/relatorio-bdd.py` confirma 33 cenários e 34 testes sem falhas.
- [ ] `git status --short` revisado para não adicionar arquivos `.env`, segredos, relatórios ou prints indesejados.
- [ ] `README.md` atualizado com seção de testes, sem apagar instruções do deployment.

## Evidências locais a guardar

- [ ] Print de `BUILD SUCCESS` com `Tests run: 34, Failures: 0, Errors: 0, Skipped: 0`.
- [ ] Print do relatório `target/test-evidence/cucumber.html` com 33 cenários aprovados.
- [ ] Arquivo `target/test-evidence/RESUMO-EXECUCAO.md` gerado e conferido.
- [ ] Caso a avaliação solicite arquivo PDF/PPT, incluir somente evidências reais e capturas da execução.

## Publicação e CI

- [ ] Commit na branch `feature/testes-automatizados`.
- [ ] Push da branch para o GitHub.
- [ ] Em Actions → CI Oracle, confirmar sucesso do job `verify` referente ao commit publicado.
- [ ] Baixar o artefato `surefire-reports` gerado pela própria execução do CI.
- [ ] Guardar link/print da execução bem-sucedida do CI.
- [ ] Abrir pull request da branch para `main` **apenas quando decidir integrar**; não é necessário alterar a Azure para realizar esses testes.

## Pacote final de entrega

- [ ] Gerar ZIP a partir de arquivos versionados (`git archive`), sem `.env` e sem credenciais.
- [ ] Conferir que o ZIP contém `pom.xml`, `src/`, `.github/workflows/ci.yml`, `deploy/`, `compose.test.yaml`, `Dockerfile` e documentação.
- [ ] Verificar ZIP com `unzip -t` antes de submeter.
- [ ] Se o enunciado específico da entrega exigir coleção Postman/Insomnia, incluir o arquivo correspondente separado; a suíte BDD não substitui automaticamente essa exigência.
- [ ] Não declarar testes de CI aprovados apenas com os logs locais.
