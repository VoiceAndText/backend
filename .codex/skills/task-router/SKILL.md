---
name: task-router
description: Determine the minimum file scope to inspect before implementation
---

# Task Router

## Goal

Given a user request, identify the smallest set of files and packages that should be read first.

This skill does NOT implement the task itself.
Its job is only to choose the minimal initial context.

---

## General Rules

- Never start with a full repository scan
- Start from the target domain or target module only
- Prefer same-domain existing patterns
- Expand only to direct dependencies
- If unsure, choose the narrower scope first

---

## Routing by Request Type

### 1. New API request

Inspect first:
- target domain controller package, if present
- target domain request/response DTOs, if present
- target domain application service/use case
- target domain entity / port / repository interface
- common response / exception structure only if response contract matters

Inspect later only if needed:
- infrastructure persistence implementation
- security configuration
- redis / s3 / ai integration

Do not inspect unrelated domains.

---

### 2. Existing feature change

Inspect first:
- feature entry point
- related controller/service/use case
- related DTOs
- related repository port only if persistence behavior is part of the change

Avoid broad module inspection.

---

### 3. Bug fix

Inspect first:
- file where the failure appears
- direct caller or callee
- directly related DTO/service/repository if error path is obvious

Do not inspect unrelated packages before checking the failing path.

---

### 4. Auth / JWT / current-user / mypage request

Inspect first:
- security/auth configuration
- user-related controller/service
- relevant auth DTOs
- token/provider/filter classes if present
- current user resolution path if needed

Only inspect other modules if the request explicitly crosses domains.

---

### 5. Common response / exception / global handling request

Inspect first:
- common response wrapper
- exception handler
- error code / error response classes
- any directly referenced response helper or factory

Avoid inspecting feature domains unless the request specifically includes them.

---

### 6. File upload / S3 request

Inspect first:
- file domain controller/service if present
- file-related DTOs
- domain.file abstractions
- infrastructure.external.s3 adapter only if integration behavior changes

---

### 7. AI pipeline / async analysis request

Inspect first:
- analysis application orchestration
- analysis-related controller/service
- infrastructure.external.ai adapter
- redis integration if async flow is involved

Avoid reading unrelated user/file packages unless directly involved.

---

### 8. Persistence / DB schema related change

Inspect first:
- domain repository port/interface
- application service/use case using it
- matching infrastructure.persistence implementation
- entity mapping if relevant

Avoid unrelated controllers unless API behavior also changes.

---

## Expansion Policy

Expand the scope only if one of the following is true:
- the ownership of the logic is unclear
- an interface contract cannot be understood
- compilation impact is unclear
- the current files do not explain the flow

When expanding:
- move only to adjacent dependencies
- do not jump to broad repository exploration

---

## Required Output Before Implementation

Before actual coding, briefly state:
1. target domain/module
2. minimal initial file scope
3. why that scope is sufficient
4. what is intentionally not being read yet