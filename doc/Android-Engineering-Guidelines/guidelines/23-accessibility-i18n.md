# 23 · Accessibility & Internationalization

[← 22 Static Analysis](22-static-analysis-quality.md) · [README](../README.md) · **Next:** [24 · Code Smells →](24-code-smells.md)

> Authority: 🟢 **Official** — [Accessibility](https://developer.android.com/guide/topics/ui/accessibility), [Localization](https://developer.android.com/guide/topics/resources/localization). Accessibility and localization are part of UI quality, not extras.

---

# PART A — Accessibility

## A.1 Core requirements — 🟢 Official · 🔴 Mandatory

- **Content descriptions** for meaningful images/icons/controls (so TalkBack announces them).
- **Touch targets ≥ 48dp.**
- **Text scales** with the user's font size (use `sp`, don't cap scaling; test at large font).
- **Sufficient contrast** between text and background.
- **Meaningful focus order** and keyboard/D-pad navigability (matters for TV, Wear, switch access).

## A.2 Meaningful vs decorative images — 🟢 Official · 🔴 Mandatory

**In plain words:** describe images that carry meaning; silence images that are just decoration.

### ✅ Recommended (Compose)
```kotlin
Icon(Icons.Default.Search, contentDescription = "Search")   // meaningful → announced
Icon(Icons.Default.Star, contentDescription = null)         // decorative → skipped
```
### ✅ Recommended (Views)
```xml
<ImageView android:contentDescription="@string/cd_search" />     <!-- meaningful -->
<ImageView android:importantForAccessibility="no" />             <!-- decorative -->
```

**Why "avoid unnecessary content descriptions"?** Announcing decorative images clutters the TalkBack experience. Describe the *purpose* ("Search", not "magnifying glass"); skip the decoration.

## A.3 Semantics — 🟢 Official · 🟡 Recommended

Use semantic components and roles (buttons are buttons), merge/clear semantics in Compose where a group should be announced as one, and label form fields. Verify with **TalkBack** and the **Accessibility Scanner**.

## A.4 Honesty about compliance — 🟠 Strong Eng.

Automated checks catch *some* issues, but **full WCAG compliance requires manual testing with assistive technologies and expert review.** We do the automated checks *and* periodic manual TalkBack passes; we don't claim full compliance from tooling alone.

---

# PART B — Internationalization

## B.1 No hardcoded user-facing strings — 🟢 Official · 🔴 Mandatory

> Every user-facing string lives in `strings.xml` (or a locale-qualified variant), never inline in Kotlin or XML.

### ✅ Recommended
```kotlin
val title = context.getString(R.string.cart_title)
```
```xml
<TextView android:text="@string/cart_title" />
```
### ❌ Avoid
```kotlin
textView.text = "Your cart"   // can't be translated or localized
```

**Exception.** Non-user-facing constants (log tags, keys, analytics event names, `tools:` attributes) stay in code — they're not "strings for the user".

## B.2 Plurals & formatting — 🟢 Official · 🔴 Mandatory

- Use **`<plurals>`** for quantities, never string concatenation (`"$n items"` is wrong across languages).
- Use **positional placeholders** (`%1$s`, `%2$d`) so translators can reorder.
- Format **numbers, dates, times, currency** with locale-aware formatters, not manually.

### ✅ Recommended
```xml
<plurals name="cart_items_count">
    <item quantity="one">%1$d item</item>
    <item quantity="other">%1$d items</item>
</plurals>
```

**Why plurals, not `"$n items"`?** Languages have different plural rules (some have several forms). `<plurals>` picks the right form per language; string concatenation can't.

## B.3 RTL & bidirectional — 🟢 Official · 🔴 Mandatory

- Use **start/end** attributes (`paddingStart`, `layout_marginEnd`) instead of left/right, and set `android:supportsRtl="true"`.
- Test in an RTL locale (Arabic/Hebrew) — layouts must mirror correctly.

## B.4 Per-app language — 🟢 Official · 🟡 Recommended

Support the **per-app language** feature (`localeConfig` / AndroidX `AppCompatDelegate`) so users can set your app's language independently of the system.

---

**Next:** [24 · Code Smells →](24-code-smells.md)
#
#
#
#