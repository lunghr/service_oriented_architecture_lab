plugins {
    id("java")
    id("war")
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("jakarta.ws.rs:jakarta.ws.rs-api:3.1.0")
    compileOnly("jakarta.json.bind:jakarta.json.bind-api:3.0.0")
    compileOnly("jakarta.json:jakarta.json-api:2.1.1")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

tasks.war {
    archiveFileName.set("jaxrs-service.war")
}