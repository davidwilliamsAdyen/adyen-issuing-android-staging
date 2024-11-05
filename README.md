# Adyen Google Wallet Provisioning

`Adyen Google Wallet Provisioning` Android SDK simplifies integration with Google Wallet.

## Installation

The SDK is available from [Maven Central](https://central.sonatype.com/artifact/com.adyen.issuing/provisioning).

### Import from Maven Central

1. Import the SDK by adding this line to your `build.gradle` file.
```kotlin
implementation("com.adyen.issuing:provisioning:0.2.0")
```

### Import manually

1. Copy the SDK package `adyen-provisioning-0.2.0.aar` to the `/libs` folder in your module.
2. Import the SDK by adding this line to your module `build.gradle` file.
```kotlin
implementation("com.adyen.issuing:provisioning:0.2.0@aar")
```

## Usage

The SDK interaction is performed through calls to an instance of `CardProvisioning`. This instance is provided by a static function of the `CardProvisioning` class:

```kotlin
fun create(
        sdkInput: String,
        activityProvider: () -> Activity,
        provisionCardRequestCode: Int,      // Optional
        createWalletRequestCode: Int        // Optional
    ): CardProvisioningCreateResult
```

The `CardProvisioning` class utilises `suspend` functions which return when the requested operation has either completed or failed and return objects which can be queried for the request state, plus a function which serves to pass an `Activity` result into the SDK:

```kotlin
suspend fun hasWallet(): HasWalletResult
suspend fun createWallet(): CreateWalletResult
suspend fun canProvision(): CanProvisionResult
suspend fun getSdkOutput(): GetSdkOutputResult
suspend fun provision(sdkInput: String, cardDisplayName: String, cardAddress: CardAddress): ProvisionResult
fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean
```

The underlying Google Wallet API makes use of the Android `onActivityResult()` API (now flagged as deprecated in Android). This means the process of provisioning cards and creating a wallet involves an `Activity` from the client application and this `Activity` must forward the data passed to`onActivityResult()` into the Adyen provisioning SDK.

### Creating a provisioning client instance

Perform a `GET` request to endpoint `/paymentInstruments/{paymentInstrumentId}/networkTokenActivationData` from your backend. The response contains `sdkInput` data.

Make a call to the `CardProvisioning.create()` static function passing in the `sdkInput` data and a function which returns an `Activity` (this function should return the `Activity` which hosts your application's card issuing functionality, or the main `Activity` for a single `Activity` application). Optionally, a `provisionCardRequestCode` value can be provided which will override the default value of `217` in case this is already in use within the client application as an `onActivityResult()` request code.

The `create()` function returns a result object which indicates success or failure of the operation. Possible failure reasons are that Google Pay is not supported for the given card or the the `sdkInput` data is invalid and cannot be parsed.
```kotlin
val result = CardProvisioning.create(
    sdkInput = sdkInput,
    activityProvider = { cardIssuingActivity },
    provisionCardRequestCode = MY_PROVISIONING_REQUEST_CODE, // Optional. Default Value: 217
    createWalletRequestCode = MY_CREATE_WALLET_REQUEST_CODE  // Optional. Default Value: 218
)

val cardProvisioning = when(result) {
    is CardProvisioningCreateResult.Success -> result.cardProvisioning
    is CardProvisioningCreateResult.Failure.GooglePayNotSupported -> throw Exception("The card does not support Google Pay")
    is CardProvisioningCreateResult.Failure.InvalidSdkInput -> throw Exception("The sdk input data is invalid")
}
```

### Checking if an active Google Wallet exists on the device

Check if the cardholder has an active Google Wallet on their device.
```kotlin
val result = cardProvisioning.hasWallet()
val hasWallet = when(result) {
    is HasWallet.Success -> result.hasWallet
    is HasWallet.Failure -> throw Exception(result.errorMessage)
}
```

### Creating a Wallet

If the `hasWallet()` call returns the result: `HasWallet.Success(false)`then a wallet must be 
created before card provisioning can proceed.

To create a wallet use the following call:

```kotlin
val walletCreated = cardProvisioning.createWallet() == CreateWalletResult.Success
```

### Checking if a card can be provisioned

Check if the cardholder can add a payment card to their Google Wallet.
```kotlin
val canProvision = cardProvisioning.canProvision() == CanProvisionResult.CanBeProvisioned
```

### Initiate card provisioning

When the cardholder opts to add a card to Google Wallet, initiate provisioning by performing the following steps:

1. Make a call to `cardProvisioning.getSdkOuput()` to retrieve the `sdkOuput` string required for step 2.
2. Make a `POST` `paymentInstruments/{paymentInstrumentId}/networkTokenActivationData` request from your backend to provision the payment instrument. The body must contain the `sdkOuput` obtained from step 1. The response contains the `sdkInput` object.
3. Make a call to `cardProvisioning.provision()` passing the `sdkInput` value plus the cardholder name string and an instance of `CardAddress`:
```kotlin
cardProvisioning.provision(
    sdkInput = sdkInput,
    cardDisplayName = "John Doe",
    cardAddress = CardAddress()
)
```

**Note:** If you provide an empty `CardAddress`, as above, then the user will be prompted to enter their address during the Google Wallet provisioning flow.

### Handle the Activity Result

The Google Wallet API delivers the result of the provisioning and wallet creation requests to the `Acivity` returned by the function passed to `CardProvisioning.create()`.

Override the `onActivityResult()` for your `Activity` (or modify your existing `onActivityResult()`):

```kotlin
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!cardProvisioning.onActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
```

**Note:** If you do not forward the `Activity.onActivityResult()` call to the provisioning client then the `cardProvisioning.provision()` and `cardProvisionoing.createWallet()` 
calls will remain suspended forever!

For more documentation refer to our [complete documentation](https://docs.adyen.com/issuing/digital-wallets/google-wallet-provisioning/)

## See also

* [Complete Documentation](https://docs.adyen.com/issuing/digital-wallets/google-wallet-provisioning/)
* [SDK Reference Adyen Google Pay Provisioning](https://adyen.github.io/adyen-google-pay-provisioning-android/0.2.0/AdyenGoogleWalletProvisioning//)
* [Data security at Adyen](https://docs.adyen.com/development-resources/adyen-data-security)

## License

This SDK is available under the Apache License, Version 2.0. For more information, see the [LICENSE](https://github.com/Adyen/adyen-google-pay-provisioning-android/blob/main/LICENSE) file.
