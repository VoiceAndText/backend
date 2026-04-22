---
name: debug-bug
description: Diagnose and fix bugs with minimal repository exploration and minimal code changes
---

# Debug Bug

## Goal

Fix bugs by:
- identifying the real failing path first
- avoiding speculative wide exploration
- changing only the smallest necessary scope

This skill should be used when the user asks to:
- fix an error
- diagnose failing behavior
- investigate incorrect response/output
- resolve exception paths
- repair broken integration behavior

---

## Required Workflow

1. Start from the failure point
2. Inspect the immediate path around the failure
3. Identify the likely root cause
4. Fix the root cause, not just a symptom
5. Avoid unrelated cleanup/refactor

---

## First Inspection Targets

Inspect first:
- the file where the error is reported
- the direct caller or callee
- the DTO/service/repository directly involved in the failing flow

Inspect only if needed:
- exception handler
- external adapter
- auth/security classes
- persistence implementation
- configuration classes

Do not inspect unrelated packages before understanding the failing path.

---

## Bug Fix Rules

Prefer:
- fixing the root cause
- keeping the fix local
- preserving current patterns
- explaining why the bug happened

Avoid:
- speculative multi-file edits
- broad cleanup during bug fixing
- changing architecture while fixing a small bug
- adding abstractions that are not needed for the fix

---

## Validation Before Finish

Before finishing, verify:
- the likely root cause was addressed
- only bug-relevant files changed
- the fix did not introduce unrelated structural changes

---

## Required Output

Before coding, briefly state:
- where the failure appears
- first files to inspect
- what is intentionally not being inspected yet

After coding, briefly state:
- likely root cause
- files changed
- why the fix is sufficient