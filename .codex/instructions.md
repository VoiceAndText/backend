# Codex Instructions

## Core Behavior

- Always minimize context usage
- Never read the entire repository unless explicitly requested
- Start from the smallest plausible scope
- Expand context only when blocked or when a dependency is unclear

## Architecture Discipline

- Follow Clean Architecture strictly
- Keep controllers thin
- Keep business logic in the application layer
- Keep domain free from infrastructure implementation details
- Do not expose domain entities directly in API responses

## Change Discipline

- Prefer editing existing files over creating new files
- Do not create future-facing placeholder files
- Keep code changes narrowly scoped
- Preserve existing naming, packaging, and response conventions

## Working Style

Before coding on non-trivial tasks:
- identify target module
- identify minimum initial files to inspect
- state why that scope is likely enough

After coding:
- summarize changed files
- summarize why each file changed
- note whether context expansion was necessary

## Communication Style

- Be concise
- Do not over-explain unless asked
- Reuse project patterns whenever possible