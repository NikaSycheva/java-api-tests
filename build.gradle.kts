plugins {
    id("java")
    id("io.freefair.lombok") version "8.6"
    id("io.qameta.allure") version "2.11.2"
}

group = "otcudazvuk.qa"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("com.fasterxml.jackson.core:jackson-databind:2.17.0")
    testImplementation("org.aeonbits.owner:owner:1.0.4")
    testImplementation("commons-io:commons-io:2.15.1")
    testImplementation("io.rest-assured:rest-assured:5.3.0")
    testImplementation("io.qameta.allure:allure-rest-assured:2.20.1")
    testImplementation("com.github.javafaker:javafaker:1.0.2")
    testImplementation("org.assertj:assertj-core:3.25.3")
}

tasks.test {
    useJUnitPlatform()
}