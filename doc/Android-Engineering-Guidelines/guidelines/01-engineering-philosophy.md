# 01 · Engineering Philosophy, Purpose & Authority

[← Back to README](../../../README.md) · **Next:
** [02 · Project Architecture →](02-project-architecture.md)

> This file sets the ground rules for *how to read and apply* everything else: what this rulebook is
> for, how we label rules, and the one principle the whole document serves.

---

## 1. Purpose & scope

### 1.1 What this is

The engineering standard for every Android developer on the team. It defines the coding,
architecture, UI, resource, performance, security, build, testing, and release rules for our Android
codebase, and it explains the *reason* behind each so you can apply it with judgement instead of
memorizing.

### 1.2 What this is **not**

- Not a generic coding-style article.
- Not a replacement for official Android/Kotlin docs — it curates and applies them to us.
- Not a place where platform requirements are invented. Every 🟢 platform/Play requirement is
  traceable to an official source (see [References](27-golden-rules-checklists.md#references)).

### 1.3 Who must follow it — 🟣 Team Standard · 🔴 Mandatory

Everyone contributing Kotlin/Android code, XML, Gradle, or release config. Reviewers enforce it.
Deviations follow the [Exception Process](26-exceptions-process.md).

---

## 2. Authority & rule classification

### 2.1 Why we classify — 🟣 Team Standard

The most common failure of guidelines is blurring *"the platform requires this"* with *"our team
prefers this."* When those blur, people either treat team preferences as laws or ignore real
platform requirements as opinions. So every rule gets an **authority** label and a **severity**
label.

See the [README → How to read every rule](../../../README.md#how-to-read-every-rule) for the label tables.

### 2.2 Authority is not severity

They're independent. Examples:

| Rule                                       | Authority          | Severity       |
|--------------------------------------------|--------------------|----------------|
| Support 16 KB page sizes (target API 35+)  | 🟢 Official        | 🔴 Mandatory   |
| Prefer `val` over `var`                    | 🔵 Kotlin          | 🟡 Recommended |
| All user-facing strings in `strings.xml`   | 🟢 Official (i18n) | 🔴 Mandatory   |
| Add a UseCase only when it earns its place | 🟣 Team            | 🟡 Recommended |
| No blanket `-keep class ** { *; }`         | 🟠 Strong Eng.     | 🔴 Mandatory   |

### 2.3 When Google's advice and our choice differ — 🟣 Team Standard

Where our preferred implementation diverges from official guidance, the relevant file states, **at
that rule**: (1) what Google recommends, (2) what we recommend, (3) why, (4)
advantages/disadvantages, (5) when Google's approach should still win. You'll see this
in [UI (Compose vs Views)](07-ui-compose.md), [DI](12-dependency-injection.md), [Singleton](12-dependency-injection.md#singleton-policy),
and [Security (cert pinning)](14-security.md).

---

## 3. The engineering philosophy

### 3.1 The one principle

> **Production-grade engineering without unnecessary over-engineering.**

We optimize for code that is simple, readable, predictable, testable, maintainable, scalable,
performant, secure, easy to debug, easy to review, and compatible with modern Android requirements.

### 3.2 Every abstraction must have a reason — 🟠 Strong Eng. · 🔴 Mandatory

Before you add an interface, a `Manager`/`Helper`, a UseCase, a Repository, a module, a dependency,
a design pattern, or a new concurrency mechanism, answer three questions:

- **What problem does it solve** that the simpler option doesn't?
- **What complexity does it add** (indirection, files, cognitive load, build time)?
- **When would it be unnecessary?**

If the honest answer is "it makes the project look enterprise," delete it.

```text
Do I need this abstraction?
        │
        ├── Removes real, present duplication or coupling?  ── No ──┐
        ├── Enables testing that's otherwise impossible?    ── No ──┤
        ├── Is there a real second implementation today
        │   (or a certain one this quarter)?                ── No ──┤
        │                                                            ▼
        └── Yes to at least one ──► Add it, with a comment      Do NOT add it.
              saying why.                                       Use the concrete type.
```

**Why.** Indirection added "just in case" is a permanent tax on every future reader, paid for
flexibility you may never use. Concrete, direct code is easier to read, delete, and change. YAGNI ("
You Aren't Gonna Need It") is the default; abstraction is the justified exception.

**Exception.** Public library/module boundaries and DI seams need interfaces earlier — there the "
second implementation" is the test double or a consumer you don't control.

### 3.3 Prefer the simplest production-appropriate solution

- A bug fix fixes the bug; it does not "clean up the surrounding file."
- A simple feature doesn't need configurability nobody asked for.
- A large feature *may* justify reshaping existing design to stay cohesive — that's engineering, not
  over-engineering.

### 3.4 Readability over cleverness — 🟠 Strong Eng. · 🟡 Recommended

Write for the next developer, who has less context than you have right now. A clever one-liner that
needs a five-minute stare is a defect, not a flourish.

### 3.5 What "done" means here

A change is done when it: solves the asked problem, builds, passes tests and static analysis,
follows naming/size rules, has no dead/commented-out code, and — if it deviates from any rule —
carries a documented exception. See the [checklists](27-golden-rules-checklists.md).

---

**Next:** [02 · Project Architecture →](02-project-architecture.md)

#

#

#

#