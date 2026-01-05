/*
 * Copyright (c) 2025 Adyen N.V.
 *
 * This file is open source and available under the MIT license. See the LICENSE file for more info.
 *
 * Created by davidw on 8/5/2024.
 */

package com.adyen.issuing.mobile.provisioning.exampleapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adyen.issuing.mobile.provisioning.client.data.CardAddress
import com.adyen.issuing.mobile.provisioning.exampleapp.data.CardState
import com.adyen.issuing.mobile.provisioning.exampleapp.data.ProvisioningMethod
import com.adyen.issuing.mobile.provisioning.exampleapp.provisioningsdk.repository.ProvisioningSdkRepository
import com.adyen.issuing.mobile.provisioning.exampleapp.provisioningsessions.repository.ProvisioningSessionsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val paymentInstrumentId: String,
    private val provisioningSdkRepository: ProvisioningSdkRepository,
    private val provisioningSessionsRepository: ProvisioningSessionsRepository,
) : ViewModel() {

    private var provisioningMethod: ProvisioningMethod = ProvisioningMethod.SDK

    private val mutableCardState = MutableStateFlow<CardState>(CardState.Loading)
    val cardState: StateFlow<CardState> = mutableCardState.asStateFlow()

    init {
        fetchState()
    }

    /**
     * Set the [ProvisioningMethod] which specifies whether the provisioning is performed using the
     * Provisioning SDK or Provisioning Sessions.
     */
    fun onProvisioningMethodSelected(method: ProvisioningMethod) {
        provisioningMethod = method
    }

    /**
     * Fetch the card activation data from the backend and initialize the provisioning SDK with it
     */
    private fun fetchState() {
        viewModelScope.launch {
            val cardState = when (provisioningMethod) {
                ProvisioningMethod.SDK -> provisioningSdkRepository.getCardState(paymentInstrumentId)
                ProvisioningMethod.SESSIONS -> provisioningSessionsRepository.getCardState(
                    paymentInstrumentId
                )
            }
            mutableCardState.emit(cardState)
        }
    }

    /**
     * Try to provision the card.
     *
     * This will fail in this example app due to the lack of live backend data which is needed to
     * construct valid Opaque Payment Card data.
     */
    fun provisionCard(
        cardholderName: String = "John Doe",
        cardAddress: CardAddress = CardAddress()
    ) {
        viewModelScope.launch {
            val state = when (provisioningMethod) {
                ProvisioningMethod.SDK -> provisioningSdkRepository.provisionCard(
                    paymentInstrumentId,
                    cardholderName,
                    cardAddress
                )

                ProvisioningMethod.SESSIONS -> provisioningSessionsRepository.provisionCard(
                    paymentInstrumentId,
                    cardholderName,
                    cardAddress
                )
            }
            mutableCardState.emit(state)
        }
    }
}