#!/usr/bin/env bash
set -euo pipefail
# Nunca sobrescreve um arquivo existente; valores aleatórios não chegam ao terminal.
file=${1:-.env}
if [[ -e "$file" ]]; then echo "Arquivo já existe: $file" >&2; exit 1; fi
umask 077
set -o noclobber
{
  printf 'ORACLE_PASSWORD=%s\n' "$(openssl rand -hex 24)"
  printf 'DB_USERNAME=esgapp\n'
  printf 'DB_PASSWORD=%s\n' "$(openssl rand -hex 24)"
  printf 'JWT_SECRET=%s\n' "$(openssl rand -hex 32)"
} > "$file"
echo "Configuração criada em $file (permissão 600)."
