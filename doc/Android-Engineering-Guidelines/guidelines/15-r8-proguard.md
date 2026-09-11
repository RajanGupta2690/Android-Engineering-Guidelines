# 15 · R8 / ProGuard (Shrinking, Obfuscation, Release Builds)

[← 14 Security](14-security.md) · [README](../README.md) · **Next:**
#
# [16 · 16 KB Page-Size →](16-16kb-page-size.md)

> Authority: 🟢 **Official** — [Shrink, obfuscate, and optimize your app](https://developer.android.com/build/shrink-code). Severity 🔴 for release verification and no-blanket-keep rules.

---

## 1. What R8 does (plain words)

**R8** is the tool that processes your **release** build. It does four things:
- **Shrinking (tree shaking):** removes unused classes/methods/fields.
- **Optimization:** inlines and simplifies code, removes dead code.
- **Obfuscation:** renames symbols to short names (smaller + harder to reverse-engineer).
- **Resource shrinking** (with `shrinkResources`): removes unused resources.

Result: a smaller, faster, harder-to-reverse app. ProGuard is legacy — **R8 is the modern default** and uses the same `-keep` rule syntax.

---

## 2. Enable it for release — 🟢 Official · 🔴 Mandatory

```kotlin
android {
    buildTypes {
        release {
            isMinifyEnabled = true       // R8 shrink + optimize + obfuscate
            isShrinkResources = true     // remove unused resources
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
}
```

---

## 3. Why blanket keep rules are dangerous — 🟠 Strong Eng. · 🔴 Mandatory

### ❌ Avoid
```proguard
-keep class ** { *; }              # keeps EVERYTHING
-keep class com.company.** { *; }
```

**Why.** A blanket keep switches off shrinking/obfuscation for that scope — bigger app, no optimization, and your code stays readable to anyone reversing it. It's the lazy "fix" for a release crash that hides the real, narrow rule you actually needed.

**Rule:** every keep rule must be **as narrow as possible** and carry a **comment explaining why**.
```proguard
# Retrofit response models are created via reflection by the JSON converter.
-keep class com.company.app.data.dto.** { *; }
```

---

## 4. What commonly needs keeping — and why — 🟢 Official · 🟡 Recommended

**In plain words:** R8 renames/removes things it *thinks* are unused. Anything accessed "invisibly" (by name, at runtime) can break. Those are what you keep.

| Trigger                                | Why R8 breaks it                              | Handling                                                              |
|----------------------------------------|-----------------------------------------------|-----------------------------------------------------------------------|
| **Reflection**                         | R8 can't see reflective access                | Keep the reflected members                                            |
| **Serialization** (Gson/Moshi/kotlinx) | Field names read by reflection/generated code | Keep model classes/fields; kotlinx.serialization ships consumer rules |
| **JNI / native**                       | Native code calls Java by name                | Keep native-referenced methods/classes                                |
| **Retrofit / Room**                    | Ship **consumer rules** — usually no action   | Verify they're applied                                                |
| **Enums used by name / `valueOf`**     | Renamed by obfuscation                        | Keep `values`/`valueOf` if used reflectively                          |

Most good libraries ship **consumer rules**, so you add few rules yourself — only for *your own* reflection/serialization/JNI.

---

## 5. Debugging release-only crashes — 🟢 Official · 🔴 Mandatory flow

```text
Works in Debug ──► Fails in Release
                        │
                        ▼
                Check R8 / shrinking
                        │
     ┌──────────┬───────┼────────┬───────────────┐
     ▼          ▼       ▼        ▼               ▼
 Missing    Reflection Serial-  JNI /      Wrong/broad keep
 class      access     ization  native     rule or missing
 (kept?)                                    consumer rule
```

---

## 6. Mapping files & symbolication — 🟢 Official · 🔴 Mandatory

R8 renames symbols, so release stack traces are obfuscated. **Preserve and upload `mapping.txt`** for every release (to Play Console and/or your crash reporter) so crashes de-obfuscate ("symbolicate"). Lose the mapping file → unreadable production crashes.

---

## 7. Release verification checklist

```text
[ ] isMinifyEnabled = true and isShrinkResources = true on release
[ ] App fully exercised on a real RELEASE build (not debug)
[ ] All reflection/serialization/JNI paths tested in release
[ ] No blanket -keep class ** rules
[ ] Every custom keep rule has a comment explaining why
[ ] mapping.txt archived + uploaded to crash reporting / Play
[ ] Release crash reporting verified with a symbolicated test crash
```

---

**Next:**[16 · 16 KB Page-Size →](16-16kb-page-size.md)
#
#
#
#