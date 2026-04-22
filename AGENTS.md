# VoiceAndText - Repository Operating Guide

## Purpose
This repository is configured for AI-assisted development with Codex.

The primary goal is:
- minimize token usage
- avoid unnecessary repository scanning
- preserve architecture consistency
- modify only the smallest necessary scope

Codex must treat this repository as a structured Clean Architecture project and work incrementally.

---

## Project Summary

VoiceAndText is a multimodal emotion analysis platform.

It analyzes:
- spoken audio
- transcribed text

It produces:
- text-based emotion
- voice-based emotion
- final emotion classification
- mismatch score between text and voice
- summary explanation

This system is NOT primarily user-centric.

The core domain concept is:

**AnalysisRequest**

Users (member/guest) create analysis requests.
Audio file, transcription, and analysis result are attached to an analysis request.

---

## Core Domain Concepts

Main entities:
- User
- AnalysisRequest
- AudioFile
- Transcription
- AnalysisResult

Key relation summary:
- User 1:N AnalysisRequest
- AnalysisRequest 1:1 AudioFile
- AnalysisRequest 1:1 Transcription
- AnalysisRequest 1:1 AnalysisResult

Guest flow:
- analysis_requests.user_id may be null
- is_guest distinguishes guest requests

Codex must not assume all flows are member-only.

---

## Architecture

This project uses **Clean Architecture**.

Top-level package root:
- com.quadcore.voiceandtext

Layer structure:
- common
- domain
- application
- infrastructure
- presentation

Responsibilities:

### presentation
Contains:
- controllers
- request DTOs
- response DTOs

Must NOT contain:
- business logic
- persistence logic
- domain-infrastructure coupling

### application
Contains:
- use cases
- application services
- orchestration logic
- transaction boundary handling as needed

Must NOT contain:
- HTTP-specific response formatting
- direct persistence implementation details unless abstracted through ports

### domain
Contains:
- entities
- enums
- value objects
- repository ports/interfaces
- domain rules

Must NOT contain:
- controller DTOs
- framework-specific infrastructure code
- external system implementation details

### infrastructure
Contains:
- JPA implementations
- Redis integration
- S3 integration
- AI external client integration
- other external adapters

### common
Contains:
- common response wrapper
- global exception handling
- shared configuration
- base classes
- utilities that are truly cross-cutting

---

## Infrastructure Constraints

Deployment / runtime assumptions:
- backend runs on AWS EC2
- database is PostgreSQL
- cache / async queue is Redis
- file storage is S3
- AI inference server is external university server, NOT AWS

Codex must not assume AI inference is handled inside the Spring application.

---

## Hard Rules

Codex must follow all of the following:

1. Never scan the whole repository unless the user explicitly asks for a repository-wide audit.
2. Always begin with the smallest plausible set of files.
3. Expand context only when blocked or when dependencies are unclear.
4. Keep diffs minimal.
5. Prefer modifying existing files over creating new files.
6. Do not create placeholder files for future work.
7. Do not introduce new architectural patterns unless explicitly requested.
8. Do not expose entity objects directly through controllers.
9. Do not put business logic into controllers.
10. Do not let domain depend on infrastructure.
11. Reuse existing common response / exception format.
12. Respect existing package naming and code style.

---

## File Reading Policy

Codex must choose files incrementally.

### For new API work
First inspect only:
- controller package of the target domain, if present
- request/response DTOs of the target domain, if present
- related application service or use case
- related domain entity / port / repository interface
- common response / exception structure only if response shape is relevant

Inspect infrastructure only if:
- persistence behavior changes
- external integration changes
- no existing adapter pattern is clear

### For feature modification
Inspect only:
- target feature entry point
- directly related DTO / service / use case
- directly related repository port or adapter if needed

### For bug fixing
Inspect only:
- failing file
- direct dependencies
- immediate caller/callee if needed

### For auth / JWT / mypage work
Start from:
- related auth/security configuration
- target user controller/service
- relevant DTOs
- current authentication / authorization extraction path

Do not inspect unrelated analysis/file modules unless the request actually needs them.

### For common response / global exception work
Start from:
- common response classes
- exception handler
- any directly used response factory / error code structures

---

## Required Working Style

Before coding, Codex should briefly state:
- target domain or module
- minimal initial file scope
- why that scope is likely sufficient

While coding:
- prefer existing patterns
- avoid broad refactors
- avoid unrelated cleanup

After coding, Codex should briefly state:
- files changed
- why each file changed
- whether scope expansion was necessary
- what was intentionally not touched

---

## Preferred Decision Order

For any non-trivial request, Codex should reason in this order:

1. What is the actual target domain?
2. What is the smallest file scope that can answer this?
3. Is there an existing pattern in the same domain?
4. Can the task be solved by editing existing files only?
5. Is any additional context truly necessary?

---

## Skill Usage

Use the following skill roles:

- `task-router`
    - decide the smallest file set to inspect first

- `create-api`
    - create or extend any API generically
    - applies to analysis, user, auth, file, admin, mypage, common response related APIs

- `edit-existing-feature`
    - change or extend existing feature behavior with minimal impact

- `debug-bug`
    - diagnose and fix bugs with minimal exploration

---

## Completion Criteria

A task is considered complete only if:
- the requested behavior is implemented
- architecture boundaries are preserved
- only necessary files were changed
- no unnecessary files were created
- existing response / exception conventions were preserved
- the change is scoped and explainable