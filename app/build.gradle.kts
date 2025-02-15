plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.java.liyao"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.java.liyao"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    buildFeatures {
        viewBinding = true
    }

    packagingOptions {
        exclude("META-INF/DEPENDENCIES")
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.activity)
    implementation(libs.swiperefreshlayout)
    testImplementation(libs.junit)
    // 为了方便我们使用……算了我也忘了叫什么了，反正原来是implementation("com.google.code.gson:gson:2.8.9")一类的
    implementation(libs.gson)
    implementation(libs.glide)
    implementation(libs.okhttp)
    implementation(libs.lombok)
    annotationProcessor(libs.lombok)
    implementation(libs.jackson.core)
    implementation(libs.jackson.databind)
    implementation(libs.jackson.annotations)
    implementation(libs.commons.codec)
    implementation(libs.glide.v4120)
    // implementation(libs.oapi.java.sdk)
    // testImplementation(platform(libs.junit.bom))
    // testImplementation(libs.junit.jupiter)
    annotationProcessor(libs.compiler)
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("cn.bigmodel.openapi:oapi-java-sdk:release-V4-2.0.2")
    implementation(kotlin("script-runtime", "1.4.32"))
    implementation(libs.core)
    implementation("io.noties.markwon:ext-strikethrough:4.6.2")
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}