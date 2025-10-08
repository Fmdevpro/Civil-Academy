
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.FileInputStream
import java.util.Properties

plugins {
    // Android
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    // Compose
    alias(libs.plugins.kotlin.compose)

    // Serialization
    alias(libs.plugins.kotlinxSerialization)

    // Hilt
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)

    // Firebase
    alias(libs.plugins.google.services)
    alias(libs.plugins.crashlytics)
}

android {
    namespace = "com.fmdev.civilacademy"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.fmdev.civilacademy"
        minSdk = 30
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0-alpha"
        val properties = Properties()
        val localPropsFile = rootProject.file("local.properties")
        if (localPropsFile.exists()) {
            properties.load(FileInputStream(localPropsFile))
        }

        val webClientId: String = properties.getProperty("WEB_CLIENT_ID")
            ?: throw GradleException("WEB_CLIENT_ID not found in local.properties")

        buildConfigField("String", "WEB_CLIENT_ID", "\"$webClientId\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        val localProperties = Properties().apply {
            load(rootProject.file("keystore.properties").inputStream())
        }

        create("release") {
            if (localProperties.isNotEmpty()) {
                storeFile = (file(localProperties.getProperty("RELEASE_STORE_FILE") as String))
                storePassword = (localProperties.getProperty("RELEASE_STORE_PASSWORD") as String)
                keyAlias = (localProperties.getProperty("RELEASE_KEY_ALIAS") as String)
                keyPassword = (localProperties.getProperty("RELEASE_KEY_PASSWORD") as String)
            }
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}

dependencies {

    // Modules
    implementation(project(":domain"))
    implementation(project(":presentation"))
    implementation(project(":data"))
    implementation(project(":shared"))
    implementation(project(":androidshared"))
    implementation(project(":di"))

    // Core & Compose
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Firebase
    implementation(platform(libs.firebase.bom))

    implementation(libs.bundles.firebase)
    // Splash Screen
    implementation(libs.androidx.core.splashscreen)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.testing.android)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}