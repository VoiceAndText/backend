---
name: create-api
description: Create or extend any API in this repository using minimal context and existing project conventions
---

# Create API

## Goal

Create or extend any API in this repository while:
- minimizing file reads
- preserving Clean Architecture
- reusing existing patterns
- keeping changes minimal

This skill is generic and applies to:
- analysis APIs
- user APIs
- auth/JWT APIs
- mypage APIs
- file APIs
- admin APIs
- common response related APIs
- future APIs in new domains

---

## Required Workflow

1. Use the smallest relevant scope first
2. Inspect existing patterns in the same domain
3. Prefer extending existing files over creating parallel structures
4. Keep controller thin
5. Place business logic in application layer
6. Reuse common response and exception conventions
7. Create only the minimum required files

---

## What to Inspect First

Always inspect, if present:
- target domain controller
- target domain request/response DTOs
- target domain application service/use case
- target domain entity / repository port / interface
- common response and exception format if API contract depends on them

Inspect only if needed:
- persistence implementation
- auth/security config
- mapper classes
- validator classes
- redis/s3/ai adapters

Do not inspect unrelated domains first.

---

## API Design Rules

### Controller
Controller should:
- accept request
- validate / delegate
- return standardized response

Controller must NOT:
- contain business logic
- perform persistence directly
- assemble unrelated cross-domain workflows unless already established by project pattern

### Application
Application layer should:
- contain business flow
- orchestrate domain + ports
- handle use case logic

Application layer must NOT:
- leak HTTP response concerns
- directly format controller responses unless project pattern explicitly does so

### Domain
Domain should:
- contain entity, enum, domain rules, repository ports

Domain must NOT:
- depend on controller DTOs
- depend on infrastructure implementations

### Infrastructure
Infrastructure should:
- implement repository/adapters
- integrate DB, Redis, S3, AI, auth internals where appropriate

---

## Reuse Rules

When possible:
- extend an existing controller
- extend an existing service/use case
- reuse DTO naming patterns
- reuse existing response wrapper
- reuse existing exception strategy

Avoid:
- creating duplicate controller families
- inventing a new response style
- inventing a new package style
- adding unnecessary abstractions

---

## If No Existing Pattern Exists

If the target domain does not yet have an API structure, create only the minimal required set.

Usually this means only what is necessary for the requested behavior, such as:
- controller
- request DTO if needed
- response DTO if needed
- application service/use case
- domain port/interface only if needed
- infrastructure implementation only if truly required

Do not generate future placeholder files for imagined expansion.

---

## Auth / JWT / Current User Rules

If the API request involves:
- login
- signup
- token issuance
- token refresh
- current-user lookup
- mypage authorization

then:
- inspect existing security/auth structure first
- align with current token/filter/provider pattern
- do not invent a separate auth style
- do not assume analysis-domain conventions apply to auth unless shared by existing project code

---

## Response Contract Rules

If the repository already has:
- a common response wrapper
- a success response structure
- a standard error response structure
- a global exception handler

then the new API must follow those exactly.

Do not create a second response convention.

---

## Validation Before Finish

Before finishing, verify:
- controller is thin
- business logic is not in controller
- dto and entity are separated
- existing conventions were reused
- unrelated files were not changed
- newly created files are the minimum necessary

---

## Required Output

Before coding, briefly state:
- target domain
- initial file scope
- why that scope is likely enough

After coding, briefly state:
- changed files
- why each changed
- whether scope expansion happened