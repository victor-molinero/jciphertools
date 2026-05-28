# JCipherTools

JCipherTools is a full-stack encryption playground with a Spring Boot backend and Angular frontend.

It demonstrates secure encryption and decryption workflows using multiple algorithms, exposes REST endpoints, and provides a UI for interactive testing.

## Features
1. AES-CBC-256 encryption and decryption.
2. AES-GCM-256 authenticated encryption and decryption.
3. RSA-OAEP encryption and decryption using injected key pairs.
4. REST API with validation and standardized error handling.
5. Swagger/OpenAPI documentation and Spring Actuator endpoints.
6. Angular UI with runtime backend URL injection.
7. Azure Container Apps deployment with images in Azure Container Registry and runtime secrets loaded from Azure Key Vault.

## Architecture
1. Frontend runs on port `4000` in Docker Compose and calls backend endpoints under `/api/v1`.
2. Backend runs on port `8081`, management endpoints on `8082`.
3. Runtime secrets are provided as environment variables, ideally from Azure Key Vault at deployment time.
4. Backend entrypoint starts the JVM directly and expects env vars to already be present.
5. Frontend SSR serves `/runtime-config.js` and reads `API_BACKEND_URL` at runtime.
6. Angular SSR host validation uses `NG_ALLOWED_HOSTS`; Azure Container Apps frontend hostnames must be included.
7. Azure Container Registry stores the backend and frontend images.

Key files:
1. [docker-compose.yml](docker-compose.yml)
2. [jciphertools-backend/docker-entrypoint.sh](jciphertools-backend/docker-entrypoint.sh)
3. [jciphertools-backend/src/main/resources/application.yml](jciphertools-backend/src/main/resources/application.yml)

## API Summary
Base path: `/api/v1`

1. `POST /encrypt`
2. `POST /decrypt`

Supported algorithms:
1. `AES_CBC_256`
2. `AES_GCM_256`
3. `RSA_OAEP`

Example request body:

```json
{
	"input": "Hello World",
	"algorithm": "AES_GCM_256"
}
```

OpenAPI and actuator endpoints are configured in [jciphertools-backend/src/main/resources/application.yml](jciphertools-backend/src/main/resources/application.yml).

## Project Structure

```text
jciphertools/
├── docker-compose.yml
├── jciphertools-backend/
└── jciphertools-frontend/
```

## Prerequisites
1. Git
2. Docker Engine with Docker Compose plugin
3. For non-Docker local development:
4. Java 21
5. Maven 3.9+
6. Node.js 22+
7. npm

## Clone

```bash
git clone https://github.com/victor-molinero/jciphertools.git
cd jciphertools
```

## Quick Start With Docker Compose

```bash
docker compose up --build
```

Services:
1. Frontend: `http://localhost:4000`
2. Backend API: `http://localhost:8081`
3. Backend management: `http://localhost:8082/actuator`
4. Swagger UI: `http://localhost:8081/swagger-ui.html`

Stop and clean:

```bash
docker compose down -v
```

Notes:
1. Provide the required backend environment variables in a local `.env` file or shell session before starting Compose.
2. For Azure deployments, inject the same variables from Azure Key Vault through the target runtime.

## Azure Deployment

The current deployment flow targets Azure Container Apps with two separate apps:

1. `ca-jciphertools-backend` for the Spring Boot API.
2. `ca-jciphertools-frontend` for the Angular SSR frontend.

The validated runtime model is:

1. Build both images locally with Docker Compose.
2. Push both images to Azure Container Registry.
3. Store AES and RSA values in Azure Key Vault.
4. Create the backend Container App first.
5. Load Key Vault secret values into Container App secrets and map them to backend env vars.
6. Create the frontend Container App with `API_BACKEND_URL` pointing at the deployed backend.
7. Set `CORS_ALLOWED_ORIGINS` on the backend to the deployed frontend URL.
8. Set `NG_ALLOWED_HOSTS` on the frontend so Angular SSR accepts the Azure Container Apps hostname.

### Required Azure Resources

1. Resource group.
2. Azure Container Registry Basic SKU.
3. Azure Key Vault.
4. Azure Container Apps environment.
5. One backend Container App.
6. One frontend Container App.

### Backend Runtime Variables

The backend must receive these environment variables in Azure:

1. `AES_CBC_KEY`
2. `AES_CBC_IV`
3. `AES_GCM_KEY`
4. `AES_GCM_IV`
5. `RSA_PUBLIC_KEY_PEM`
6. `RSA_PRIVATE_KEY_PEM`
7. `CORS_ALLOWED_ORIGINS`
8. `SERVER_PORT`
9. `MANAGEMENT_PORT`

For Container Apps, the working setup is:

1. `SERVER_PORT=8081`
2. `MANAGEMENT_PORT=8081`

This keeps actuator endpoints reachable on the same ingress port used by the backend app.

### Frontend Runtime Variables

The frontend Container App must receive these environment variables:

