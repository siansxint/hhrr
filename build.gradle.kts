plugins {
    java
    id("org.springframework.boot") version "3.3.3"
    id("io.spring.dependency-management") version "1.1.6"
    id("org.siouan.frontend-jdk17") version "8.1.0"
}

group = "me.siansxint.hhrr"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-web")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("org.thymeleaf:thymeleaf-spring6:3.1.2.RELEASE")
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")

    implementation("nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect:3.3.0")

    implementation("org.apache.poi:poi:5.3.0")
    implementation("org.apache.poi:poi-ooxml:5.3.0")


    runtimeOnly("com.h2database:h2")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
}

tasks {
    bootJar {
        dependsOn(publishFrontend)
    }

    processResources {
        exclude("**/*.html")
        exclude("**/*.js")
        exclude("**/*.svg")
        exclude("**/*.css")
    }
}

frontend {
    nodeVersion.set("20.17.0")
    installScript.set("install")
    assembleScript.set("run build")
    publishScript.set("run build-prod")
    verboseModeEnabled.set(true)
}