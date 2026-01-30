# v2rayNG-DeviceKit-Addon

Android library module (`com.v2ray.devicekit`)

## Features

- DeviceKit settings UI (HWID/User-Agent overrides) provided via AndroidX Preferences resources.
- "Happ" links decryption/expansion helpers.

## Used by

- Custom v2rayNG fork (this addon is integrated here): https://github.com/lolka1333/v2rayNG

## What this repo is

This repository is an **Android library module** intended to be added into the upstream `v2rayNG` project as:

- a **git submodule** under `V2rayNG/devicekit`
- a **Gradle included module** (`include(":devicekit")`)

It does **not** replace the app: you still need a few small edits in `v2rayNG` to:

- show DeviceKit settings screen
- apply HWID / User-Agent headers to subscription requests
- support importing / storing `happ://crypt4/...` subscription URLs

## Integration into v2rayNG (as a submodule / included Gradle module)

This repo is intended to be used as a git submodule inside the v2rayNG repo.

### 1) Add as a git submodule

From the root of your `v2rayNG` repository:

```bash
git submodule add https://github.com/lolka1333/v2rayNG-DeviceKit-Addon.git V2rayNG/devicekit
git submodule update --init --recursive
```

To update later:

```bash
git submodule update --remote --merge V2rayNG/devicekit
```

Example layout:

- `v2rayNG/V2rayNG/devicekit` (submodule)

### 2) Include Gradle module

In `v2rayNG/V2rayNG/settings.gradle.kts`:

```kotlin
include(":devicekit")
```

### 3) Add dependency from app

In `v2rayNG/V2rayNG/app/build.gradle.kts`:

```kotlin
dependencies {
    implementation(project(":devicekit"))
}
```

### 4) Install DeviceKit preferences into Settings screen

File:

- `v2rayNG/V2rayNG/app/src/main/java/com/v2ray/ang/ui/SettingsActivity.kt`

In `SettingsActivity.SettingsFragment.onCreatePreferences(...)` after your main preferences are loaded:

1) Import:

```kotlin
import com.v2ray.devicekit.SettingsUi
```

2) Call after `addPreferencesFromResource(...)` and after your summary initialization:

```kotlin
addPreferencesFromResource(R.xml.pref_settings)

// Install DeviceKit preferences (adds pref_devicekit.xml + binds logic)
SettingsUi.install(this)
```

`SettingsUi.install()` appends `pref_devicekit.xml` to the existing `PreferenceScreen`, installs summary providers (so values are visible without clicking) and then calls `UiBinder.bind()`.

## v2rayNG code changes (required)

This library is not only UI. For full functionality you need to apply it in the places below.

If you already have some of these edits — keep them, just compare with this list.

### HttpUtil.kt

File:

- `v2rayNG/V2rayNG/app/src/main/java/com/v2ray/ang/util/HttpUtil.kt`

1) Add imports:

```kotlin
import com.v2ray.devicekit.Compat
import com.v2ray.devicekit.Kit
```

2) Decrypt Happ-style subscription URLs before opening connection:

```kotlin
val effectiveUrl = Compat.decryptSubscriptionUrl(currentUrl) ?: currentUrl
val conn = createProxyConnection(effectiveUrl, httpPort, timeout, timeout) ?: continue
```

3) Apply DeviceKit headers from settings to the request:

```kotlin
Kit.applyToConnectionFromSettings(
    conn = conn,
    context = com.v2ray.ang.AngApplication.application,
    subscriptionUserAgent = userAgent,
    defaultUserAgent = "v2rayNG/${BuildConfig.VERSION_NAME}",
    appVersionName = BuildConfig.VERSION_NAME,
)
```

This ensures:

- `happ://crypt4/...` subscription links work in background updates (they are decrypted before request)
- HWID / custom UA are applied to `HttpURLConnection` according to DeviceKit settings

## Subscription URLs: manual add/edit (SubEditActivity)

File:

- `v2rayNG/V2rayNG/app/src/main/java/com/v2ray/ang/ui/SubEditActivity.kt`

Goal:

- Allow user to paste `happ://crypt4/...` into the UI
- Validate **decrypted** URL
- Save **decrypted** URL into MMKV (so it displays as plain `https://...` and works everywhere)

Required change inside `saveServer()` (after reading `subItem.url`):

```kotlin
val validateUrl = Compat.decryptSubscriptionUrl(subItem.url) ?: subItem.url
subItem.url = validateUrl

if (!Utils.isValidUrl(validateUrl)) {
    toast(R.string.toast_invalid_url)
    return false
}

if (!Utils.isValidSubUrl(validateUrl)) {
    toast(R.string.toast_insecure_url_protocol)
    if (!subItem.allowInsecureUrl) {
        return false
    }
}
```

## Subscription URLs: clipboard / batch import (AngConfigManager)

File:

- `v2rayNG/V2rayNG/app/src/main/java/com/v2ray/ang/handler/AngConfigManager.kt`

### A) Parse subscription lines (detect happ:// as subscription)

In `parseBatchSubscription(...)`, when iterating lines:

```kotlin
val decrypted = Compat.decryptSubscriptionUrl(str)
if (Utils.isValidSubUrl(decrypted)) {
    count += importUrlAsSubscription(str)
}
```

This makes the importer treat `happ://crypt4/...` as a subscription URL.

### B) Store decrypted URL (so subscription is not kept encrypted)

In `importUrlAsSubscription(url: String)` keep it small:

```kotlin
val decryptedUrl = Compat.decryptSubscriptionUrl(url) ?: url

val subscriptions = MmkvManager.decodeSubscriptions()
subscriptions.forEach {
    if (it.subscription.url == url || it.subscription.url == decryptedUrl) {
        return 0
    }
}

val uri = URI(Utils.fixIllegalUrl(decryptedUrl))
val subItem = SubscriptionItem()
subItem.remarks = uri.fragment ?: "import sub"
subItem.url = decryptedUrl
MmkvManager.encodeSubscription("", subItem)
return 1
```

Result:

- you can paste `happ://crypt4/...` into clipboard import
- it will be saved as plain `https://...`
- repeated imports won’t create duplicates

## Notes about DeviceKit version used by v2rayNG

When you add this repo as a **submodule**, the v2rayNG project will use whatever commit the submodule points to.

To update DeviceKit inside v2rayNG later:

```bash
git submodule update --remote --merge V2rayNG/devicekit
```

Don’t forget to commit the submodule pointer in your v2rayNG repo.

### Other integration points

- If you accept multiline subscriptions/links in text fields, use:

```kotlin
val expanded = Compat.expandHappLinksInText(text)
```

## Troubleshooting

### Build fails in :devicekit (missing libs aliases)

This module uses `libs.versions.toml` aliases:

- `libs.mmkv.static`
- `libs.preference.ktx`

So your v2rayNG project must have these aliases in its `gradle/libs.versions.toml`.
If they don’t exist, either add them to the catalog or replace devicekit dependencies with explicit coordinates.

## API usage

### Decrypt subscription URL (single URL)

```kotlin
val url = Compat.decryptSubscriptionUrl(rawUrl)
```

### Expand Happ links in multi-line text

```kotlin
val expanded = Compat.expandHappLinksInText(text)
```

## Requirements

- `minSdk = 24`
- Uses AndroidX Preference (`androidx.preference:preference-ktx`) and MMKV.