1. `PORT=4000`
2. `API_BACKEND_URL=https://<backend-fqdn>/api/v1`
3. `NG_ALLOWED_HOSTS=<frontend-fqdn>,localhost,127.0.0.1`

`API_BACKEND_URL` is read at runtime through `/runtime-config.js`, so the same image can be reused across environments.

`NG_ALLOWED_HOSTS` is required because Angular SSR validates the incoming `Host` header. If the Azure Container Apps hostname is missing, the frontend returns a bad request similar to:

```text
Header "host" with value "<frontend-fqdn>" is not allowed.
```

### Example Deployment Flow

```bash
az login
az account set --subscription <subscription-id>

export LOCATION=eastus
export RG=rg-jciphertools
export ACR=<globally-unique-acr-name>
export KV=<globally-unique-keyvault-name>
export ENV=env-jciphertools
export BACKEND_APP=ca-jciphertools-backend
export FRONTEND_APP=ca-jciphertools-frontend

az group create --name "$RG" --location "$LOCATION"

az provider register --namespace Microsoft.App
az provider register --namespace Microsoft.OperationalInsights
az provider register --namespace Microsoft.ContainerRegistry
az provider register --namespace Microsoft.KeyVault

az acr create --resource-group "$RG" --name "$ACR" --sku Basic
az acr update --name "$ACR" --admin-enabled true

az containerapp env create --name "$ENV" --resource-group "$RG" --location "$LOCATION"
az keyvault create --name "$KV" --resource-group "$RG" --location "$LOCATION"

docker compose build

az acr login --name "$ACR"
export ACR_LOGIN_SERVER=$(az acr show -n "$ACR" -g "$RG" --query loginServer -o tsv)

docker tag jciphertools-backend:local "$ACR_LOGIN_SERVER/jciphertools-backend:v1"
docker tag jciphertools-frontend:local "$ACR_LOGIN_SERVER/jciphertools-frontend:v1"

docker push "$ACR_LOGIN_SERVER/jciphertools-backend:v1"
docker push "$ACR_LOGIN_SERVER/jciphertools-frontend:v1"
```

Generate and upload secrets:

```bash
openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 -out private.pem
openssl rsa -pubout -in private.pem -out public.pem

az keyvault secret set --vault-name "$KV" --name rsa-private-key-pem --file private.pem
az keyvault secret set --vault-name "$KV" --name rsa-public-key-pem --file public.pem

openssl rand -base64 32 | tr -d '\n' | az keyvault secret set --vault-name "$KV" --name aes-cbc-key --value @-
openssl rand -base64 16 | tr -d '\n' | az keyvault secret set --vault-name "$KV" --name aes-cbc-iv --value @-
openssl rand -base64 32 | tr -d '\n' | az keyvault secret set --vault-name "$KV" --name aes-gcm-key --value @-
openssl rand -base64 12 | tr -d '\n' | az keyvault secret set --vault-name "$KV" --name aes-gcm-iv --value @-
```

Create backend app:

```bash
export ACR_USER=$(az acr credential show -n "$ACR" -g "$RG" --query username -o tsv)
export ACR_PASS=$(az acr credential show -n "$ACR" -g "$RG" --query "passwords[0].value" -o tsv)

az containerapp create \
	--name "$BACKEND_APP" \
	--resource-group "$RG" \
	--environment "$ENV" \
	--image "$ACR_LOGIN_SERVER/jciphertools-backend:v1" \
	--ingress external \
	--target-port 8081 \
	--registry-server "$ACR_LOGIN_SERVER" \
	--registry-username "$ACR_USER" \
	--registry-password "$ACR_PASS" \
	--env-vars SERVER_PORT=8081 MANAGEMENT_PORT=8081
```

Load backend secrets from Key Vault values and map them to env vars:

```bash
export AES_CBC_KEY=$(az keyvault secret show --vault-name "$KV" --name aes-cbc-key --query value -o tsv)
export AES_CBC_IV=$(az keyvault secret show --vault-name "$KV" --name aes-cbc-iv --query value -o tsv)
export AES_GCM_KEY=$(az keyvault secret show --vault-name "$KV" --name aes-gcm-key --query value -o tsv)
export AES_GCM_IV=$(az keyvault secret show --vault-name "$KV" --name aes-gcm-iv --query value -o tsv)
export RSA_PUBLIC_KEY_PEM=$(az keyvault secret show --vault-name "$KV" --name rsa-public-key-pem --query value -o tsv)
export RSA_PRIVATE_KEY_PEM=$(az keyvault secret show --vault-name "$KV" --name rsa-private-key-pem --query value -o tsv)

az containerapp secret set \
	--name "$BACKEND_APP" \
	--resource-group "$RG" \
	--secrets \
		aes-cbc-key="$AES_CBC_KEY" \
		aes-cbc-iv="$AES_CBC_IV" \
		aes-gcm-key="$AES_GCM_KEY" \
		aes-gcm-iv="$AES_GCM_IV" \
		rsa-public-key-pem="$RSA_PUBLIC_KEY_PEM" \
		rsa-private-key-pem="$RSA_PRIVATE_KEY_PEM"

az containerapp update \
	--name "$BACKEND_APP" \
	--resource-group "$RG" \
	--set-env-vars \
		AES_CBC_KEY=secretref:aes-cbc-key \
		AES_CBC_IV=secretref:aes-cbc-iv \
		AES_GCM_KEY=secretref:aes-gcm-key \
		AES_GCM_IV=secretref:aes-gcm-iv \
		RSA_PUBLIC_KEY_PEM=secretref:rsa-public-key-pem \
		RSA_PRIVATE_KEY_PEM=secretref:rsa-private-key-pem \
		CORS_ALLOWED_ORIGINS=https://placeholder.local
```

