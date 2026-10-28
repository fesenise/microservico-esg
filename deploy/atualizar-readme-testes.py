#!/usr/bin/env python3
"""Atualiza somente a seção Testes do README, preservando outras instruções."""
from pathlib import Path
import re

root = Path(__file__).resolve().parent.parent
readme = root / "README.md"
content = readme.read_text(encoding="utf-8")
section = """## Testes automatizados (BDD e contratos JSON)

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
"""
pattern = r'(?ms)^## Testes\s*\n.*?(?=^## |\Z)'
if re.search(pattern, content):
    new = re.sub(pattern, lambda m: section+"\n", content, count=1)
elif '## Testes automatizados (BDD e contratos JSON)' in content:
    print('README já atualizado.'); raise SystemExit(0)
else:
    raise SystemExit('Não encontrei a seção "## Testes". README preservado; atualize manualmente.')
if new != content:
    readme.write_text(new,encoding='utf-8')
    print('Atualizada a seção Testes de README.md; demais seções foram preservadas.')
else:
    print('README já atualizado.')
