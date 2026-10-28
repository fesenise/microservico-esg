# Cidades ESG Inteligentes — Gestão de reciclagem

Projeto de Felipe Senise (RM565259).

## Estado desta etapa
Base preparada para versionamento. A aplicação Java utiliza Oracle, JPA e Flyway.
A atividade acadêmica MongoDB foi entregue separadamente e não está integrada a este código.
Docker Compose, CI e roteiro de deploy manual Azure estão preparados neste pacote; a execução e as evidências ainda precisam ser obtidas. CD automático e staging/produção separados permanecem pendentes.
Este pacote ainda não é a entrega final de DevOps.

## Configuração
Forneça DB_URL, DB_USERNAME, DB_PASSWORD e JWT_SECRET como variáveis de ambiente.
Consulte .env.example; não versione segredos. O Spring não carrega .env automaticamente.
Para Java local, exporte as variáveis no terminal antes de executar ./mvnw spring-boot:run.
Cada ambiente deve utilizar banco/schema e segredo JWT próprios.

## Docker
O Dockerfile compila com Java 21 e Maven Wrapper, e executa o JAR como usuário sem privilégios.

```sh
docker build -t microservico-esg:local .
docker run --rm --env-file .env -p 8080:8080 microservico-esg:local
```

DB_URL deve ser acessível de dentro do contêiner; localhost nele aponta para o próprio contêiner.

## Testes automatizados (BDD e contratos JSON)

O projeto tem 33 cenários BDD Cucumber/REST Assured, além do teste Spring Boot `contextLoads`, abrangendo login JWT, usuários, pontos de coleta, descartes e health check. Os testes verificam respostas HTTP, conteúdo e JSON Schemas nos endpoints que retornam JSON; respostas 204/404 sem corpo são verificadas como vazias.

Para executar **sem acessar o Oracle acadêmico ou a Azure**:

```bash
bash deploy/test.sh
```

O script cria credenciais temporárias, inicia Oracle 23.6 efêmero via Docker Compose, aplica o Flyway e executa a suíte com Java 21. O Oracle e seus volumes são removidos ao final. Resultados locais disponíveis em `target/test-evidence/` (não versionados): `cucumber.html`, `cucumber.json` e `TEST-*.xml`.

```bash
python3 deploy/revisar-estrutura-testes.py
python3 deploy/relatorio-bdd.py
open target/test-evidence/cucumber.html  # macOS
```

A execução local de 28/10/2026 registrou **33 cenários BDD aprovados e 34 testes Maven aprovados**. A aprovação do GitHub Actions precisa ser conferida separadamente no push da branch. O workflow existente `.github/workflows/ci.yml` já executa `deploy/test.sh` e publica os relatórios como artefato.

Documentação detalhada: [Guia de testes](docs/testes/GUIA_TESTES_AUTOMATIZADOS.md), [matriz de cobertura](docs/testes/MATRIZ_COBERTURA.md) e [checklist de entrega](docs/testes/CHECKLIST_ENTREGA.md).

## Tecnologias
Java 21, Spring Boot 4.0.6, Spring Security/JWT, Maven, Oracle, JPA, Flyway e Docker.


## Deployment Azure preparado

Consulte [deploy/GUIA_AZURE.md](deploy/GUIA_AZURE.md) para Compose, CI Oracle, secrets e deploy manual. Os arquivos foram preparados; a execução real e as evidências ainda precisam ser obtidas.
