plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("com.google.gms.google-services")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    kotlin("plugin.serialization") version "2.1.10"

    //apollo
//    id("com.apollographql.apollo") version "4.3.0"
}

android {
    namespace = "com.example.m_commerce"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.m_commerce"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}



dependencies {
    implementation(libs.volley)
    implementation(libs.androidx.foundation.android)
    val room_version = "2.6.1"
    val nav_version = "2.8.8"

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //!Database
    //*Room
    implementation("androidx.room:room-runtime:$room_version")
    ksp("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")

    //?==================================================

    //!Network
    //*Apollo
//    implementation("com.apollographql.apollo:apollo-runtime:4.3.0")
    //*Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    //*Shopify
    implementation("com.shopify.mobilebuysdk:buy3:2025.4.0")

    //?==================================================

    //!UI
    //*Coil
    implementation("io.coil-kt.coil3:coil-compose:3.0.4")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.0.4")
    implementation("io.coil-kt.coil3:coil-svg:3.0.4")

    //?==================================================

    //!Dependency Injection
    //*hilt
    implementation("com.google.dagger:hilt-android:2.51.1")
    ksp("com.google.dagger:hilt-android-compiler:2.51.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.5")

    //!Navigation
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("androidx.navigation:navigation-compose:$nav_version")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")

    // firebase auth
    implementation(platform("com.google.firebase:firebase-bom:33.15.0"))
    implementation("com.google.firebase:firebase-auth")

    // extended icons
    implementation("androidx.compose.material:material-icons-extended:1.6.1")

    // view model
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")

    // Material3 Pager
    implementation("androidx.compose.foundation:foundation:1.8.2")
    // Google Maps
    implementation("com.google.maps.android:maps-compose:6.5.2")
    implementation("com.google.maps.android:maps-compose-utils:6.5.2")
    implementation("com.google.maps.android:maps-compose-widgets:6.5.2")
}

//apollo {
//    service("service") {
//        packageName.set("com.example.m_commerce")
//    }
//}