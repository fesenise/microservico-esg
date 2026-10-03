# Deployment Azure do microservico-esg

Base conferida: `0318d0793c848936c46d2575716bf764a73fc037`, HEAD observado em 03/10/2026 UTC. Pacote preparado; não houve publicação, criação de VM nem execução de testes Oracle nesta sessão.

## Decisão

Para uma demonstração acadêmica sem Oracle externo confirmado: uma VM Ubuntu x86_64 + Docker Compose + Oracle Free persistente + aplicação. Primeiro acesso via túnel SSH, sem domínio ou porta HTTP pública. Se já houver VM compatível, reutilize-a e pule a criação. Se já houver Oracle autorizado e alcançável da VM, veja a alternativa ao final; pode ser o caminho mais curto.

Dimensionamento operacional sugerido: pelo menos 8 GiB RAM e 64 GiB de disco para Oracle, JVM, compilação e imagens. Isso é uma margem para este fluxo, não o mínimo oficial do Oracle. Teste e aplicação rodam sequencialmente no primeiro deploy. Para atualizações com app e Oracle já rodando, pode ser necessário executar CI fora da VM ou aumentar a memória. Não presume disponibilidade de SKU, quota, crédito nem preço. Não é arquitetura de alta disponibilidade nem entrega completa de staging/produção.

## O que foi encontrado no código

- Java 21, Spring Boot 4.0.6, JDBC Oracle, JPA com `ddl-auto=validate` e Flyway habilitado. MongoDB não participa deste serviço.
- `contextLoads` usa `@SpringBootTest`: precisa de URL, usuário/senha Oracle, segredo JWT, conexão, migrações bem-sucedidas e schema compatível com Hibernate. Não é um teste unitário sem banco.
- O Dockerfile usa `-DskipTests`: pode produzir imagem sem iniciar o Spring, testar credenciais, aplicar SQL ou validar o mapeamento JPA. Isso não basta para provar funcionamento.
- `.env` não é carregado automaticamente por Spring. Compose interpola o arquivo e injeta as variáveis explicitamente.
- V1 cria três tabelas e três sequences; não é idempotente. Para schema novo use baseline false/0. O schema precisa de CREATE SESSION, CREATE TABLE, CREATE SEQUENCE e quota de tablespace. O usuário criado pela imagem Oracle serve ao cenário de demonstração.
- Em schema existente sem histórico Flyway, `baseline-on-migrate=true` com `baseline-version=0` executa V1 e colide com objetos existentes. Somente após conferir equivalência completa com V1 e fazer backup, um baseline deliberado em 1 pode pular V1. O pacote parametriza essa versão; não aplica baseline automaticamente. Nunca use `clean` ou apague tabelas para contornar esse erro.
- O teste funcional precisa ser executado: `contextLoads` não testa HTTP nem emissão/validação de JWT. O smoke usa as rotas e o campo `token` reais do projeto.

## Arquivos entregues

`deployment.patch` contém alterações e arquivos novos, aplicáveis ao commit-base. `compose.yaml` sobe banco persistente e app; `compose.test.yaml` usa Oracle separado, efêmero. Scripts em `deploy/` cuidam de secrets locais, instalação, testes, provisionamento, smoke e início. A CI roda o mesmo teste e smoke. Actuator expõe apenas `/actuator/health`, sem detalhes, incluindo estado da conexão ao banco. HTTPS via Caddy é opcional.

## 1. Aplicar no Mac, na cópia Git

Requisitos: Git, GitHub CLI autenticado se o repositório for privado, Azure CLI e chave SSH. Use a autenticação do GitHub CLI; não coloque token em URL ou arquivo do projeto.

```bash
gh auth login
gh repo clone fesenise/microservico-esg
cd microservico-esg
git status --short
git rev-parse HEAD
# Deve ser 0318d0793c848936c46d2575716bf764a73fc037 e não haver alterações locais.
git switch -c deployment/azure
# Ajuste o caminho ao arquivo extraído do ZIP.
git apply --check ~/Downloads/deployment.patch
git apply ~/Downloads/deployment.patch
```

