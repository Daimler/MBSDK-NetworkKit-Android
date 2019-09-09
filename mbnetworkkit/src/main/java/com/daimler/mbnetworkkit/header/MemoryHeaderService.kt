package com.daimler.mbnetworkkit.header

import com.daimler.mbnetworkkit.NetworkServiceConfig

internal class MemoryHeaderService(
    applicationName: String,
    applicationVersion: String,
    sdkVersion: String,
    osName: String,
    osVersion: String,
    private var serviceLocale: String
) : BaseHeaderService(applicationName, applicationVersion, sdkVersion, osName, osVersion) {

    override fun updateNetworkLocale(locale: String) {
        this.serviceLocale = locale
    }

    override fun currentNetworkLocale(): String = serviceLocale

    companion object {
        fun fromNetworkServiceConfig(config: NetworkServiceConfig): MemoryHeaderService {
            return MemoryHeaderService(
                    config.applicationName,
                    config.applicationVersion,
                    config.sdkVersion,
                    config.osName,
                    config.osVersion,
                    config.locale
            )
        }
    }
}
