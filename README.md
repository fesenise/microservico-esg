# Cidades ESG Inteligentes — Gestão de reciclagem

Projeto de Felipe Senise (RM565259).

## Estado desta etapa
Base preparada para versionamento. A aplicação Java utiliza Oracle, JPA e Flyway.
A atividade acadêmica MongoDB foi entregue separadamente e não está integrada a este código.
Docker Compose, pipeline CI/CD, deploy staging/produção e evidências serão adicionados nas próximas etapas.
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

## Testes
O teste existente contextLoads inicializa o Spring e necessita de um Oracle acessível.
A execução isolada em CI ainda será configurada. Nenhum resultado de teste ou deploy é declarado nesta etapa.

## Tecnologias
Java 21, Spring Boot 4.0.6, Spring Security/JWT, Maven, Oracle, JPA, Flyway e Docker.