Se o HEAD tiver mudado, revise o diff e adapte o patch; não faça reset para descartar trabalho. Esta entrega não modificou seu GitHub.

## 2. Escolher região e tamanho e criar a VM

```bash
az login
az account show --query '{assinatura:name,id:id}' -o table
# Se necessário: az account set --subscription 'ID_DA_ASSINATURA'
export AZ_LOCATION='REGIAO_PERMITIDA'
az vm list-skus --location "$AZ_LOCATION" --resource-type virtualMachines \
  --all --query "[?length(restrictions)==\`0\`].{SKU:name}" -o table
az vm list-usage --location "$AZ_LOCATION" -o table
```

Escolha SKU x86_64 com 8 GiB ou mais, por exemplo `Standard_B2ms` ou `Standard_D2as_v5`, **se disponível e com quota**. Listagem sem restrições não garante capacidade física no momento da alocação. `SkuNotAvailable` exige outro SKU/região; `OperationNotAllowed` pode indicar quota; `RequestDisallowedByPolicy` exige região/recurso aceito pela política. Não repita criação sem ler o erro.

```bash
export AZ_VM_SIZE='SKU_ESCOLHIDO'
export MY_IP_CIDR='SEU_IP_PUBLICO/32'
export SSH_PUBLIC_KEY="$HOME/.ssh/id_ed25519.pub"
# Se ainda não existir essa chave, crie uma sem sobrescrever chaves existentes:
# ssh-keygen -t ed25519 -f "$HOME/.ssh/id_ed25519"
bash deploy/create-vm.sh
export ESG_IP=$(az vm show -d -g rg-esg-demo -n vm-esg-demo --query publicIps -o tsv)
```

O script cria recursos cobrados: VM, disco, IP público e rede. Ele permite SSH somente do IP informado. Portas 1521 e 8080 não são abertas. Se reutilizar VM, confirme arquitetura, memória, Docker e regras de rede; não execute o instalador sobre Docker existente sem revisar.

## 3. Transferir código sem credenciais

No Mac, ainda na raiz do repositório:

```bash
# O diff deve conter apenas os arquivos desta entrega e suas alterações intencionais.
git diff --check
git add README.md Dockerfile pom.xml .env.example compose.yaml compose.test.yaml compose.https.yaml \
  deploy .github/workflows/ci.yml \
  src/main/resources/application.properties \
  src/main/java/br/com/fiap/microservico_esg/security/SecurityConfig.java
git diff --cached --stat
git commit -m 'Configura deploy Azure com Oracle, CI e healthcheck'
# Arquivo fora do repositório; git archive inclui somente arquivos versionados.
git archive --format=tar.gz -o /tmp/esg-source.tar.gz HEAD
scp /tmp/esg-source.tar.gz "azureuser@$ESG_IP:/tmp/esg-source.tar.gz"
ssh "azureuser@$ESG_IP"
```

Na primeira conexão, verifique a chave do host por canal confiável; não desabilite a verificação SSH. O uso de `git archive` evita copiar `.env`, `.git`, chaves e configuração de autenticação. Não é necessário instalar token GitHub na VM.

## 4. Executar na VM

```bash
mkdir -p ~/microservico-esg
cd ~/microservico-esg
tar -xzf /tmp/esg-source.tar.gz
sudo bash deploy/install-docker.sh
bash deploy/init-env.sh
# O arquivo fica no usuário azureuser, com permissão 600.
# Não publique seu conteúdo nem a saída de docker compose config/inspect.
sudo bash deploy/start.sh
```

O fluxo primeiro cria um Oracle **de testes**, espera ficar saudável e executa `./mvnw -B --no-transfer-progress verify` em Java 21, sem pular `contextLoads`. O Oracle de testes é removido com seu volume, sem tocar no volume persistente `esg_oracle-data`. Se o teste falhar, a aplicação não será iniciada pelo script. Depois faz build, sobe o banco persistente e a aplicação e exige saúde e smoke. Na atualização, uma falha no teste mantém a aplicação anterior; falhas depois da troca da imagem exigem investigação/rollback manual.

