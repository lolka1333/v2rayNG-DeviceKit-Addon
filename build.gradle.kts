plugins {
    id("com.android.library") version "9.0.0"
    id("org.jetbrains.kotlin.android") version "2.3.0"
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

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
}

dependencies {
    implementation("com.tencent:mmkv-static:1.3.16")
    implementation("androidx.preference:preference-ktx:1.2.1")
}
