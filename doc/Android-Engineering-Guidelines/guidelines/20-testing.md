# 20 · Testing

[← 19 Background Work](19-background-work.md) · [README](../README.md) · **Next:** [21 · Git & Code Review →](21-git-code-review.md)

> Authority: 🟢 **Official** — [Test apps on Android](https://developer.android.com/training/testing) + 🟠 engineering. We don't mandate a test on *every* change, but logic changes should be tested, and CI quality gates ([22](22-static-analysis-quality.md)) are 🔴.

---

## 1. The test pyramid (plain words) — 🟢 Official · 🟡 Recommended

**In plain words:** write **many** small fast tests, **some** medium ones, and **few** slow end-to-end ones.

```text
        ▲  fewer, slower, higher-fidelity
        │        ┌───────────────┐
        │        │  UI / E2E tests│   Espresso / Compose UI test
        │      ┌─┴───────────────┴─┐
        │      │  Integration tests │  Repository+DB, DAO (Robolectric/instrumented)
        │   ┌──┴────────────────────┴──┐
        │   │        Unit tests          │  ViewModel, UseCase, mappers, pure logic
        ▼   └───────────────────────────┘  many, fast, JVM-only
```

Most tests should be **fast JVM unit tests** on logic you own (ViewModels, UseCases, mappers, repositories with fakes).

---

## 2. Mock vs fake — 🟠 Strong Eng. · 🟡 Recommended

**In plain words:**
- A **mock** is a stand-in you program to return canned answers and verify calls on.
- A **fake** is a lightweight *working* implementation (e.g. an in-memory repository).

| Situation                                     | Prefer                           | Why                                                      |
|-----------------------------------------------|----------------------------------|----------------------------------------------------------|
| Your own interfaces (repository, data source) | **Fake**                         | Behaves realistically; survives refactors; reads clearly |
| A single external call boundary               | **Mock** (MockK)                 | Fine for verifying one interaction                       |
| Room DAO                                      | **In-memory Room** DB            | Real SQL, real behavior                                  |
| Retrofit API                                  | **MockWebServer**                | Exercises real serialization + error paths               |
| Time / dispatchers                            | **Test dispatcher / fake clock** | Deterministic                                            |

**Why prefer fakes over heavy mocking?** Over-mocking tests your *assumptions about* a class, not the class. Mock-heavy tests break on every refactor and can pass while production breaks. Realistic fakes test behavior.

---

## 3. Naming & structure — 🟣 Team Standard · 🟡 Recommended

- Structure with **Arrange–Act–Assert** (Given–When–Then).
- Name tests as behavior sentences (backticks).
- Test **edge cases**: empty, null, error, boundary, cancellation, and (for stateful logic) config change / process death.

### ✅ Recommended
```kotlin
@Test
fun `uiState becomes Error when repository fails`() = runTest {
    // Arrange
    val repo = FakeUserRepository(failWith = AppError.NoConnection)
    val vm = ProfileViewModel(repo)

    // Act
    vm.load()

    // Assert
    assertEquals(AppError.NoConnection, (vm.uiState.value as Error).error)
}
```

---

## 4. What NOT to test — 🟡 Recommended

Don't test the framework, generated code, or trivial getters. Don't write brittle tests glued to implementation detail. Test **behavior and contracts.**

---

**Next:** [21 · Git & Code Review →](21-git-code-review.md)
#
#
#
#