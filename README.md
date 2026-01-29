# v2rayNG-DeviceKit-Addon

Android library module (`com.v2ray.devicekit`) used by v2rayNG.

## Features

- DeviceKit settings UI (HWID/User-Agent overrides) provided via AndroidX Preferences resources.
- "Happ" links decryption/expansion helpers.

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

In `V2rayNG/settings.gradle.kts`:

```kotlin
include(":devicekit")
```

### 3) Add dependency from app

In `V2rayNG/app/build.gradle.kts`:

```kotlin
dependencies {
    implementation(project(":devicekit"))
}
```

### 4) Install DeviceKit preferences into Settings screen

In `SettingsActivity.SettingsFragment.onCreatePreferences(...)` after your main preferences are loaded:

```kotlin
addPreferencesFromResource(R.xml.pref_settings)

// Install DeviceKit preferences (adds pref_devicekit.xml + binds logic)
SettingsUi.install(this)
```

`SettingsUi.install()` appends `pref_devicekit.xml` to the existing `PreferenceScreen`, installs summary providers (so values are visible without clicking) and then calls `UiBinder.bind()`.

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
