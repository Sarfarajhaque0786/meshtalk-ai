package com.meshtalk.ai

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Composition root for the whole app. Hilt generates the top-level component here.
 *
 * Deliberately thin: any real startup work (mesh bootstrap, key material check,
 * AI model warm-up) is delegated to WorkManager-scheduled initializers or the
 * MeshForegroundService, not run inline on the application's main thread.
 */
@HiltAndroidApp
class MeshTalkApplication : Application()
