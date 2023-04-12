# Json Processor Gradle Plugin
Gradle plugin allowing JSON/JSON5 processing as part of Copy and ProcessResources tasks/actions

# Features

* JSON5 input and output support via [Jankson](https://github.com/falkreon/Jankson/)
* A variety of built-in processors, from object flattening, to visiting all primitives
* Full support for custom json processing via `transform((JsonElement) -> JsonElement`

# Usage example

## Kotlin DSL
```kt
import coffee.cypher.json_processor.*
import org.jetbrains.kotlin.util.capitalizeDecapitalize.toUpperCaseAsciiOnly

plugins {
    id("coffee.cypher.json-processor") version "0.1.0"
}

tasks.processResources {
    filesMatching("*.json5") {
        name = name.dropLast(1) // rename to .json

        processJson {
            pipeline {
                flatten {
                    //flatten nested objects, joining all keys with a dot, unless ending in a slash or double colon
                    //e.g. { a: { "b::": { c: 3 } } }
                    //into { "a.b::c": 3 }
                    keyDelimiter = "."
                    skipDelimiterAfter("/", "::")
                }

                mapStrings { it.toUpperCaseAsciiOnly() } // convert all string literals to upper-case
            }
        }
    }
}
```
## Groovy DSL
```gradle
plugins {
    id "coffee.cypher.json-processor" version "0.1.0"
}

tasks {
    processResources {
        filesMatching("*.json5") {
            name = name.dropRight(1) // rename to .json

            processJson {
                pipeline {
                    //flatten nested objects, joining all keys with a dot, unless ending in a slash or double colon
                    //e.g. { a: { "b::": { c: 3 } } }
                    //into { "a.b::c": 3 }
                    flatten {
                        keyDelimiter = "."
                        skipDelimiterAfter("/", "::")
                    }

                    mapStrings { it.toUpperCase() } // convert all string literals to upper-case
                }
            }
        }
    }
}
```
