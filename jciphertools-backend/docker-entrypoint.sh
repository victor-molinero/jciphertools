#!/bin/sh
set -eu

ENV_FILE="/secrets/jciphertools.env"
MAX_WAIT_SECONDS="${SECRETS_WAIT_TIMEOUT:-60}"
WAITED_SECONDS=0

while [ ! -f "$ENV_FILE" ] && [ "$WAITED_SECONDS" -lt "$MAX_WAIT_SECONDS" ]; do
  echo "Waiting for generated env file at $ENV_FILE (${WAITED_SECONDS}s/${MAX_WAIT_SECONDS}s)"
  sleep 1
  WAITED_SECONDS=$((WAITED_SECONDS + 1))
done

if [ ! -f "$ENV_FILE" ]; then
  echo "Error: missing generated env file at $ENV_FILE after ${MAX_WAIT_SECONDS}s" >&2
  exit 1
fi

set -a
. "$ENV_FILE"
set +a

exec java -jar /app/app.jar