package coffee.cypher.json_processor

import blue.endless.jankson.JsonElement
import groovy.lang.Closure
import groovy.lang.DelegatesTo
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType
import org.codehaus.groovy.runtime.DefaultGroovyMethods
import org.gradle.api.file.ContentFilterable

private inline fun <R> closureCall(func: (R.() -> Unit) -> Unit, closure: Closure<*>) {
    func {
        closure.delegate = this
        closure.call()
    }
}

private inline fun <T, A> closureCallExplicit(func: ((A) -> T) -> Unit, closure: Closure<T>) {
    func {
        closure.call(it)
    }
}

private inline fun <reified T, reified M> mixin() {
    DefaultGroovyMethods.mixin(T::class.java, M::class.java)
}

@Suppress("UNUSED")
internal class GroovyInterop {
    companion object {
        fun setup() {
            mixin<ContentFilterable, ContentFilterableExtensions>()
            mixin<JsonPipelineScope, JsonPipelineScopeExtensions>()
            mixin<JsonFilterConfiguration, JsonFilterConfigurationExtensions>()
        }
    }

    class JsonFilterConfigurationExtensions {
        companion object {
            @JvmStatic
            fun JsonFilterConfiguration.pipeline(
                @DelegatesTo(JsonPipelineScope::class)
                closure: Closure<*>
            ) {
                closureCall(this::pipeline, closure)
            }
        }
    }

    class JsonPipelineScopeExtensions {
        companion object {
            @JvmStatic
            fun JsonPipelineScope.transform(
                @ClosureParams(SimpleType::class, options = ["blue.endless.jankson.JsonElement"])
                closure: Closure<JsonElement>
            ) {
                closureCallExplicit(this::transform, closure)
            }

            @JvmStatic
            fun JsonPipelineScope.mapPrimitives(
                @ClosureParams(SimpleType::class, options = ["blue.endless.jankson.JsonPrimitive"])
                closure: Closure<JsonElement>
            ) {
                closureCallExplicit(this::mapPrimitives, closure)
            }

            @JvmStatic
            fun JsonPipelineScope.mapStrings(
                @ClosureParams(SimpleType::class, options = ["java.lang.String"])
                closure: Closure<String>
            ) {
                closureCallExplicit(this::mapStrings, closure)
            }

            @JvmStatic
            fun JsonPipelineScope.flatten(
                @DelegatesTo(JsonFlattenScope::class)
                closure: Closure<*>
            ) {
                closureCall(this::flatten, closure)
            }
        }
    }

    class ContentFilterableExtensions {
        companion object {
            @JvmStatic
            fun ContentFilterable.processJson(
                @DelegatesTo(JsonFilterConfiguration::class)
                closure: Closure<*>
            ) {
                closureCall(this::processJson, closure)
            }
        }
    }
}