plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.domain"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

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
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

// Все тесты модуля используют JUnit 5
tasks.withType<Test> {
    useJUnitPlatform()
    // Разрешение динамической загрузки Java-агентов (ByteBuddy и др.)
    jvmArgs("-XX:+EnableDynamicAgentLoading")
}

dependencies {

    // Базовые зависимости
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Testing
    // Модульные тесты (JVM)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.mockito.junit.jupiter)
    testImplementation(libs.kotlinx.coroutines.test)

    // Если domain использует Hilt только для аннотаций (без внедрения)
    compileOnly(libs.hilt.android)
}