Create frontend app and point it to the backend:

```bash
export BACKEND_FQDN=$(az containerapp show -n "$BACKEND_APP" -g "$RG" --query properties.configuration.ingress.fqdn -o tsv)

az containerapp create \
	--name "$FRONTEND_APP" \
	--resource-group "$RG" \
	--environment "$ENV" \
	--image "$ACR_LOGIN_SERVER/jciphertools-frontend:v1" \
	--ingress external \
	--target-port 4000 \
	--registry-server "$ACR_LOGIN_SERVER" \
	--registry-username "$ACR_USER" \
	--registry-password "$ACR_PASS" \
	--env-vars \
		PORT=4000 \
		API_BACKEND_URL=https://$BACKEND_FQDN/api/v1
```

After the frontend exists, allow its hostname in both backend CORS and frontend SSR host validation:

```bash
export FRONTEND_FQDN=$(az containerapp show -n "$FRONTEND_APP" -g "$RG" --query properties.configuration.ingress.fqdn -o tsv)

az containerapp update \
	--name "$BACKEND_APP" \
	--resource-group "$RG" \
	--set-env-vars CORS_ALLOWED_ORIGINS=https://$FRONTEND_FQDN

az containerapp update \
	--name "$FRONTEND_APP" \
	--resource-group "$RG" \
	--set-env-vars NG_ALLOWED_HOSTS=$FRONTEND_FQDN,localhost,127.0.0.1

echo https://$FRONTEND_FQDN
```

### Post-Deployment Checks

Backend health:

```bash
curl https://<backend-fqdn>/actuator/health
```

Backend environment validation:

```bash
curl https://<backend-fqdn>/api/v1/debug/env
```

If `/api/v1/debug/env` returns a non-200 response, one or more required values are missing or malformed.

## Local Development Without Docker

### Backend

The backend requires these environment variables because no fallbacks are defined:

1. `AES_CBC_KEY`
2. `AES_CBC_IV`
3. `AES_GCM_KEY`
4. `AES_GCM_IV`
5. `RSA_PUBLIC_KEY_PEM`
6. `RSA_PRIVATE_KEY_PEM`
7. `CORS_ALLOWED_ORIGINS`

Run backend tests and package:

```bash
cd jciphertools-backend
mvn clean install
```

Run backend app (after exporting required env vars):

```bash
mvn spring-boot:run
```

### Frontend

```bash
cd jciphertools-frontend
npm ci
npm start
```

Default dev URL: `http://localhost:4200`

## Testing

Backend:

```bash
cd jciphertools-backend
mvn test
```

Frontend (headless):

```bash
cd jciphertools-frontend
npm test
```

## Useful Files
1. API request examples: [jciphertools-backend/http/test-endpoints.http](jciphertools-backend/http/test-endpoints.http)
2. Backend compose entrypoint: [jciphertools-backend/docker-entrypoint.sh](jciphertools-backend/docker-entrypoint.sh)
3. Backend runtime configuration: [jciphertools-backend/src/main/resources/application.yml](jciphertools-backend/src/main/resources/application.yml)
4. Backend environment validation endpoint: [jciphertools-backend/src/main/java/com/jciphertools/presentation/controllers/DebugController.java](jciphertools-backend/src/main/java/com/jciphertools/presentation/controllers/DebugController.java)
5. Frontend SSR runtime config and host validation entrypoint: [jciphertools-frontend/src/server.ts](jciphertools-frontend/src/server.ts)
6. Frontend SSR allow-list defaults: [jciphertools-frontend/angular.json](jciphertools-frontend/angular.json)

## Troubleshooting
1. If backend fails on missing env vars, confirm the local `.env` file or Azure Key Vault mappings provide all required values.
2. If the frontend shows `API_BACKEND_URL_NOT_SET`, confirm `API_BACKEND_URL` is defined in the frontend Container App and points to `https://<backend-fqdn>/api/v1`.
3. If the frontend returns `Header "host" with value "<frontend-fqdn>" is not allowed.`, update the frontend Container App with `NG_ALLOWED_HOSTS=<frontend-fqdn>,localhost,127.0.0.1`.
4. If `/actuator/health` is unreachable in Azure Container Apps, confirm the backend app uses `SERVER_PORT=8081` and `MANAGEMENT_PORT=8081`.
5. If ports are busy locally, stop conflicting processes or remap ports in [docker-compose.yml](docker-compose.yml).

