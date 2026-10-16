# Gradle with Spring Boot - Complete Guide

A detailed reference for building Spring Boot applications with **Gradle**. It explains every common block and property of `build.gradle` (Groovy DSL) and `build.gradle.kts` (Kotlin DSL) with examples, the Spring Boot Gradle plugin and its tasks, `settings.gradle`, `gradle.properties`, version catalogs, and how to structure a **multi-module** Gradle project with Spring Boot.

[⬅ Back to Spring Boot Annotations Guide](java-spring-boot-document-with-full-details.md#table-of-contents)

> **Version note:** Examples target **Spring Boot 3.5.x** (Java 17+, the examples use Java 21) with **Gradle 8.14**. They also work on **Gradle 9.x**, which needs Java 17+ to run Gradle itself. Where **Spring Boot 4.x** differs, it is called out. Kotlin DSL examples assign lazy properties with `=` (for example `mainClass = "..."`), which needs Gradle 8.2+; on older Gradle use `.set(...)`.

> **Related guides:** [Java Spring Boot Annotations - Complete Guide](java-spring-boot-document-with-full-details.md) · [Spring Boot Configuration (@ConfigurationProperties)](java-spring-boot-document-with-full-details.md#3-spring-boot-configuration) · [Testing Annotations](java-spring-boot-document-with-full-details.md#16-testing) · [Lombok](java-spring-boot-document-with-full-details.md#17-lombok--commonly-used-with-spring-boot) · [Kotlin vs Java](kotlin-features-over-java.md)

## Table of Contents

- [1. Gradle Basics](#1-gradle-basics)
    - [1.1 What Gradle Is](#11-what-gradle-is)
    - [1.2 Files in a Gradle Project](#12-files-in-a-gradle-project)
    - [1.3 Groovy DSL vs Kotlin DSL](#13-groovy-dsl-vs-kotlin-dsl)
    - [1.4 Build Lifecycle](#14-build-lifecycle)
    - [1.5 Gradle Wrapper](#15-gradle-wrapper)
- [2. Complete Spring Boot Build File](#2-complete-spring-boot-build-file)
    - [2.1 Kotlin DSL (build.gradle.kts)](#21-kotlin-dsl-buildgradlekts)
    - [2.2 Groovy DSL (build.gradle)](#22-groovy-dsl-buildgradle)
- [3. build.gradle Properties Explained](#3-buildgradle-properties-explained)
    - [3.1 plugins](#31-plugins)
    - [3.2 Project Coordinates](#32-project-coordinates)
    - [3.3 java Extension and Toolchains](#33-java-extension-and-toolchains)
    - [3.4 repositories](#34-repositories)
    - [3.5 dependencies](#35-dependencies)
    - [3.6 Dependency Configurations (Scopes)](#36-dependency-configurations-scopes)
    - [3.7 Versions, Exclusions and Constraints](#37-versions-exclusions-and-constraints)
    - [3.8 Dependency Management and BOMs](#38-dependency-management-and-boms)
    - [3.9 configurations](#39-configurations)
    - [3.10 Extra Properties (ext / extra)](#310-extra-properties-ext--extra)
    - [3.11 tasks](#311-tasks)
    - [3.12 sourceSets](#312-sourcesets)
    - [3.13 Kotlin Projects](#313-kotlin-projects)
    - [3.14 publishing](#314-publishing)
    - [3.15 Other Common Plugins](#315-other-common-plugins)
- [4. Spring Boot Gradle Plugin](#4-spring-boot-gradle-plugin)
    - [4.1 What the Plugin Adds](#41-what-the-plugin-adds)
    - [4.2 springBoot Extension](#42-springboot-extension)
    - [4.3 bootJar and jar](#43-bootjar-and-jar)
    - [4.4 bootRun and bootTestRun](#44-bootrun-and-boottestrun)
    - [4.5 bootBuildImage](#45-bootbuildimage)
    - [4.6 Build Info](#46-build-info)
    - [4.7 Native Images and AOT](#47-native-images-and-aot)
    - [4.8 WAR Packaging](#48-war-packaging)
- [5. settings.gradle](#5-settingsgradle)
    - [5.1 rootProject.name and include](#51-rootprojectname-and-include)
    - [5.2 pluginManagement](#52-pluginmanagement)
    - [5.3 dependencyResolutionManagement](#53-dependencyresolutionmanagement)
    - [5.4 Settings Plugins and Feature Previews](#54-settings-plugins-and-feature-previews)
- [6. gradle.properties](#6-gradleproperties)
    - [6.1 Build Environment Properties](#61-build-environment-properties)
    - [6.2 Project Properties](#62-project-properties)
- [7. Version Catalog](#7-version-catalog)
    - [7.1 libs.versions.toml Format](#71-libsversionstoml-format)
    - [7.2 Using the Catalog](#72-using-the-catalog)
- [8. Multi-Module Projects](#8-multi-module-projects)
    - [8.1 Why Multi-Module](#81-why-multi-module)
    - [8.2 Project Structure](#82-project-structure)
    - [8.3 Root settings.gradle.kts](#83-root-settingsgradlekts)
    - [8.4 Ways to Share Build Configuration](#84-ways-to-share-build-configuration)
    - [8.5 Convention Plugins in build-logic](#85-convention-plugins-in-build-logic)
    - [8.6 Module Build Files](#86-module-build-files)
    - [8.7 api vs implementation Between Modules](#87-api-vs-implementation-between-modules)
    - [8.8 Spring Boot Wiring Across Modules](#88-spring-boot-wiring-across-modules)
    - [8.9 Platform Module (Internal BOM)](#89-platform-module-internal-bom)
    - [8.10 Sharing Test Code (Test Fixtures)](#810-sharing-test-code-test-fixtures)
    - [8.11 Running Tasks in a Multi-Module Build](#811-running-tasks-in-a-multi-module-build)
    - [8.12 Multiple Spring Boot Applications in One Build](#812-multiple-spring-boot-applications-in-one-build)
    - [8.13 Composite Builds](#813-composite-builds)
    - [8.14 Common Mistakes](#814-common-mistakes)
- [9. Gradle Commands Cheat Sheet](#9-gradle-commands-cheat-sheet)
- [10. Maven to Gradle Mapping](#10-maven-to-gradle-mapping)
- [11. Best Practices](#11-best-practices)
- [12. Resources](#12-resources)

---

# 1. Gradle Basics

## 1.1 What Gradle Is

Gradle is a **build automation tool**. It compiles the code, downloads dependencies from Maven repositories, runs the tests and packages the application (for Spring Boot: an executable jar). The build is described in a **script** written in Groovy or Kotlin instead of XML.

Core concepts:

| Concept | Meaning |
|---------|---------|
| **Build** | One run of Gradle. Contains one or more projects. |
| **Project** | One unit of the build, usually one directory with a `build.gradle(.kts)`. In a multi-module build every module is a project. |
| **Task** | One unit of work: `compileJava`, `test`, `bootJar`. Tasks depend on other tasks and form a graph. |
| **Plugin** | Adds tasks, configurations and conventions. The `java` plugin adds `compileJava`, `test`, `jar`; the Spring Boot plugin adds `bootJar`, `bootRun`. |
| **Dependency configuration** | A named group of dependencies with one purpose (`implementation`, `testImplementation`...). Similar to a Maven scope. |
| **Repository** | Where dependencies are downloaded from (`mavenCentral()`, a company Nexus...). |
| **Wrapper** | A script (`gradlew`) that downloads and runs the exact Gradle version the project needs. |

Gradle compared with Maven:

| | Gradle | Maven |
|---|--------|-------|
| Build file | `build.gradle` (Groovy) / `build.gradle.kts` (Kotlin) | `pom.xml` (XML) |
| Style | Code: conditions, loops, custom tasks | Declarative XML; logic lives in plugins |
| Speed | Incremental builds, build cache, daemon, parallel execution | Rebuilds more; slower on large builds |
| Version conflicts | **Highest version wins** | **Nearest declaration wins** |
| Multi-module | `include(...)` in `settings.gradle(.kts)` | `<modules>` in the parent POM |
| Spring Boot support | Spring Boot Gradle plugin | Spring Boot Maven plugin |

[⬆ Back to Table of Contents](#table-of-contents)

---

## 1.2 Files in a Gradle Project

A single-module Spring Boot project generated by [Spring Initializr](https://start.spring.io) looks like this:

```
demo/
├── build.gradle.kts               ← build script: plugins, dependencies, tasks
├── settings.gradle.kts            ← build name + list of modules (evaluated first)
├── gradle.properties              ← (optional) Gradle settings + custom properties
├── gradle/
│   ├── libs.versions.toml         ← (optional) version catalog
│   └── wrapper/
│       ├── gradle-wrapper.jar     ← small program that downloads Gradle
│       └── gradle-wrapper.properties  ← Gradle version to use
├── gradlew                        ← wrapper script (Linux / macOS)
├── gradlew.bat                    ← wrapper script (Windows)
├── src/
│   ├── main/java/com/example/demo/DemoApplication.java
│   ├── main/resources/application.yml
│   └── test/java/com/example/demo/DemoApplicationTests.java
└── build/                         ← output: classes, jars, reports (not committed)
```

| File / directory | Purpose | Commit to Git? |
|------------------|---------|----------------|
| `settings.gradle(.kts)` | Name of the build and the list of modules (projects). Read first. | Yes |
| `build.gradle(.kts)` | Build script of one project: plugins, dependencies, tasks. | Yes |
| `gradle.properties` | Settings for Gradle itself (memory, caching) and custom properties. | Yes (no secrets) |
| `gradle/libs.versions.toml` | Version catalog: dependency and plugin versions in one place. | Yes |
| `gradle/wrapper/*` | Wrapper jar and the Gradle version to download. | Yes |
| `gradlew`, `gradlew.bat` | Wrapper scripts. Always run the build through them. | Yes |
| `build/` | Compiled classes, jars (`build/libs`), test reports (`build/reports`). | No |
| `.gradle/` | Project-level Gradle cache. | No |
| `~/.gradle/` (`GRADLE_USER_HOME`) | Downloaded dependencies and Gradle versions, user-wide `gradle.properties` (credentials). | Not in the project |

Default source layout added by the `java` plugin:

| Directory | Contents |
|-----------|----------|
| `src/main/java` | Application code |
| `src/main/resources` | `application.yml`, `static/`, `templates/`, `db/migration/` |
| `src/test/java` | Tests |
| `src/test/resources` | Test configuration, e.g. `application-test.yml` |

`.gitignore` entries for Gradle:

```gitignore
.gradle/
build/
!gradle/wrapper/gradle-wrapper.jar
```

[⬆ Back to Table of Contents](#table-of-contents)

---

## 1.3 Groovy DSL vs Kotlin DSL

The same build can be written in two languages. Gradle picks the language from the file name: `build.gradle` is **Groovy**, `build.gradle.kts` is **Kotlin**. Spring Initializr offers both ("Gradle - Groovy" and "Gradle - Kotlin"); `gradle init` uses Kotlin by default since Gradle 8.2.

| Topic | Groovy (`build.gradle`) | Kotlin (`build.gradle.kts`) |
|-------|-------------------------|-----------------------------|
| Strings | `'single'` or `"double"` quotes | `"double"` quotes only |
| Method calls | Parentheses optional: `implementation 'g:a'` | Parentheses required: `implementation("g:a")` |
| Core plugin | `id 'java'` | `java` or `id("java")` |
| Assignment | `version = '1.0'` | `version = "1.0"` |
| String templates | `"${version}"` (double quotes only) | `"$version"` / `"${project.version}"` |
| Lists / maps | `['a', 'b']` / `[key: 'v']` | `listOf("a", "b")` / `mapOf("key" to "v")` |
| Extra properties | `ext.foo = 'x'` | `extra["foo"] = "x"` |
| Configure a task | `tasks.named('test') { }` or `test { }` | `tasks.test { }` or `tasks.named<Test>("test") { }` |
| Tasks of a type | `tasks.withType(JavaCompile).configureEach { }` | `tasks.withType<JavaCompile>().configureEach { }` |
| Variables | `def x = 1` | `val x = 1` |
| IDE support | Limited auto-completion | Full auto-completion and compile-time errors |

The same dependency block in both:

```groovy
// build.gradle (Groovy)
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
```

```kotlin
// build.gradle.kts (Kotlin)
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
```

**Which one to choose:** Kotlin DSL for new projects (type-safe, IDE auto-completion, errors shown while typing). Groovy DSL is still fully supported and common in older projects. The first configuration of a Kotlin build is a little slower because the scripts are compiled.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 1.4 Build Lifecycle

Every Gradle build runs in **three phases**:

```
1. Initialization   settings.gradle(.kts) is evaluated
                    → which projects (modules) are part of the build?

2. Configuration    build.gradle(.kts) of every project is executed
                    → plugins applied, tasks created and configured, task graph built

3. Execution        only the tasks needed for the requested task(s) run
                    → ./gradlew build runs compileJava, test, bootJar, ...
```

Code placed directly in a build script runs in the **configuration** phase, on **every** build, even `./gradlew help`. Work that should happen only when a task runs belongs in `doFirst { }` / `doLast { }`:

```kotlin
println("Configuration phase: printed on every build")

tasks.register("hello") {
    println("Configuring hello: printed only when 'hello' is part of the task graph")
    doLast {
        println("Execution phase: printed when 'hello' runs")
    }
}
```

Use `tasks.register(...)` (lazy: the task is created only if needed), not the old `tasks.create(...)` (eager).

**Task graph of a Spring Boot project** (`java` + Spring Boot plugins):

```
compileJava ──┐
              ├──► classes ──┬──► jar (plain jar)  ──┐
processResources┘            └──► bootJar (fat jar) ──┴──► assemble ──┐
                                                                      ├──► build
compileTestJava ──┐                                                   │
                  ├──► testClasses ──► test ──► check ────────────────┘
processTestResources┘
```

| Lifecycle task | What it does |
|----------------|--------------|
| `classes` | Compiles main code and copies resources. |
| `assemble` | Builds the artifacts (`jar`, `bootJar`) without running tests. |
| `check` | Runs all verification tasks (`test`, plus `jacocoTestCoverageVerification`, Checkstyle... when configured). |
| `build` | `assemble` + `check`. |
| `clean` | Deletes the `build/` directory. |

[⬆ Back to Table of Contents](#table-of-contents)

---

## 1.5 Gradle Wrapper

The wrapper makes everyone (developers and CI) use the **same Gradle version** without installing Gradle. Always run `./gradlew` (Linux/macOS) or `gradlew.bat` / `.\gradlew` (Windows) instead of a locally installed `gradle`.

`gradle/wrapper/gradle-wrapper.properties`:

```properties
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.14.3-bin.zip
networkTimeout=10000
validateDistributionUrl=true
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
```

| Property | Description |
|----------|-------------|
| `distributionUrl` | Gradle distribution to download. `-bin` = runtime only; `-all` = also sources and docs (better IDE help for Groovy scripts). A company mirror URL can be used here. |
| `distributionSha256Sum` | (optional) Checksum of the zip; the wrapper refuses a download that doesn't match. Get the value from gradle.org. |
| `distributionBase` / `distributionPath` | Where the unpacked distribution is stored (default `~/.gradle/wrapper/dists`). |
| `zipStoreBase` / `zipStorePath` | Where the downloaded zip is stored. |
| `networkTimeout` | Download timeout in milliseconds. |
| `validateDistributionUrl` | Check the URL is reachable before downloading. |

Upgrade the wrapper:

```bash
./gradlew wrapper --gradle-version 8.14.3 --distribution-type bin
./gradlew wrapper          # run once more so the new version regenerates gradlew scripts and the jar
./gradlew --version        # verify
```

Create a wrapper in a project that has none (needs Gradle installed once): `gradle wrapper`.

[⬆ Back to Table of Contents](#table-of-contents)

---

# 2. Complete Spring Boot Build File

A realistic build file for a REST + JPA service that uses Lombok, MapStruct, Spring Cloud OpenFeign, PostgreSQL, Testcontainers and JaCoCo. The numbers point to the section that explains each block.

## 2.1 Kotlin DSL (build.gradle.kts)

```kotlin
// ① plugins → 3.1
plugins {
    java
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.7"
    jacoco
}

// ② project coordinates → 3.2
group = "com.example"
version = "0.0.1-SNAPSHOT"
description = "Order service"

// ③ Java version → 3.3
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

// ④ make annotation processors (Lombok) visible at compile time → 3.9
configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

// ⑤ where dependencies are downloaded from → 3.4
repositories {
    mavenCentral()
}

// ⑥ extra properties → 3.10
extra["springCloudVersion"] = "2025.0.0"
val mapstructVersion = "1.6.3"

// ⑦ dependencies → 3.5, 3.6
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("org.mapstruct:mapstruct:$mapstructVersion")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    runtimeOnly("org.postgresql:postgresql")
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// ⑧ BOMs that Spring Boot doesn't import itself → 3.8
dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

// ⑨ Spring Boot plugin settings → 4.2, 4.6
springBoot {
    mainClass = "com.example.order.OrderApplication"
    buildInfo()
}

// ⑩ task configuration → 3.11, 4.3
tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-Amapstruct.defaultComponentModel=spring")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required = true
        html.required = true
    }
}

tasks.bootJar {
    archiveFileName = "order-service.jar"
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

## 2.2 Groovy DSL (build.gradle)

The same build in Groovy:

```groovy
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.5.6'
    id 'io.spring.dependency-management' version '1.1.7'
    id 'jacoco'
}

group = 'com.example'
version = '0.0.1-SNAPSHOT'
description = 'Order service'

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom annotationProcessor
    }
}

repositories {
    mavenCentral()
}

ext {
    set('springCloudVersion', '2025.0.0')
    mapstructVersion = '1.6.3'
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    implementation 'org.springframework.cloud:spring-cloud-starter-openfeign'
    implementation "org.mapstruct:mapstruct:${mapstructVersion}"

    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
    annotationProcessor "org.mapstruct:mapstruct-processor:${mapstructVersion}"
    annotationProcessor 'org.projectlombok:lombok-mapstruct-binding:0.2.0'
    annotationProcessor 'org.springframework.boot:spring-boot-configuration-processor'

    runtimeOnly 'org.postgresql:postgresql'
    developmentOnly 'org.springframework.boot:spring-boot-devtools'

    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.springframework.boot:spring-boot-testcontainers'
    testImplementation 'org.testcontainers:junit-jupiter'
    testImplementation 'org.testcontainers:postgresql'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
}

dependencyManagement {
    imports {
        mavenBom "org.springframework.cloud:spring-cloud-dependencies:${springCloudVersion}"
    }
}

springBoot {
    mainClass = 'com.example.order.OrderApplication'
    buildInfo()
}

tasks.withType(JavaCompile).configureEach {
    options.encoding = 'UTF-8'
    options.compilerArgs << '-Amapstruct.defaultComponentModel=spring'
}

tasks.named('test') {
    useJUnitPlatform()
    finalizedBy jacocoTestReport
}

jacocoTestReport {
    dependsOn test
    reports {
        xml.required = true
        html.required = true
    }
}

tasks.named('bootJar') {
    archiveFileName = 'order-service.jar'
}
```

Build and run it:

```bash
./gradlew build                      # compile, test, coverage report, build/libs/order-service.jar
java -jar build/libs/order-service.jar
./gradlew bootRun                    # run from sources (with devtools)
```

[⬆ Back to Table of Contents](#table-of-contents)

---

# 3. build.gradle Properties Explained

Each subsection explains one block of the build script: what it does, every common property, examples in Kotlin DSL (Groovy where it differs), and common mistakes.

## 3.1 plugins

**Applies to:** top of `build.gradle(.kts)`; must be the first block (only `buildscript { }` may come before it).

#### What it does
Applies plugins to the project. A plugin adds tasks (`bootJar`), extensions (`springBoot { }`), dependency configurations (`developmentOnly`) and conventions (source layout).

#### Properties

| Property | Description |
|----------|-------------|
| `id("plugin.id")` | Plugin ID. Core Gradle plugins (`java`, `java-library`, `application`, `war`, `jacoco`, `maven-publish`, `java-platform`, `java-test-fixtures`) need no version. |
| `version "x.y.z"` | Required for plugins from the Gradle Plugin Portal (unless the version is set in `settings.gradle` `pluginManagement` or comes from an included build). |
| `apply false` | Put the plugin on the build classpath but don't apply it to this project. Used in the root project of a multi-module build. |
| `alias(libs.plugins.x)` | Plugin ID and version from the [version catalog](#7-version-catalog). |
| `kotlin("jvm")` | Kotlin DSL shortcut for `id("org.jetbrains.kotlin.jvm")`. |
| `` `java-library` `` | Kotlin DSL accessor for core plugins with a dash (backticks); `java`, `jacoco`, `war`, `application` need no backticks. |

#### Example

```kotlin
plugins {
    java                                                   // core plugin, no version
    id("org.springframework.boot") version "3.5.6"         // Gradle Plugin Portal: version required
    id("io.spring.dependency-management") version "1.1.7"
    id("org.graalvm.buildtools.native") version "0.10.6" apply false   // on classpath, not applied
}
```

```groovy
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.5.6'
    id 'io.spring.dependency-management' version '1.1.7'
}
```

Plugins used with Spring Boot:

| Plugin ID | Purpose |
|-----------|---------|
| `java` | Compile, test and package Java code. |
| `java-library` | `java` + the `api` configuration. Use it for library modules. |
| `org.springframework.boot` | `bootJar`, `bootRun`, `bootBuildImage`... ([section 4](#4-spring-boot-gradle-plugin)). |
| `io.spring.dependency-management` | Maven-style dependency management; imports the Spring Boot BOM automatically. |
| `org.jetbrains.kotlin.jvm`, `.plugin.spring`, `.plugin.jpa` | Kotlin projects ([3.13](#313-kotlin-projects)). |
| `jacoco` | Code coverage. |
| `maven-publish` | Publish jars to a Maven repository ([3.14](#314-publishing)). |
| `org.graalvm.buildtools.native` | GraalVM native images ([4.7](#47-native-images-and-aot)). |
| `war` | WAR packaging ([4.8](#48-war-packaging)). |

#### Legacy syntax (avoid in new builds)

```groovy
buildscript {
    repositories { mavenCentral() }
    dependencies { classpath 'org.springframework.boot:spring-boot-gradle-plugin:3.5.6' }
}
apply plugin: 'java'
apply plugin: 'org.springframework.boot'
```

#### Common mistakes
- Giving a version to a core plugin (`id("java") version "..."`) fails: core plugins are versioned with Gradle.
- The `plugins { }` block is restricted: it can't use variables declared in the script. Keep versions in `settings.gradle(.kts)` or the version catalog.
- Applying `org.springframework.boot` to a **library** module creates a `bootJar` task that fails because there is no main class ([8.8](#88-spring-boot-wiring-across-modules)).

[⬆ Back to Table of Contents](#table-of-contents)

---

## 3.2 Project Coordinates

#### What they do
Identify the artifact (like Maven's `groupId:artifactId:version`) and give the jar its name.

#### Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `group` | `Object` | `""` (root) / parent path (subprojects) | Maven `groupId` used when publishing, e.g. `com.example`. |
| `version` | `Object` | `"unspecified"` | Artifact version. Becomes part of the jar name: `order-service-0.0.1-SNAPSHOT.jar`. |
| `description` | `String` | `null` | Shown by `./gradlew projects` and written to published POMs. |
| `name` | `String` | directory name | **Read-only** in the build script. Set it with `rootProject.name` in `settings.gradle(.kts)`. Maven `artifactId` when publishing. |
| `base { archivesName }` | `Property<String>` | `project.name` | Base name of the jar files. |

#### Example

```kotlin
group = "com.example"
version = "1.2.0"
description = "Order service"

base {
    archivesName = "order-api"      // build/libs/order-api-1.2.0.jar
}
```

`version` and `group` can also live in `gradle.properties` (`version=1.2.0`), which makes it easy for CI to override them: `./gradlew build -Pversion=1.2.1`.

#### Common mistakes
- Gradle 9 removed the old `archivesBaseName` property; use `base { archivesName = "..." }`.
- `version` is shown as `unspecified` in the jar name when it isn't set.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 3.3 java Extension and Toolchains

**Added by:** the `java` / `java-library` plugin.

#### What it does
Configures the Java version used to compile, test and run the code, and extra jars to produce.

#### Properties

| Property / method | Type | Default | Description |
|-------------------|------|---------|-------------|
| `toolchain.languageVersion` | `Property<JavaLanguageVersion>` | JDK that runs Gradle | Java version for `compileJava`, `test`, `bootRun`. Gradle finds a matching installed JDK (or downloads one, see [5.4](#54-settings-plugins-and-feature-previews)). |
| `toolchain.vendor` | `Property<JvmVendorSpec>` | any vendor | Restrict the vendor: `JvmVendorSpec.ADOPTIUM`, `AMAZON`, `AZUL`, `BELLSOFT`, `GRAAL_VM`, `MICROSOFT`, `ORACLE`... |
| `toolchain.implementation` | `Property<JvmImplementation>` | `VENDOR_SPECIFIC` | `JvmImplementation.J9` to request an OpenJ9 JVM. |
| `sourceCompatibility` | `JavaVersion` | toolchain version | Language level accepted by the compiler. Alternative to toolchains. |
| `targetCompatibility` | `JavaVersion` | `sourceCompatibility` | Bytecode version produced. |
| `withSourcesJar()` | method | — | Also build `-sources.jar` (published with `maven-publish`). |
| `withJavadocJar()` | method | — | Also build `-javadoc.jar`. |

#### Example

```kotlin
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
        vendor = JvmVendorSpec.ADOPTIUM          // optional
    }
    withSourcesJar()
}
```

Without toolchains (uses the JDK that runs Gradle):

```kotlin
java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}
```

Compile for an older release with a newer JDK (`javac --release`):

```kotlin
tasks.withType<JavaCompile>().configureEach {
    options.release = 17
}
```

**Toolchain vs `sourceCompatibility`:** a toolchain fixes the JDK itself, so the build behaves the same on every machine, regardless of `JAVA_HOME`. `sourceCompatibility` only sets compiler flags on whatever JDK runs Gradle. Prefer toolchains.

Show the JDKs Gradle can see: `./gradlew -q javaToolchains`.

#### Common mistakes
- Setting `sourceCompatibility` at the top level of the script (`sourceCompatibility = '21'`) instead of inside `java { }`. Gradle 9 removed that top-level form.
- Spring Boot 3.x and 4.x need **Java 17+**; a toolchain of 11 fails with `class file has wrong version 61.0`.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 3.4 repositories

#### What it does
Lists the repositories where Gradle looks for dependencies, **in the declared order**.

#### Repository types

| Method | Description |
|--------|-------------|
| `mavenCentral()` | Maven Central. Spring Boot and most Java libraries are here. |
| `google()` | Google's Maven repository (Android, some Google libraries). |
| `gradlePluginPortal()` | Gradle Plugin Portal. Normally used only in `pluginManagement` for plugins. |
| `mavenLocal()` | `~/.m2/repository`. Avoid in normal builds: it is slow, can contain half-installed artifacts and makes builds machine-dependent. Use only to test a library you `mvn install`-ed locally. |
| `maven { url = uri("...") }` | Any Maven repository: Nexus, Artifactory, GitHub Packages, Spring milestones. |
| `flatDir { dirs("libs") }` | Folder with plain jars (no metadata). Prefer `implementation(files("libs/x.jar"))`. |

#### `maven { }` properties

| Property | Description |
|----------|-------------|
| `name` | Name of the repository; also used to find credentials (`<name>Username` / `<name>Password`). |
| `url` | Repository URL (`uri("https://...")`). |
| `credentials { username = ...; password = ... }` | Basic-auth credentials. |
| `credentials(PasswordCredentials::class)` | Read credentials from the Gradle properties `<name>Username` and `<name>Password`. |
| `content { includeGroup("com.mycompany") }` | Only look for matching artifacts in this repository (faster, safer). |
| `mavenContent { releasesOnly() }` / `snapshotsOnly()` | Only releases or only snapshots. |

#### Example

```kotlin
repositories {
    mavenCentral()

    maven {
        name = "companyNexus"
        url = uri("https://nexus.mycompany.com/repository/maven-public/")
        credentials(PasswordCredentials::class)       // companyNexusUsername / companyNexusPassword
        content {
            includeGroupByRegex("com\\.mycompany.*")   // only company artifacts come from here
        }
    }

    maven { url = uri("https://repo.spring.io/milestone") }   // Spring milestone versions (M1, RC1)
}
```

Credentials in `~/.gradle/gradle.properties` (never in the project):

```properties
companyNexusUsername=deploy-user
companyNexusPassword=secret
```

or as environment variables in CI: `ORG_GRADLE_PROJECT_companyNexusUsername`, `ORG_GRADLE_PROJECT_companyNexusPassword`.

#### Common mistakes
- Hard-coding passwords in `build.gradle(.kts)`: they end up in Git.
- In a multi-module build, repeating `repositories { }` in every module. Declare them once in `settings.gradle(.kts)` ([5.3](#53-dependencyresolutionmanagement)).

[⬆ Back to Table of Contents](#table-of-contents)

---

## 3.5 dependencies

#### What it does
Declares the libraries the project needs and in which **configuration** (scope) they are used ([3.6](#36-dependency-configurations-scopes)).

#### Dependency notations

| Notation | Example | Description |
|----------|---------|-------------|
| `"group:name:version"` | `"org.mapstruct:mapstruct:1.6.3"` | Full coordinates. |
| `"group:name"` | `"org.springframework.boot:spring-boot-starter-web"` | Version comes from a BOM (Spring Boot manages hundreds of libraries). |
| `"group:name:version:classifier"` | `"com.example:native-lib:1.0.0:linux-x86_64"` | Artifact with a classifier (platform-specific jars, `-tests` jars). |
| `project(":module")` | `implementation(project(":common"))` | Another module of the same build. |
| `projects.module` | `implementation(projects.common)` | Type-safe project accessor ([5.4](#54-settings-plugins-and-feature-previews)). |
| `platform("g:n:v")` | `implementation(platform("org.springframework.cloud:spring-cloud-dependencies:2025.0.0"))` | Import a BOM ([3.8](#38-dependency-management-and-boms)). |
| `files(...)` / `fileTree(...)` | `implementation(files("libs/legacy.jar"))` | Local jar files. |
| `testFixtures(project(":x"))` | `testImplementation(testFixtures(project(":common")))` | Test fixtures of another module ([8.10](#810-sharing-test-code-test-fixtures)). |
| `libs.alias` | `implementation(libs.spring.boot.starter.web)` | Entry of the version catalog ([7](#7-version-catalog)). |

Groovy also has a map notation: `implementation group: 'org.mapstruct', name: 'mapstruct', version: '1.6.3'`.

#### Example

```kotlin
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.9")   // not managed by Boot → version needed
    implementation(project(":common"))
    implementation(files("libs/legacy-client.jar"))
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
```

#### Common Spring Boot dependencies

| Dependency | What it brings |
|------------|----------------|
| `spring-boot-starter-web` | Spring MVC, embedded Tomcat, Jackson |
| `spring-boot-starter-webflux` | Spring WebFlux, Reactor Netty |
| `spring-boot-starter-data-jpa` | Hibernate, Spring Data JPA, HikariCP |
| `spring-boot-starter-data-mongodb` / `-data-redis` | Spring Data MongoDB / Redis |
| `spring-boot-starter-validation` | Hibernate Validator (`jakarta.validation`) |
| `spring-boot-starter-security` | Spring Security |
| `spring-boot-starter-oauth2-resource-server` | JWT / opaque-token resource server |
| `spring-boot-starter-actuator` | Health, metrics, info endpoints |
| `spring-boot-starter-cache` | Spring cache abstraction |
| `spring-boot-starter-amqp` | RabbitMQ |
| `org.springframework.kafka:spring-kafka` | Kafka (not a starter, version managed by Boot) |
| `spring-boot-starter-graphql` | Spring for GraphQL |
| `spring-boot-starter-test` | JUnit 5, Mockito, AssertJ, Spring Test |
| `spring-boot-testcontainers` | `@ServiceConnection` for Testcontainers |
| `spring-boot-devtools` | Automatic restart, LiveReload (`developmentOnly`) |
| `spring-boot-docker-compose` | Starts `compose.yaml` services on `bootRun` (`developmentOnly`) |
| `spring-boot-configuration-processor` | Metadata for `@ConfigurationProperties` auto-completion (`annotationProcessor`) |

All `org.springframework.boot` artifacts above use the group `org.springframework.boot`.

> **Spring Boot 4.x:** Boot 4 splits its code into smaller modules and renames some starters (for example `spring-boot-starter-webmvc` for Spring MVC). The old names keep working but are deprecated. The Gradle setup itself doesn't change.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 3.6 Dependency Configurations (Scopes)

#### What they are
A configuration says **where** a dependency is used: at compile time, at runtime, in tests, as an annotation processor, or only during development.

#### Configurations

| Configuration | Compile classpath | Runtime classpath | Visible to consumers | Maven equivalent | Typical use |
|---------------|:-----------------:|:-----------------:|:--------------------:|------------------|-------------|
| `implementation` | ✔ | ✔ | runtime only | `compile` | Almost every dependency. |
| `api` *(java-library)* | ✔ | ✔ | compile + runtime | `compile` | Types that appear in a library module's public methods ([8.7](#87-api-vs-implementation-between-modules)). |
| `compileOnly` | ✔ | ✘ | ✘ | `provided` / `optional` | Lombok annotations, compile-time-only APIs. |
| `compileOnlyApi` *(java-library)* | ✔ | ✘ | compile only | — | Annotations needed by consumers at compile time. |
| `runtimeOnly` | ✘ | ✔ | runtime | `runtime` | JDBC drivers, logging backends. |
| `annotationProcessor` | processor path | ✘ | ✘ | `<annotationProcessorPaths>` | Lombok, MapStruct processor, `spring-boot-configuration-processor`. |
| `testImplementation` | tests | tests | ✘ | `test` | `spring-boot-starter-test`, Testcontainers. |
| `testCompileOnly` | tests | ✘ | ✘ | — | Lombok in tests. |
| `testRuntimeOnly` | ✘ | tests | ✘ | `test` | `junit-platform-launcher`, H2 for tests. |
| `testAnnotationProcessor` | test processor path | ✘ | ✘ | — | Lombok in tests. |
| `developmentOnly` *(Boot plugin)* | ✘ | `bootRun` only | ✘ | `optional` + plugin config | `spring-boot-devtools`, `spring-boot-docker-compose`. Never packaged in `bootJar`. |
| `testAndDevelopmentOnly` *(Boot 3.2+)* | tests | tests + `bootRun` | ✘ | — | Dependencies for tests and local runs but not production. |
| `providedRuntime` *(war + Boot)* | ✘ | ✔ | ✘ | `provided` | Embedded Tomcat in a deployable WAR ([4.8](#48-war-packaging)). |

How the classpaths are built:

```
compileClasspath      = compileOnly + implementation (+ api)
runtimeClasspath      = runtimeOnly + implementation (+ api)
annotationProcessor   = separate path, only used by javac to run processors
testCompileClasspath  = testCompileOnly + testImplementation + implementation (+ api)
testRuntimeClasspath  = testRuntimeOnly + testImplementation + runtimeOnly + implementation (+ api)
bootJar (BOOT-INF/lib) = runtimeClasspath (without developmentOnly)
```

#### Lombok: why two configurations?
Gradle keeps the **annotation processor path** separate from the compile classpath. Lombok's annotations (`@Getter`) must be on the compile classpath (`compileOnly`) and the processor that generates the code must be on the processor path (`annotationProcessor`):

```kotlin
dependencies {
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    testCompileOnly("org.projectlombok:lombok")          // only if tests use Lombok
    testAnnotationProcessor("org.projectlombok:lombok")
}
```

Spring Initializr adds `configurations { compileOnly { extendsFrom(configurations.annotationProcessor.get()) } }`, which makes everything in `annotationProcessor` also visible on the compile classpath.

#### Why `junit-platform-launcher`?
Gradle 9 no longer adds the JUnit Platform launcher to the test runtime automatically (Gradle 8 deprecated this). Declare it with `testRuntimeOnly("org.junit.platform:junit-platform-launcher")`; the version comes from the Spring Boot BOM.

#### Common mistakes
- Lombok only in `compileOnly`: compiles but getters don't exist → `cannot find symbol: method getName()`.
- JDBC driver in `implementation` instead of `runtimeOnly`: works, but code can accidentally import driver classes.
- `spring-boot-devtools` in `implementation`: it ends up in the production jar. Use `developmentOnly`.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 3.7 Versions, Exclusions and Constraints

#### How Gradle picks a version
When two dependencies need different versions of the same library, Gradle picks the **highest** version (Maven picks the one **nearest** to the project). See why a version was chosen:

```bash
./gradlew dependencies --configuration runtimeClasspath
./gradlew dependencyInsight --dependency jackson-databind --configuration runtimeClasspath
```

#### Rich versions

```kotlin
dependencies {
    implementation("com.google.guava:guava") {
        version {
            strictly("33.4.0-jre")    // exactly this version; fails if something needs another one
            // require("33.0.0-jre")  // at least this version (same as a normal version)
            // prefer("33.4.0-jre")   // use this unless something needs a different one
            // reject("32.0.0-jre")   // never use this version
        }
    }
}
```

Dynamic versions (`"1.+"`, `"[1.0, 2.0)"`, `"latest.release"`) make builds non-reproducible; avoid them.

#### Exclusions

Exclude a transitive dependency from one dependency, e.g. swap Tomcat for Jetty:

```kotlin
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web") {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
    }
    implementation("org.springframework.boot:spring-boot-starter-jetty")
}
```

```groovy
dependencies {
    implementation('org.springframework.boot:spring-boot-starter-web') {
        exclude group: 'org.springframework.boot', module: 'spring-boot-starter-tomcat'
    }
    implementation 'org.springframework.boot:spring-boot-starter-jetty'
}
```

Exclude from **all** configurations, e.g. switch Logback to Log4j2:

```kotlin
configurations.configureEach {
    exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
}
```

Turn off all transitive dependencies of one dependency: `implementation("g:a:v") { isTransitive = false }` (Groovy: `transitive = false`).

#### Constraints
A constraint sets the version of a dependency **if** it appears in the graph, without adding it:

```kotlin
dependencies {
    constraints {
        implementation("org.yaml:snakeyaml:2.4") {
            because("upgrade for a security fix")
        }
    }
}
```

#### Resolution strategy

```kotlin
configurations.configureEach {
    resolutionStrategy {
        failOnVersionConflict()                        // fail instead of silently picking the highest
        force("commons-io:commons-io:2.18.0")          // force a version (prefer strictly/constraints)
        cacheChangingModulesFor(0, "seconds")          // always re-check SNAPSHOT dependencies
    }
}
```

#### Common mistakes
- With the `io.spring.dependency-management` plugin, versions from the Spring Boot BOM **override** constraints and transitive versions (Maven behavior). Override a Boot-managed version with its property instead ([3.8](#38-dependency-management-and-boms)).
- Excluding by `module` only when the library also appears under another group name.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 3.8 Dependency Management and BOMs

A **BOM** (bill of materials) is a POM that lists tested versions of many libraries. `spring-boot-dependencies` is Spring Boot's BOM; it lets you write `implementation("org.springframework.boot:spring-boot-starter-web")` without a version. There are two ways to use a BOM in Gradle.

#### Option A: `io.spring.dependency-management` plugin (Spring Initializr default)

When applied together with the Spring Boot plugin, it **imports the Spring Boot BOM automatically**.

```kotlin
plugins {
    java
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.7"
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2025.0.0")   // extra BOMs
        mavenBom("software.amazon.awssdk:bom:2.31.0")
    }
    dependencies {
        dependency("org.mapstruct:mapstruct:1.6.3")                 // manage one artifact
        dependencySet("org.mapstruct:1.6.3") {                      // several artifacts, same group and version
            entry("mapstruct")
            entry("mapstruct-processor")
        }
    }
}

dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")   // version from the Cloud BOM
    implementation("org.mapstruct:mapstruct")                                    // version from dependencyManagement
}
```

| Element | Description |
|---------|-------------|
| `imports { mavenBom("g:a:v") }` | Import a BOM. Later imports win over earlier ones. |
| `dependencies { dependency("g:a:v") }` | Manage the version of one artifact. |
| `dependencySet("group:version") { entry("name") }` | Manage several artifacts that share a group and version. |
| `applyMavenExclusions(false)` | Ignore exclusions declared in POMs (faster resolution). |

**Override a version managed by Spring Boot** by setting the BOM's version property (property names are listed in the Spring Boot reference, "Dependency Versions → Version Properties"):

```kotlin
extra["jackson-bom.version"] = "2.19.2"
extra["postgresql.version"] = "42.7.7"
```

```groovy
ext['jackson-bom.version'] = '2.19.2'
```

#### Option B: Gradle's native `platform()` (no extra plugin)

```kotlin
import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
    java
    id("org.springframework.boot") version "3.5.6"
    // no io.spring.dependency-management
}

dependencies {
    implementation(platform(SpringBootPlugin.BOM_COORDINATES))     // "org.springframework.boot:spring-boot-dependencies:<plugin version>"
    implementation(platform("org.springframework.cloud:spring-cloud-dependencies:2025.0.0"))
    annotationProcessor(platform(SpringBootPlugin.BOM_COORDINATES)) // each configuration needs the platform

    implementation("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
}
```

Override a version: declare a **higher** version directly (highest wins), or use `strictly(...)` to go lower. `enforcedPlatform(...)` forces every BOM version, even downgrades; avoid it in libraries.

#### Which one?

| | `io.spring.dependency-management` | `platform()` |
|---|-----------------------------------|--------------|
| Extra plugin | Yes | No (built into Gradle) |
| Applies to | All configurations automatically | Each configuration where you add the platform |
| Override a Boot version | `extra["xxx.version"] = "..."` | Declare a higher version / `strictly` |
| Conflict behavior | BOM versions win (Maven-like) | Highest version wins (Gradle-like) |
| Build speed | Slower on large builds | Faster |
| Published POM | Contains the imported BOMs | Use `versionMapping` ([3.14](#314-publishing)) |

Both are supported by Spring Boot. Option A is simpler for most applications; option B (often through a [platform module](#89-platform-module-internal-bom)) suits large multi-module builds.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 3.9 configurations

#### What it does
Creates new dependency configurations or changes existing ones (inheritance, exclusions, resolution rules).

#### Properties

| Property / method | Description |
|-------------------|-------------|
| `extendsFrom(other)` | This configuration also contains all dependencies of `other`. |
| `exclude(group, module)` | Exclude a transitive dependency from this configuration. |
| `resolutionStrategy { }` | Version conflict rules ([3.7](#37-versions-exclusions-and-constraints)). |
| `isTransitive` | `false` = resolve only the declared dependencies. |
| `configurations.creating` / `create("name")` | Define a new configuration. |
| `configurations.configureEach { }` | Configure all configurations (lazy). |

#### Example: annotation processors on the compile classpath

```kotlin
configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}
```

```groovy
configurations {
    compileOnly {
        extendsFrom annotationProcessor
    }
}
```

#### Example: custom configuration for a Java agent (Mockito on JDK 21+)

JDK 21+ warns when Mockito attaches its agent at runtime. Mockito recommends loading it as a `-javaagent`:

```kotlin
val mockitoAgent = configurations.create("mockitoAgent")

dependencies {
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    mockitoAgent("org.mockito:mockito-core") { isTransitive = false }   // version from the Boot BOM
}

tasks.test {
    jvmArgs("-javaagent:${mockitoAgent.asPath}")
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

## 3.10 Extra Properties (ext / extra)

#### What they are
User-defined properties stored on the project. Used for versions and values shared between blocks. In a multi-module build, extra properties of the root project are visible to subprojects.

#### Example

```kotlin
// Kotlin DSL
extra["springCloudVersion"] = "2025.0.0"              // define
val springCloudVersion: String by extra               // read with a delegate
println(property("springCloudVersion"))               // read by name (fails if missing)
println(findProperty("springCloudVersion"))           // read by name (null if missing)

val testcontainersVersion by extra("1.21.3")          // define + read in one line
```

```groovy
// Groovy DSL
ext {
    springCloudVersion = '2025.0.0'
}
ext.testcontainersVersion = '1.21.3'
println springCloudVersion                            // read directly
```

#### Properties from `gradle.properties` / command line
Values in `gradle.properties` or passed with `-Pname=value` are **project properties** and can be read the same way:

```kotlin
val springCloudVersion: String by project                         // from gradle.properties
val imageTag = providers.gradleProperty("imageTag").getOrElse("latest")   // lazy, configuration-cache friendly
```

#### Common mistakes
- Using extra properties as a "version catalog" in large builds. A [version catalog](#7-version-catalog) is type-safe and understood by tools like Dependabot and Renovate.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 3.11 tasks

#### What it does
Configures existing tasks (`test`, `bootJar`, `compileJava`) and registers new ones.

#### Configure existing tasks

```kotlin
tasks.test { useJUnitPlatform() }                                   // accessor (plugin applied in plugins { })
tasks.named<Test>("test") { useJUnitPlatform() }                    // by name and type
tasks.withType<Test>().configureEach { useJUnitPlatform() }         // all tasks of a type (lazy)
```

```groovy
tasks.named('test') { useJUnitPlatform() }
tasks.withType(Test).configureEach { useJUnitPlatform() }
```

#### Register a new task

```kotlin
tasks.register("printVersion") {
    group = "help"
    description = "Prints the project version."
    val projectVersion = project.version.toString()     // read during configuration
    doLast {
        println("Version: $projectVersion")
    }
}

tasks.register<Copy>("copyJarToDist") {
    group = "distribution"
    from(tasks.bootJar)                                  // also makes this task depend on bootJar
    into(layout.projectDirectory.dir("dist"))
}

tasks.register<Exec>("dockerBuild") {
    dependsOn(tasks.bootJar)
    commandLine("docker", "build", "-t", "order-service:${project.version}", ".")
}
```

Built-in task types often used: `Copy`, `Sync`, `Delete`, `Zip`, `Exec`, `JavaExec`, `Test`.

#### Common task properties

| Property / method | Description |
|-------------------|-------------|
| `group` / `description` | Shown by `./gradlew tasks`. |
| `dependsOn(...)` | Tasks that must run before this one. |
| `finalizedBy(...)` | Tasks that run after this one, even if it fails (e.g. a report). |
| `mustRunAfter(...)` | Order only; doesn't add a dependency. |
| `shouldRunAfter(...)` | Soft order; Gradle may ignore it (e.g. in parallel builds). |
| `enabled` | `false` skips the task. |
| `onlyIf { condition }` | Run only if the condition is true. |
| `doFirst { }` / `doLast { }` | Actions at the start / end of execution. |
| `inputs` / `outputs` | Declared inputs and outputs, used for up-to-date checks and the build cache. |

#### Test task properties

| Property | Default | Description |
|----------|---------|-------------|
| `useJUnitPlatform { }` | — | Run JUnit 5 tests. Without it Gradle looks for JUnit 4 tests and finds none. Filter with `includeTags("fast")`, `excludeTags("slow")`. |
| `maxParallelForks` | `1` | Number of test JVMs running in parallel. |
| `forkEvery` | `0` | Start a new test JVM after N test classes (0 = never). |
| `maxHeapSize` | JVM default | `-Xmx` of the test JVM, e.g. `"1g"`. |
| `jvmArgs(...)` | — | Extra JVM arguments. |
| `systemProperty(k, v)` | — | Values for `System.getProperty`. |
| `environment(k, v)` | — | Environment variables, e.g. `SPRING_PROFILES_ACTIVE`. |
| `filter { includeTestsMatching("*IT") }` | — | Select tests by class/method name. |
| `failFast` | `false` | Stop after the first failing test. |
| `ignoreFailures` | `false` | Don't fail the build when tests fail. |
| `testLogging { }` | — | What to print to the console. |
| `reports.html.required` | `true` | HTML report in `build/reports/tests/test/index.html`. |

```kotlin
tasks.test {
    useJUnitPlatform {
        excludeTags("slow")
    }
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)
    systemProperty("user.timezone", "UTC")
    environment("SPRING_PROFILES_ACTIVE", "test")
    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}
```

#### JavaCompile properties

| Property | Description |
|----------|-------------|
| `options.encoding` | Source file encoding. Set `"UTF-8"`; on Windows the default is the system code page. |
| `options.compilerArgs` | Extra `javac` arguments: `-parameters`, `-Xlint:all`, `-Amapstruct.defaultComponentModel=spring`. |
| `options.release` | `javac --release N`. |
| `options.isDeprecation` (Groovy: `deprecation`) | Show deprecation details. |

The Spring Boot plugin adds `-parameters` automatically. Spring 6.1+ needs it to read parameter names (`@PathVariable Long id` without a name, constructor binding).

#### processResources: put the project version into `application.yml`

```kotlin
tasks.processResources {
    val props = mapOf("version" to project.version)
    inputs.properties(props)
    filesMatching("application.yml") {
        expand(props)
    }
}
```

```yaml
# src/main/resources/application.yml
app:
  version: ${version}               # replaced by Gradle at build time
  db-url: \${DB_URL}                # escaped: left for Spring to resolve at runtime
```

`expand` replaces **every** `${...}` in the file, so Spring placeholders must be escaped as `\${...}`.

#### jar manifest

```kotlin
tasks.jar {
    manifest {
        attributes(
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version
        )
    }
}
```

#### Common mistakes
- Doing work at configuration time (outside `doLast`): it runs on every build.
- `tasks.withType<Test> { }` (eager) instead of `tasks.withType<Test>().configureEach { }` (lazy) in large builds.
- Using `project` inside `doLast { }`: not allowed with the configuration cache. Read the value during configuration (as in `printVersion` above).

[⬆ Back to Table of Contents](#table-of-contents)

---

## 3.12 sourceSets

#### What it does
A source set is a group of source files compiled and run together. The `java` plugin creates `main` and `test`.

#### Properties

| Property | Description |
|----------|-------------|
| `java.srcDir("dir")` / `java.setSrcDirs(listOf(...))` | Add / replace Java source directories. |
| `resources.srcDir("dir")` | Add a resources directory. |
| `compileClasspath` | Classpath used to compile this source set. |
| `runtimeClasspath` | Classpath used to run it. |
| `output` | Compiled classes and processed resources. |
| `output.classesDirs` | Directories with the compiled classes. |

Each source set gets its own configurations and tasks: for `integrationTest` → `integrationTestImplementation`, `integrationTestRuntimeOnly`, `compileIntegrationTestJava`...

#### Example: add generated sources

```kotlin
sourceSets {
    main {
        java.srcDir(layout.buildDirectory.dir("generated/sources/openapi/src/main/java"))
    }
}
```

#### Example: separate integration tests (`src/integrationTest/java`)

```kotlin
sourceSets {
    create("integrationTest") {
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
}

val integrationTestImplementation by configurations.getting {
    extendsFrom(configurations.testImplementation.get())
}
val integrationTestRuntimeOnly by configurations.getting {
    extendsFrom(configurations.testRuntimeOnly.get())
}

val integrationTest = tasks.register<Test>("integrationTest") {
    description = "Runs integration tests."
    group = "verification"
    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath
    shouldRunAfter(tasks.test)
    useJUnitPlatform()
}

tasks.check { dependsOn(integrationTest) }
```

Run with `./gradlew integrationTest`; `./gradlew build` runs unit tests first, then integration tests. Gradle's **JVM Test Suite** plugin (`testing { suites { ... } }`) is a newer, shorter way to declare extra test suites.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 3.13 Kotlin Projects

A Spring Boot application written in **Kotlin** (not only the build script) needs extra plugins:

| Plugin | Why |
|--------|-----|
| `kotlin("jvm")` | Compiles Kotlin code. |
| `kotlin("plugin.spring")` | Kotlin classes are `final` by default; Spring needs to subclass `@Component`, `@Configuration`, `@Transactional`, `@Async`... classes for proxies. This plugin opens them automatically. |
| `kotlin("plugin.jpa")` | Generates no-arg constructors for `@Entity`, `@Embeddable`, `@MappedSuperclass` (JPA needs them). |
| `kotlin("kapt")` | Runs Java annotation processors (e.g. `spring-boot-configuration-processor`) on Kotlin code. |

```kotlin
plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    kotlin("plugin.jpa") version "1.9.25"
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.7"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")   // Kotlin data classes ↔ JSON
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")     // Spring's @Nullable annotations → Kotlin nullability
    }
}

allOpen {                                              // JPA entities must be open for lazy-loading proxies
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
```

The Spring Boot plugin aligns the Kotlin library versions with the Kotlin plugin version and adds `-java-parameters` to Kotlin compile tasks.

> **Spring Boot 4.x** requires **Kotlin 2.2+**. Spring Initializr uses Kotlin 1.9.x for Boot 3.x projects.

More on Kotlin itself: [Kotlin vs Java](kotlin-features-over-java.md).

[⬆ Back to Table of Contents](#table-of-contents)

---

## 3.14 publishing

**Added by:** the `maven-publish` plugin.

#### What it does
Publishes jars (and a generated POM) to a Maven repository, so other builds can use them. Typical for shared library modules.

#### Properties

| Element | Description |
|---------|-------------|
| `publications { create<MavenPublication>("name") { } }` | What to publish. |
| `from(components["java"])` | Publish the jar, its dependencies and (with `withSourcesJar()`) sources. |
| `artifact(tasks.bootJar)` | Publish the executable jar of an application. |
| `groupId` / `artifactId` / `version` | Override the coordinates (default: `group`, `project.name`, `version`). |
| `pom { name; description; licenses { } }` | Extra POM metadata. |
| `versionMapping { }` | Write resolved versions into the POM (needed with `platform()` BOMs). |
| `repositories { maven { } }` | Where to publish. |

#### Example: publish a library module

```kotlin
plugins {
    `java-library`
    `maven-publish`
}

java {
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = "shop-common"
            versionMapping {
                usage("java-api") { fromResolutionOf("runtimeClasspath") }
                usage("java-runtime") { fromResolutionResult() }
            }
            pom {
                name = "Shop Common"
                description = "Shared DTOs and utilities"
            }
        }
    }
    repositories {
        maven {
            name = "companyNexus"
            val releases = "https://nexus.mycompany.com/repository/maven-releases/"
            val snapshots = "https://nexus.mycompany.com/repository/maven-snapshots/"
            url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshots else releases)
            credentials(PasswordCredentials::class)
        }
    }
}
```

```bash
./gradlew publishToMavenLocal     # install to ~/.m2 (like mvn install)
./gradlew publish                 # upload to the configured repositories
```

#### Example: publish a Spring Boot application jar

```kotlin
publishing {
    publications {
        create<MavenPublication>("bootJava") {
            artifact(tasks.named("bootJar"))
        }
    }
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

## 3.15 Other Common Plugins

#### jacoco (code coverage, core plugin)

```kotlin
plugins {
    jacoco
}

jacoco {
    toolVersion = "0.8.13"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required = true       // for SonarQube / CI
        html.required = true      // build/reports/jacoco/test/html/index.html
    }
    classDirectories.setFrom(files(classDirectories.files.map {
        fileTree(it) { exclude("**/config/**", "**/*Application*") }
    }))
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.80".toBigDecimal()     // fail below 80% line coverage
            }
        }
    }
}

tasks.check { dependsOn(tasks.jacocoTestCoverageVerification) }
```

#### application (core plugin)
With the Spring Boot plugin it adds `bootStartScripts`, `bootDistZip` and `bootDistTar`: a zip/tar with the jar plus `bin/` start scripts.

#### Community plugins often used with Spring Boot

Check the [Gradle Plugin Portal](https://plugins.gradle.org) for the current version of each.

| Plugin ID | Purpose |
|-----------|---------|
| `org.graalvm.buildtools.native` | GraalVM native images ([4.7](#47-native-images-and-aot)). |
| `org.cyclonedx.bom` | SBOM generation; Spring Boot 3.3+ packages the SBOM in the jar and exposes it via the actuator `sbom` endpoint. |
| `com.gorylenko.gradle-git-properties` | Generates `git.properties` → `GitProperties` bean and git info in `/actuator/info` ([4.6](#46-build-info)). |
| `org.openapi.generator` | Generate Spring controllers/clients from an OpenAPI spec. |
| `com.diffplug.spotless` | Code formatting check/apply. |
| `org.sonarqube` | SonarQube analysis. |
| `com.google.cloud.tools.jib` | Build container images without a Dockerfile or Docker daemon. |
| `org.flywaydb.flyway` | Run Flyway migrations from Gradle (Spring Boot already runs them at startup). |
| `com.github.ben-manes.versions` | `./gradlew dependencyUpdates` lists newer dependency versions. |

[⬆ Back to Table of Contents](#table-of-contents)

---

# 4. Spring Boot Gradle Plugin

**Plugin ID:** `org.springframework.boot` (artifact `org.springframework.boot:spring-boot-gradle-plugin`). Its version is the Spring Boot version.

## 4.1 What the Plugin Adds

The plugin doesn't apply other plugins itself; it **reacts** when other plugins are applied:

| When this plugin is applied | The Spring Boot plugin... |
|-----------------------------|---------------------------|
| `java` | Adds `bootJar` (executable jar), `bootRun`, `bootTestRun`, `bootBuildImage`, `resolveMainClassName`. Makes `assemble` depend on `bootJar`. Gives the plain `jar` the classifier `plain`. Adds the `developmentOnly`, `testAndDevelopmentOnly` and `productionRuntimeClasspath` configurations. Adds `-parameters` to every `JavaCompile` task. |
| `io.spring.dependency-management` | Imports the `spring-boot-dependencies` BOM, so starters need no version. |
| `org.jetbrains.kotlin.jvm` | Aligns Kotlin library versions with the plugin and adds `-java-parameters`. |
| `war` | Adds `bootWar` (executable WAR) and the `providedRuntime` configuration ([4.8](#48-war-packaging)). |
| `application` | Adds `bootStartScripts`, `bootDistZip`, `bootDistTar`. |
| `org.graalvm.buildtools.native` | Adds `processAot` / `processTestAot` and configures native image builds ([4.7](#47-native-images-and-aot)). |
| `org.cyclonedx.bom` | (Boot 3.3+) Generates an SBOM and puts it in the jar. |

Tasks it adds (`./gradlew tasks --group build` / `application`):

| Task | Description |
|------|-------------|
| `bootJar` | Builds the executable fat jar in `build/libs`. |
| `bootRun` | Runs the application from the project's classes. |
| `bootTestRun` | (Boot 3.1+) Runs the application with the **test** classpath, using a main class from `src/test`. |
| `bootBuildImage` | Builds an OCI/Docker image with Cloud Native Buildpacks. |
| `bootBuildInfo` | Writes `META-INF/build-info.properties` (when `buildInfo()` is configured). |
| `resolveMainClassName` | Finds the class with `public static void main`. |

[⬆ Back to Table of Contents](#table-of-contents)

---

## 4.2 springBoot Extension

#### Properties

| Property / method | Type | Default | Description |
|-------------------|------|---------|-------------|
| `mainClass` | `Property<String>` | detected | Fully-qualified main class. Needed only when there are several `main` methods or detection fails. Used by `bootJar`, `bootRun`, `bootWar`, `bootBuildImage`. |
| `buildInfo()` | method | not configured | Registers `bootBuildInfo` and generates `build-info.properties` ([4.6](#46-build-info)). |
| `buildInfo { }` | method | — | Same, with configuration. |

```kotlin
springBoot {
    mainClass = "com.example.order.OrderApplication"
    buildInfo()
}
```

```groovy
springBoot {
    mainClass = 'com.example.order.OrderApplication'
    buildInfo()
}
```

The main class can also be set per task: `tasks.bootJar { mainClass = "..." }`, `tasks.bootRun { mainClass = "..." }`, or with `application { mainClass = "..." }` when the `application` plugin is applied.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 4.3 bootJar and jar

#### What they produce

```bash
./gradlew build
ls build/libs
# order-service-0.0.1-SNAPSHOT.jar         ← bootJar: executable fat jar (java -jar)
# order-service-0.0.1-SNAPSHOT-plain.jar   ← jar: only your classes, no dependencies
```

Structure of the executable jar:

```
order-service-0.0.1-SNAPSHOT.jar
├── META-INF/MANIFEST.MF          Main-Class: org.springframework.boot.loader.launch.JarLauncher
│                                 Start-Class: com.example.order.OrderApplication
├── org/springframework/boot/loader/...   ← Spring Boot loader (reads nested jars)
└── BOOT-INF/
    ├── classes/                  ← your compiled classes + application.yml
    ├── lib/                      ← every runtime dependency as a nested jar
    ├── classpath.idx             ← order of the jars on the classpath
    └── layers.idx                ← layer of each file (for Docker)
```

#### bootJar properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `archiveFileName` | `Property<String>` | `<name>-<version>.jar` | Full file name, e.g. `"app.jar"`. |
| `archiveBaseName` / `archiveVersion` / `archiveClassifier` | `Property<String>` | `project.name` / `version` / `""` | Parts of the file name. |
| `destinationDirectory` | `DirectoryProperty` | `build/libs` | Output directory. |
| `mainClass` | `Property<String>` | from `springBoot.mainClass` | Main class for this jar. |
| `manifest { attributes(...) }` | method | — | Extra manifest entries. |
| `requiresUnpack("**/x-*.jar")` | method | — | Nested jars that must be unpacked to a temp directory at startup (libraries that can't read nested jars, e.g. JRuby). |
| `layered { }` | method | enabled | Layer configuration for Docker images. |
| `includeTools` | `Property<Boolean>` | `true` | (Boot 3.3+) Include the `tools` jar mode used to extract layers. |
| `exclude(...)` / `include(...)` | method | — | Filter files (from Gradle's `Jar` task). |

#### Example

```kotlin
tasks.bootJar {
    archiveFileName = "app.jar"
    manifest {
        attributes("Implementation-Version" to project.version)
    }
}

tasks.jar {
    enabled = false          // don't build the -plain.jar in an application module
}
```

Disabling the plain jar leaves one jar in `build/libs`, so a Dockerfile `COPY build/libs/*.jar app.jar` can't pick the wrong one. **Don't** disable `jar` in library modules; other modules need it.

#### Layered jars and Docker
Layers let Docker cache dependencies (which rarely change) separately from your code. Default layers, in order: `dependencies`, `spring-boot-loader`, `snapshot-dependencies`, `application`.

Custom layers, e.g. put this build's own modules in the `application` layer:

```kotlin
tasks.bootJar {
    layered {
        application {
            intoLayer("spring-boot-loader") {
                include("org/springframework/boot/loader/**")
            }
            intoLayer("application")
        }
        dependencies {
            intoLayer("application") {
                includeProjectDependencies()        // jars of other modules of this build
            }
            intoLayer("snapshot-dependencies") {
                include("*:*:*SNAPSHOT")
            }
            intoLayer("dependencies")
        }
        layerOrder = listOf("dependencies", "spring-boot-loader", "snapshot-dependencies", "application")
    }
}
```

Dockerfile using the layers (Boot 3.3+ `tools` jar mode):

```dockerfile
FROM eclipse-temurin:21-jre AS builder
WORKDIR /builder
COPY build/libs/app.jar application.jar
RUN java -Djarmode=tools -jar application.jar extract --layers --destination extracted

FROM eclipse-temurin:21-jre
WORKDIR /application
COPY --from=builder /builder/extracted/dependencies/ ./
COPY --from=builder /builder/extracted/spring-boot-loader/ ./
COPY --from=builder /builder/extracted/snapshot-dependencies/ ./
COPY --from=builder /builder/extracted/application/ ./
ENTRYPOINT ["java", "-jar", "application.jar"]
```

#### Common mistakes
- `bootJar` fails with `Main class name has not been configured and it could not be resolved` → the module has no `main` method (a library): don't apply the Boot plugin there, or set `mainClass`.
- Two classes with `main` methods → set `springBoot { mainClass = "..." }`.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 4.4 bootRun and bootTestRun

#### bootRun
Runs the application from compiled classes, with `developmentOnly` dependencies (devtools) on the classpath. It is a `JavaExec` task.

| Property | Description |
|----------|-------------|
| `args(...)` | Program arguments (`--server.port=9090`). |
| `jvmArgs(...)` | JVM arguments (`-Xmx512m`). |
| `systemProperty(k, v)` | System properties. |
| `environment(k, v)` | Environment variables. |
| `mainClass` | Main class (default from `springBoot`). |
| `sourceResources(sourceSets.main.get())` | Serve resources from `src/main/resources` directly, so changes to static files/templates show without a rebuild. |
| `optimizedLaunch` | `true` by default: adds `-XX:TieredStopAtLevel=1` for faster startup. |

```kotlin
tasks.bootRun {
    jvmArgs("-Xmx512m")
    systemProperty("spring.profiles.active", "dev")
    environment("DB_URL", "jdbc:postgresql://localhost:5432/orders")
    sourceResources(sourceSets.main.get())
}
```

Command-line usage:

```bash
./gradlew bootRun --args='--spring.profiles.active=dev --server.port=9090'
SPRING_PROFILES_ACTIVE=dev ./gradlew bootRun          # environment variable (Linux/macOS)
./gradlew bootRun --debug-jvm                          # wait for a debugger on port 5005
```

`-Dname=value` given to `./gradlew` sets a property of **Gradle's** JVM, not of the application. Use `--args` or configure `bootRun`.

#### bootTestRun (Boot 3.1+)
Starts the application with the test classpath and a main class from `src/test/java`. Used to run the app locally with Testcontainers instead of installed databases:

```java
// src/test/java/com/example/order/TestOrderApplication.java
public class TestOrderApplication {
    public static void main(String[] args) {
        SpringApplication.from(OrderApplication::main)
                .with(TestcontainersConfiguration.class)   // @TestConfiguration with @ServiceConnection containers
                .run(args);
    }
}
```

```bash
./gradlew bootTestRun
```

[⬆ Back to Table of Contents](#table-of-contents)

---

## 4.5 bootBuildImage

Builds an OCI image (runs on Docker/Kubernetes) using **Cloud Native Buildpacks**, without a Dockerfile. Needs a running Docker-compatible daemon.

| Property | Description |
|----------|-------------|
| `imageName` | Image name, default `docker.io/library/<project.name>:<version>`. |
| `builder` / `runImage` | Buildpacks builder and base run image. |
| `environment` | Buildpack settings, e.g. `BP_JVM_VERSION`, `BPE_DELIM_JAVA_TOOL_OPTIONS`, `BPE_APPEND_JAVA_TOOL_OPTIONS`. |
| `tags` | Extra tags. |
| `publish` | `true` = push to the registry after building. |
| `docker { publishRegistry { url; username; password } }` | Registry credentials. |
| `imagePlatform` | (Boot 3.4+) Target platform, e.g. `"linux/arm64"`. |

```kotlin
tasks.bootBuildImage {
    imageName = "registry.example.com/shop/order-service:${project.version}"
    environment.put("BP_JVM_VERSION", "21")
    publish = false
}
```

```bash
./gradlew bootBuildImage
./gradlew bootBuildImage --imageName=myorg/order-service:1.0 --publishImage
docker run -p 8080:8080 registry.example.com/shop/order-service:0.0.1-SNAPSHOT
```

[⬆ Back to Table of Contents](#table-of-contents)

---

## 4.6 Build Info

`buildInfo()` generates `META-INF/build-info.properties`. Spring Boot then creates a `BuildProperties` bean, and `/actuator/info` shows the build data (when the `info` endpoint is exposed).

```kotlin
springBoot {
    buildInfo {
        excludes = setOf("time")                 // leave out the timestamp → reproducible, cacheable builds
        properties {
            name = "Order Service"
            additional = mapOf("team" to "payments")
        }
    }
}
```

| Property | Default | Description |
|----------|---------|-------------|
| `properties.group` / `artifact` / `version` / `name` | project values | Values written to the file. |
| `properties.additional` | empty | Extra `build.*` entries. |
| `excludes` | empty | Properties to leave out (`"time"`, `"group"`...). |

```java
@RestController
@RequiredArgsConstructor
class VersionController {
    private final BuildProperties buildProperties;

    @GetMapping("/version")
    String version() {
        return buildProperties.getVersion();     // "0.0.1-SNAPSHOT"
    }
}
```

Git information (commit, branch) comes from the `com.gorylenko.gradle-git-properties` plugin, which writes `git.properties`; Spring Boot then creates a `GitProperties` bean.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 4.7 Native Images and AOT

With the GraalVM Native Build Tools plugin, Spring Boot compiles the application ahead of time into a native executable (fast startup, low memory).

```kotlin
plugins {
    java
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.graalvm.buildtools.native") version "0.10.6"
}

graalvmNative {
    binaries {
        named("main") {
            imageName = "order-service"
            buildArgs.add("--verbose")
        }
    }
}
```

| Task | Description |
|------|-------------|
| `processAot` | Generates AOT-optimized source code and hints for the main application. |
| `processTestAot` | Same for tests. |
| `nativeCompile` | Builds a native executable in `build/native/nativeCompile` (needs a GraalVM JDK). |
| `nativeTest` | Runs the tests as a native image. |
| `bootBuildImage` | Builds a container image with a native executable (no local GraalVM needed). |

[⬆ Back to Table of Contents](#table-of-contents)

---

## 4.8 WAR Packaging

To deploy to an external servlet container (Tomcat, JBoss) instead of running the embedded server:

```kotlin
plugins {
    war
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.7"
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")   // the container provides Tomcat
}
```

```java
@SpringBootApplication
public class OrderApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}
```

`bootWar` builds an executable WAR (works with `java -jar` **and** in a container; `providedRuntime` jars go to `WEB-INF/lib-provided`). The plain `war` task output gets the classifier `plain`.

[⬆ Back to Table of Contents](#table-of-contents)

---

# 5. settings.gradle

`settings.gradle` (Groovy) / `settings.gradle.kts` (Kotlin) is evaluated **first**. It defines the build's name, its modules, where plugins come from, and (optionally) the repositories for all modules. Every build has one, in the root directory.

## 5.1 rootProject.name and include

| Property / method | Description |
|-------------------|-------------|
| `rootProject.name` | Name of the root project. Default: directory name. It becomes the jar name and the Maven `artifactId`. Always set it (CI checkouts may use a random directory name). |
| `include("a", "b")` | Add modules. `include("services:order")` adds the project `:services:order` in `services/order/`. |
| `project(":a").projectDir = file("path")` | Use a different directory for a module. |

```kotlin
rootProject.name = "shop"

include("common", "service", "app")
include("services:order-service")                         // project path :services:order-service
project(":app").projectDir = file("applications/shop-app") // custom directory
```

```groovy
rootProject.name = 'shop'
include 'common', 'service', 'app'
```

**Project path vs directory:** Gradle uses `:` to separate levels in project paths (`:services:order-service`) and `/` for directories (`services/order-service`).

[⬆ Back to Table of Contents](#table-of-contents)

---

## 5.2 pluginManagement

Controls where **plugins** are resolved from and their default versions. Must be the first block in `settings.gradle(.kts)`.

| Element | Description |
|---------|-------------|
| `repositories { }` | Repositories for plugins. Default: Gradle Plugin Portal only. Add a company mirror here. |
| `plugins { id("x") version "v" }` | Default plugin versions; build scripts then write `id("x")` without a version. |
| `includeBuild("build-logic")` | Make convention plugins from an included build available ([8.5](#85-convention-plugins-in-build-logic)). |

```kotlin
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    plugins {
        id("org.springframework.boot") version "3.5.6"
        id("io.spring.dependency-management") version "1.1.7"
    }
    includeBuild("build-logic")
}
```

```kotlin
// any build.gradle.kts of the build
plugins {
    id("org.springframework.boot")            // version from pluginManagement
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

## 5.3 dependencyResolutionManagement

Declares the repositories (and version catalogs) **once for all modules**.

```kotlin
dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        mavenCentral()
    }
}
```

| Element | Description |
|---------|-------------|
| `repositories { }` | Repositories used by every project. |
| `repositoriesMode` | `PREFER_PROJECT` (default): a module's own `repositories { }` replaces these. `PREFER_SETTINGS`: module repositories are ignored. `FAIL_ON_PROJECT_REPOS`: a module declaring repositories fails the build (enforces one place). |
| `versionCatalogs { create("x") { from(...) } }` | Extra catalogs or a catalog in a non-default location. `gradle/libs.versions.toml` is imported as `libs` automatically. |

[⬆ Back to Table of Contents](#table-of-contents)

---

## 5.4 Settings Plugins and Feature Previews

**Automatic JDK download for toolchains.** Without a resolver plugin, Gradle can only use JDKs that are already installed. The Foojay resolver downloads a missing JDK:

```kotlin
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"    // use 1.0.0+ on Gradle 9
}
```

**Type-safe project accessors.** Refer to modules as `projects.common` instead of the string `project(":common")`; typos fail at compile time and the IDE auto-completes them:

```kotlin
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
```

```kotlin
dependencies {
    implementation(projects.common)              // = project(":common")
    implementation(projects.services.orderService)   // = project(":services:order-service") (dashes → camelCase)
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

# 6. gradle.properties

A Java properties file read before the build starts. It holds **Gradle settings** (`org.gradle.*`) and **project properties** (any other key).

Where it can live (the first location that defines a property wins):

| Priority | Location | Use |
|----------|----------|-----|
| 1 | Command line: `-Pname=value`, `-Dorg.gradle.xxx=value` | One-off overrides, CI. |
| 2 | `~/.gradle/gradle.properties` (`GRADLE_USER_HOME`) | Personal settings and **credentials**. |
| 3 | `<project root>/gradle.properties` | Shared team settings (committed). |
| 4 | `<Gradle installation>/gradle.properties` | Rarely used. |

Environment variables `ORG_GRADLE_PROJECT_<name>` also set project properties (handy for CI secrets).

## 6.1 Build Environment Properties

```properties
# gradle.properties
org.gradle.jvmargs=-Xmx2g -XX:MaxMetaspaceSize=512m -Dfile.encoding=UTF-8
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.configuration-cache=true
```

| Property | Default | Description |
|----------|---------|-------------|
| `org.gradle.jvmargs` | `-Xmx512m -XX:MaxMetaspaceSize=384m` | JVM arguments of the Gradle daemon. Raise memory for large multi-module builds. |
| `org.gradle.parallel` | `false` | Build independent modules in parallel. |
| `org.gradle.caching` | `false` | Reuse task outputs from the build cache (also across branches and `clean`). |
| `org.gradle.configuration-cache` | `false` | Cache the result of the configuration phase; much faster repeated builds. Plugins must support it; `org.gradle.configuration-cache.problems=warn` reports problems without failing. |
| `org.gradle.daemon` | `true` | Keep a background Gradle process between builds. |
| `org.gradle.java.home` | `JAVA_HOME` | JDK that runs Gradle (not the one that compiles: use toolchains for that). |
| `org.gradle.console` | `auto` | `plain`, `rich`, `verbose`. `plain` is useful in CI logs. |
| `org.gradle.logging.level` | `lifecycle` | `quiet`, `warn`, `lifecycle`, `info`, `debug`. |
| `org.gradle.warning.mode` | `summary` | `all` shows every deprecation (useful before upgrading Gradle); `fail` fails the build on deprecations. |
| `org.gradle.workers.max` | CPU count | Maximum parallel workers. |
| `org.gradle.vfs.watch` | `true` on supported OS | Watch the file system between builds for faster up-to-date checks. |
| `kotlin.code.style` | — | `official` (read by the Kotlin plugin). |

[⬆ Back to Table of Contents](#table-of-contents)

---

## 6.2 Project Properties

Any other key becomes a **project property**, available in all projects of the build:

```properties
# gradle.properties
version=1.4.0-SNAPSHOT          # sets project.version
group=com.example.shop          # sets project.group
springCloudVersion=2025.0.0
dockerRegistry=registry.example.com
```

Reading them:

```kotlin
// build.gradle.kts
val springCloudVersion: String by project                                    // delegate
val registry = providers.gradleProperty("dockerRegistry").get()             // provider API
val tag = findProperty("imageTag")?.toString() ?: "latest"                  // optional
```

```groovy
// build.gradle
println springCloudVersion                          // directly by name
println findProperty('imageTag') ?: 'latest'
```

Override on the command line: `./gradlew build -Pversion=1.4.0 -PimageTag=rc1`.

#### Common mistakes
- Committing passwords in the project's `gradle.properties`. Put them in `~/.gradle/gradle.properties` or CI environment variables.
- Expecting `systemProp.xxx` values to reach the application: `systemProp.` entries become system properties of **Gradle's** JVM.

[⬆ Back to Table of Contents](#table-of-contents)

---

# 7. Version Catalog

A version catalog (`gradle/libs.versions.toml`) keeps all dependency and plugin coordinates and versions **in one file**. Gradle generates type-safe accessors (`libs.spring.boot.starter.web`) from it. It is most useful in multi-module builds.

## 7.1 libs.versions.toml Format

```toml
# gradle/libs.versions.toml
[versions]
spring-boot = "3.5.6"
spring-dependency-management = "1.1.7"
spring-cloud = "2025.0.0"
mapstruct = "1.6.3"
springdoc = "2.8.9"

[libraries]
# no version: managed by the Spring Boot BOM
spring-boot-starter-web = { module = "org.springframework.boot:spring-boot-starter-web" }
spring-boot-starter-data-jpa = { module = "org.springframework.boot:spring-boot-starter-data-jpa" }
spring-boot-starter-validation = { module = "org.springframework.boot:spring-boot-starter-validation" }
spring-boot-starter-actuator = { module = "org.springframework.boot:spring-boot-starter-actuator" }
spring-boot-starter-test = { module = "org.springframework.boot:spring-boot-starter-test" }
lombok = { module = "org.projectlombok:lombok" }
postgresql = { module = "org.postgresql:postgresql" }

# with versions
spring-boot-dependencies = { module = "org.springframework.boot:spring-boot-dependencies", version.ref = "spring-boot" }
spring-cloud-dependencies = { module = "org.springframework.cloud:spring-cloud-dependencies", version.ref = "spring-cloud" }
mapstruct-core = { module = "org.mapstruct:mapstruct", version.ref = "mapstruct" }
mapstruct-processor = { module = "org.mapstruct:mapstruct-processor", version.ref = "mapstruct" }
springdoc-openapi = { module = "org.springdoc:springdoc-openapi-starter-webmvc-ui", version.ref = "springdoc" }

[bundles]
spring-web = ["spring-boot-starter-web", "spring-boot-starter-validation", "spring-boot-starter-actuator"]

[plugins]
spring-boot = { id = "org.springframework.boot", version.ref = "spring-boot" }
spring-dependency-management = { id = "io.spring.dependency-management", version.ref = "spring-dependency-management" }
```

| Section | Description |
|---------|-------------|
| `[versions]` | Named versions, referenced with `version.ref`. Accessor: `libs.versions.spring.cloud`. |
| `[libraries]` | Dependencies: `module = "group:name"` plus `version`, `version.ref`, or no version (BOM-managed). |
| `[bundles]` | Groups of libraries added with one line. |
| `[plugins]` | Plugins with ID and version. |

**Naming:** `-`, `_` and `.` in aliases become `.` in accessors: `spring-boot-starter-web` → `libs.spring.boot.starter.web`.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 7.2 Using the Catalog

```kotlin
// build.gradle.kts
plugins {
    java
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${libs.versions.spring.cloud.get()}")
    }
}

dependencies {
    implementation(libs.bundles.spring.web)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.mapstruct.core)
    implementation(libs.springdoc.openapi)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    annotationProcessor(libs.mapstruct.processor)
    runtimeOnly(libs.postgresql)
    testImplementation(libs.spring.boot.starter.test)
}

println(libs.versions.spring.boot.get())     // "3.5.6"
```

```groovy
// build.gradle (same accessors)
plugins {
    id 'java'
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

dependencies {
    implementation libs.bundles.spring.web
    compileOnly libs.lombok
    annotationProcessor libs.lombok
}
```

With Gradle's native platform instead of the dependency-management plugin: `implementation(platform(libs.spring.cloud.dependencies))`.

**Benefits:** one place to upgrade versions, IDE auto-completion, shared by every module, and understood by Dependabot / Renovate. The catalog only **declares** dependencies; a module uses only what it adds in its own `dependencies { }`.

[⬆ Back to Table of Contents](#table-of-contents)

---

# 8. Multi-Module Projects

A **multi-module** (multi-project) build is one Gradle build made of several projects (modules). Each module has its own `build.gradle(.kts)` and `src/`, and one `settings.gradle(.kts)` at the root lists them all. Modules depend on each other with `project(":name")`.

## 8.1 Why Multi-Module

| Benefit | Explanation |
|---------|-------------|
| **Enforced layering** | If `app` doesn't depend on `persistence`, controllers *can't* import repositories: the compiler stops them. |
| **Reuse** | A `common` module (DTOs, exceptions, utilities) is shared by several services or applications. |
| **Faster builds** | Gradle rebuilds only changed modules and the modules that depend on them, builds independent modules in parallel, and caches outputs. |
| **Focused tests** | Each module's tests run on their own: `./gradlew :service:test`. |
| **Several deployables** | One build can produce several Spring Boot applications that share code ([8.12](#812-multiple-spring-boot-applications-in-one-build)). |
| **Clear ownership** | Teams own modules; dependencies between them are explicit in build files. |

**Multi-module is not microservices.** Modules are build units. A multi-module build can produce **one** deployable (a modular monolith) or **several**.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 8.2 Project Structure

The example used in this section: an online shop with one Spring Boot application and three library modules.

```
shop/
├── settings.gradle.kts              ← lists modules, plugin + repository setup
├── build.gradle.kts                 ← root build (can stay empty)
├── gradle.properties
├── gradle/
│   ├── libs.versions.toml           ← versions for all modules
│   └── wrapper/
├── gradlew, gradlew.bat
├── build-logic/                     ← convention plugins = shared build configuration
│   ├── settings.gradle.kts
│   ├── build.gradle.kts
│   └── src/main/kotlin/
│       ├── shop.java-conventions.gradle.kts
│       ├── shop.spring-library-conventions.gradle.kts
│       └── shop.spring-boot-app-conventions.gradle.kts
├── common/                          ← library: DTOs, exceptions, utilities
│   ├── build.gradle.kts
│   └── src/main/java/com/example/shop/common/
├── persistence/                     ← library: JPA entities + Spring Data repositories
│   ├── build.gradle.kts
│   └── src/main/java/com/example/shop/persistence/
├── service/                         ← library: business logic, transactions
│   ├── build.gradle.kts
│   └── src/main/java/com/example/shop/service/
└── app/                             ← Spring Boot application: controllers, main class, application.yml
    ├── build.gradle.kts
    └── src/main/java/com/example/shop/
```

Dependencies between modules (arrows point to the dependency):

```
          ┌──────────────────────────────┐
          │ app  (Spring Boot, bootJar)  │
          └──────────────┬───────────────┘
                         │ implementation
                         ▼
          ┌──────────────────────────────┐
          │ service                      │
          └───────┬──────────────┬───────┘
          api     │              │ implementation
                  ▼              ▼
          ┌─────────────┐  ┌─────────────┐
          │ common      │  │ persistence │
          └─────────────┘  └─────────────┘
```

| Module | Plugin (via convention) | Produces | Depends on |
|--------|-------------------------|----------|------------|
| `common` | `java-library` | plain jar | — |
| `persistence` | `java-library` | plain jar | Spring Data JPA |
| `service` | `java-library` | plain jar | `common` (api), `persistence` (implementation) |
| `app` | `java-library` + `org.springframework.boot` | executable `bootJar` | `service` |

Rules:
- Dependencies point **one way** (down the layers). Gradle fails on cycles (`Circular dependency between the following tasks`).
- **Only the application module** applies the Spring Boot plugin and produces a `bootJar`.
- All modules share the **root package** `com.example.shop`, so `@SpringBootApplication` in `app` finds beans in every module ([8.8](#88-spring-boot-wiring-across-modules)).

[⬆ Back to Table of Contents](#table-of-contents)

---

## 8.3 Root settings.gradle.kts

```kotlin
// shop/settings.gradle.kts
pluginManagement {
    includeBuild("build-logic")          // convention plugins (8.5)
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}

dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS   // modules may not declare repositories
    repositories {
        mavenCentral()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")             // projects.common, projects.service ...

rootProject.name = "shop"

include("common", "persistence", "service", "app")
```

Groovy version (`settings.gradle`):

```groovy
pluginManagement {
    includeBuild 'build-logic'
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        mavenCentral()
    }
}

rootProject.name = 'shop'
include 'common', 'persistence', 'service', 'app'
```

Shared settings for every module go in the root `gradle.properties`:

```properties
org.gradle.jvmargs=-Xmx2g -Dfile.encoding=UTF-8
org.gradle.parallel=true
org.gradle.caching=true
```

[⬆ Back to Table of Contents](#table-of-contents)

---

## 8.4 Ways to Share Build Configuration

Every module needs the same Java version, Lombok, test setup and Spring Boot BOM. There are three ways to avoid copying that into each `build.gradle.kts`:

| Approach | Where the shared logic lives | Recommended? |
|----------|------------------------------|--------------|
| `allprojects { }` / `subprojects { }` | Root `build.gradle(.kts)` | Only for small builds. Gradle discourages it. |
| Convention plugins in `buildSrc/` | `buildSrc/src/main/kotlin/*.gradle.kts` | Works; any change to `buildSrc` invalidates the whole build. |
| Convention plugins in an **included build** (`build-logic/`) | `build-logic/src/main/kotlin/*.gradle.kts` | **Yes**: Gradle's recommended approach ([8.5](#85-convention-plugins-in-build-logic)). |

#### Approach 1: `subprojects { }` in the root build (simple, legacy)

```groovy
// shop/build.gradle (Groovy)
plugins {
    id 'org.springframework.boot' version '3.5.6' apply false
    id 'io.spring.dependency-management' version '1.1.7' apply false
}

subprojects {
    apply plugin: 'java-library'
    apply plugin: 'io.spring.dependency-management'

    group = 'com.example.shop'
    version = '0.0.1-SNAPSHOT'

    java {
        toolchain { languageVersion = JavaLanguageVersion.of(21) }
    }

    dependencyManagement {
        imports {
            mavenBom org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES
        }
    }

    dependencies {
        compileOnly 'org.projectlombok:lombok'
        annotationProcessor 'org.projectlombok:lombok'
        testImplementation 'org.springframework.boot:spring-boot-starter-test'
        testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
    }

    tasks.named('test') { useJUnitPlatform() }
}

project(':app') {
    apply plugin: 'org.springframework.boot'
}
```

The same in Kotlin DSL is clumsier: inside `subprojects { }` the type-safe accessors don't exist, so you write `configure<JavaPluginExtension> { }`, `the<DependencyManagementExtension>()` and `"implementation"("...")` with string configuration names.

**Why it is discouraged:**
- The configuration of a module is hidden in another file; reading `app/build.gradle` doesn't tell you what it does.
- Every module gets everything, even modules that need something different.
- It couples projects (cross-project configuration), which blocks Gradle features such as isolated projects and parallel configuration.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 8.5 Convention Plugins in build-logic

A **convention plugin** is a small build script that a module applies with one line: `plugins { id("shop.java-conventions") }`. Each module picks the conventions it needs. The plugins live in `build-logic/`, a separate Gradle build included through `pluginManagement { includeBuild("build-logic") }`.

#### build-logic/settings.gradle.kts

```kotlin
dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))     // reuse the main build's catalog
        }
    }
}

rootProject.name = "build-logic"
```

#### build-logic/build.gradle.kts

```kotlin
plugins {
    `kotlin-dsl`                 // compiles src/main/kotlin/*.gradle.kts into plugins
}

dependencies {
    // Plugins that the convention plugins apply must be on this build's classpath.
    // Versions are set here, so module build files don't repeat them.
    implementation("org.springframework.boot:spring-boot-gradle-plugin:${libs.versions.spring.boot.get()}")
    implementation("io.spring.gradle:dependency-management-plugin:${libs.versions.spring.dependency.management.get()}")
}
```

The plugin ID is the file name without `.gradle.kts`: `shop.java-conventions.gradle.kts` → `id("shop.java-conventions")`.

#### shop.java-conventions.gradle.kts (every Java module)

```kotlin
// build-logic/src/main/kotlin/shop.java-conventions.gradle.kts
plugins {
    `java-library`
}

group = "com.example.shop"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")     // Spring needs parameter names; the Boot plugin isn't applied to libraries
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
```

#### shop.spring-library-conventions.gradle.kts (library modules that use Spring)

```kotlin
// build-logic/src/main/kotlin/shop.spring-library-conventions.gradle.kts
import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
    id("shop.java-conventions")
    id("io.spring.dependency-management")
}

dependencyManagement {
    imports {
        mavenBom(SpringBootPlugin.BOM_COORDINATES)      // Spring Boot BOM without the Boot plugin
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

dependencies {
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
```

#### shop.spring-boot-app-conventions.gradle.kts (application modules)

```kotlin
// build-logic/src/main/kotlin/shop.spring-boot-app-conventions.gradle.kts
plugins {
    id("shop.spring-library-conventions")
    id("org.springframework.boot")
}

springBoot {
    buildInfo()
}

tasks.jar {
    enabled = false          // only the executable jar in build/libs
}
```

#### Using the version catalog inside a convention plugin
The `libs.xxx` accessors aren't generated inside precompiled script plugins. Look entries up by name instead:

```kotlin
// in a build-logic/src/main/kotlin/*.gradle.kts file
val libs = the<VersionCatalogsExtension>().named("libs")

dependencies {
    implementation(libs.findLibrary("mapstruct-core").get())
    annotationProcessor(libs.findLibrary("mapstruct-processor").get())
}
```

#### Groovy convention plugins
Use the `groovy-gradle-plugin` plugin in `build-logic/build.gradle` and put `shop.java-conventions.gradle` files in `build-logic/src/main/groovy/`.

#### Root build.gradle.kts
With convention plugins, the root build file can be empty or deleted: every module configures itself.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 8.6 Module Build Files

Each module's build file is short: a convention plugin plus the module's own dependencies.

#### common/build.gradle.kts

```kotlin
plugins {
    id("shop.spring-library-conventions")
}

dependencies {
    api("jakarta.validation:jakarta.validation-api")    // DTOs carry @NotBlank etc. → part of the API
    implementation("org.apache.commons:commons-lang3")  // used only inside common
}
```

```java
// common/src/main/java/com/example/shop/common/OrderDto.java
package com.example.shop.common;

public record OrderDto(Long id, @NotBlank String product, @Positive int quantity) { }
```

#### persistence/build.gradle.kts

```kotlin
plugins {
    id("shop.spring-library-conventions")
}

dependencies {
    api("org.springframework.boot:spring-boot-starter-data-jpa")   // JpaRepository appears in our interfaces → api
    runtimeOnly("org.postgresql:postgresql")
    testRuntimeOnly("com.h2database:h2")                           // in-memory DB for @DataJpaTest
}
```

```java
// persistence/src/main/java/com/example/shop/persistence/OrderEntity.java
package com.example.shop.persistence;

@Entity
@Table(name = "orders")
@Getter @Setter @NoArgsConstructor
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String product;
    private int quantity;
}

// persistence/src/main/java/com/example/shop/persistence/OrderRepository.java
public interface OrderRepository extends JpaRepository<OrderEntity, Long> { }
```

#### service/build.gradle.kts

```kotlin
plugins {
    id("shop.spring-library-conventions")
}

dependencies {
    api(projects.common)                    // service methods take and return OrderDto
    implementation(projects.persistence)    // repositories are an internal detail of the service layer
}
```

```java
// service/src/main/java/com/example/shop/service/OrderService.java
package com.example.shop.service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;      // from :persistence

    @Transactional
    public OrderDto create(OrderDto dto) {               // OrderDto from :common
        OrderEntity entity = new OrderEntity();
        entity.setProduct(dto.product());
        entity.setQuantity(dto.quantity());
        OrderEntity saved = orderRepository.save(entity);
        return new OrderDto(saved.getId(), saved.getProduct(), saved.getQuantity());
    }
}
```

#### app/build.gradle.kts

```kotlin
plugins {
    id("shop.spring-boot-app-conventions")
}

dependencies {
    implementation(projects.service)
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
}
```

```java
// app/src/main/java/com/example/shop/ShopApplication.java
package com.example.shop;

@SpringBootApplication          // package com.example.shop → scans com.example.shop.** in every module
public class ShopApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShopApplication.class, args);
    }
}

// app/src/main/java/com/example/shop/web/OrderController.java
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
class OrderController {

    private final OrderService orderService;             // from :service

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    OrderDto create(@Valid @RequestBody OrderDto dto) {   // OrderDto visible through service's api(projects.common)
        return orderService.create(dto);
    }
}
```

`app/src/main/resources/application.yml` holds the configuration for the whole application (datasource, JPA, server port).

[⬆ Back to Table of Contents](#table-of-contents)

---

## 8.7 api vs implementation Between Modules

Both put the dependency on the module's own compile and runtime classpath. The difference is what **consumers** of the module see:

| In `service/build.gradle.kts` | `service` compiles against it | `app` compiles against it | `app` has it at runtime |
|-------------------------------|:-----------------------------:|:-------------------------:|:-----------------------:|
| `api(projects.common)` | ✔ | ✔ | ✔ |
| `implementation(projects.persistence)` | ✔ | ✘ | ✔ |

What this means for `app`:

```java
// app module
import com.example.shop.common.OrderDto;            // ✔ compiles: common is an api dependency of service
import com.example.shop.persistence.OrderRepository; // ✘ compile error: package com.example.shop.persistence does not exist
```

At runtime the `persistence` jar **is** in `app`'s `bootJar` (`BOOT-INF/lib/persistence-0.0.1-SNAPSHOT.jar`), so Spring Data still creates the repositories. Only compile-time access is blocked: the layering rule "controllers don't use repositories" is enforced by the build.

**When to use `api`:** the dependency's types appear in the module's **public** API: method parameters and return types, superclasses and interfaces, public fields, or annotations consumers must see. Everything else: `implementation`.

**Why not `api` everywhere:**
- Internal libraries leak to every consumer, and accidental usages creep in.
- Slower builds: when an `implementation` dependency changes, Gradle doesn't recompile the consumers (compile avoidance); with `api` it does.

`api` exists only with the `java-library` plugin (not plain `java`).

[⬆ Back to Table of Contents](#table-of-contents)

---

## 8.8 Spring Boot Wiring Across Modules

#### Only the application module applies the Boot plugin
Library modules produce plain jars that are packaged inside the application's `bootJar`:

```
app/build/libs/app-0.0.1-SNAPSHOT.jar
└── BOOT-INF/lib/
    ├── common-0.0.1-SNAPSHOT.jar
    ├── persistence-0.0.1-SNAPSHOT.jar
    ├── service-0.0.1-SNAPSHOT.jar
    ├── spring-boot-3.5.6.jar
    └── ...
```

If a library module must apply the Boot plugin (for example to reuse a convention), switch the executable jar off:

```kotlin
tasks.bootJar { enabled = false }
tasks.jar { enabled = true }
```

#### Component scanning and JPA scanning
`@SpringBootApplication` scans its own package and sub-packages; JPA entity scanning and Spring Data repository scanning start from the same package. With a shared root package (`com.example.shop`), beans, entities and repositories from every module are found with no extra configuration.

If modules use different root packages, list them explicitly:

```java
@SpringBootApplication(scanBasePackages = {"com.example.shop", "com.acme.persistence"})
@EnableJpaRepositories(basePackages = "com.acme.persistence")
@EntityScan(basePackages = "com.acme.persistence")
public class ShopApplication { }
```

See [@SpringBootApplication](java-spring-boot-document-with-full-details.md#springbootapplication), [@ComponentScan](java-spring-boot-document-with-full-details.md#componentscan) and [@EnableJpaRepositories](java-spring-boot-document-with-full-details.md#enablejparepositories).

#### Configuration files
- Keep `application.yml` in the **application** module only. A library's `src/main/resources/application.yml` lands on the same classpath, and only one of the two files is loaded.
- A library that needs its own beans or defaults should provide an auto-configuration (`META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`, see [@AutoConfiguration](java-spring-boot-document-with-full-details.md#autoconfiguration)) or a uniquely named file loaded with `@PropertySource`.

#### Testing a library module
`@SpringBootTest` and slice tests such as `@DataJpaTest` search **upwards** from the test's package for a `@SpringBootConfiguration`. A library module has no main class, so the test fails with `Unable to find a @SpringBootConfiguration...`. Add a small test-only application class:

```java
// persistence/src/test/java/com/example/shop/persistence/PersistenceTestApplication.java
package com.example.shop.persistence;

@SpringBootApplication
class PersistenceTestApplication { }
```

```java
// persistence/src/test/java/com/example/shop/persistence/OrderRepositoryTest.java
@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    OrderRepository orderRepository;

    @Test
    void savesOrder() {
        OrderEntity entity = new OrderEntity();
        entity.setProduct("Book");
        entity.setQuantity(2);
        assertThat(orderRepository.save(entity).getId()).isNotNull();
    }
}
```

More on test slices: [Testing Annotations](java-spring-boot-document-with-full-details.md#16-testing).

[⬆ Back to Table of Contents](#table-of-contents)

---

## 8.9 Platform Module (Internal BOM)

Instead of the `io.spring.dependency-management` plugin, a build can define its **own BOM** in a module that uses Gradle's `java-platform` plugin. All versions (Spring Boot, Spring Cloud, other libraries) are then in one module, and other modules import it like any BOM.

```kotlin
// settings.gradle.kts
include("platform", "common", "persistence", "service", "app")
```

```kotlin
// platform/build.gradle.kts
plugins {
    `java-platform`
}

javaPlatform {
    allowDependencies()          // needed to import other BOMs
}

dependencies {
    api(platform("org.springframework.boot:spring-boot-dependencies:3.5.6"))
    api(platform("org.springframework.cloud:spring-cloud-dependencies:2025.0.0"))

    constraints {
        api("org.mapstruct:mapstruct:1.6.3")
        api("org.mapstruct:mapstruct-processor:1.6.3")
        api("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.9")
    }
}
```

```kotlin
// in a convention plugin or module build file
dependencies {
    implementation(platform(project(":platform")))
    annotationProcessor(platform(project(":platform")))     // processors resolve versions separately
    testImplementation(platform(project(":platform")))

    implementation("org.mapstruct:mapstruct")                 // version from :platform
    annotationProcessor("org.mapstruct:mapstruct-processor")
}
```

The platform can be published (`maven-publish` with `from(components["javaPlatform"])`) and used as the company BOM by other repositories.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 8.10 Sharing Test Code (Test Fixtures)

Test helpers (builders, sample data, base test classes) defined in one module are not visible to other modules' tests. The `java-test-fixtures` plugin creates a **test fixtures** source set that other modules can use:

```kotlin
// common/build.gradle.kts
plugins {
    id("shop.spring-library-conventions")
    `java-test-fixtures`
}

dependencies {
    testFixturesImplementation("org.assertj:assertj-core")   // dependencies of the fixtures themselves
}
```

```java
// common/src/testFixtures/java/com/example/shop/common/OrderDtoFixtures.java
package com.example.shop.common;

public final class OrderDtoFixtures {
    public static OrderDto anOrder() {
        return new OrderDto(null, "Book", 1);
    }
}
```

```kotlin
// service/build.gradle.kts
dependencies {
    testImplementation(testFixtures(projects.common))
}
```

| Configuration | Description |
|---------------|-------------|
| `testFixturesApi` | Fixture dependencies visible to consumers of the fixtures. |
| `testFixturesImplementation` | Fixture dependencies used internally. |
| `testFixtures(project)` | Consume another module's fixtures. |

The fixtures can use the module's main code, and the module's own tests can use the fixtures.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 8.11 Running Tasks in a Multi-Module Build

Run commands from the **root** directory. A task path is `:<module>:<task>`; a task name without a path runs in **every** module that has that task.

| Command | What it does |
|---------|--------------|
| `./gradlew build` | Builds and tests all modules. |
| `./gradlew test` | Runs `test` in every module. |
| `./gradlew :app:bootRun` | Runs the application. |
| `./gradlew :app:bootJar` | Builds `app/build/libs/app-0.0.1-SNAPSHOT.jar` (and the jars of the modules it needs). |
| `./gradlew :service:test` | Tests only `service` (compiling `common` and `persistence` as needed). |
| `./gradlew :service:build` | Builds `service` and the modules it depends on. |
| `./gradlew :service:buildNeeded` | Builds **and tests** `service` and every module it depends on. |
| `./gradlew :common:buildDependents` | Builds and tests `common` and every module that depends on it; useful after changing a shared module. |
| `./gradlew :app:dependencies --configuration runtimeClasspath` | Dependency tree of one module. |
| `./gradlew projects` | Shows the module hierarchy. |
| `./gradlew build -x test` | Build everything without tests. |
| `./gradlew -p app bootRun` | Run a task in a module's directory (same as `cd app && ../gradlew bootRun`). |

Build and containerize the application module:

```bash
./gradlew :app:bootBuildImage --imageName=shop/app:latest
# or with a Dockerfile in app/
./gradlew :app:bootJar && docker build -t shop/app app/
```

[⬆ Back to Table of Contents](#table-of-contents)

---

## 8.12 Multiple Spring Boot Applications in One Build

A repository with several deployable services that share code:

```
platform-services/
├── settings.gradle.kts
├── build-logic/
├── libs/
│   ├── common/              ← shared DTOs, error handling
│   └── security/            ← shared security configuration (auto-configuration)
└── services/
    ├── order-service/       ← Spring Boot app, port 8081
    └── user-service/        ← Spring Boot app, port 8082
```

```kotlin
// settings.gradle.kts
include("libs:common", "libs:security")
include("services:order-service", "services:user-service")
```

```kotlin
// services/order-service/build.gradle.kts
plugins {
    id("shop.spring-boot-app-conventions")
}

dependencies {
    implementation(projects.libs.common)
    implementation(projects.libs.security)
    implementation("org.springframework.boot:spring-boot-starter-web")
}
```

```bash
./gradlew :services:order-service:bootRun
./gradlew :services:user-service:bootRun
./gradlew bootBuildImage            # builds an image for every application module
```

Things to watch:
- Each application has its own `application.yml`, port and main class in its own package (`com.example.order`, `com.example.user`); shared libraries must be in packages that each application scans, or provide auto-configuration.
- Module names become jar names. Two modules both named `common` (`:order:common`, `:user:common`) produce the same jar file name; give modules unique names.
- Keep shared libraries small. A large shared module couples services so they must be released together.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 8.13 Composite Builds

A **composite build** combines separate Gradle builds (each with its own `settings.gradle.kts`) without making them one multi-module build. Typical use: work on a shared library that normally comes from Nexus, from its source on disk.

```kotlin
// order-service/settings.gradle.kts
rootProject.name = "order-service"
includeBuild("../payment-client")        // a separate Gradle build
```

```kotlin
// order-service/build.gradle.kts
dependencies {
    implementation("com.example:payment-client:1.4.0")   // replaced by the included build's project
}
```

Gradle substitutes the dependency with the included build's project when its `group:name` match. Changes in `payment-client` are picked up on the next build, without publishing.

| | Multi-module build | Composite build |
|---|--------------------|-----------------|
| Settings files | One | One per build |
| Versioning | Modules share a version | Each build has its own version |
| Built separately? | No, it's one build | Yes, each build also works on its own |
| Typical use | Modules of one product | Separate repositories, `build-logic` |

[⬆ Back to Table of Contents](#table-of-contents)

---

## 8.14 Common Mistakes

| Mistake | Symptom | Fix |
|---------|---------|-----|
| Boot plugin applied to a library module | `bootJar` fails: `Main class name has not been configured...` | Apply the Boot plugin only to the application module, or disable `bootJar` and enable `jar`. |
| `implementation` for a type used in a public method | Consumer compile error: `cannot access X` / `class file for X not found` | Use `api` for that dependency. |
| `api` everywhere | Slow builds, layers bypassed | Use `implementation` by default. |
| Circular module dependencies | `Circular dependency between the following tasks` | Move shared code to a lower module (`common`). |
| `repositories { }` in a module with `FAIL_ON_PROJECT_REPOS` | `Build was configured to prefer settings repositories over project repositories but repository 'MavenRepo' was added by build file 'app/build.gradle.kts'` | Declare repositories only in `settings.gradle.kts`. |
| Lombok configured only in some modules | `cannot find symbol` for getters/builders in another module | Configure Lombok in a convention plugin used by every module. |
| `@SpringBootApplication` in a package that doesn't contain the modules' packages | `Consider defining a bean of type 'OrderService' in your configuration`, repositories not found | Use a shared root package or set `scanBasePackages`, `@EnableJpaRepositories`, `@EntityScan`. |
| `application.yml` in a library module | Some properties ignored | Keep `application.yml` in the application module only. |
| `@SpringBootTest` / `@DataJpaTest` in a library module | `Unable to find a @SpringBootConfiguration` | Add a test-only `@SpringBootApplication` class. |
| Different Spring Boot versions in different modules | `NoSuchMethodError`, `ClassNotFoundException` at runtime | Set versions once: version catalog + convention plugins, or a platform module. |
| Running `gradle` instead of `./gradlew` | Build works on one machine and fails on another | Always use the wrapper. |

[⬆ Back to Table of Contents](#table-of-contents)

---

# 9. Gradle Commands Cheat Sheet

On Windows use `gradlew.bat` or `.\gradlew` instead of `./gradlew`.

| Command | Description |
|---------|-------------|
| `./gradlew tasks` / `tasks --all` | List available tasks. |
| `./gradlew help --task bootRun` | Details and options of one task. |
| `./gradlew build` | Compile, test and package. |
| `./gradlew clean build` | Delete `build/` first. Rarely needed: Gradle's up-to-date checks are reliable. |
| `./gradlew assemble` | Package without running tests. |
| `./gradlew build -x test` | Build, skipping the `test` task. |
| `./gradlew test` | Run tests. |
| `./gradlew test --tests "com.example.order.OrderServiceTest"` | Run one test class. |
| `./gradlew test --tests "*OrderServiceTest.createsOrder"` | Run one test method. |
| `./gradlew test --rerun` | Run tests even if nothing changed (Gradle 7.6+). |
| `./gradlew bootRun` | Start the application. |
| `./gradlew bootRun --args='--spring.profiles.active=dev'` | Start with program arguments. |
| `./gradlew bootJar` | Build the executable jar. |
| `./gradlew bootBuildImage` | Build a container image. |
| `./gradlew dependencies --configuration runtimeClasspath` | Dependency tree. |
| `./gradlew dependencyInsight --dependency jackson-databind --configuration runtimeClasspath` | Why a library / version is on the classpath. |
| `./gradlew build --refresh-dependencies` | Re-check all dependencies in the repositories (ignore the cache). |
| `./gradlew build --offline` | Build with cached dependencies only. |
| `./gradlew build --scan` | Upload a build scan (detailed web report) to scans.gradle.com. |
| `./gradlew build --info` / `--debug` / `--stacktrace` | More logging / full stack trace on failure. |
| `./gradlew build --continuous` (`-t`) | Re-run the task when files change. |
| `./gradlew build --dry-run` (`-m`) | Show which tasks would run without running them. |
| `./gradlew build --rerun-tasks` | Ignore up-to-date checks for all tasks. |
| `./gradlew build --configuration-cache` | Use the configuration cache for this run. |
| `./gradlew build --parallel` | Build modules in parallel for this run. |
| `./gradlew projects` | Show the module structure. |
| `./gradlew properties` | Show project properties. |
| `./gradlew -q javaToolchains` | Show detected JDKs. |
| `./gradlew buildEnvironment` | Show plugins on the build script classpath. |
| `./gradlew wrapper --gradle-version 8.14.3` | Upgrade the wrapper. |
| `./gradlew --status` / `--stop` | Show / stop Gradle daemons. |
| `./gradlew publishToMavenLocal` | Install artifacts to `~/.m2`. |

[⬆ Back to Table of Contents](#table-of-contents)

---

# 10. Maven to Gradle Mapping

| Maven (`pom.xml`) | Gradle |
|-------------------|--------|
| `<parent>spring-boot-starter-parent</parent>` | Plugins `org.springframework.boot` + `io.spring.dependency-management` |
| `<groupId>` / `<version>` | `group` / `version` |
| `<artifactId>` | `rootProject.name` (settings) or the module directory name |
| `<properties><java.version>21</java.version>` | `java { toolchain { languageVersion = JavaLanguageVersion.of(21) } }` |
| Custom `<properties>` | `gradle.properties` or `extra["..."]` |
| `<dependencyManagement><scope>import</scope>` | `dependencyManagement { imports { mavenBom(...) } }` or `platform(...)` |
| `<scope>compile</scope>` (default) | `implementation` (or `api` in libraries) |
| `<scope>provided</scope>` | `compileOnly` |
| `<optional>true</optional>` (Lombok) | `compileOnly` + `annotationProcessor` |
| `<scope>runtime</scope>` | `runtimeOnly` |
| `<scope>test</scope>` | `testImplementation` / `testRuntimeOnly` |
| `<exclusions>` | `exclude(group = "...", module = "...")` |
| `<repositories>` | `repositories { }` (or in `settings.gradle.kts`) |
| `spring-boot-maven-plugin` | Spring Boot Gradle plugin |
| `<modules>` in the parent POM | `include(...)` in `settings.gradle(.kts)` |
| Parent POM with shared config | Convention plugins in `build-logic` |
| `<profiles>` | `-P` properties + conditions in the build script |
| `mvnw` | `gradlew` |
| `target/` | `build/` |
| `mvn clean install` | `./gradlew build` (+ `publishToMavenLocal`) |
| `mvn spring-boot:run` | `./gradlew bootRun` |
| `mvn package -DskipTests` | `./gradlew assemble` or `./gradlew build -x test` |
| `mvn dependency:tree` | `./gradlew dependencies` |
| `mvn spring-boot:build-image` | `./gradlew bootBuildImage` |
| `mvn -pl app -am package` | `./gradlew :app:build` |

Run `gradle init` in a Maven project's directory to generate a first Gradle build from `pom.xml`, then clean it up by hand.

Dependency snippets in the [Spring Boot Annotations Guide](java-spring-boot-document-with-full-details.md) are written for Maven; translate them with the table above. For example:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <optional>true</optional>
</dependency>
```

becomes

```kotlin
annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
```

[⬆ Back to Table of Contents](#table-of-contents)

---

# 11. Best Practices

**Build files**
- Use the **Kotlin DSL** for new projects and always run builds with the **wrapper**.
- Use a **Java toolchain** instead of relying on `JAVA_HOME`.
- Omit versions for Spring Boot-managed dependencies; let the BOM choose tested versions.
- Keep versions in a **version catalog**; avoid dynamic versions (`1.+`, `latest.release`).
- Use the narrowest configuration: `implementation` over `api`, `runtimeOnly` for drivers, `developmentOnly` for devtools, `compileOnly` + `annotationProcessor` for Lombok.
- Register tasks lazily (`tasks.register`, `configureEach`) and keep work inside `doLast { }`.

**Spring Boot**
- Disable the `-plain.jar` in application modules (`tasks.jar { enabled = false }`) so only one jar is in `build/libs`.
- Use `buildInfo { excludes = setOf("time") }` to keep builds reproducible and cacheable.
- Use layered jars or `bootBuildImage` for container images.

**Multi-module**
- Put shared build configuration in **convention plugins** (`build-logic`), not in `subprojects { }`.
- Declare repositories once in `settings.gradle.kts` with `FAIL_ON_PROJECT_REPOS`.
- Apply the Spring Boot plugin only to application modules.
- Give all modules a common root package so `@SpringBootApplication` finds everything.
- Keep dependencies one-directional; a module should depend only on the layers below it.
- Enable `org.gradle.parallel`, `org.gradle.caching` and, when your plugins support it, `org.gradle.configuration-cache`.

**Security**
- Never commit credentials; use `~/.gradle/gradle.properties` or `ORG_GRADLE_PROJECT_*` environment variables.
- Pin the wrapper with `distributionSha256Sum` and commit `gradle-wrapper.jar`.

[⬆ Back to Table of Contents](#table-of-contents)

---

# 12. Resources

- [Gradle User Manual](https://docs.gradle.org/current/userguide/userguide.html)
- [Gradle: Structuring multi-project builds](https://docs.gradle.org/current/userguide/multi_project_builds.html)
- [Gradle: Sharing build logic with convention plugins](https://docs.gradle.org/current/userguide/sharing_build_logic_between_subprojects.html)
- [Gradle: Version catalogs](https://docs.gradle.org/current/userguide/version_catalogs.html)
- [Spring Boot Gradle Plugin Reference](https://docs.spring.io/spring-boot/gradle-plugin/index.html)
- [Spring guide: Creating a Multi Module Project](https://spring.io/guides/gs/multi-module)
- [Spring Initializr](https://start.spring.io)

[⬆ Back to Table of Contents](#table-of-contents)
