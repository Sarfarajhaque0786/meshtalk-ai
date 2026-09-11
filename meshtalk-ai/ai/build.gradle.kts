plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.meshtalk.ai.service"
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
    implementation(project(":domain"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.android)

    // On-device inference runtimes. Actual .tflite/.onnx model assets are not
    // bundled in this skeleton - see /docs/ai-models.md (to be written) for the
    // planned model list (a small on-device LLM for smart replies/NL commands,
    // Whisper-tiny/base for transcription, ML Kit for language ID/translation).
    // implementation("org.tensorflow:tensorflow-lite:2.16.1")
    // implementation("com.microsoft.onnxruntime:onnxruntime-android:1.18.0")

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
