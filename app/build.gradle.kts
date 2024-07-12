import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}


android {
    namespace = "com.noatnoat.pianoapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.noatnoat.pianoapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        ndk {
            abiFilters.add("armeabi-v7a")
            abiFilters.add("arm64-v8a")
            abiFilters.add("x86")
            abiFilters.add("x86_64")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    bundle {
        language {
            enableSplit = false
        }
    }
    kapt {
        correctErrorTypes = true
    }
    applicationVariants.all {
        val outputFileName = "PianoApp" +
                "_${SimpleDateFormat("dd-MM", Locale.US).format(Date())}"+
                "_build1.apk"
        outputs.all {
            val output = this as? BaseVariantOutputImpl
            output?.outputFileName = outputFileName
        }
    }

}


dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.compose.ui:ui-android:1.6.0")
    implementation("androidx.compose.foundation:foundation-android:1.6.0")
    implementation("androidx.compose.material3:material3:1.1.2")
    implementation("androidx.lifecycle:lifecycle-process:2.7.0")
    implementation(libs.androidx.preference.ktx)
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-android-compiler:2.50")
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")

    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.6")
    implementation("com.github.Amir-yazdanmanesh:AndroidChart:3.1.0.16")
    implementation("com.tbuonomo:dotsindicator:5.0")


    // for firebase
    implementation("com.google.firebase:firebase-messaging:22.0.0")
    implementation("com.google.android.gms:play-services-ads:22.6.0")
    implementation("com.google.firebase:firebase-crashlytics:18.2.3")
    implementation("com.google.android.ump:user-messaging-platform:2.0.0")
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-config")
    implementation("androidx.browser:browser:1.7.0")
    implementation("com.google.android.play:review:2.0.1")
    implementation("com.google.android.play:review-ktx:2.0.1")
    implementation("com.github.commandiron:WheelPickerCompose:1.1.11")
    implementation("com.github.ome450901:SimpleRatingBar:1.5.1")
    implementation("dev.b3nedikt.applocale:applocale:3.1.0")
    implementation("dev.b3nedikt.viewpump:viewpump:4.0.10")
    implementation("dev.b3nedikt.reword:reword:4.0.4")

    // for mediation
    implementation("com.google.ads.mediation:unity:4.1.0.0")
    implementation("com.google.ads.mediation:vungle:7.1.0.0")
    implementation("com.google.ads.mediation:mintegral:16.6.21.0")
    implementation("com.google.ads.mediation:facebook:6.16.0.0")
    implementation("com.google.ads.mediation:applovin:12.1.0.0")
    implementation("com.facebook.android:facebook-android-sdk:16.0.0")

    implementation("com.github.AraujoJordan:ExcuseMe:0.9.5")
    implementation("androidx.multidex:multidex:2.0.1")
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    implementation("com.intuit.sdp:sdp-android:1.1.0")
    implementation("com.intuit.ssp:ssp-android:1.1.0")

    // for adjust
    implementation("com.google.android.gms:play-services-appset:16.0.2")
    implementation(files("libs/adjust-android-signature-3.12.0.aar"))
    implementation("com.adjust.sdk:adjust-android:4.33.5")
    implementation("com.android.installreferrer:installreferrer:2.2")
    implementation("com.google.android.gms:play-services-ads-identifier:18.0.1")
    implementation("com.android.installreferrer:installreferrer:2.2")

    //exoplayer
    implementation("androidx.media3:media3-exoplayer:1.3.1")
    implementation("androidx.media3:media3-exoplayer:1.3.1")
    implementation("androidx.media3:media3-exoplayer-dash:1.3.1")
    implementation("androidx.media3:media3-ui:1.3.1")
    implementation("androidx.media3:media3-exoplayer-hls:1.3.1")
}