/*
 * Copyright (c) 2025 Adyen N.V.
 *
 * This file is open source and available under the MIT license. See the LICENSE file for more info.
 *
 * Created by davidw on 8/5/2024.
 */

package com.adyen.issuing.mobile.provisioning.exampleapp.provisioningsdk.fake

import com.adyen.issuing.mobile.provisioning.exampleapp.provisioningsdk.backend.SdkBackend
import com.adyen.issuing.mobile.provisioning.exampleapp.provisioningsdk.data.CardActivationResult
import com.adyen.issuing.mobile.provisioning.exampleapp.provisioningsdk.data.OpaqueCardDataResponse

/**
 * Fake backend implementation for Provisioning SDK example.
 */
class FakeSdkBackend : SdkBackend {

    /**
     * Request card activation data (`sdkInput`) by making a **GET** request to
     * `paymentInstruments/$[paymentInstrumentId]/networkTokenActivationData` endpoint.
     */
    override suspend fun requestCardActivation(paymentInstrumentId: String): CardActivationResult =
        CardActivationResult.Active(dummySdkInput)

    /**
     * Request opaque payment card data by making **POST** request to
     * `paymentInstruments/$[paymentInstrumentId]/networkTokenActivationData` endpoint and passing
     * [sdkOutput] in the body.
     */
    override suspend fun requestOpaquePaymentCardData(
        paymentInstrumentId: String,
        sdkOutput: String
    ): OpaqueCardDataResponse =
        OpaqueCardDataResponse("dummy opaque data")

    companion object {
        private val dummySdkInput: String =
            """eyJwYXltZW50SW5zdHJ1bWVudElkIjoiUEFZTUVOVF9JTlNUUlVNRU5UX0lEIiwiY2FyZGhvb
            GRlck5hbWUiOiJKb2hubnkgQXBwbGVzZWVkIiwiYnJhbmQiOiJ2aXNhIiwibGFzdEZvdXIiOiI4OTUyIiwic3
            VwcG9ydGVkV2FsbGV0cyI6WyJnb29nbGVQYXkiLCJhcHBsZVBheSJdfQ=="""
                .trimIndent().replace(Regex("""(\r\n)|\n|\s"""), "")
    }
}

