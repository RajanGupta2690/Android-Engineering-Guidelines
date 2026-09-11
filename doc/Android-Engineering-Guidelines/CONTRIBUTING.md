# Contributing to the Android Engineering Guidelines

These guidelines are a living document owned by the Android team. This file explains how to change a rule, keep platform facts current, and record exceptions.

---

## 1. When to change a rule vs. take an exception

- **One-off situation** where a rule doesn't fit this specific code → don't change the rule; take a documented **exception** ([guidelines/26-exceptions-process.md](guidelines/26-exceptions-process.md)).
- **The rule itself is wrong, outdated, or disputed** → propose a change here, so the whole team benefits and the doc stays honest.

> "I disagree with a rule" is a reason to open a proposal — never a reason to quietly ignore it.

---

## 2. How to propose a change

1. Open a branch: `chore/guidelines-<short-desc>`.
2. Edit the relevant file under [`guidelines/`](guidelines). Keep the house style:
   - Each rule carries an **Authority** label (🟢 Official / 🔵 Kotlin / 🟠 Strong Eng / 🟣 Team) and a **Severity** label (🔴 Mandatory / 🟡 Recommended).
   - Prefer the **Recommended → Avoid → Why → Example → Exception** shape.
   - Add an **"In plain words"** explanation for anything a junior might not know.
   - Every 🟢 Official claim must link to an official source (developer.android.com / kotlinlang.org).
3. If you add/adjust a runnable pattern, update [`examples/`](examples) too.
4. Open a PR; tag the Android lead as reviewer. Discuss, then merge.

---

## 3. House rules for the docs themselves

- **Don't present a Team Standard (🟣) as an official Google rule.** Keep authority honest.
- **Don't invent platform requirements.** If it's not in an official doc, it's 🟠 or 🟣, not 🟢.
- **Don't over-claim numbers** (e.g. "WebP always saves X%"). Say "measure it".
- Keep each file focused on its topic; cross-link instead of duplicating.
- Keep examples honest: label bad examples clearly and say *why* they're bad.

---

## 4. Keeping platform facts current — 🔴 do this every quarter and after each Android major

The [README currency box](../../README.md#currency-verified-against-official-sources--september-2026) and these files contain time-sensitive facts. Re-verify against the live official docs:

```text
[ ] Google Play target API requirement (README, 17) — currently Android 16 / API 36
[ ] 16 KB page-size deadlines & guidance (16) — update deadline currently Feb 1, 2027
[ ] Latest Android Gradle Plugin version (17) — currently AGP 9.x
[ ] Latest stable Kotlin version (17) — currently 2.4.x
[ ] R8 guidance (15) — R8 is the default shrinker
[ ] Any new Play app-quality requirements (18) — e.g. memory footprint
[ ] Deprecations introduced by the newest API level (22)
```

Update the "verified" date in the README when you finish a pass.

---

## 5. Definition of done for a guideline change

```text
[ ] Authority + severity labels present and honest
[ ] Official claims link to official sources
[ ] Plain-words explanation for junior readers
[ ] At least one concrete example where useful
[ ] Cross-links updated (README TOC, related files, decision table 25)
[ ] examples/ updated if a pattern changed
```
