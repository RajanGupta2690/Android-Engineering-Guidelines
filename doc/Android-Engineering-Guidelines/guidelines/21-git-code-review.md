# 21 · Git & Code Review

[← 20 Testing](20-testing.md) · [README](../../../README.md) · **Next:** [22 · Static Analysis & Quality →](22-static-analysis-quality.md)

> Authority: 🟣 **Team Standard**. Severity 🔴 for the PR gate.

---

## 1. Branches & commits — 🟣 Team Standard · 🟡 Recommended

- **Branch names:** `feature/<ticket>-short-desc`, `fix/<ticket>-short-desc`, `chore/…`.
- **Commit messages:** imperative present tense, concise subject (~50 chars), body explaining *why*. Conventional Commits (`feat:`, `fix:`, `refactor:`) encouraged.
- **Never commit directly to `main`/`master`** — push a branch, open a PR.

---

## 2. PR size & hygiene — 🟠 Strong Eng. · 🟡 Recommended

Keep PRs small and single-purpose — big PRs get rubber-stamped. No commented-out code, debug logs, TODOs without a ticket, dead code, or temporary hacks ([24 · Code Smells](24-code-smells.md)).

**Why small PRs?** A 200-line PR gets a real review; a 2000-line PR gets an "LGTM". Small PRs catch more bugs and merge faster.

---

## 3. PR checklist (paste into the PR template) — 🔴 Mandatory

```text
[ ] Naming conventions followed (04)
[ ] No unnecessary abstraction / no pass-through layers (01, 02)
[ ] Functions/classes within size thresholds; big units split by responsibility (03)
[ ] Each non-trivial function has a one-line purpose comment (03)
[ ] One primary type per file; no unrelated classes stacked together (03)
[ ] No hardcoded user-facing strings (23)
[ ] Icons use vector/SVG; banners/large images are remote, not bundled (08)
[ ] No unnecessary/duplicate resources (08)
[ ] No commented-out code, dead code, or debug logs (03, 13)
[ ] No secrets committed; no PII in logs (13, 14)
[ ] Tests added/updated where logic changed (20)
[ ] Lint / detekt / ktlint clean — warnings fixed, not suppressed (22)
[ ] No unnecessary dependency added (17)
[ ] UI reviewed (loading/success/error/empty states) (07)
[ ] Accessibility considered (23)
[ ] Performance impact considered — no main-thread work (18)
[ ] Release/R8 impact considered (keep rules?) (15)
[ ] 16 KB impact considered if a dependency was added (16)
[ ] Any rule deviation documented per Exception Process (26)
```

---

## 4. Review culture — 🟠 Strong Eng. · 🟡 Recommended

Review the **design and behavior**, not just syntax (formatting is automated — [22](22-static-analysis-quality.md)). Be direct and specific; correct mistakes plainly; explain reasoning. Approve when it's correct and maintainable — not when it's "how I'd have written it."

---

**Next:** [22 · Static Analysis & Quality →](22-static-analysis-quality.md)
#
#
#
#