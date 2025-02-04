/*
 * Copyright (c) 2024 Adyen N.V.
 *
 * This file is open source and available under the MIT license. See the LICENSE file for more info.
 *
 * Created by davidw on 8/5/2024.
 */

plugins {
    alias(example.plugins.android.application)
    alias(example.plugins.compose.compiler)
    alias(example.plugins.kotlin.android)
}

android {
    namespace = "com.adyen.issuing.mobile.provisioning.exampleapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.adyen.issuing.mobile.provisioning.exampleapp"
        minSdk = 31
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(example.adyen.provisioning)
    implementation(example.androidx.activity.compose)
    implementation(example.androidx.core.ktx)
    implementation(example.androidx.lifecycle.runtime.ktx)
    implementation(example.androidx.material3)
    implementation(example.androidx.ui)
    implementation(example.androidx.ui.graphics)
    implementation(example.androidx.ui.tooling.preview)
    debugImplementation(example.ui.tooling)
}