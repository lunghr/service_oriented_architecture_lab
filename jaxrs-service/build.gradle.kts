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
    providedCompile("jakarta.platform:jakarta.jakartaee-api:10.0.0")
    compileOnly ("org.projectlombok:lombok:1.18.30")  // Compile-time only
    annotationProcessor ("org.projectlombok:lombok:1.18.30")
    implementation("org.slf4j:slf4j-api:2.0.7")
    implementation("io.github.cdimascio:dotenv-java:3.0.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.war {
    archiveFileName.set("jaxrs-service.war")
}