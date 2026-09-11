package com.meshtalk.crypto.di

import com.meshtalk.crypto.identity.IdentityManager
import com.meshtalk.crypto.identity.TinkIdentityManager
import com.meshtalk.crypto.ratchet.DoubleRatchetSessionManager
import com.meshtalk.crypto.ratchet.RatchetSessionManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class CryptoModule {
    @Binds
    abstract fun bindIdentityManager(impl: TinkIdentityManager): IdentityManager

    @Binds
    abstract fun bindRatchetSessionManager(impl: DoubleRatchetSessionManager): RatchetSessionManager
}
