#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
file=".env.test-$$"
project="esg-test-$$"
bash deploy/init-env.sh "$file"
compose=(docker compose --env-file "$file" -p "$project" -f compose.test.yaml)
cleanup() {
  "${compose[@]}" down --volumes --remove-orphans >/dev/null 2>&1 || true
  rm -f "$file"
}
trap cleanup EXIT
mkdir -p target/test-evidence
"${compose[@]}" build test
"${compose[@]}" up -d --wait --wait-timeout 900 oracle
"${compose[@]}" run --rm test
