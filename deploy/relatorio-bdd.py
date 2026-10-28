#!/usr/bin/env python3
"""Consolida evidências locais Cucumber + Maven, sem executar testes nem modificar o banco."""
import json
from collections import Counter
from pathlib import Path
from xml.etree import ElementTree as ET

ROOT = Path(__file__).resolve().parent.parent
EVID = ROOT / "target" / "test-evidence"
OUT = EVID / "RESUMO-EXECUCAO.md"


def main():
    arquivo = EVID / "cucumber.json"
    html = EVID / "cucumber.html"
    if not arquivo.is_file() or not html.is_file():
        raise SystemExit("Faltam cucumber.json ou cucumber.html em target/test-evidence. Execute bash deploy/test.sh primeiro.")
    try:
        features = json.loads(arquivo.read_text(encoding="utf-8"))
    except (json.JSONDecodeError, UnicodeDecodeError) as err:
        raise SystemExit(f"cucumber.json inválido: {err}") from err
    if not isinstance(features, list):
        raise SystemExit("cucumber.json não contém a lista esperada de features.")

    total = 0
    passos = 0
    falhas = []
    grupos = Counter()
    for feat in features:
        for element in feat.get("elements", []):
            if element.get("type") != "scenario":
                continue
            total += 1
            nome = element.get("name", "(sem nome)")
            steps = element.get("steps", [])
            grupos[feat.get("name", "(sem funcionalidade)")] += 1
            for step in steps:
                passos += 1
                status = step.get("result", {}).get("status", "undefined")
                if status != "passed":
                    falhas.append(f"{nome}: '{step.get('name', '')}' = {status}")
    if total == 0:
        raise SystemExit("Nenhum cenário do Cucumber encontrado no relatório.")

    xmls = sorted(EVID.glob("TEST-*.xml"))
    if not xmls:
        raise SystemExit("Nenhum relatório JUnit XML encontrado em target/test-evidence.")
    tests = errors = failures = skipped = 0
    for path in xmls:
        root = ET.parse(path).getroot()
        suites = [root] if root.tag == 'testsuite' else root.findall('testsuite')
        for suite in suites:
            tests += int(suite.attrib.get("tests", 0))
            errors += int(suite.attrib.get("errors", 0))
            failures += int(suite.attrib.get("failures", 0))
            skipped += int(suite.attrib.get("skipped", 0))

    ok = (not falhas and errors == 0 and failures == 0 and skipped == 0 and tests >= total)
    lines = [
        "# Resumo da execução local dos testes ESG",
        "",
        "Relatório gerado a partir dos arquivos produzidos pelo Maven/Cucumber. Não comprova a execução no GitHub Actions.",
        "",
        f"- Cenários BDD: **{total}**",
        f"- Passos Gherkin: **{passos}**",
        f"- Total de testes Maven: **{tests}**",
        f"- Falhas Maven: **{failures}**; erros: **{errors}**; ignorados: **{skipped}**",
        f"- Situação consolidada: **{'APROVADA' if ok else 'REVISAR'}**",
        "",
        "## Cenários por funcionalidade",
        "",
    ]
    lines += [f"- {nome}: {qtd}" for nome, qtd in sorted(grupos.items())]
    if falhas:
        lines += ["", "## Passos não aprovados", ""] + [f"- {x}" for x in falhas]
    lines += ["", "## Evidências", "", "- `cucumber.html` — relatório navegável", "- `cucumber.json` — execução dos passos", "- `TEST-*.xml` — resultado Maven/JUnit", ""]
    OUT.write_text("\n".join(lines), encoding="utf-8")
    print(f"Cenários: {total}; passos: {passos}; testes Maven: {tests}; falhas: {failures}; erros: {errors}; ignorados: {skipped}.")
    print(f"Resumo gravado em {OUT.relative_to(ROOT)}")
    if not ok:
        raise SystemExit("Relatórios contêm problemas ou resultados incompletos. Confira os arquivos originais.")


if __name__ == "__main__":
    main()
