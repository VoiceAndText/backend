---
name: edit-existing-feature
description: Modify or extend an existing feature with minimal file changes and no unnecessary restructuring
---

# Edit Existing Feature

## Goal

Modify existing functionality while:
- preserving current architecture
- minimizing context usage
- minimizing file changes
- avoiding unnecessary new abstractions

This skill should be used when the user asks to:
- adjust existing behavior
- extend an existing endpoint
- change validation
- change response fields
- modify service logic
- add a small capability to an existing flow

---

## Required Workflow

1. Identify the existing entry point
2. Read only the files directly involved in the current behavior
3. Trace direct dependencies only as needed
4. Modify the smallest possible set of files
5. Preserve existing structure unless the request explicitly asks for redesign

---

## First Inspection Targets

Inspect first:
- existing controller for the feature
- related request/response DTOs
- related application service/use case
- related domain rule/entity if the behavior depends on domain logic

Inspect only if needed:
- persistence implementation
- mapper classes
- validators
- auth/security classes
- external adapters

Do not inspect unrelated domains or modules.

---

## Change Rules

Prefer:
- editing existing methods
- extending existing DTOs
- reusing existing response format
- preserving method/class naming style

Avoid:
- creating replacement structures beside current ones
- broad refactor while doing a small change
- changing package boundaries unnecessarily
- renaming unrelated symbols

---

## If New Files Are Truly Necessary

Create a new file only if:
- the current project pattern clearly requires it
- the existing structure cannot hold the change cleanly
- the user request explicitly implies a new component

Even then:
- create the minimum number of files
- keep naming aligned with current conventions

---

## Validation Before Finish

Before finishing, verify:
- only feature-relevant files changed
- architecture boundaries stayed intact
- no unrelated cleanup was mixed in
- any new file was truly necessary

---

## Required Output

Before coding, briefly state:
- existing feature entry point
- minimum files to inspect
- why no broader scan is needed

After coding, briefly state:
- changed files
- why each changed
- whether any new file was unavoidable