```bash
sudo docker compose ps
curl --fail http://127.0.0.1:8080/actuator/health
python3 deploy/smoke.py
```

Resultado esperado: `{"status":"UP"}` e `PASS: health + cadastro + login JWT + consulta autenticada + limpeza`. O smoke cria um usuário de teste e o remove; não imprime senha nem token. Se falhar após o cadastro, pode restar um usuário `smoke-...@example.invalid` para limpeza. Não trate os resultados esperados como resultados já obtidos.

## 5. Abrir no Mac e registrar evidências

Em outro terminal do Mac:

```bash
ssh -N -L 8080:127.0.0.1:8080 "azureuser@$ESG_IP"
```

Mantenha aberto. Acesse `http://localhost:8080/actuator/health` ou use Postman em `http://localhost:8080`. A API roda no Azure; o transporte até a VM é protegido por SSH. Se a porta local estiver ocupada, use `-L 18080:127.0.0.1:8080` e acesse 18080. Este acesso depende do túnel; não é uma URL pública para terceiros.

Evidências reais a capturar: commit aplicado (`git rev-parse HEAD` no Mac), execução verde da CI, relatório Surefire com 1 teste e zero falhas/erros, VM e IP no Azure, `docker compose ps`, health e saída do smoke. Evite screenshots com secrets e tokens. O relatório local fica em `target/test-evidence/`. Revise relatórios antes de compartilhar: erros de conexão podem revelar nomes de host/schema, mesmo sem senha.

## 6. CI no GitHub e fronteira do CD

```bash
# No Mac, depois de revisar o commit:
git push -u origin deployment/azure
```

A CI executa em push/PR: gera secrets temporários, inicia Oracle isolado, roda `verify`, constrói imagem e testa API. Não precisa de secrets Oracle/Azure no GitHub. Requer Actions habilitado, runner Linux com Docker e acesso aos registries/Maven. Relatórios Surefire são publicados como artefato da execução.

Esta etapa entrega **CI automática e deploy manual reproduzível**, não CD automático nem promoção staging/produção. Para o primeiro ambiente isso dispensa ACR, identidade federada e permissão Azure no GitHub. Depois de validar, CD pode construir/publicar uma imagem imutável por SHA e promover a mesma imagem; para Azure use OIDC, evitando senha de service principal. Não coloque GitHub runner de PRs na VM da aplicação. Não declare a entrega completa de DevOps se a rubrica ainda exigir dois ambientes, promoção ou rollback automatizado.

## Secrets, persistência e diagnóstico

- `deploy/init-env.sh` gera senha administrativa do Oracle, senha do schema e JWT distintos. Nenhuma credencial fixa foi adicionada. `.env` e `.env.*` já são ignorados; apenas `.env.example` é versionado. A imagem recebe secrets apenas em runtime.
- A senha inicial é aplicada pela imagem Oracle quando cria o banco. Trocar `.env` depois não altera a senha no volume existente: faça rotação coordenada no banco e na aplicação. Não regenere `.env` em atualização. Preserve o segredo JWT para não invalidar tokens inadvertidamente.
- Compose guarda variáveis no contêiner; administradores e usuários com acesso ao Docker podem lê-las. Permissão 600 e Git ignore atendem esta demonstração, não equivalem a um cofre. Para operação permanente, use Key Vault/identidade gerenciada ou secrets montados e adapte a leitura pelo Spring.
- `docker compose down` preserva o volume da aplicação; **não use `down -v` no compose principal**. Volume não é backup. Exclusão da VM/disco pode perder dados. Para dados reais, implante backup/restore Oracle antes.
- `sudo docker compose logs --tail=150 app` ajuda a distinguir erro Flyway, autenticação ou schema validation. Revise antes de compartilhar. Não desligue Flyway nem troque `ddl-auto=validate` por `update` para mascarar falhas.
- `ORA-01017`: usuário/senha/service incorretos; `ORA-12514`: service desconhecido; timeout: DNS/rede/firewall; `ORA-01031`/quota: privilégios ou tablespace; `ORA-00955`: objetos existentes ou migração parcial. DDL Oracle não tem rollback transacional completo: schema parcialmente migrado exige revisão antes de repetir.
- Health `UP` não comprova autorização por papéis. O cadastro atual aceita `role` do cliente e as rotas protegidas exigem autenticação, sem regras por papel; revise antes de liberar a aplicação para usuários reais.

