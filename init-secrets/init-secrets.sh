#!/bin/sh
set -eu

SECRETS_DIR="/secrets"
ENV_FILE="$SECRETS_DIR/jciphertools.env"
PUBLIC_KEY_PATH="$SECRETS_DIR/public.pem"
PRIVATE_KEY_PATH="$SECRETS_DIR/private.pem"

# Force full regeneration on each run.
mkdir -p "$SECRETS_DIR"
find "$SECRETS_DIR" -mindepth 1 -delete

echo "Generating RSA key pair in $SECRETS_DIR"
openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 -out "$PRIVATE_KEY_PATH"
openssl rsa -pubout -in "$PRIVATE_KEY_PATH" -out "$PUBLIC_KEY_PATH"

AES_CBC_KEY="${AES_CBC_KEY:-$(openssl rand -base64 32 | tr -d '\n')}"
AES_CBC_IV="${AES_CBC_IV:-$(openssl rand -base64 16 | tr -d '\n')}"
AES_GCM_KEY="${AES_GCM_KEY:-$(openssl rand -base64 32 | tr -d '\n')}"
AES_GCM_IV="${AES_GCM_IV:-$(openssl rand -base64 12 | tr -d '\n')}"
CORS_ALLOWED_ORIGINS="${CORS_ALLOWED_ORIGINS:-http://localhost:4000,http://localhost:4200}"
SERVER_PORT="${SERVER_PORT:-8081}"
MANAGEMENT_PORT="${MANAGEMENT_PORT:-8082}"

cat > "$ENV_FILE" <<EOF
AES_CBC_KEY=$AES_CBC_KEY
AES_CBC_IV=$AES_CBC_IV
AES_GCM_KEY=$AES_GCM_KEY
AES_GCM_IV=$AES_GCM_IV
RSA_PUBLIC_KEY_PATH=$PUBLIC_KEY_PATH
RSA_PRIVATE_KEY_PATH=$PRIVATE_KEY_PATH
CORS_ALLOWED_ORIGINS=$CORS_ALLOWED_ORIGINS
SERVER_PORT=$SERVER_PORT
MANAGEMENT_PORT=$MANAGEMENT_PORT
EOF

echo "Generated $ENV_FILE"