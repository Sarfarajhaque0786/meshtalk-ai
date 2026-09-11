plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.meshtalk.core.database"
    compileSdk = 36
    defaultConfig {
        minSdk = 29
        // Room schema exports let us diff migrations in code review instead of trusting memory.
        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
            arg("room.generateKotlin", "true")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    implementation(project(":core:common"))

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // SQLCipher backs the Room SQLiteOpenHelper so the on-disk .db file itself is
    // encrypted at rest, on top of per-message payload encryption from :crypto.
    implementation(libs.sqlcipher.android)
    // Wraps the SQLCipher passphrase itself in a Keystore-backed EncryptedSharedPreferences.
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
