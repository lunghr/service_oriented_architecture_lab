plugins {
    id("java")
    id("war")
    id("com.github.bjornvester.xjc") version "1.8.2" // Для генерации классов из XSD
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.ws:spring-ws-core:4.0.11")
    implementation("wsdl4j:wsdl4j:1.6.3")
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:4.0.2")
    implementation("org.glassfish.jaxb:jaxb-runtime:4.0.5")
    implementation(platform("org.springframework:spring-framework-bom:6.1.13"))
    implementation("org.springframework:spring-context")
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
    compileOnly("jakarta.servlet:jakarta.servlet-api:6.0.0")
    compileOnly("jakarta.annotation:jakarta.annotation-api:2.1.1")
}


tasks.test {
    useJUnitPlatform()
}

tasks.war {
    archiveFileName.set("hr-service.war")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
