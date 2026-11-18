// Fichero de configuración de nivel superior: opciones comunes a todos los módulos.

// AGP 9 trae Kotlin integrado (no hace falta el plugin org.jetbrains.kotlin.android).
// Aquí fijamos la versión de Kotlin (KGP) para que coincida con la del plugin de Compose.
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath(libs.kotlin.gradle.plugin)
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
}
