plugins {
    alias(kotlinx.plugins.multiplatform)
    alias(kotlinx.plugins.serialization)
    alias(androidx.plugins.library)
    alias(libs.plugins.sqldelight)
}

kotlin {
    androidTarget()
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(projects.domain)

                api(libs.bundles.db)
            }
        }
        val androidMain by getting {
            dependencies {
                api(libs.bundles.db.android)
                implementation(projects.sourceApi)
            }
        }
    }
}

android {
    namespace = "yokai.data"
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("yokai.data")
            dialect(libs.sqldelight.dialects.sql)
            schemaOutputDirectory.set(project.file("./src/commonMain/sqldelight"))
        }
    }
}
