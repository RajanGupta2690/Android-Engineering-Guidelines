# Examples

Reference code for the guidelines. Read the guideline first, then the matching example.

- [`good/`](good) — implementations that follow the rules. Copy these patterns.
- [`bad/`](bad) — anti-patterns. Each file's header comment explains **exactly what's wrong** and links to the rule.

> These files are illustrative snippets (some helper types are omitted for brevity). They demonstrate patterns, not a compilable module.

| Topic                 | Good                                                   | Bad                                                          | Guideline                                                                                        |
|-----------------------|--------------------------------------------------------|--------------------------------------------------------------|--------------------------------------------------------------------------------------------------|
| ViewModel & UI state  | [`good/ProfileViewModel.kt`](good/ProfileViewModel.kt) | [`bad/ProfileViewModel.kt`](bad/ProfileViewModel.kt)         | [06](../guidelines/06-android-architecture.md), [05](../guidelines/05-coroutines-concurrency.md) |
| Repository & mapping  | [`good/UserRepository.kt`](good/UserRepository.kt)     | [`bad/UserRepository.kt`](bad/UserRepository.kt)             | [06](../guidelines/06-android-architecture.md), [10](../guidelines/10-networking.md)             |
| Splitting a god class | [`good/CheckoutSplit.kt`](good/CheckoutSplit.kt)       | [`bad/CheckoutGodViewModel.kt`](bad/CheckoutGodViewModel.kt) | [03 §11–§12](../guidelines/03-kotlin.md)                                                         |
