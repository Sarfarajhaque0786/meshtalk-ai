package com.meshtalk.data.di

import com.meshtalk.data.repository.CryptoRepositoryImpl
import com.meshtalk.data.repository.MeshRepositoryImpl
import com.meshtalk.data.repository.MessageRepositoryImpl
import com.meshtalk.domain.repository.CryptoRepository
import com.meshtalk.domain.repository.MeshRepository
import com.meshtalk.domain.repository.MessageRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    abstract fun bindMessageRepository(impl: MessageRepositoryImpl): MessageRepository

    @Binds
    abstract fun bindMeshRepository(impl: MeshRepositoryImpl): MeshRepository

    @Binds
    abstract fun bindCryptoRepository(impl: CryptoRepositoryImpl): CryptoRepository
}
