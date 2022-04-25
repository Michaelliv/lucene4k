package com.github.michaelliv.lucene4k

import org.apache.lucene.document.*
import org.apache.lucene.index.IndexableField
import org.apache.lucene.index.VectorSimilarityFunction
import java.net.InetAddress

open class DocumentBuilder {
    val fields: MutableList<IndexableField> = mutableListOf()

    class FieldBuilder(
        private val fields: MutableList<IndexableField>,
        private val stored: Boolean
    ) {
        private val store = if (stored) Field.Store.YES else Field.Store.NO

        fun stringField(k: String, vararg v: String) =
            v.forEach { fields.add(StringField(k, it, store)) }

        fun textField(k: String, vararg v: String) =
            v.forEach { fields.add(TextField(k, it, store)) }

        fun point(k: String, vararg v: Int) =
            fields.add(IntPoint(k, *v)).`if`(stored) { v.forEach { fields.add(StoredField(k, it)) } }

        fun point(k: String, vararg v: Long) =
            fields.add(LongPoint(k, *v)).`if`(stored) { v.forEach { fields.add(StoredField(k, it)) } }

        fun point(k: String, vararg v: Float) =
            fields.add(FloatPoint(k, *v)).`if`(stored) { v.forEach { fields.add(StoredField(k, it)) } }

        fun point(k: String, vararg v: Double) =
            fields.add(DoublePoint(k, *v)).`if`(stored) { v.forEach { fields.add(StoredField(k, it)) } }

        @JvmName("intRange")
        fun range(k: String, min: Iterable<Int>, max: Iterable<Int>) =
            fields.add(intRange(k, min, max))

        @JvmName("longRange")
        fun range(k: String, min: Iterable<Long>, max: Iterable<Long>) =
            fields.add(longRange(k, min, max))

        @JvmName("floatRange")
        fun range(k: String, min: Iterable<Float>, max: Iterable<Float>) =
            fields.add(floatRange(k, min, max))

        @JvmName("doubleRange")
        fun range(k: String, min: Iterable<Double>, max: Iterable<Double>) =
            fields.add(doubleRange(k, min, max))

        fun xyPoint(k: String, x: Float, y: Float) =
            fields.add(XYPointField(k, x, y)).`if`(stored) { fields.add(XYDocValuesField(k, x, y)) }

        fun feature(k: String, name: String, value: Float) =
            fields.add(FeatureField(k, name, value))

        fun knnVector(k: String, v: FloatArray, sim: VectorSimilarityFunction = VectorSimilarityFunction.EUCLIDEAN) =
            fields.add(KnnVectorField(k, v, sim))

        fun inetAddressPoint(k: String, v: InetAddress) = fields.add(InetAddressPoint(k, v))
        fun inetAddressRange(k: String, min: InetAddress, max: InetAddress) = fields.add(InetAddressRange(k, min, max))

    }

    fun storeFields(block: FieldBuilder.() -> Unit) =
        FieldBuilder(fields, true).also(block)

    fun indexFields(block: FieldBuilder.() -> Unit) =
        FieldBuilder(fields, false).also(block)

    fun build() = Document().also { fields.forEach(it::add) }
}


fun buildDocument(block: DocumentBuilder.() -> Unit): Document = DocumentBuilder().also(block).build()

fun dummyDocument(block: DocumentBuilder.() -> Unit): MutableList<IndexableField> = DocumentBuilder().also(block).fields