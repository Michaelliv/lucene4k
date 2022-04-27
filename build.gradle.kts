import java.util.*

plugins {
    `java-library`
    `maven-publish`
    signing
}

group = "io.github.michaelliv.lucene4k"
version = "1.0.0"

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("library") {
            from(components["java"])

            artifactId = "lucene4k"
            pom {
                name.set("lucene4k")
                description.set("A Kotlin builder DSL wrapper for common Lucene functionality")
                url.set("https://github.com/Michaelliv/lucene4k")
                licenses {
                    license {
                        name.set("MIT")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("Michaellivs")
                        name.set("Michael Livshits")
                        email.set("livsmichael@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/Michaelliv/lucene4k.git")
                    developerConnection.set("scm:git:ssh://github.com/Michaelliv/lucene4k.git")
                    url.set("https://github.com/Michaelliv/lucene4k")
                }
            }
        }
    }

    repositories {
        maven {
            credentials(PasswordCredentials::class.java)
            name = "lucene4k"
            setUrl("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
        }
    }

}

repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

signing {
    sign(publishing.publications)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.21")
    implementation("org.apache.lucene:lucene-core:9.1.0")
    implementation("org.apache.lucene:lucene-queryparser:9.1.0")
    implementation("org.apache.lucene:lucene-queries:9.1.0")
    implementation("org.apache.lucene:lucene-misc:9.1.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.6.21")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:1.6.21")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.2.3")
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.2.3")
    testImplementation("io.kotest:kotest-property-jvm:5.2.3")
}