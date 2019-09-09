package com.daimler.mbnetworkkit

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class HeaderServiceTest {

    @Before
    fun setup() {
        val config = NetworkServiceConfig.Builder(
                APP_NAME,
                APP_VERSION,
                SDK_VERSION
        ).apply {
            useOSVersion(OS_VERSION)
            useLocale(DEFAULT_LOCALE)
        }.build()

        MBNetworkKit.init(config)
    }

    @Test
    fun testHeaderServiceLocale() {
        assertEquals(DEFAULT_LOCALE, MBNetworkKit.headerService().currentNetworkLocale())
        MBNetworkKit.headerService().updateNetworkLocale("ABC123")
        assertEquals("ABC123", MBNetworkKit.headerService().currentNetworkLocale())
    }

    @Test
    fun testRisHeaderInterceptor() {
        val interceptor = MBNetworkKit.headerService().createRisHeaderInterceptor()
        val chain = DummyChain()
        interceptor.intercept(chain)
        val request = chain.request()
        assertEquals(APP_NAME, request.header(HEADER_APPLICATION_NAME))
        assertEquals(APP_VERSION, request.header(HEADER_APPLICATION_VERSION))
        assertEquals(SDK_VERSION, request.header(HEADER_SDK_VERSION))
        assertEquals(OS_VERSION, request.header(HEADER_OS_VERSION))
        assertEquals(DEFAULT_LOCALE, request.header(HEADER_LOCALE))
    }

    @Test
    fun testRisHeaderLocaleChange() {
        val interceptor = MBNetworkKit.headerService().createRisHeaderInterceptor()

        val chain = DummyChain()
        interceptor.intercept(chain)
        val request = chain.request()
        assertEquals(DEFAULT_LOCALE, request.header(HEADER_LOCALE))

        MBNetworkKit.headerService().updateNetworkLocale("12345")

        val newChain = DummyChain()
        interceptor.intercept(newChain)
        val newRequest = newChain.request()
        assertEquals("12345", newRequest.header(HEADER_LOCALE))
    }

    private companion object {
        private const val HEADER_APPLICATION_NAME = "X-ApplicationName"
        private const val HEADER_LOCALE = "X-Locale"
        private const val HEADER_APPLICATION_VERSION = "ris-application-version"
        private const val HEADER_OS_VERSION = "ris-os-version"
        private const val HEADER_SDK_VERSION = "ris-sdk-version"

        private const val APP_NAME = "appName"
        private const val APP_VERSION = "appVersion"
        private const val SDK_VERSION = "1.0-Test"
        private const val OS_VERSION = "X-1.0-Test"
        private const val DEFAULT_LOCALE = "de-DE"
    }
}