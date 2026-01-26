# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

FoxDen Vue frontend - A Vue 3 application that serves as the web UI for the FoxDen multi-tenant SaaS system. Built with Vite, TypeScript, PrimeVue, and connects to a Spring Boot backend.

## Build and Development Commands

### Development server
```bash
bun run dev
```
Starts Vite dev server with hot reload. Proxies API requests to `http://127.0.0.1:11003`.

### Build for production
```bash
bun run build
```
Runs type-check and build-only in parallel.

### Type checking only
```bash
bun run type-check
```
Runs `vue-tsc` for type checking.

### Linting
```bash
npx xo [file|glob...]
```
XO is used for linting. Configuration is in [xo.config.ts](xo.config.ts).

### Unit tests (Vitest)
```bash
bun run test:unit
```
Runs unit tests with Vitest using jsdom environment.

### E2E tests (Cypress)
```bash
bun run test:e2e:dev    # Run against dev server
bun run test:e2e        # Run against production build
```

### Install dependencies
Use **bun** (preferred) or npm:
```bash
bun install
```
Note: Use `bun install` if you encounter module resolution errors with `xo` (e.g., `packageDirectorySync` not found).

## Technology Stack

- **Runtime**: Node.js ^20.19.0 || >=22.12.0
- **Package Manager**: Bun (preferred) - see [bun.lock](bun.lock)
- **Framework**: Vue 3.5+ with Composition API
- **Build Tool**: Vite 7.x
- **Language**: TypeScript 5.9
- **UI Library**: PrimeVue 4.5+ with Aura theme
- **Router**: Vue Router 4.6+
- **i18n**: vue-i18n 11.2+
- **State**: `vue-fn` for domain aggregation pattern
- **HTTP**: Axios
- **Encryption**: `crypto-js`, `jsencrypt`
- **Testing**: Vitest (unit), Cypress (E2E)
- **Linting**: XO with TypeScript config

## Architecture

### Directory Structure
```
src/
├── api/          # API layer - HTTP request functions grouped by domain
│   └── login/    # Login API with types.ts for request/response types
├── components/   # Vue components
│   ├── icons/    # Icon components
│   └── __tests__/ # Component tests
├── domain/       # Domain aggregates using vue-fn pattern
│   ├── language/ # i18n language aggregate (singleton)
│   └── user/     # User domain aggregate
├── utils/        # Utility modules
├── views/        # Page components (routes)
├── router/       # Vue Router configuration
├── assets/       # Static assets
├── global.d.ts   # Global type declarations
└── main.ts       # Application entry point
```

### Domain Aggregation Pattern (vue-fn)

This project uses `vue-fn` for domain-driven state management. Each domain has an "aggregate" (agg) that encapsulates state and commands.

Example - Language Aggregate ([src/domain/language/index.ts](src/domain/language/index.ts)):
```typescript
const agg = createSingletonAgg(() => {
  const currentLanguage = ref<Language>(Language.zhCN);
  return {
    states: { currentLanguage },
    commands: {},
  };
});
```

Use aggregates with composable functions:
```typescript
import { useLanguageAgg } from '@/domain/language';
const languageAgg = useLanguageAgg();
```

### Request/Response Layer

The axios instance in [src/utils/request.ts](src/utils/request.ts) provides:
- **Request interceptor**: Adds auth token, handles encryption, prevents duplicate submits
- **Response interceptor**: Handles decryption, error codes, session expiration
- **Encryption**: AES + RSA hybrid encryption (configurable via `VITE_APP_ENCRYPT`)
- **i18n**: Adds `Content-Language` header based on current language

Key headers for API requests:
- `isToken: false` - Skip adding auth token
- `isEncrypt: 'true'` - Enable request/response encryption
- `repeatSubmit: false` - Disable duplicate submit prevention

### Path Aliases

The `@` alias maps to `src/` directory (configured in both [vite.config.ts](vite.config.ts) and [tsconfig.app.json](tsconfig.app.json)).

## TypeScript Configuration

The project uses project references with three configs:
- [tsconfig.app.json](tsconfig.app.json) - Application code
- [tsconfig.node.json](tsconfig.node.json) - Node tooling (Vite config)
- [tsconfig.vitest.json](tsconfig.vitest.json) - Test files

## XO Linting Rules

Key XO configuration ([xo.config.ts](xo.config.ts)):
- Uses `@typescript-eslint/consistent-type-definitions: 'off'` (both `type` and `interface` allowed)
- Restricted types: `object` (use `Record<string, unknown>`), `Buffer` (use `Uint8Array`)
- Naming convention allows: `UPPER_CASE`, `camelCase`, `strictCamelCase`, `PascalCase`, `StrictPascalCase`
- Filename case: `kebabCase`, `camelCase`, or `pascalCase`
- 2-space indentation
- Ignores: `node_modules/`, `designs/`, `**/*.test.ts`

## Environment Variables

The project expects these Vite environment variables:
- `VITE_APP_PORT` - Dev server port
- `VITE_APP_BASE_API` - API base path for proxy
- `VITE_APP_CLIENT_ID` - OAuth client ID for requests
- `VITE_APP_ENCRYPT` - Enable/disable request encryption ('true'/'false')

## Known Issues

- **XO `pkg-dir` module error**: If you see `Named export 'packageDirectorySync' not found`, run `bun install` to fix dependency hoisting.
