package com.adyen.issuing.mobile.provisioning.exampleapp.provisioningsessions.repository

import android.app.Activity
import com.adyen.issuing.mobile.provisioning.client.annotation.AdyenExperimentalApi
import com.adyen.issuing.mobile.provisioning.client.data.CardAddress
import com.adyen.issuing.mobile.provisioning.exampleapp.data.CardState
import com.adyen.issuing.mobile.provisioning.exampleapp.provisioningsessions.backend.SessionsBackend
import com.adyen.issuing.mobile.provisioning.exampleapp.repository.ProvisioningRepository
import com.adyen.issuing.mobile.provisioning.sessions.ProvisioningSession
import com.adyen.issuing.mobile.provisioning.sessions.results.CanProvisionResult
import com.adyen.issuing.mobile.provisioning.sessions.results.ProvisionResult
import java.io.InputStream

/**
 * Implementation of the [ProvisioningRepository] interface utilising the provisioning sessions SDK.
 */
@OptIn(AdyenExperimentalApi::class)
class ProvisioningSessionsRepository(
    private val applicationCertificateInputStream: InputStream,
    private val sessionsBackend: SessionsBackend,
    private val activityProvider: () -> Activity,
) : ProvisioningRepository {

    private var cardProvisioningSession: ProvisioningSession? = null

    /**
     * Create a [ProvisioningSession] instance for the given [paymentInstrumentId] and use the
     * instance to retrieve the state of the payment instrument.
     */
    override suspend fun getCardState(paymentInstrumentId: String): CardState {
        val result = ProvisioningSession.create(
            appCertInputStream = applicationCertificateInputStream,
            paymentInstrumentIds = setOf(paymentInstrumentId),
            activityProvider = activityProvider,
            sessionTokenProvider = { paymentInstrumentIds ->
                sessionsBackend.fetchToken(paymentInstrumentIds)
            }
        ).apply {
            cardProvisioningSession = this
        }.canProvisionCard(paymentInstrumentId)
        return when (result) {
            is CanProvisionResult.CanBeProvisioned -> CardState.NotAddedToWallet
            is CanProvisionResult.CannotBeProvisioned.AlreadyExistsInWallet -> CardState.AddedToWallet
            is CanProvisionResult.CannotBeProvisioned.ApiError ->
                CardState.Error("A Google Tap and Pay API error occurred, Error Code: ${result.statusCode}")
            is CanProvisionResult.CannotBeProvisioned.DecryptError -> CardState.Error(result.message)
            is CanProvisionResult.CannotBeProvisioned.EncryptError -> CardState.Error(result.message)
            is CanProvisionResult.CannotBeProvisioned.Error -> CardState.Error(result.message)
            is CanProvisionResult.CannotBeProvisioned.GooglePayNotSupported -> CardState.NotSupported
            is CanProvisionResult.CannotBeProvisioned.HttpError -> CardState.Error(result.errorMessage)
            is CanProvisionResult.CannotBeProvisioned.InvalidApplicationCertificate ->
                CardState.Error("Invalid Application Certificate")
            is CanProvisionResult.CannotBeProvisioned.PaymentInstrumentNotAllowed ->
                CardState.Error("Payment Instrument not allowed")
            is CanProvisionResult.CannotBeProvisioned.SerializationError -> CardState.Error(result.message)
            is CanProvisionResult.CannotBeProvisioned.TokenProviderError -> CardState.Error(result.message)
            is CanProvisionResult.CannotBeProvisioned.UnknownFailure -> CardState.Error("Unknown failure")
        }
    }

    /**
     * Use the [ProvisioningSession] created in the [getCardState] method to provision a payment
     * instrument.
     */
    override suspend fun provisionCard(
        paymentInstrumentId: String,
        cardholderName: String,
        cardAddress: CardAddress
    ): CardState {
        val result = cardProvisioningSession?.provisionCard(
            paymentInstrumentId = paymentInstrumentId,
            cardDisplayName = cardholderName,
            cardAddress = cardAddress,
        )
        return when (result) {
            is ProvisionResult.Success -> CardState.AddedToWallet
            is ProvisionResult.Failure.AlreadyProvisioned -> CardState.AddedToWallet
            is ProvisionResult.Failure.Canceled -> CardState.Error("Provisioning Cancelled")
            is ProvisionResult.Failure.DecryptError -> CardState.Error(result.message)
            is ProvisionResult.Failure.EncryptError -> CardState.Error(result.message)
            is ProvisionResult.Failure.Error -> CardState.Error(result.message)
            is ProvisionResult.Failure.HttpError -> CardState.Error(result.message)
            is ProvisionResult.Failure.InvalidApplicationCertificate -> CardState.Error("The application certificate is not valid")
            is ProvisionResult.Failure.PaymentInstrumentNotAllowed -> CardState.Error("The given payment instrument id is not present in the set of ids used when creating the session")
            is ProvisionResult.Failure.ProvisioningInProgress -> CardState.Error("Provisioning or wallet creation already in-progress for this card")
            is ProvisionResult.Failure.SerializationError -> CardState.Error(result.message)
            is ProvisionResult.Failure.TokenProviderError -> CardState.Error("The given token provider function threw an exception or returned an invalid token")
            is ProvisionResult.Failure.ProvisioningSdkFailure -> CardState.Error(result.message)
            is ProvisionResult.Failure.TapAndPayApiFailure -> CardState.Error(result.message)
            is ProvisionResult.Failure.TapAndPayNotAvailable -> CardState.Error("The TapAndPay API cannot be called by the current application")
            null -> CardState.Error("The provisioning session has not been initialized")
        }
    }
}