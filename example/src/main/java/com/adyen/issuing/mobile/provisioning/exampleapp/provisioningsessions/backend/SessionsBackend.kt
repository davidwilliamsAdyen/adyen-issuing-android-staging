package com.adyen.issuing.mobile.provisioning.exampleapp.provisioningsessions.backend

interface SessionsBackend {
    suspend fun fetchToken(paymentInstrumentIds: Set<String>): String
}