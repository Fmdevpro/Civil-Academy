import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    // Android
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)

    // Compose
    alias(libs.plugins.kotlin.compose)

    // Serialization
    alias(libs.plugins.kotlinxSerialization)

    // Hilt
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)

    // Jacoco reports
    id("jacoco")
}

android {
    namespace = "com.fmdev.civilacademy.presentation"
    compileSdk = 36

    defaultConfig {
        minSdk = 30

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    testOptions {
        unitTests.all { test ->
            test.doFirst {
                val byteBuddyAgentJar = test.classpath
                    .filter { it.name.contains("byte-buddy-agent") }
                    .firstOrNull()

                if (byteBuddyAgentJar != null) {
                    test.jvmArgs("-javaagent:${byteBuddyAgentJar.absolutePath}")
                } else {
                    test.logger.warn("Byte-buddy-agent JAR not found for test task ${test.name}. Mockito agent might not be attached.")
                }
            }
            test.configure<JacocoTaskExtension> {
                isIncludeNoLocationClasses = true
                excludes = listOf("jdk.internal.*")
            }
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}

dependencies {
    // Modules
    api(project(":domain"))
    api(project(":shared"))
    api(project(":di"))

    // Core & Compose
    implementation(libs.androidx.core.ktx)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Coroutines
    implementation(libs.kotlinx.coroutines.test)

    // ViewModel KTX & Lifecycle Runtime Compose
    implementation(libs.bundles.lifecycle)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.ui.tooling)
    implementation(libs.core.ktx)
    debugImplementation(libs.ui.tooling)
    ksp(libs.hilt.compiler)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.bundles.mockito.test)
    testImplementation(libs.turbine)
    androidTestImplementation(libs.bundles.testing.android)
}
tasks.withType<Test> {
    configure<JacocoTaskExtension> {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
}