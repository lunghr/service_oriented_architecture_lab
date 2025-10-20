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
    implementation(platform("org.springframework:spring-framework-bom:6.1.13"))
    implementation(platform("com.fasterxml.jackson:jackson-bom:2.17.2"))
    implementation("org.springframework:spring-webmvc")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    compileOnly("jakarta.servlet:jakarta.servlet-api:6.0.0")
    compileOnly("jakarta.validation:jakarta.validation-api:3.0.2")
    compileOnly("jakarta.annotation:jakarta.annotation-api:2.1.1")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.springframework:spring-test")
}

tasks.test {
    useJUnitPlatform()
}

tasks.war{
    archiveFileName.set("spring-service.war")
}