plugins {
    application
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation(libs.guava)
    implementation("com.google.code.gson:gson:2.10.1")

    implementation("org.openjfx:javafx-controls:21:win")
    implementation("org.openjfx:javafx-base:21:win")
    implementation("org.openjfx:javafx-graphics:21:win")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    mainClass = "org.example.App"
    applicationDefaultJvmArgs = listOf(
        "--add-modules", "javafx.controls,javafx.base,javafx.graphics"
    )
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}