@file:JvmName("JsonPipelineScopeExtensions")

package coffee.cypher.json_filters

import blue.endless.jankson.JsonArray
import blue.endless.jankson.JsonElement
import blue.endless.jankson.JsonObject
import blue.endless.jankson.JsonPrimitive
import groovy.lang.Closure

class JsonPipelineScope {
    private val processors = mutableListOf<(JsonElement) -> JsonElement>()

    fun transform(mapping: (JsonElement) -> JsonElement) {
        processors += mapping
    }

    internal fun collect() = processors
}

fun JsonPipelineScope.mapPrimitives(mapping: (JsonPrimitive) -> JsonElement) = transform {
    fun process(element: JsonElement): JsonElement {
        return when (element) {
            is JsonPrimitive -> mapping(element)
            is JsonArray -> JsonArray(element.map(::process), element.marshaller)
            is JsonObject -> element.mapValuesTo(JsonObject()) { (_, v) -> process(v) }
                .also { obj -> obj.marshaller = element.marshaller }

            else -> element
        }
    }

    process(it)
}

fun JsonPipelineScope.mapStrings(mapping: (String) -> String) = mapPrimitives {
    val value = it.value

    if (value is String) {
        JsonPrimitive(mapping(value))
    } else {
        JsonPrimitive(value)
    }
}

fun JsonPipelineScope.flatten(flattenConfig: JsonFlattenScope.() -> Unit) {
    val flattener = JsonFlattenScope().apply(flattenConfig).createFlattener()

    transform(flattener)
}

//groovy extensions

fun JsonPipelineScope.transform(closure: Closure<JsonElement>) = closureCallExplicit(this::transform, closure)
fun JsonPipelineScope.mapPrimitives(closure: Closure<JsonElement>) = closureCallExplicit(this::mapPrimitives, closure)
fun JsonPipelineScope.mapStrings(closure: Closure<String>) = closureCallExplicit(this::mapStrings, closure)
fun JsonPipelineScope.flatten(closure: Closure<*>) = closureCall(this::flatten, closure)