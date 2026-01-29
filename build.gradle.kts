plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.v2ray.devicekit"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.mmkv.static)
    implementation(libs.preference.ktx)
}
