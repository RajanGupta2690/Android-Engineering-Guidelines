# 26 · Exceptions & Exception Process

[← 25 Decision Tables](25-decision-tables.md) · [README](../../../README.md) · **Next:** [27 · Golden Rules & Checklists →](27-golden-rules-checklists.md)

> Authority: 🟣 **Team Standard**. Severity 🔴 — the *process* is mandatory even when a rule is being broken.

---

## 1. Every rule can have a legitimate exception

No guideline covers every situation. You **may** deviate from a rule when at least one is true:

- A **technical limitation** makes the rule impossible.
- A **platform requirement** forces it.
- **Performance measurements** (not guesses) justify it.
- A **third-party SDK** requires it.
- **Backward compatibility** (`minSdk`) requires it.
- **Security** or **accessibility** requires it.

---

## 2. A deviation must be intentional, documented, justified, reviewable — 🔴 Mandatory

The deviation must be:

1. **Intentional** — a conscious decision, not an accident.
2. **Documented** — a code comment (and ticket link) at the deviation, naming *which* rule and *why*.
3. **Technically justified** — a real reason from §1.
4. **Reviewable** — called out in the PR so a reviewer can agree.

> **"I prefer it this way" is never sufficient justification.**

### ✅ Recommended — a documented exception
```kotlin
// EXCEPTION (03 §5, no-!!): `binding` is guaranteed non-null between
// onCreateView and onDestroyView; cleared in onDestroyView. Ticket: APP-1234
private var _binding: FragmentProfileBinding? = null
private val binding get() = _binding!!
```

### ✅ Recommended — a documented size exception
```kotlin
// EXCEPTION (03 §11, class-size): this mapper is a single responsibility
// (exhaustive when over 40 sealed API variants). Splitting would scatter one
// concept across files. Ticket: APP-2001
class ApiEventMapper { /* long but cohesive */ }
```

---

## 3. This mirrors suppression policy

Suppressing a lint/detekt rule ([22 §A.3](22-static-analysis-quality.md)) is itself an exception and follows the same four requirements: **narrow, commented, justified, reviewed.**

---

## 4. How to propose changing a rule itself

If a rule is wrong or outdated (not just inconvenient for one PR), don't quietly ignore it — **change the rule** via [CONTRIBUTING.md](../CONTRIBUTING.md). That keeps the guideline honest and the team aligned.

---

**Next:** [27 · Golden Rules & Checklists →](27-golden-rules-checklists.md)
#
#
#
#