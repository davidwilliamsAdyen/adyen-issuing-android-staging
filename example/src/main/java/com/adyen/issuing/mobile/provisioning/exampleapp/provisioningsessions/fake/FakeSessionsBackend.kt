package com.adyen.issuing.mobile.provisioning.exampleapp.provisioningsessions.fake

import com.adyen.issuing.mobile.provisioning.exampleapp.provisioningsessions.backend.SessionsBackend

class FakeSessionsBackend : SessionsBackend {
    override suspend fun fetchToken(paymentInstrumentIds: Set<String>): String = fakeSessionToken

    companion object {
        private const val fakeSessionToken: String =
            "ewogICAgImFsbG93T3JpZ2luIjogImh0dHBzOi8vd3d3LmFkeWVuLmNvbSIsCiAgICAicHJvZHVjdCI6" +
                "ICJwbGF0Zm9ybSIsCiAgICAicG9saWN5IjogewogICAgICAgICJyZXNvdXJjZXMiOiBbCiAgICAgICAg" +
                "ICAgIHsKICAgICAgICAgICAgICAgICJ0eXBlIjogInBheW1lbnRJbnN0cnVtZW50IiwKICAgICAgICAg" +
                "ICAgICAgICJwYXltZW50SW5zdHJ1bWVudElkIjogIlBJMDEyMzQ1Njc4OUFCQ0RFRkdISUpLTE0iCiAg" +
                "ICAgICAgICAgIH0KICAgICAgICBdLAogICAgICAgICJyb2xlcyI6IFsiTmV0d29yayBUb2tlbjogQ3Jl" +
                "YXRlIl0KICAgIH0KfQ=="
    }
}