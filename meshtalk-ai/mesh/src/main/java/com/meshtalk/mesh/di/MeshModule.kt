package com.meshtalk.mesh.di

import com.meshtalk.mesh.routing.HybridRoutingEngine
import com.meshtalk.mesh.routing.RoutingEngine
import com.meshtalk.mesh.transport.BleTransport
import com.meshtalk.mesh.transport.MeshTransport
import com.meshtalk.mesh.transport.WifiDirectTransport
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

/**
 * Every [MeshTransport] implementation binds into this Set so that
 * [com.meshtalk.mesh.discovery.DiscoveryService] and [com.meshtalk.mesh.MeshManager]
 * can iterate all available radios without a hardcoded list. Adding Wi-Fi Aware or
 * UWB support later is purely additive here: implement MeshTransport, add one more
 * @Binds @IntoSet method.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class MeshModule {

    @Binds
    @IntoSet
    abstract fun bindWifiDirectTransport(impl: WifiDirectTransport): MeshTransport

    @Binds
    @IntoSet
    abstract fun bindBleTransport(impl: BleTransport): MeshTransport

    @Binds
    abstract fun bindRoutingEngine(impl: HybridRoutingEngine): RoutingEngine
}
