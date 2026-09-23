import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.kotlinSerialization)
    id("com.google.devtools.ksp") version "2.3.12"
}

kotlin {

    sourceSets.all {
        languageSettings.optIn("kotlinx.serialization.InternalSerializationApi")
    }

    android {
        namespace = "xyz.superbet.supercoctails.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
        withHostTest {}
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation("io.ktor:ktor-client-core:2.3.12")
            implementation("io.insert-koin:koin-core:3.5.6")
            implementation("io.ktor:ktor-client-content-negotiation:2.3.12")
            implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.12")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
            implementation("androidx.room:room-runtime:2.8.5")
            implementation("androidx.sqlite:sqlite-bundled:2.7.0")
        }
        androidMain.dependencies {
            implementation("io.ktor:ktor-client-android:2.3.12")
            implementation("io.insert-koin:koin-android:3.5.6")
            implementation("io.insert-koin:koin-androidx-compose:3.5.6")
            implementation("androidx.datastore:datastore-preferences:1.1.7")
            implementation("androidx.room:room-runtime:2.8.5")
            implementation("androidx.sqlite:sqlite-bundled:2.7.0")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
        }
    }
}

dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
}