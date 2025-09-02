# Android Key Attestation API Wrapper

This repository now includes a Ktor-based REST API wrapper around the Android Key Attestation verification library.

## Quick Start

### 1. Build and Run Locally

```bash
./gradlew run
```

The server will start on port 8080 (or the port specified in the `PORT` environment variable).

### 2. API Usage

#### Endpoint: `POST /verify`

**Request Body:**
```json
{
  "attestationChainPem": [
    "-----BEGIN CERTIFICATE-----\nMIIB...==\n-----END CERTIFICATE-----",
    "-----BEGIN CERTIFICATE-----\nMIIC...==\n-----END CERTIFICATE-----"
  ],
  "challenge": "BASE64_SERVER_NONCE"
}
```

**Response (Success):**
```json
{
  "ok": true,
  "packageName": "com.example.app",
  "signingCertDigest": "a1b2c3...",
  "verifiedBootState": "VERIFIED"
}
```

**Response (Error):**
```json
{
  "ok": false,
  "error": "Challenge mismatch"
}
```

### 3. Deploy to Railway

1. Go to [Railway](https://railway.app)
2. Create a new project → "Deploy from GitHub repo"
3. Select this repository
4. Railway will auto-detect the Gradle project and deploy it
5. Your API will be available at: `https://<your-app>.up.railway.app/verify`

### 4. Example cURL Request

```bash
curl -X POST https://your-app.up.railway.app/verify \
  -H "Content-Type: application/json" \
  -d '{
    "attestationChainPem": [
      "-----BEGIN CERTIFICATE-----\nYOUR_CERT_HERE\n-----END CERTIFICATE-----"
    ],
    "challenge": "YOUR_BASE64_CHALLENGE"
  }'
```

## Project Structure

- `src/main/kotlin/Main.kt` - Ktor API server
- `attestation/` - Java attestation verification library
- `build.gradle.kts` - Main build configuration
- `attestation/build.gradle.kts` - Attestation module build configuration

## Environment Variables

- `PORT` - Server port (default: 8080)

## Original Library

This API wrapper is built on top of the original Android Key Attestation library. See the original documentation for details about the attestation verification process.
