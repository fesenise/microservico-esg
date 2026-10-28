# Matriz de cobertura — API ESG

Mapeamento dos **14 endpoints/operações HTTP** existentes e seus cenários automatizados. Valores abaixo correspondem à execução local aprovada em 28/10/2026, não ao GitHub Actions.

| Operação | Endpoint | Teste(s) de cobertura |
|---|---|---|
| POST | `/auth/login` | Login válido, senha incorreta, obtenção de JWT |
| POST | `/usuarios` | Cadastro válido; nome vazio; email inválido; senha curta; email duplicado |
| GET | `/usuarios` | Listagem com JWT; acesso sem JWT |
| GET | `/usuarios/{id}` | Consulta existente e inexistente |
| DELETE | `/usuarios/{id}` | Exclusão existente e inexistente |
| POST | `/pontos-coleta` | Cadastro válido; estado inválido |
| GET | `/pontos-coleta` | Listagem com JWT; acesso sem JWT |
| GET | `/pontos-coleta/{id}` | Consulta existente e inexistente |
| DELETE | `/pontos-coleta/{id}` | Exclusão existente e inexistente |
| POST | `/descartes` | Cadastro válido; peso zero; referência de usuário/ponto inválida |
| GET | `/descartes` | Listagem com JWT; acesso sem JWT |
| GET | `/descartes/{id}` | Consulta existente e inexistente |
| DELETE | `/descartes/{id}` | Exclusão existente e inexistente |
| GET | `/actuator/health` | Saúde sem JWT e status `UP` |

## Contagem por arquivo Gherkin

| Arquivo | Cenários executados |
|---|---:|
| `autenticacao.feature` | 4 |
| `usuarios.feature` | 5 (inclui 3 exemplos do Esquema do Cenário) |
| `usuarios-consulta.feature` | 5 |
| `pontos-coleta.feature` | 8 |
| `descartes.feature` | 10 |
| `saude.feature` | 1 |
| **Total BDD** | **33** |

O teste adicional `MicroservicoEsgApplicationTests.contextLoads` valida a inicialização Spring Boot com banco de teste, resultando em **34 testes Maven**.

## Contratos

- `usuario.schema.json` e `usuarios-lista.schema.json`
- `ponto-coleta.schema.json` e `pontos-coleta-lista.schema.json`
- `descarte.schema.json` e `descartes-lista.schema.json`
- `token.schema.json`
- `erro.schema.json` e `erro-login.schema.json` (formatos diferentes da API)
- `health.schema.json`

Os cenários 204/404 não esperam objeto JSON no corpo: verificam status e corpo vazio. Os cenários 401 sem JWT verificam o status HTTP correspondente; a API pode produzir resposta sem contrato JSON de erro.

## Observações de arquitetura

- Geração de dados únicos evita colisão com execuções anteriores.
- `CenarioContext` usa escopo por cenário.
- Hooks removem descartes antes dos registros relacionados.
- Os testes foram executados com Oracle isolado; **não representam teste de carga, análise de vulnerabilidades nem prova de deploy no Azure**.
