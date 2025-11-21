import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.time.LocalDate
import java.io.File

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.serializationPlugin)
    id("module.publication")
    //id("io.github.ttypic.swiftklib") version "0.6.3"
    alias(libs.plugins.vlc.setup)
}

kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser{
            testTask {
                useKarma{
                    useChrome()
                }
            }
        }
        binaries.executable()
    }

    js {
        browser()
        binaries.executable()
    }
    jvm()
    androidTarget {
        publishLibraryVariants("release")
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
        }
    }

    /*
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    linuxX64()
     */

    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.startup.runtime)
            implementation(libs.androidx.media3.exoplayer.hls)

            implementation(libs.androidx.media3.exoplayer)
            implementation(libs.androidx.media3.ui)
            implementation(libs.androidx.activity.compose)
            //implementation(libs.github.mirzemehdi.google)
            //implementation("com.google.devtools.ksp:symbol-processing-api:2.0.21-1.0.25")
        }
        val commonMain by getting {
            dependencies {
                implementation(compose.components.resources)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.coil.compose) // Check for latest version
                implementation(libs.coil.network.ktor) // For network images
                implementation(libs.ktor.client.core)
                implementation(libs.media.player.kmp)
                //api(libs.kmp.compose.webview)
                //put your multiplatform dependencies here
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.system.lambda)
            }
        }
        val wasmJsMain by getting {
            dependencies {
                //implementation(libs.ktor.client.core.wasmJs)

                implementation(npm("currency-formatter", "1.5.9"))
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(libs.ktor.client.js)
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.common)
                implementation(libs.vlcj)
                //implementation(compose.desktop.currentOs)
                implementation("org.jogamp.jogl:jogl-all:2.6.0")
                implementation("org.jogamp.gluegen:gluegen-rt:2.6.0")
                implementation(libs.compose.multiplatform.media.player)
                implementation(libs.ktor.client.okhttp)
                implementation(libs.ktor.client.java)
                implementation(libs.slf4j.simple)
            }
        }
    }
    /*
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {
        it.compilations {
            val main by getting {
                cinterops {
                    create("HelloSwift")
                    create("Utils")
                }
            }
        }
    }

     */
}

android {
    namespace = "id.dreamfighter.kmp.json.layout.compose"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}

vlcSetup {
    vlcVersion = "3.0.21"
    shouldCompressVlcFiles = true
    shouldIncludeAllVlcFiles = false
    pathToCopyVlcLinuxFilesTo = rootDir.resolve("vlc-libs/linux/")
    pathToCopyVlcMacosFilesTo = rootDir.resolve("vlc-libs/macos/")
    pathToCopyVlcWindowsFilesTo = rootDir.resolve("vlc-libs/windows/")
}

compose.desktop {
    application {
        jvmArgs += listOf(
            "-Dprism.verbose=true",
            "-Dprism.order=es2,sw"
        )
        nativeDistributions {
            appResourcesRootDir = rootDir.resolve("vlc-libs/")
        }
    }
}