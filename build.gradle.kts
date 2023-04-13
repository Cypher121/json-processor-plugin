@file:Suppress("UnstableApiUsage")

plugins {
    `embedded-kotlin`
    id("com.gradle.plugin-publish") version "1.2.0"
}

group = "coffee.cypher"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    api("blue.endless:jankson:1.2.2")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

gradlePlugin {
    website.set("https://github.com/Cypher121/json-processor-plugin")
    vcsUrl.set("https://github.com/Cypher121/json-processor-plugin.git")

    plugins {
        create("jsonProcessor") {
            id = "coffee.cypher.json-processor"
            implementationClass = "coffee.cypher.json_processor.ProcessorPlugin"

            displayName = "JSON Processor Plugin"
            description = "Plugin providing JSON processing pipelines to Copy tasks and actions"

            tags.set("json,resources,processing,json5".split(','))
        }
    }
}

publishing {
    repositories {
        mavenLocal()
    }
}