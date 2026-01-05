package com.adyen.issuing.mobile.provisioning.exampleapp.repository

import com.adyen.issuing.mobile.provisioning.client.data.CardAddress
import com.adyen.issuing.mobile.provisioning.exampleapp.data.CardState

interface ProvisioningRepository {
    suspend fun getCardState(paymentInstrumentId: String): CardState
    suspend fun provisionCard(
        paymentInstrumentId: String,
        cardholderName: String,
        cardAddress: CardAddress
    ) : CardState
}