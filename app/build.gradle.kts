plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.android.campinglight"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.android.campinglight"
        minSdk = 32
        targetSdk = 34
        versionCode = 11
        versionName = "1.1"
    }

    signingConfigs {
        create("release") {
            storeFile = file("D:\\Android12SignerGUI\\SignFiles\\NewPublic\\platform.jks")
            keyAlias = "android"
            keyPassword = "android"
            storePassword = "android"
        }
    }
    buildTypes {
        debug {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
        }
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
}