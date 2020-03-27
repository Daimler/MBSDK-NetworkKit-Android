package com.daimler.mbnetworkkit

import com.daimler.mbnetworkkit.header.HeaderService
import com.daimler.mbnetworkkit.header.MemoryHeaderService

object MBNetworkKit {

    private lateinit var headerService: HeaderService

    fun init(config: NetworkServiceConfig) {
        headerService = MemoryHeaderService(
            config.applicationName,
            config.applicationVersion,
            config.sdkVersion,
            config.osName,
            config.osVersion,
            config.locale
        )
    }

    fun headerService(): HeaderService = headerService
}