## Oracle externo já disponível

É a opção mais curta **se** houver hostname, porta, service/PDB, schema dedicado, senha, privilégios, versão compatível e rede liberada desde a VM. `localhost` dentro do contêiner nunca aponta ao Oracle de outro host. Se exigir VPN, IP permitido, TCPS/wallet, há configuração adicional real; a URL JDBC sozinha pode não bastar.

Execute o teste efêmero do pacote primeiro. Em seguida, use o Dockerfile com `.env` externo preenchido localmente e protegido por `chmod 600`: `DB_URL=jdbc:oracle:thin:@//HOST:1521/SERVICE`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET` e baseline adequado. Não use SYSTEM/SYS. O Compose principal é para banco próprio; não o utilize inalterado para Oracle externo.

```bash
# Na VM; .env externo já preenchido. Não execute init-env.sh sobre ele.
sudo docker build -t microservico-esg:external .
sudo docker run -d --name esg-external --restart unless-stopped \
  --env-file .env -p 127.0.0.1:8080:8080 microservico-esg:external
curl --fail http://127.0.0.1:8080/actuator/health
python3 deploy/smoke.py
```

O contêiner pode levar tempo para inicializar; só avance após health e smoke aprovados. Não rode os dois modos na mesma porta. Um teste Oracle efêmero bem-sucedido não prova acesso ao banco externo; o início da aplicação e smoke validam esse caminho específico.

## URL pública HTTPS, quando necessária

Requer domínio com registro A apontando ao IP da VM e acesso público às portas 80/443 para emissão do certificado. Na VM, acrescente `APP_DOMAIN=api.seudominio.com` ao `.env` sem compartilhar o arquivo, e execute:

```bash
sudo docker compose -f compose.yaml -f compose.https.yaml up -d --wait --wait-timeout 900
```

No computador com Azure CLI:

```bash
az network nsg rule create -g rg-esg-demo --nsg-name vm-esg-demo-nsg \
  -n HTTPSPublico --priority 1010 --access Allow --protocol Tcp --direction Inbound \
  --source-address-prefixes Internet --source-port-ranges '*' \
  --destination-address-prefixes '*' --destination-port-ranges 80 443
```

Teste `https://api.seudominio.com/actuator/health` e execute `python3 deploy/smoke.py https://api.seudominio.com`. Nunca envie senha ou JWT por HTTP público. Passe os dois arquivos Compose nas operações posteriores de atualização. O proxy resolve TLS; não corrige as regras de autorização da aplicação.

## Verificação desta entrega

Realizada: leitura dos arquivos no GitHub, conferência do commit/HEAD, revisão de rotas/SQL/configuração, sintaxe Bash/Python/XML/YAML e integridade do patch. Não realizada aqui: resolução Maven/build Java 21, execução Docker/Oracle, `contextLoads`, smoke real, CI no GitHub, provisionamento ou deployment Azure. O ambiente desta sessão possui Java 17 e não possui Docker/Azure CLI; não havia acesso às credenciais Azure nem a Oracle. Os scripts impõem esses testes no ambiente de destino.

## Fontes técnicas

- Base: https://github.com/fesenise/microservico-esg/commit/0318d0793c848936c46d2575716bf764a73fc037
- VM por Azure CLI: https://learn.microsoft.com/en-us/azure/virtual-machines/linux/quick-create-cli
- Regras NSG: https://learn.microsoft.com/en-us/cli/azure/network/nsg/rule
- Docker no Ubuntu: https://docs.docker.com/engine/install/ubuntu/
- Oracle Free container (imagem comunitária, não serviço Oracle gerenciado): https://github.com/gvenzl/oci-oracle-free
- Actuator health: https://docs.spring.io/spring-boot/api/rest/actuator/health.html
- HTTPS Caddy: https://caddyserver.com/docs/automatic-https
