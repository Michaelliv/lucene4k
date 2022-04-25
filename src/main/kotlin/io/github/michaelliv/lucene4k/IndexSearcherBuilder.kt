package io.github.michaelliv.lucene4k

import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexCommit
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.store.Directory

class IndexSearcherBuilder {
    private var directory: (() -> Directory)? = null
    fun withDirectory(block: () -> Directory) {
        directory = block
    }

    private var indexWriter: (() -> IndexWriter)? = null
    fun withIndexWriter(block: () -> IndexWriter) {
        indexWriter = block
    }

    private var indexCommit: (() -> IndexCommit)? = null
    fun withIndexCommit(block: () -> IndexCommit) {
        indexCommit = block
    }

    internal fun build(): IndexSearcher {
        directory?.invoke()?.let(DirectoryReader::open)?.let { return IndexSearcher(it) }
        indexWriter?.invoke()?.let(DirectoryReader::open)?.let { return IndexSearcher(it) }
        indexCommit?.invoke()?.let(DirectoryReader::open)?.let { return IndexSearcher(it) }
        throw Exception("Either Directory, IndexWriter or IndexCommit must be initialized ")
    }
}

fun buildIndexSearcher(block: IndexSearcherBuilder.() -> Unit) = IndexSearcherBuilder().also(block).build()