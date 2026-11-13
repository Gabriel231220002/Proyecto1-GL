#!/usr/bin/env bash
# Script para lanzar el runner interactivo o automatizado de pruebas de flujo
set -e

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
cd "$DIR"

python3 runner.py "$@"
