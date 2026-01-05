/*
 * Copyright (c) 2025 Adyen N.V.
 *
 * This file is open source and available under the MIT license. See the LICENSE file for more info.
 *
 * Created by davidw on 8/5/2024.
 */

package com.adyen.issuing.mobile.provisioning.exampleapp.provisioningsdk.backend

import com.adyen.issuing.mobile.provisioning.exampleapp.provisioningsdk.data.CardActivationResult
import com.adyen.issuing.mobile.provisioning.exampleapp.provisioningsdk.data.OpaqueCardDataResponse

interface SdkBackend {

    /**
     * Request card activation data (`sdkInput`) by making a **GET** request to
     * `paymentInstruments/$[paymentInstrumentId]/networkTokenActivationData` endpoint.
     */
    suspend fun requestCardActivation(paymentInstrumentId: String): CardActivationResult

    /**
     * Request opaque payment card data by making **POST** request to
     * `paymentInstruments/$[paymentInstrumentId]/networkTokenActivationData` endpoint and passing
     * [sdkOutput] in the body.
     */
    suspend fun requestOpaquePaymentCardData(
        paymentInstrumentId: String,
        sdkOutput: String
    ): OpaqueCardDataResponse
}