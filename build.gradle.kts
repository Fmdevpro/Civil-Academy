plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlinxSerialization) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.google.services) apply false
    id("jacoco")
}

val jacocoVersion = libs.versions.jacoco.get()

allprojects {
    plugins.withType<JacocoPlugin> {
        configure<JacocoPluginExtension> {
            toolVersion = jacocoVersion
        }
    }
}

subprojects {
    plugins.apply("jacoco")

    val excludes = listOf(
        "/R.class", "/R$.class", "/BuildConfig.",
        "/*\$ViewInjector*.*", "/*_Factory*.*", "**/*Dagger*.class"
    )

    // Android Module Jacoco report Task
    plugins.withId("com.android.library") {
        tasks.register<JacocoReport>("jacocoAndroidReport") {
            group = "Reporting"
            description = "Generates Jacoco coverage report for the ${project.name} (Android) module."

            dependsOn("testDebugUnitTest")

            reports {
                html.required.set(true)
                xml.required.set(true)
                html.outputLocation.set(file("${layout.buildDirectory.get()}/reports/jacoco/html"))
                xml.outputLocation.set(file("${layout.buildDirectory.get()}/reports/jacoco/jacocoTestReport.xml"))
            }

            val mainSrc = "${project.projectDir}/src/main/java"
            val mainSrcKotlin = "${project.projectDir}/src/main/kotlin"

            sourceDirectories.setFrom(files(listOf(mainSrc, mainSrcKotlin)))

            val debugTree = fileTree("${layout.buildDirectory.get()}/tmp/kotlin-classes/debug") {
                exclude(excludes)
            }

            classDirectories.setFrom(files(listOf(debugTree)))

            // Buscar el archivo .exec en ambas ubicaciones posibles
            executionData.setFrom(
                fileTree(layout.buildDirectory.get()) {
                    include(
                        "jacoco/testDebugUnitTest.exec",
                        "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec"
                    )
                }
            )
        }
    }

    // JVM module Jacoco report Task
    plugins.withId("org.jetbrains.kotlin.jvm") {
        tasks.register<JacocoReport>("jacocoJvmReport") {
            group = "Reporting"
            description = "Generates Jacoco coverage report for the ${project.name} (JVM) module."

            dependsOn("test")

            reports {
                html.required.set(true)
                xml.required.set(true)
                html.outputLocation.set(file("${layout.buildDirectory.get()}/reports/jacoco/html"))
                xml.outputLocation.set(file("${layout.buildDirectory.get()}/reports/jacoco/jacocoTestReport.xml"))

            }

            sourceDirectories.setFrom(files("src/main/java", "src/main/kotlin"))
            classDirectories.setFrom(
                fileTree("${layout.buildDirectory.get()}/classes/kotlin/main") { exclude(excludes) },
                fileTree("${layout.buildDirectory.get()}/classes/java/main") { exclude(excludes) }
            )
            executionData.setFrom(file("${layout.buildDirectory.get()}/jacoco/test.exec"))
        }
    }
}

// Root Jacoco report Task
tasks.register<JacocoReport>("jacocoRootReport") {
    group = "Reporting"
    description = "Generates combined Jacoco coverage report for all modules."

    val jacocoVersion = jacocoVersion

    val jacocoAntConfiguration = configurations.maybeCreate("jacocoAnt")
    dependencies {
        jacocoAntConfiguration("org.jacoco:org.jacoco.ant:$jacocoVersion")
    }

    jacocoClasspath = jacocoAntConfiguration

    val androidModules = listOf(":presentation", ":data")
    val jvmModules = listOf(":domain")

    dependsOn(androidModules.map { "$it:testDebugUnitTest" })
    dependsOn(jvmModules.map { "$it:test" })

    classDirectories.setFrom(
        files(
            androidModules.map {
                fileTree("$rootDir/${it.removePrefix(":")}/build/tmp/kotlin-classes/debug") {
                    exclude(
                        "**/R.class", "**/R$*.class", "**/BuildConfig.*",
                        "**/*\$ViewInjector*.*", "**/*_Factory*.*", "**/*Dagger*.class"
                    )
                }
            } + jvmModules.flatMap { module ->
                listOf(
                    fileTree("$rootDir/${module.removePrefix(":")}/build/classes/kotlin/main") {
                        exclude(
                            "**/R.class", "**/R$*.class", "**/BuildConfig.*",
                            "**/*\$ViewInjector*.*", "**/*_Factory*.*", "**/*Dagger*.class"
                        )
                    },
                    fileTree("$rootDir/${module.removePrefix(":")}/build/classes/java/main") {
                        exclude(
                            "**/R.class", "**/R$*.class", "**/BuildConfig.*",
                            "**/*\$ViewInjector*.*", "**/*_Factory*.*", "**/*Dagger*.class"
                        )
                    }
                )
            }
        )
    )

    sourceDirectories.setFrom(
        files(
            androidModules.flatMap { listOf(
                "$rootDir/${it.removePrefix(":")}/src/main/kotlin",
                "$rootDir/${it.removePrefix(":")}/src/main/java"
            ) } +
                    jvmModules.flatMap { listOf(
                        "$rootDir/${it.removePrefix(":")}/src/main/kotlin",
                        "$rootDir/${it.removePrefix(":")}/src/main/java"
                    ) }
        )
    )

    executionData.setFrom(
        files(
            androidModules.map {
                fileTree("$rootDir/${it.removePrefix(":")}/build/jacoco") {
                    include("testDebugUnitTest.exec")
                }
            } +
                    jvmModules.map {
                        fileTree("$rootDir/${it.removePrefix(":")}/build/jacoco") {
                            include("test.exec")
                        }
                    }
        ).filter { it.exists() }
    )

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
        html.outputLocation.set(file("${layout.buildDirectory.get()}/reports/jacocoRootReport/html"))
        xml.outputLocation.set(file("${layout.buildDirectory.get()}/reports/jacocoRootReport/report.xml"))
    }
}