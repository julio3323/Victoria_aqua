plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.app_aqua"

    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.app_aqua"

        minSdk = 27
        targetSdk = 36

        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android-optimize.txt"
                ),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {

    implementation("androidx.activity:activity-ktx:1.13.0")
    // Android Core
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.activity:activity:1.13.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")

    // Material Design 3
    implementation("com.google.android.material:material:1.14.0")

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.4.0")

    // ViewPager2 (carrossel da tela Início)
    implementation("androidx.viewpager2:viewpager2:1.1.0")

    // Swipe Refresh
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")

    // Glide
    implementation("com.github.bumptech.glide:glide:5.0.7")

    // Lottie
    implementation("com.airbnb.android:lottie:6.7.1")

    // Testes
    testImplementation("junit:junit:4.13.2")

    androidTestImplementation(
        "androidx.test.ext:junit:1.3.0"
    )

    androidTestImplementation(
        "androidx.test.espresso:espresso-core:3.7.0"
    )
}