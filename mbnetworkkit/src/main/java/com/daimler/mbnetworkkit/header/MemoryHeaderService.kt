package com.daimler.mbnetworkkit.header

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
}