@file:JvmName("JsonFilterConfigurationExtensions")

package coffee.cypher.json_filters

import blue.endless.jankson.JsonElement
import blue.endless.jankson.JsonGrammar
import groovy.lang.Closure
import org.gradle.api.file.CopySpec

class JsonFilterConfiguration(private val spec: CopySpec) {
    var outputFormat = JsonGrammar.STRICT
    private val pipeline = mutableListOf<(JsonElement) -> JsonElement>()

    fun pipeline(config: JsonPipelineScope.() -> Unit) {
        pipeline += JsonPipelineScope().apply(config).collect()
    }

    internal fun doFilter() {
        spec.filter(
            mapOf(
                "outputFormat" to outputFormat,
                "pipeline" to pipeline
            ),
            JsonProcessingFilter::class.java
        )
    }
}

fun JsonFilterConfiguration.outputJson5() {
    outputFormat = JsonGrammar.JSON5
}

fun JsonFilterConfiguration.pipeline(closure: Closure<*>) = closureCall(this::pipeline, closure)