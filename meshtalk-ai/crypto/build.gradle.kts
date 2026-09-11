plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.meshtalk.crypto"
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
    implementation(project(":core:database"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.biometric)
    // Tink provides audited implementations of X25519 (via Hybrid Encryption /
    // Ecies primitives), Ed25519 signatures, AES-256-GCM and ChaCha20-Poly1305 -
    // deliberately not hand-rolled. Its KeysetHandle integrates with Android
    // Keystore for hardware-backed key wrapping where available.
    implementation(libs.google.tink.android)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
