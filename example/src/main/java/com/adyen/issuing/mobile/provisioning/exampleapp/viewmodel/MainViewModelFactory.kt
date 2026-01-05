/*
 * Copyright (c) 2025 Adyen N.V.
 *
 * This file is open source and available under the MIT license. See the LICENSE file for more info.
 *
 * Created by davidw on 8/5/2024.
 */

package com.adyen.issuing.mobile.provisioning.exampleapp.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.adyen.issuing.mobile.provisioning.exampleapp.provisioningsdk.backend.SdkBackend
import com.adyen.issuing.mobile.provisioning.exampleapp.provisioningsdk.fake.FakeSdkBackend
import com.adyen.issuing.mobile.provisioning.exampleapp.provisioningsdk.repository.ProvisioningSdkRepository
import com.adyen.issuing.mobile.provisioning.exampleapp.provisioningsessions.backend.SessionsBackend
import com.adyen.issuing.mobile.provisioning.exampleapp.provisioningsessions.fake.FakeSessionsBackend
import com.adyen.issuing.mobile.provisioning.exampleapp.provisioningsessions.repository.ProvisioningSessionsRepository
import java.io.InputStream

@Suppress("UNCHECKED_CAST")
class MainViewModelFactory(
    private val paymentInstrumentId: String = "PAYMENT_INSTRUMENT_ID",
    private val sdkBackend: SdkBackend = FakeSdkBackend(),
    private val sessionsBackend: SessionsBackend = FakeSessionsBackend(),
    private val applicationCertificateInputStream: InputStream,
    private val activityProvider: () -> Activity,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(
            paymentInstrumentId = paymentInstrumentId,
            provisioningSdkRepository = ProvisioningSdkRepository(
                sdkBackend = sdkBackend,
                activityProvider = activityProvider
            ),
            provisioningSessionsRepository = ProvisioningSessionsRepository(
                applicationCertificateInputStream = applicationCertificateInputStream,
                sessionsBackend = sessionsBackend,
                activityProvider = activityProvider,
            ),
        ) as T
    }
}