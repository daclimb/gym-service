import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.7.4"
    id("io.spring.dependency-management") version "1.0.15.RELEASE"
    id("com.epages.restdocs-api-spec") version "0.16.0"

    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
    kotlin("plugin.jpa") version "1.6.21"
}

group = "app.daclimb"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("com.amazonaws:aws-java-sdk-s3:1.12.307")
    implementation("org.flywaydb:flyway-core")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.jsonwebtoken:jjwt:0.9.1")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.2")

    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("io.mockk:mockk:1.13.2")
    testImplementation("com.h2database:h2")
    testImplementation("org.testcontainers:localstack:1.17.3")
    testImplementation("org.testcontainers:junit-jupiter:1.17.3")
    testImplementation("com.epages:restdocs-api-spec-mockmvc:0.16.0") //2.2
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

openapi3 {
    title = "Gym service"
    description = "Gym service api document"
    version = "0.1.0"
    format = "yaml"

    copy {
        from("$buildDir/api-spec/openapi3.yaml") // 복제할 yaml 파일 타겟팅
        into("$projectDir/openapi") // 타겟 디렉토리로 파일 복제
    }
}