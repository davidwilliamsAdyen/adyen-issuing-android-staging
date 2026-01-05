[![License][shield.license.image]][shield.license.link]
# Adyen Google Wallet Provisioning Example App

This example application demonstrates how to integrate the Adyen Issuing Android SDKs to add Google Wallet card provisioning functionality to your Android banking application.

It showcases the usage of two key libraries:

*   **[Provisioning SDK][readme.provisioning.sdk]**: Provides basic functionality for provisioning Adyen issued cards to Google Wallet. Look at the class [ProvisioningSdkRepository](example/src/main/java/com/adyen/issuing/mobile/provisioning/exampleapp/provisioningsdk/repository/ProvisioningSdkRepository.kt) for example usage.
*   **[Provisioning Session-based SDK][readme.provisioning.sessions]**: Provides a higher level interface which manages both the provisioning session lifecycle and the communication with the Adyen backend.Look at the class [ProvisioningSessionsRepository](example/src/main/java/com/adyen/issuing/mobile/provisioning/exampleapp/provisioningsessions/repository/ProvisioningSessionsRepository.kt) for example usage.

## Purpose

The primary goal of this app is to provide developers with reference implementations for both the **Provisioning SDK** and **Provisioning Sessions** which demonstrate how to:

1.  Initialize a provisioning session.
2.  Check if a card can be provisioned to Google Wallet.
3.  Provision a card to Google Wallet.

## Important Note: Non-Functional State

**Please be aware that this example app is provided in a non-functional state.**

To successfully provision cards, the SDK requires:

*   Integration between your backend and the Adyen API.
*   For Provisioning Sessions: A valid Application Certificate

Credentials for Adyen integration are strictly controlled and are only available by contacting Adyen directly. The example app currently uses **fake/mock values** for these credentials. Consequently, running the provisioning flows within this app will result in error responses from the SDK.

This is expected behavior and is intended to demonstrate error handling and the overall flow without exposing sensitive credentials.

## Getting Started

To use this example as a reference for your own integration:

1.  **Explore the Code**: Examine the source code to understand how the `Provisioning Sessions SDK` and `Provisioning SDK` are initialized and used.
2.  **Review the Flow**: Follow the user journey from session creation to the provisioning result handling.
3.  **Contact Adyen**: To implement this in your own production application, please contact Adyen support to obtain your unique API keys and Application Certificate.

## Prerequisites

*   Android Studio
*   An Android device or emulator with Google Play Services installed.

## License

This example app is distributed under the MIT License.

[shield.license.image]: https://img.shields.io/badge/License-MIT-green.svg
[shield.license.link]: LICENSE
[readme.provisioning.sdk]: ../provisioning/sdk/README.md
[readme.provisioning.sessions]: ../provisioning/sessions/README.md