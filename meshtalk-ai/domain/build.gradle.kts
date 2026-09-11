plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

// Modelled as an Android library (rather than a pure kotlin-jvm module) purely so it can
// depend on :core:common's coroutine/DispatcherProvider types without an AAR-vs-jar
// mismatch. The module still contains zero android.* framework imports in its own source -
// models, repository interfaces and use cases here are plain Kotlin and remain trivially
// unit-testable without Robolectric or an emulator.
android {
    namespace = "com.meshtalk.domain"
    compileSdk = 36
    defaultConfig { minSdk = 29 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    implementation(project(":core:common"))
    implementation(libs.kotlinx.coroutines.android)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
