# FIAP | Microserviço ESG | Lote de integração BDD

## Objetivo

Ampliar os testes automatizados de API no projeto existente, preservando o código em `src/main`, o deploy na Azure e o Oracle acadêmico. Todos os testes de integração usam o Oracle temporário configurado em `compose.test.yaml` e executam via `bash deploy/test.sh`.

## Arquitetura

- `src/test/java/.../bdd/support/ApiTeste.java`: cliente REST Assured, dados de teste e registro dos IDs para limpeza.
- `src/test/java/.../bdd/steps/`: passos separados por domínio funcional.
- `src/test/java/.../bdd/hooks/LimpezaHooks.java`: limpeza já existente, na ordem descarte, ponto, usuário.
- `src/test/resources/features/`: cenários Gherkin em português.
- `src/test/resources/schemas/`: contratos JSON Schema.

## Cobertura de endpoints

| Método | Endpoint | Cenários cobertos |
|---|---|---|
| GET | `/actuator/health` | Saúde pública, status 200 e JSON Schema |
| POST | `/auth/login` | Token válido, senha incorreta e erro 401 |
| POST | `/usuarios` | Cadastro válido, campos inválidos e e-mail repetido (lote anterior) |
| GET | `/usuarios` | Lista autenticada e acesso sem token |
| GET | `/usuarios/{id}` | Existente e inexistente |
| DELETE | `/usuarios/{id}` | Exclusão 204 e 404 |
| POST | `/pontos-coleta` | Cadastro válido e estado inválido |
| GET | `/pontos-coleta` | Lista e acesso sem JWT |
| GET | `/pontos-coleta/{id}` | Existente e inexistente |
| DELETE | `/pontos-coleta/{id}` | Exclusão 204 e 404 |
| POST | `/descartes` | Cadastro válido, peso zero e referências inexistentes |
| GET | `/descartes` | Lista e acesso sem JWT |
| GET | `/descartes/{id}` | Existente e inexistente |
| DELETE | `/descartes/{id}` | Exclusão 204 e 404 |

## Como executar

1. Abrir Docker Desktop.
2. Na raiz do repositório: `bash deploy/test.sh`.
3. Abrir o relatório: `open target/test-evidence/cucumber.html` (macOS).
4. Conferir também `target/test-evidence/cucumber.json` e arquivos `TEST-*.xml`.

**Importante:** os arquivos de teste foram preparados a partir do código público dos controllers/modelos e dos testes anteriores. A execução deste lote ainda não foi verificada no computador do aluno. Uma falha deve ser investigada antes de coletar evidências definitivas.

## Contratos e particularidades

- Login inválido (`/auth/login`) retorna JSON textual com os campos `status`, `erro` e `mensagem`, não o registro `ErroResponse` usado pelas demais validações. Por isso há um schema específico.
- `DELETE` e `GET` inexistentes retornam status sem corpo: os cenários validam resposta vazia, não um JSON de erro inexistente.
- O estado de pontos de coleta deve ter duas letras maiúsculas. `ativo` é inteiro `0` ou `1`.
- Descartes precisam de referências reais de usuário e ponto de coleta.
- Os dados são gerados por cenário e registrados no `CenarioContext`; o hook de limpeza existente remove primeiro descartes, depois pontos e por fim usuários.
- Os testes negativos de campos inválidos de usuário e o contrato `erro.schema.json` já fazem parte do lote anterior.

## Após a validação

Atualizar o README principal com as instruções de execução e documentar as evidências aprovadas. Integrar a suíte na pipeline existente, sem interferir no deploy produtivo.
