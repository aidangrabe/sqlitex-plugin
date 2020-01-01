package com.aidangrabe.sqlitex.extensions

/**
 * Returns `true` when the given [String] starts with at least one of the given [prefixes].
 */
fun String.startsWithAnyOf(prefixes: List<String>): Boolean {
    for (prefix in prefixes) {
        if (startsWith(prefix)) return true
    }
    return false
}