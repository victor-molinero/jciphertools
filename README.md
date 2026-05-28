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
7. Azure-ready container deployment with secrets injected from Azure Key Vault and images published to Azure Container Registry.

## Architecture
1. Frontend runs on port `4000` in Docker Compose and calls backend endpoints under `/api/v1`.
2. Backend runs on port `8081`, management endpoints on `8082`.
3. Runtime secrets are provided as environment variables, ideally from Azure Key Vault at deployment time.
4. Backend entrypoint starts the JVM directly and expects env vars to already be present.
5. Azure Container Registry stores the backend and frontend images.

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

### Azure Deployment Overview

1. Build backend and frontend images and push them to Azure Container Registry.
2. Create secrets in Azure Key Vault for AES keys, RSA PEM values, and any required runtime settings.
3. Deploy the containers to an Azure runtime such as Azure Container Apps or AKS.
4. Map Key Vault secrets to container environment variables.
5. Configure the frontend with the deployed backend URL.

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
3. Azure deployment and secret-injection notes: update this README and deployment scripts as the Key Vault flow is finalized.

## Troubleshooting
1. If backend fails on missing env vars, confirm the local `.env` file or Azure Key Vault mappings provide all required values.
2. If ports are busy, stop conflicting processes or remap ports in [docker-compose.yml](docker-compose.yml).

