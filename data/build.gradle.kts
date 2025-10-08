import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    // Android
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)

    // Hilt
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    
    // Serialization
    alias(libs.plugins.kotlinxSerialization)

    // Jacoco reports
    id("jacoco")
}


android {
    namespace = "com.fmdev.data"
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
    implementation(project(":domain"))
    implementation(project(":shared"))
    api(project(":androidshared"))

    // Core & Compose
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)

    // Google Sign In
    implementation(libs.androidx.google.signin)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    
    // DataStore
    implementation(libs.androidx.datastore)
    
    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // Testing
    testImplementation(libs.byte.buddy.agent)
    testImplementation(libs.bundles.testing.data)
    kspAndroidTest(libs.hilt.compiler)
    androidTestImplementation(project(":di"))
    androidTestImplementation(libs.bundles.testing.android)
}

tasks.withType<Test> {
    configure<JacocoTaskExtension> {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
}