import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    // Java
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    
    // Serialization
    alias(libs.plugins.kotlinxSerialization)

    // Jacoco reports
    id("jacoco")
}

jacoco {
    toolVersion = libs.versions.jacoco.get()
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}

tasks.withType<Test>().configureEach {
    val byteBuddyAgentJar = classpath.filter { it.name.contains("byte-buddy-agent") }.firstOrNull()?.absolutePath
    if (byteBuddyAgentJar != null) {
        jvmArgs("-javaagent:$byteBuddyAgentJar")
    } else {
        logger.warn("Byte-buddy-agent JAR not found in classpath for test task $name in domain module (JVM). Mockito agent configuration might be incomplete.")
    }
    jacoco {
        setExcludes(listOf("jdk.internal.*", "kotlin.*"))
    }
}

dependencies {

    api(project(":shared"))

    // Hilt
    implementation(libs.javax.inject)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // Testing
    testRuntimeOnly(libs.byte.buddy.agent)
    testImplementation(libs.bundles.testing.domain)
    testImplementation(libs.bundles.mockito.test)
    testImplementation(kotlin("test"))
}