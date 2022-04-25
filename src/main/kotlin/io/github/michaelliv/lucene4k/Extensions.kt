package io.github.michaelliv.lucene4k

import org.apache.lucene.document.DoubleRange
import org.apache.lucene.document.FloatRange
import org.apache.lucene.document.IntRange
import org.apache.lucene.document.LongRange
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.search.BooleanQuery

inline fun <T> T.`if`(boolean: Boolean, block: (T) -> Unit): T {
    if (boolean) block(this)
    return this
}

fun <T> List<T>.second(): T {
    if (this.size <= 1)
        throw NoSuchElementException("List does not have second element.")
    return this[1]
}

fun intRange(k: String, min: Iterable<Int>, max: Iterable<Int>) =
    IntRange(k, min.toList().toIntArray(), max.toList().toIntArray())

fun floatRange(k: String, min: Iterable<Float>, max: Iterable<Float>) =
    FloatRange(k, min.toList().toFloatArray(), max.toList().toFloatArray())

fun longRange(k: String, min: Iterable<Long>, max: Iterable<Long>) =
    LongRange(k, min.toList().toLongArray(), max.toList().toLongArray())

fun doubleRange(k: String, min: Iterable<Double>, max: Iterable<Double>) =
    DoubleRange(k, min.toList().toDoubleArray(), max.toList().toDoubleArray())

fun BooleanQuery.isEmpty() = this.clauses().isEmpty()
fun BooleanQuery.isNotEmpty() = this.clauses().isNotEmpty()
fun BooleanQuery.clausesAboveOrEquals(n: Int) = this.clauses().size > n
fun BooleanQuery.firstOrNull() = this.clauses().firstOrNull()

fun IndexWriter.addDocument(block: DocumentBuilder.() -> Unit) = this.addDocument(buildDocument(block))
