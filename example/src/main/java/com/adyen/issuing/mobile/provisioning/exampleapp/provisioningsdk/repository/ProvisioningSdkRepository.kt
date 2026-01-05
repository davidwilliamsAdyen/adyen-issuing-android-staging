package com.adyen.issuing.mobile.provisioning.exampleapp.provisioningsdk.repository

import android.app.Activity
import com.adyen.issuing.mobile.provisioning.client.CardProvisioning
import com.adyen.issuing.mobile.provisioning.client.annotation.AdyenExperimentalApi
import com.adyen.issuing.mobile.provisioning.client.data.CardAddress
import com.adyen.issuing.mobile.provisioning.client.results.CanProvisionResult
import com.adyen.issuing.mobile.provisioning.client.results.CardProvisioningCreateResult
import com.adyen.issuing.mobile.provisioning.client.results.CreateSdkOutputResult
import com.adyen.issuing.mobile.provisioning.client.results.ProvisionResult
import com.adyen.issuing.mobile.provisioning.exampleapp.data.CardState
import com.adyen.issuing.mobile.provisioning.exampleapp.provisioningsdk.backend.SdkBackend
import com.adyen.issuing.mobile.provisioning.exampleapp.provisioningsdk.data.CardActivationResult
import com.adyen.issuing.mobile.provisioning.exampleapp.repository.ProvisioningRepository

/**
 * Implementation of the [ProvisioningRepository] interface utilising the provisioning SDK.
 */
@OptIn(AdyenExperimentalApi::class)
class ProvisioningSdkRepository(
    private val sdkBackend: SdkBackend,
    private val activityProvider: () -> Activity,
) : ProvisioningRepository {

    private var cardProvisioning: CardProvisioning? = null

    /**
     * Calls the backend to get the card activation data, uses that to initialize a
     * [CardProvisioning] instance and then requests the card state from the SDK.
     */
    override suspend fun getCardState(paymentInstrumentId: String): CardState =
        sdkBackend.requestCardActivation(paymentInstrumentId).let { cardActivationResult ->
            when (cardActivationResult) {
                // Use the activation data to initialize the provisioning SDK.
                is CardActivationResult.Active -> CardProvisioning.create(
                    cardActivationResult.sdkInput, activityProvider
                ).let { provisioningCreateResult ->
                    when (provisioningCreateResult) {
                        is CardProvisioningCreateResult.Success -> {
                            cardProvisioning = provisioningCreateResult.cardProvisioning
                            getCardStateFromProvisioningSdk()
                        }

                        is CardProvisioningCreateResult.Failed.GooglePayNotSupported -> CardState.NotSupported
                        is CardProvisioningCreateResult.Failed.InvalidSdkInput -> CardState.Error(
                            message = "Activation data is invalid"
                        )
                    }
                }

                CardActivationResult.Disabled -> CardState.Disabled
            }
        }

    private suspend fun getCardStateFromProvisioningSdk(): CardState =
        cardProvisioning?.canProvision()?.let {
            when (it) {
                // Card can be provisioned.
                is CanProvisionResult.CanBeProvisioned -> CardState.NotAddedToWallet
                // Card has already been added to the wallet.
                is CanProvisionResult.CannotBeProvisioned.AlreadyExistsInWallet -> CardState.AddedToWallet
                // The Google Tap and Pay API returned an error.
                is CanProvisionResult.CannotBeProvisioned.ApiError -> CardState.Error(
                    message = "A Google Tap and Pay API error occurred, Error Code: ${it.statusCode}"
                )
                // The operation failed with an exception.
                is CanProvisionResult.CannotBeProvisioned.Error -> CardState.Error(
                    message = it.throwable.message
                )
                // Something unexpected happened!
                is CanProvisionResult.CannotBeProvisioned.UnknownFailure -> CardState.Error(
                    message = "Unknown failure"
                )
            }
        } ?: CardState.Error(message = "Provisioning client not set")


    /**
     * Use the [CardProvisioning] instance created in the [getCardState] method to provision a payment
     * instrument using opaque card data from the backend.
     */
    override suspend fun provisionCard(
        paymentInstrumentId: String,
        cardholderName: String,
        cardAddress: CardAddress
    ) : CardState {
        // Request the SDK Output value from the provisioning SDK.
        return cardProvisioning?.createSdkOutput()?.let {
            when (it) {
                is CreateSdkOutputResult.Failure -> CardState.Error("Failed to get SDK Output value")
                // Use the SDK Output value to request the Opaque Payment Card data.
                is CreateSdkOutputResult.Success -> {
                    val opcResponse = sdkBackend.requestOpaquePaymentCardData(
                        paymentInstrumentId,
                        it.sdkOutput
                    )
                    // Use the Opaque Payment Card data to provision the card.
                    cardProvisioning?.provision(
                        opcResponse.sdkInput,
                        cardholderName,
                        cardAddress
                    )
                        ?.let { provisioningResult ->
                            when (provisioningResult) {
                                is ProvisionResult.Success -> CardState.AddedToWallet
                                else -> CardState.Error("Failed to provision card $provisioningResult")
                            }
                        }
                }
            } ?: CardState.Error("Provisioning Failed")
        } ?: CardState.Error("Card Provisioning SDK not initialized")
    }
}