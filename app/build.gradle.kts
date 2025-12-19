plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.jhernandez.frontend.bazaar"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.jhernandez.frontend.bazaar"
        minSdk = 24
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation (libs.java.jwt)
    implementation (libs.imagepicker) // Recortar una imagen
    implementation (libs.github.glide) // Leer imágenes
    implementation (libs.lottie) // animaciones
    implementation (libs.stripe.android) // Pasarela de pago
    implementation (libs.lombok)
    annotationProcessor (libs.lombok)


}