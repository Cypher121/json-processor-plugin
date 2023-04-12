@file:JvmName("CopySpecExtensions")

package coffee.cypher.json_processor

import groovy.lang.Closure
import org.gradle.api.file.CopySpec

fun CopySpec.processJson(config: JsonFilterConfiguration.() -> Unit) {
    JsonFilterConfiguration(this).apply(config).doFilter()
}

fun CopySpec.processJson(closure: Closure<*>) {
    closureCall(this::processJson, closure)
}