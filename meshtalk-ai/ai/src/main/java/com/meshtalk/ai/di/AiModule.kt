package com.meshtalk.ai.di

import com.meshtalk.ai.service.AiService
import com.meshtalk.ai.service.OnDeviceAiService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AiModule {
    @Binds
    abstract fun bindAiService(impl: OnDeviceAiService): AiService
}
