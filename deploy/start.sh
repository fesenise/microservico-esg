#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
[[ -f .env ]] || { echo "Execute bash deploy/init-env.sh primeiro" >&2; exit 1; }
# Testes usam outro Oracle, efêmero, e não alteram o banco da aplicação.
bash deploy/test.sh
export APP_TAG=${APP_TAG:-local}
docker compose build app
docker compose up -d --wait --wait-timeout 900
python3 deploy/smoke.py
