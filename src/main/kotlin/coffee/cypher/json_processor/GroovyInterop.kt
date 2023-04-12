package coffee.cypher.json_processor

import groovy.lang.Closure

inline fun <R> closureCall(func: (R.() -> Unit) -> Unit, closure: Closure<*>) {
    func {
        closure.delegate = this
        closure.call()
    }
}

inline fun <T, A> closureCallExplicit(func: ((A) -> T) -> Unit, closure: Closure<T>) {
    func {
        closure.call(it)
    }
}