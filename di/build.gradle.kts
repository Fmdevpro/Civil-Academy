import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    // Android
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)

    // Hilt
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.fmdev.civilacademy.di"
    compileSdk = 36

    defaultConfig {
        minSdk = 30

        consumerProguardFiles("consumer-rules.pro")
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
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}

dependencies {
    // Modules
    implementation(project(":domain"))
    api(project(":data"))

    // Core & Compose
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.hilt.testing)
    ksp(libs.hilt.compiler)

    // DataStore
    implementation(libs.androidx.datastore)
}