import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
//    kotlin("plugin.serialization")

    kotlin("plugin.serialization") version "1.4.10"
    kotlin("jvm") version "1.4.10"
    id("application")
    id("com.github.johnrengelman.shadow") version "4.0.4"


}
group = "com.epam"
version = "1.0-SNAPSHOT"

application {
    mainClassName = "com.epam.kotlinapp.ApplicationKt"
}
tasks {
    build {
        dependsOn(shadowJar)
    }
}

repositories {
    mavenCentral()
    jcenter()
    maven {
        url = uri("https://jitpack.io")
    }
    maven {
        url = uri("https://dl.bintray.com/kotlin/ktor")
    }
    maven {
        url = uri("https://dl.bintray.com/kotlin/kotlinx")
    }
}



dependencies {
    val ktorVersion: String = "1.4.0"
    val h2Version: String = "1.4.200"
    val loggerVersion: String = "1.7.30"
    val immutableCollectionsVersion = "0.3.3"
    val serializationVersion = "0.20.0"
    val swaggerVersion = "0.5.0-drill.2"

    testImplementation(kotlin("test-junit"))
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-html-builder:$ktorVersion")
    //implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.2") //I don't think i need this
    implementation("com.h2database:h2:$h2Version")
    implementation("io.ktor:ktor-gson:$ktorVersion")
    implementation("org.slf4j:slf4j-simple:$loggerVersion")
    implementation("io.ktor:ktor-websockets:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    //implementation("com.github.papsign:Ktor-OpenAPI-Generator:-SNAPSHOT")
//    implementation("com.github.nielsfalk:ktor-swagger:v0.7.0")
    implementation("com.github.Drill4J:ktor-swagger:v0.5.0-drill.2")
    implementation("junit:junit:4.4")
    implementation("io.ktor:ktor-server-test-host:$ktorVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:$immutableCollectionsVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")

//    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:$serializationVersion")
//    implementation("org.jetbrains.kotlinx:kotlinx-serialization-cbor:$serializationVersion")
//    implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:$serializationVersion")

}
tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}