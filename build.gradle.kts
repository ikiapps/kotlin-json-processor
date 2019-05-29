plugins {
    id("org.jetbrains.kotlin.jvm").version("1.3.30")
    application
}

repositories {
    jcenter()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.2.1")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.3.0") // attempt to reduce build warnings
    implementation("com.beust:klaxon:5.0.5")
}

application {
    mainClassName = "com.ikiapps.kotlinJSONProcessor.AppKt"
}
