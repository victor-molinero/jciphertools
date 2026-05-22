#!/bin/sh
set -eu

ENV_FILE="/secrets/jciphertools.env"

if [ ! -f "$ENV_FILE" ]; then
  echo "Error: missing generated env file at $ENV_FILE" >&2
  exit 1
fi

set -a
. "$ENV_FILE"
set +a

exec java -jar /app/app.jar