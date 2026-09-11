# 08 · XML Standards, Resources & Images

[← 07 UI & Compose](07-ui-compose.md) · [README](../../../README.md) · **Next:** [09 · Lifecycle →](09-lifecycle.md)

> Rules for XML layouts (View-based UI), all resource files, and — importantly — **how to choose and manage images** so the app stays small and fast.
>
> Authority: 🟢 **Official** — [App resources](https://developer.android.com/guide/topics/resources/providing-resources), [Vector drawables](https://developer.android.com/develop/ui/views/graphics/vector-drawable-resources), [Reduce app size](https://developer.android.com/topic/performance/reduce-apk-size); 🟣 team naming.

---

# PART A — XML Layout Standards

## A.1 Layout & ID naming — 🟣 Team Standard · 🔴 Mandatory

- **Layout files:** `snake_case`, prefixed by type: `activity_login.xml`, `fragment_profile.xml`, `item_order.xml`, `view_rating.xml`, `dialog_confirm.xml`.
- **IDs:** `lowerCamelCase`, meaningful: `emailInput`, `submitButton`, `ordersRecyclerView`. Be consistent across the project.

| Element         | ✅ Correct            | ❌ Incorrect                      | Why                                          |
|-----------------|----------------------|----------------------------------|----------------------------------------------|
| Activity layout | `activity_login.xml` | `LoginActivity.xml`, `login.xml` | Type prefix groups files, matches convention |
| List item       | `item_order.xml`     | `order_row.xml`, `cell.xml`      | Predictable prefix                           |
| View id         | `submitButton`       | `btn1`, `button_submit_final`    | Meaningful, consistent case                  |

## A.2 No hardcoded user-facing values — 🟢 Official (i18n + resources) · 🔴 Mandatory

### ✅ Recommended
```xml
<TextView
    android:text="@string/login_title"
    android:textAppearance="?attr/textAppearanceHeadlineSmall"
    android:padding="@dimen/spacing_md" />
```
### ❌ Avoid
```xml
<TextView android:text="Login" android:textSize="18sp" android:padding="16dp" />
```

**Why.** Hardcoded strings can't be translated ([23 · i18n](23-accessibility-i18n.md)); hardcoded sizes/colors can't be themed and break dark mode + dynamic text size.

**Exception.** Truly non-user-facing values (`0dp` in a ConstraintLayout, `tools:text` for preview) are fine.

## A.3 Dimensions, colors, styles, themes — 🟢 Official · 🟡 Recommended

- Centralize spacing in `dimens.xml` (`spacing_xs/sm/md/lg`), colors in `colors.xml`, and use theme attributes (`?attr/colorPrimary`) instead of raw colors so dark mode + theming work.
- Extract repeated attribute sets into **styles**; put app-wide look into **themes** (Material 3 / `Theme.Material3.*`).

## A.4 Flat hierarchies — 🟠 Strong Eng. · 🟡 Recommended

Prefer a flat `ConstraintLayout` over deeply nested `LinearLayout`s. Deep nesting costs extra measure/layout passes and causes **jank** (stutter). See [18 · Performance](18-performance.md).

**In plain words:** every level of nesting makes Android do more work to lay out the screen. Fewer levels = smoother scrolling.

## A.5 Drawables in XML — 🟢 Official · 🟡 Recommended

Use `<selector>` for state lists (pressed/normal), `<shape>` for simple backgrounds/dividers, and **vector drawables** for icons (Part C). Prefer theme attributes for `tint`.

## A.6 Accessibility & units — 🟢 Official · 🔴 Mandatory

- Provide `contentDescription` for meaningful images; mark decorative ones `android:importantForAccessibility="no"`.
- Touch targets ≥ 48dp. Use `dp` for sizes, `sp` for text — **never `px`**. Full rules in [23 · Accessibility](23-accessibility-i18n.md).

## A.7 XML code smells — 🟠 Strong Eng. · 🟡 Recommended

Watch for: giant single-layout files, deep nesting, duplicated attribute blocks (extract a style), hardcoded strings/dimens/colors, and `px` units.

---

# PART B — Resource Naming & Management

## B.1 Naming by resource type — 🟣 Team Standard · 🔴 Mandatory

| Resource                | Folder                    | Convention                          | Example                             |
|-------------------------|---------------------------|-------------------------------------|-------------------------------------|
| Layout                  | `res/layout/`             | `type_feature`                      | `fragment_cart.xml`                 |
| Drawable (vector/shape) | `res/drawable/`           | `ic_`, `bg_`, `shape_`, `selector_` | `ic_search_24.xml`, `bg_card.xml`   |
| Launcher icon           | `res/mipmap-*/`           | `ic_launcher*`                      | `ic_launcher`                       |
| Color                   | `res/values/colors.xml`   | semantic                            | `brand_primary`, `text_secondary`   |
| Dimension               | `res/values/dimens.xml`   | scale/semantic                      | `spacing_md`, `text_body`           |
| String                  | `res/values/strings.xml`  | `feature_purpose`                   | `login_title`, `cart_empty_message` |
| Plural                  | `strings.xml` `<plurals>` | `feature_count`                     | `cart_items_count`                  |
| Style                   | `res/values/styles.xml`   | `Type.Variant`                      | `Widget.App.Button.Primary`         |
| Theme                   | `res/values/themes.xml`   | `Theme.App*`                        | `Theme.App`, `Theme.App.Dark`       |
| Font                    | `res/font/`               | family_weight                       | `inter_medium`                      |
| Raw                     | `res/raw/`                | snake_case                          | `onboarding_intro.json`             |
| Navigation              | `res/navigation/`         | `nav_feature`                       | `nav_main.xml`                      |

**Icon size suffix (🟣):** append dp size to icon drawables (`ic_close_24`) so the size is visible at the call site.

## B.2 Why resource qualifiers exist — 🟢 Official · 🔴 Mandatory (use them, don't fork logic)

**In plain words:** qualified folders let Android automatically pick the right resource for the device — no `if` statements in code.

| Qualifier                                            | Purpose                                 |
|------------------------------------------------------|-----------------------------------------|
| `values-night/`                                      | Dark theme values                       |
| `layout-land/`, `layout-sw600dp/`                    | Orientation / large-screen layouts      |
| `drawable-hdpi/…xxxhdpi/`                            | Density-specific raster images          |
| `drawable-nodpi/`                                    | Density-independent raster (no scaling) |
| `values-<locale>/` (e.g. `values-fr/`, `values-ar/`) | Translations                            |

### ✅ Recommended
Put dark colors in `values-night/colors.xml`; the system swaps them automatically on dark mode.
### ❌ Avoid
Branching on `Configuration.uiMode` in Kotlin to choose colors.

**Why.** Qualifiers are declarative, tested by the platform, and cover configurations you'd otherwise forget. Code branches duplicate that logic and rot.

## B.3 Remove unused resources — 🟠 Strong Eng. · 🟡 Recommended

Delete unused resources and enable resource shrinking for release ([15 · R8](15-r8-proguard.md)). Unused resources bloat the app and slow builds.

---

# PART C — Icons & Images (read this carefully)

> This is the most misunderstood area. The rules below tell you **exactly what to use and when**, so app size and memory stay low.

## C.1 The golden order — decide by **asset type first**, not by file format — 🟠 Strong Eng. · 🔴 Mandatory

```text
What TYPE of asset is it?
        │
        ├── Icon / logo / simple shape / line art / monochrome symbol
        │        └──►  Use a VECTOR (SVG imported as VectorDrawable .xml)   ← DEFAULT
        │
        ├── Banner / ad creative / photo / complex artwork
        │   (things a vector CANNOT represent)
        │        └──►  See C.3 — prefer REMOTE/API, and minimize
        │
        └── Small, essential, stable raster that must ship in the app
                 └──►  Use ONE optimized raster (WebP preferred) — as few as possible
```

## C.2 Vectors (SVG) are the default for icons — 🟢 Official · 🔴 Mandatory

**Rule:** for icons, logos, simple illustrations, and UI symbols, you **must** use a **vector drawable** (import your SVG into Android Studio → it becomes a `VectorDrawable` `.xml`). Do not ship PNG icons across `mdpi…xxxhdpi` folders.

### ✅ Recommended
```xml
<!-- res/drawable/ic_search_24.xml — one file, crisp at every screen density -->
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp"
    android:viewportWidth="24" android:viewportHeight="24"
    android:tint="?attr/colorControlNormal">
    <path android:fillColor="@android:color/white" android:pathData="M..." />
</vector>
```

**Why vectors for icons.** One vector file renders sharply on every screen density, replacing five PNG copies (`mdpi`, `hdpi`, `xhdpi`, `xxhdpi`, `xxxhdpi`). That means **smaller app size**, no blurry icons, and easy tinting via theme colors.

**Limit / exception.** Vectors aren't free at runtime — very complex paths cost CPU to draw. Keep vectors simple. A highly detailed illustration should be simplified, or treated as a raster/remote asset.

## C.3 Banners, ads, photos — you can't make these in SVG — 🟠 Strong Eng. · 🔴 Mandatory

**The reality:** a **banner**, an **ad creative**, or a **photograph** cannot be drawn as an SVG/vector — they're pixel images. For these, follow this priority, in order:

1. **Prefer REMOTE / API / CDN.** Don't bundle banners and ad images inside the app. Load them at runtime from your server/CDN using an image library with caching (e.g. Coil/Glide).
   - **Especially do this when:** there are **many** images, they are **banners/ads**, they are **large in size**, or they **change often**.
   - **Why:** bundled images increase the app download size for every user, forever. Remote images keep the app small, can be updated without a new release, and only download when needed.

2. **If it must ship in the app, minimize aggressively.**
   - Use **as few raster images as possible.** Every raster you avoid keeps the app smaller.
   - Prefer **WebP** (Android Studio can convert PNG/JPEG → WebP); it's usually smaller at similar quality. JPEG for photos without transparency; PNG only when you need lossless + transparency or a tiny simple graphic.
   - **Right-size the file** to the largest size it's actually displayed at — never ship a 4000px image to show it at 200px.

### ✅ Recommended — load a banner from the API, don't bundle it
```kotlin
// Banner comes from the server; the app ships zero banner bytes.
bannerImageView.load(banner.imageUrl) {
    size(ViewSizeResolver(bannerImageView))   // decode at display size
    crossfade(true)
}
```
### ❌ Avoid
```text
res/drawable-xxxhdpi/home_banner_1.png   (2.1 MB)
res/drawable-xxxhdpi/home_banner_2.png   (1.8 MB)   ← bundled banners bloat every install
res/drawable-xxxhdpi/promo_ad_july.png   (2.4 MB)
```

## C.4 App size — the summary rule — 🟠 Strong Eng. · 🔴 Mandatory

> **Icons → SVG/vector. Banners/ads/photos → remote/API. Bundled raster → as few as possible, WebP-first, right-sized.**
> The fewer WebP/JPEG/PNG files you ship, the smaller and faster the app. When in doubt, don't bundle it — serve it.

**Honesty note (don't over-claim):** don't write fixed savings like "WebP always saves 30–50%." Real savings depend on the image content, encoder, quality, dimensions, and transparency. **Measure** the actual output in APK Analyzer and keep whatever is genuinely smaller.

## C.5 Right-size & decode efficiently — 🟢 Official (memory) · 🔴 Mandatory

A 4000×3000 photo shown in a 200×200 view wastes memory and can cause an **OutOfMemory** crash. Always request/resize to display dimensions (an image library does this for you).

**Why.** A decoded bitmap uses `width × height × 4 bytes` of RAM regardless of the file size on disk. Loading oversized images into small views is the #1 cause of image-related crashes and jank.

## C.6 Launcher icons & densities — 🟢 Official · 🟡 Recommended

Use **adaptive icons** (foreground + background layers) in `mipmap-*`. Keep launcher icons in `mipmap` (not `drawable`) so they survive density stripping. Use `-nodpi` for density-independent raster.

---

## Quick decision table — which image approach?

| Asset                               | Use                | Bundle or remote?    | Why                                   |
|-------------------------------------|--------------------|----------------------|---------------------------------------|
| App icon, tab icons, symbols        | Vector (SVG→XML)   | Bundle               | Scales to all densities, tiny         |
| Logo (simple)                       | Vector             | Bundle               | Crisp, small                          |
| Home banner / promo / ad            | Raster (WebP/JPEG) | **Remote/API**       | Large, changes often, keeps app small |
| Product photos (many)               | Raster             | **Remote/API**       | Many + large → don't bloat the app    |
| One small essential illustration    | WebP               | Bundle (right-sized) | Needed offline, small                 |
| Photo with no transparency          | JPEG or WebP       | Remote if large      | Good compression                      |
| Simple graphic needing transparency | WebP or PNG        | Bundle if tiny       | Lossless + alpha                      |

---

**Next:** [09 · Lifecycle →](09-lifecycle.md)
#
#
#
#