package io.github.michaelliv.lucene4k

import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.store.Directory


class IndexWriterBuilder {
    private var indexWriterConfig: (() -> IndexWriterConfig)? = null
    fun withIndexWriterConfig(block: () -> IndexWriterConfig) {
        indexWriterConfig = block
    }

    private var directory: (() -> Directory)? = null
    fun withDirectory(block: () -> Directory) {
        directory = block
    }

    internal fun build(): IndexWriter {
        return IndexWriter(
            directory?.invoke() ?: throw Exception("Directory was not initialized"),
            indexWriterConfig?.invoke() ?: throw Exception("IndexWriterConfig was not initialized")
        )
    }
}


fun buildIndexWriter(block: IndexWriterBuilder.() -> Unit) = IndexWriterBuilder().also(block).build()
