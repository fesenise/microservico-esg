#!/usr/bin/env python3
"""Conferência estática do lote de testes (não inicia Docker, GitHub ou Oracle)."""
from pathlib import Path
import subprocess

ROOT = Path(__file__).resolve().parent.parent
paths = [
    ".github/workflows/ci.yml", "deploy/test.sh", "compose.test.yaml",
    "src/test/java/br/com/fiap/microservico_esg/CucumberTest.java",
    "src/test/java/br/com/fiap/microservico_esg/bdd/support/CenarioContext.java",
    "src/test/java/br/com/fiap/microservico_esg/bdd/support/ApiTeste.java",
    "src/test/java/br/com/fiap/microservico_esg/bdd/steps/AutenticacaoSteps.java",
    "src/test/java/br/com/fiap/microservico_esg/bdd/steps/DescarteSteps.java",
    "src/test/java/br/com/fiap/microservico_esg/bdd/steps/PontoColetaSteps.java",
    "src/test/java/br/com/fiap/microservico_esg/bdd/steps/UsuarioConsultaSteps.java",
    "src/test/java/br/com/fiap/microservico_esg/bdd/hooks/LimpezaHooks.java",
]
features = ["autenticacao.feature", "descartes.feature", "pontos-coleta.feature", "saude.feature", "usuarios-consulta.feature", "usuarios.feature"]
schemas = ["token.schema.json", "usuario.schema.json", "usuarios-lista.schema.json", "erro.schema.json", "erro-login.schema.json", "ponto-coleta.schema.json", "pontos-coleta-lista.schema.json", "descarte.schema.json", "descartes-lista.schema.json", "health.schema.json"]
paths += [f"src/test/resources/features/{x}" for x in features]
paths += [f"src/test/resources/schemas/{x}" for x in schemas]

missing = [path for path in paths if not (ROOT/path).is_file()]
ci = (ROOT / '.github/workflows/ci.yml').read_text(encoding='utf-8') if (ROOT / '.github/workflows/ci.yml').exists() else ''
print('Conferência de arquivos:', len(paths)-len(missing), '/', len(paths))
for path in missing:
    print('  AUSENTE:',path)
for text in ['bash deploy/test.sh', 'actions/upload-artifact@v4']:
    if text not in ci:
        print('  AVISO: workflow CI não contém:', text)
try:
    branch = subprocess.check_output(['git','branch','--show-current'],cwd=ROOT,text=True).strip()
    print('Branch atual:', branch)
    if branch == 'main':
        print('  ALERTA: trabalhe na branch feature/testes-automatizados, não na main.')
except (FileNotFoundError, subprocess.CalledProcessError):
    print('Não foi possível confirmar a branch Git.')
if missing:
    raise SystemExit('Estrutura incompleta; não prepare o ZIP final ainda.')
print('Estrutura dos testes presente. Execute relatorio-bdd.py para verificar os resultados reais.')
