@file:JvmName("JsonPipelineScopeExtensions")

package coffee.cypher.json_processor

import blue.endless.jankson.JsonArray
import blue.endless.jankson.JsonElement
import blue.endless.jankson.JsonObject
import blue.endless.jankson.JsonPrimitive

class JsonPipelineScope {
    private val processors = mutableListOf<(JsonElement) -> JsonElement>()

    fun transform(mapping: (JsonElement) -> JsonElement) {
        processors += mapping
    }

    internal fun collect() = processors
}

fun JsonPipelineScope.mapPrimitives(mapping: (JsonPrimitive) -> JsonElement) = transform {
    processPrimitives(it, mapping)
}

private fun processPrimitives(element: JsonElement, mapping: (JsonPrimitive) -> JsonElement): JsonElement {
    return when (element) {
        is JsonPrimitive -> mapping(element)
        is JsonArray -> JsonArray(element.map { processPrimitives(it, mapping) }, element.marshaller)
        is JsonObject -> element.mapValuesTo(JsonObject()) { (_, v) -> processPrimitives(v, mapping) }
            .also { obj -> obj.marshaller = element.marshaller }

        else -> element
    }
}

fun JsonPipelineScope.mapStrings(mapping: (String) -> String) = mapPrimitives {
    val value = it.value

    if (value is String) {
        JsonPrimitive(mapping(value))
    } else {
        JsonPrimitive(value)
    }
}

fun JsonPipelineScope.flatten(flattenConfig: JsonFlattenScope.() -> Unit = {}) {
    val flattener = JsonFlattenScope().apply(flattenConfig).createFlattener()

    transform(flattener)
}
