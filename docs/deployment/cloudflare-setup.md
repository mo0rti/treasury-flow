# Cloudflare Workers Deployment - TreasuryFlow

## Current Approach

- Web apps deploy to **Cloudflare Workers** using **OpenNext**. This template does not target Cloudflare Pages.
- `npm run dev` uses the normal Next.js development server.
- `npm run preview` is the closest local production check because it runs the built app through Wrangler and `workerd`.
- OpenNext's official guidance still recommends Linux, macOS, or WSL for the most reliable Cloudflare build and deploy experience.

## Applications

| Application | Worker Name | Hosting |
|-------------|-------------|---------|
| User Web App | `treasuryflow-web-user-app` | Cloudflare Workers via OpenNext |
| Admin Web Portal | `treasuryflow-web-admin-portal` | Cloudflare Workers via OpenNext |


## 1. Configure Non-Secret Worker Variables

Edit each app's `wrangler.jsonc` before the first deploy.


### `web-user-app/wrangler.jsonc`

Set these values to your deployed backend URL and public web URL:

| Variable | Purpose |
|----------|---------|
| `API_BASE_URL` | Server-side calls from NextAuth and route handlers |
| `NEXT_PUBLIC_API_BASE_URL` | Browser-visible API base URL |
| `AUTH_URL` | Public URL of the deployed user web app |
| `AUTH_TRUST_HOST` | Keep as `true` behind Cloudflare |
| `AUTH_GOOGLE_ID` | Google OAuth client ID |
| `AUTH_APPLE_ID` | Apple service/client ID |




### `web-admin-portal/wrangler.jsonc`

Set these values to your deployed backend URL and public admin URL:

| Variable | Purpose |
|----------|---------|
| `API_BASE_URL` | Server-side calls from NextAuth and route handlers |
| `NEXT_PUBLIC_API_BASE_URL` | Browser-visible API base URL |
| `AUTH_URL` | Public URL of the deployed admin portal |
| `AUTH_TRUST_HOST` | Keep as `true` behind Cloudflare |


## 2. Prepare Local Preview Variables

For `npm run preview`, copy `.dev.vars.example` to `.dev.vars` inside each web app you want to preview.


```bash
cd web-user-app
cp .dev.vars.example .dev.vars
```



```bash
cd web-admin-portal
cp .dev.vars.example .dev.vars
```


## 3. Create Cloudflare Secrets

Create secrets once per Worker using Wrangler or the Cloudflare dashboard.


### User Web App Secrets

```bash
cd web-user-app
npx wrangler secret put AUTH_SECRET
npx wrangler secret put AUTH_GOOGLE_SECRET
npx wrangler secret put AUTH_APPLE_SECRET

```



### Admin Web Portal Secrets

```bash
cd web-admin-portal
npx wrangler secret put AUTH_SECRET
```


## 4. Preview and Deploy


```bash
cd web-user-app
npm install
npm run preview
npm run deploy
```



```bash
cd web-admin-portal
npm install
npm run preview
npm run deploy
```


## 5. Backend Interop

If your backend is deployed to Azure from this template, set `CORS_ALLOWED_ORIGINS` there to match the public Cloudflare URLs for the web apps. Otherwise the browser apps can deploy successfully but fail CORS checks against the backend.

## CI/CD

GitHub Actions deploys on push to `main`:
- Runs lint, typecheck, `next build`, OpenNext bundle generation, and `wrangler deploy --dry-run` on validation jobs
- Runs `npm run deploy` on `main`

## Required GitHub Secrets

| Secret | Description |
|--------|-------------|
| `CLOUDFLARE_ACCOUNT_ID` | Cloudflare account ID |
| `CLOUDFLARE_API_TOKEN` | API token with Workers edit permissions |

## References

- Cloudflare Next.js on Workers guide: https://developers.cloudflare.com/workers/framework-guides/web-apps/nextjs/
- Cloudflare Wrangler configuration reference: https://developers.cloudflare.com/workers/wrangler/configuration/
- OpenNext Cloudflare docs: https://opennext.js.org/cloudflare

## Related Docs

- [Architecture Overview](../architecture.md) for hosting layout and platform map
- [User Web App Guide](../../web-user-app/docs/guide.md) for app-local commands and structure
- [Admin Web Portal Guide](../../web-admin-portal/docs/guide.md) for portal-local commands and structure
- [CI/CD](ci-cd.md) for deploy automation and required secrets
