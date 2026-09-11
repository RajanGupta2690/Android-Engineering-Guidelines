# 16 · 16 KB Page-Size Compatibility

[← 15 R8 / ProGuard](15-r8-proguard.md) · [README](../../../README.md) · **Next:** [17 · Gradle & Build →](17-gradle-build.md)

> Authority: 🟢 **Official** — [Support 16 KB page sizes](https://developer.android.com/guide/practices/page-sizes). Severity 🔴 **Mandatory** — this is a Google Play requirement.

---

## 1. What a memory page is (plain words)

The operating system manages RAM in fixed-size blocks called **pages**. Android historically used **4 KB** pages. Starting with **Android 15**, AOSP supports devices configured with **16 KB** pages, which improves performance (faster app launch under memory pressure, faster camera and boot). Newer high-RAM devices are moving to 16 KB.

---

## 2. The requirement — 🟢 Official · 🔴 Mandatory

- Apps **targeting Android 15 (API 35) or higher must support 16 KB page sizes on 64-bit devices** to comply with Google Play.
- **From February 1, 2027**, app updates that don't support 16 KB **cannot be released** on Google Play.
- Our target is **Android 16 (API 36)** ([README currency](../../../README.md#currency-verified-against-official-sources--september-2026)), so **this applies to us now.**

---

## 3. The critical point: a pure-Kotlin app can STILL fail — 🟢 Official · 🔴 Mandatory understanding

> **Your team can write zero lines of native code and still fail 16 KB compatibility — because a third-party SDK bundles native `.so` libraries.**

Your app "uses native code" if **any** of these is true:
- You use C/C++/NDK directly.
- You link **any third-party library/SDK** that ships native `.so` files (analytics, image/video, ML, maps, crash SDKs, payment SDKs, …).
- A third-party app builder injects native libraries.

If your app **and all its libraries** are only Kotlin/Java, you already support 16 KB — but still test it.

---

## 4. Key terms (plain words)

| Term              | Meaning                                                                       |
|-------------------|-------------------------------------------------------------------------------|
| **NDK**           | Android's toolkit for building C/C++ native code                              |
| **ABI**           | The CPU architecture a native lib is built for (e.g. `arm64-v8a`)             |
| **`.so` file**    | A compiled native shared library, packaged under `lib/<abi>/`                 |
| **ELF**           | The binary file format of `.so` files                                         |
| **ELF alignment** | Native libs must have segments aligned to 16 KB so they load on 16 KB devices |

---

## 5. How to find native libraries — 🟢 Official · 🔴 Mandatory

1. **APK Analyzer** (Android Studio → **Build → Analyze APK…**): open the `lib/` folder. Any `.so` files there = your app has native code (yours or a dependency's).
2. **Trace each `.so` to its dependency.** Use the Gradle dependency report or the SDK's docs to find *which library* bundled it — that's the SDK you must update.
3. **Check ELF alignment.** Android Studio flags alignment issues automatically; there are also official command-line checks for segment alignment.

---

## 6. How to become compatible — 🟢 Official · 🔴 Mandatory

- **Update third-party SDKs** to 16 KB-compatible versions (most major SDKs have shipped them).
- If you build native code, **rebuild with a recent NDK** that produces 16 KB-aligned libraries and set the correct packaging/alignment.
- **Update AGP** to a version that packages aligned native libs correctly (AGP 9.x — [17](17-gradle-build.md)).
- **Test in a 16 KB environment**: use the Android 15+ 16 KB emulator system image and verify launch + core flows.

---

## 7. Practical checklist

```text
[ ] Analyze the release APK/AAB for native libraries (APK Analyzer → lib/)
[ ] Identify every .so present
[ ] Map each .so to the dependency/SDK that introduced it
[ ] Verify ELF segment alignment (Studio / official CLI check)
[ ] Verify packaging (uncompressed, aligned native libs)
[ ] Update any incompatible SDKs to 16 KB-ready versions
[ ] Update NDK if we build native code
[ ] Update AGP if required
[ ] Test on a 16 KB emulator/device (Android 15+ image)
[ ] Verify on a RELEASE build (not just debug)
[ ] Confirm Play Console shows no 16 KB compatibility warning
```

---

## 8. Put it in CI — 🟠 Strong Eng. · 🟡 Recommended

Add a CI step that inspects the built artifact for `.so` files and fails if any are present without verified 16 KB alignment. This catches a newly added dependency that silently reintroduces incompatible native code.

**Why in CI:** a developer can add an SDK next month that pulls in a non-compliant `.so` without anyone noticing until Play rejects the release. CI catches it at the PR.

---

**Next:** [17 · Gradle & Build →](17-gradle-build.md)
#
#
#
#