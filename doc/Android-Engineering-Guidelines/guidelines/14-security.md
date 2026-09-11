# 14 · Security

[← 13 Error Handling & Logging](13-error-handling-logging.md) · [README](../../../README.md) · **Next:** [15 · R8 / ProGuard →](15-r8-proguard.md)

> Authority: 🟢 **Official** — [Security best practices](https://developer.android.com/privacy-and-security/security-tips), [Network security config](https://developer.android.com/privacy-and-security/security-config). Severity 🔴 for transport, secrets, and exported-component rules.

---

## 1. What is actually secret — 🟠 Strong Eng. · 🔴 Mandatory understanding

**In plain words.** Anything shipped inside your APK/AAB is **not secret** — APKs can be decompiled easily. A "hidden" API key in code or `BuildConfig` can be extracted by anyone.

- **Not truly secret:** anything in the app binary (client IDs, keys that are safe to be public by design).
- **Truly sensitive:** user credentials, auth tokens, private keys — anything granting access to user data or money. Keep these **server-side**; never hardcode them.

**Why this matters.** Developers often "hide" a secret in the app and assume it's safe. It isn't. If leaking a value would be harmful, it must not ship in the app.

---

## 2. Secure storage — 🟢 Official · 🔴 Mandatory

- Store tokens/credentials with **Android Keystore**-backed encryption; not in plain `SharedPreferences`/DataStore/files.
- Use hardware-backed keys where available; **never roll your own crypto.**

---

## 3. Network security — 🟢 Official · 🔴 Mandatory

- **HTTPS everywhere.** Use a **Network Security Configuration** to disallow cleartext (`cleartextTrafficPermitted="false"`).
- **Certificate pinning:** only when justified — see the trade-off.

### Certificate pinning trade-off (🟠)

|                     | Pros                             | Cons                                                                                                                       |
|---------------------|----------------------------------|----------------------------------------------------------------------------------------------------------------------------|
| Certificate pinning | Defends against rogue CAs / MITM | **High operational risk:** a cert rotation without an app update can **brick the app**; needs backup pins + a rollout plan |

**Rule.** Pin only high-value apps (finance, health) with a disciplined cert-rotation process and backup pins. For most apps, a correct Network Security Config + HTTPS is enough; blind pinning causes more outages than it prevents attacks.

---

## 4. Components, intents, permissions — 🟢 Official · 🔴 Mandatory

- **`exported`:** set `android:exported` explicitly on every Activity/Service/Receiver; export only what must be public, and guard exported components with permissions.
- **Prefer explicit intents** for internal navigation; treat data from implicit intents as **untrusted input** and validate it.
- **`PendingIntent`:** use `FLAG_IMMUTABLE` (use `FLAG_MUTABLE` only when genuinely required).
- **Permissions:** request the minimum, at runtime, with a rationale; prefer scoped APIs (photo picker) over broad storage access.

**In plain words about "exported":** an exported component can be launched by *other apps*. If you don't need that, mark it `false` so other apps can't poke your screens/services.

---

## 5. WebView, backups, screenshots — 🟢 Official · 🔴 where noted

- **WebView:** disable JavaScript unless required; never expose `@JavascriptInterface` to untrusted content; validate URLs.
- **Backups:** review `allowBackup` and backup rules so tokens/sensitive files aren't backed up to the cloud (🔴 for sensitive data).
- **Sensitive screens:** apply `FLAG_SECURE` to block screenshots/screen recording where sensitive data is shown.

---

## 6. Root / tamper — 🟠 Strong Eng. · 🟡 Recommended

For high-value apps, use the **Play Integrity API** rather than brittle homemade root checks. It's risk-reduction, not a guarantee — never treat the client as trusted.

---

**Next:** [15 · R8 / ProGuard →](15-r8-proguard.md)
#
#
#
#