# CI/CD - TreasuryFlow

## Overview

All CI/CD runs on **GitHub Actions** with **path-based triggers** - each platform's workflow only runs when its files change.

## Workflows

| Workflow | Trigger Paths | What It Does |
|----------|--------------|--------------|
| `backend.yml` | `backend/**`, `shared/**` | Gradle test + build, Docker push to Azure ACR, deploy to Container Apps |
| `web-user-app.yml` | `web-user-app/**`, `shared/**` | npm lint + typecheck + `next build` + OpenNext bundle + `wrangler deploy --dry-run`, then deploy to Cloudflare Workers on `main` |
| `web-admin-portal.yml` | `web-admin-portal/**`, `shared/**` | npm lint + typecheck + `next build` + OpenNext bundle + `wrangler deploy --dry-run`, then deploy to Cloudflare Workers on `main` |
| `mobile-android.yml` | `mobile-android/**`, `shared/**` | Gradle test + assembleDebug, Fastlane deploy on tags |
| `mobile-ios.yml` | `mobile-ios/**`, `shared/**` | xcodebuild test (macOS runner), Fastlane deploy on tags |
| `api-contracts.yml` | `shared/api-contracts/**` | Validate OpenAPI spec |

## Required GitHub Secrets

### Azure
| Secret | Description |
|--------|-------------|
| `AZURE_CREDENTIALS` | Service principal JSON |
| `AZURE_CONTAINER_REGISTRY` | ACR login server |
| `AZURE_CONTAINER_APP_NAME` | Container App name |
| `AZURE_RESOURCE_GROUP` | Resource group |


### Cloudflare
| Secret | Description |
|--------|-------------|
| `CLOUDFLARE_ACCOUNT_ID` | Account ID |
| `CLOUDFLARE_API_TOKEN` | API token with Workers edit permissions |



### Android
| Secret | Description |
|--------|-------------|
| `ANDROID_KEYSTORE_BASE64` | Signing keystore (base64) |
| `ANDROID_KEYSTORE_PASSWORD` | Keystore password |
| `ANDROID_KEY_ALIAS` | Key alias |
| `ANDROID_KEY_PASSWORD` | Key password |
| `GOOGLE_PLAY_SERVICE_ACCOUNT_JSON` | Play Console service account |



### iOS
| Secret | Description |
|--------|-------------|
| `MATCH_PASSWORD` | Fastlane match encryption password |
| `APP_STORE_CONNECT_API_KEY` | App Store Connect API key JSON |

