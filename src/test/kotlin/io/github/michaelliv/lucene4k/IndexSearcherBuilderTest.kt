package io.github.michaelliv.lucene4k

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.store.ByteBuffersDirectory

class IndexSearcherBuilderTest : ShouldSpec({
    should("buildIndexSearcher should create index searcher") {
        val searcher = buildIndexSearcher {
            withIndexWriter {
                buildIndexWriter {
                    withDirectory { ByteBuffersDirectory() }
                    withIndexWriterConfig { IndexWriterConfig(StandardAnalyzer()) }
                }.also { iw ->
                    iw.isOpen shouldBe true
                    iw.analyzer.shouldBeTypeOf<StandardAnalyzer>()
                    iw.addDocument {
                        storeFields {
                            textField("field", "this is a test")
                            stringField("id", "123")
                        }
                    }
                    iw.docStats.numDocs shouldBe 1
                }
            }
        }

        val results = searcher.search(termQuery("field", "this"), 3)
        val doc = searcher.doc(results.scoreDocs.first().doc)
        doc.get("id") shouldBe "123"
    }

    should("buildIndexSearcher should create index searcher withDirectory") {
        val directory = ByteBuffersDirectory()

        buildIndexWriter {
            withDirectory { directory }
            withIndexWriterConfig { IndexWriterConfig(StandardAnalyzer()) }
        }.run {
            addDocument(buildDocument { storeFields { stringField("id", "123") } })
            docStats.numDocs shouldBe 1
            commit()
        }

        buildIndexSearcher {
            withDirectory { directory }
        }.run {
            search(termQuery("id", "123"), 1).totalHits.value shouldBe 1
        }
    }

    should("buildIndexSearcher should create index searcher withIndexCommit") {
        val directory = ByteBuffersDirectory()

        buildIndexWriter {
            withDirectory { directory }
            withIndexWriterConfig { IndexWriterConfig(StandardAnalyzer()) }
        }.run {
            addDocument(buildDocument { storeFields { stringField("id", "123") } })
            docStats.numDocs shouldBe 1
            commit()
        }

        val reader = DirectoryReader.listCommits(directory)

        buildIndexSearcher {
            withIndexCommit { reader.last() }
        }.run {
            search(termQuery("id", "123"), 1).totalHits.value shouldBe 1
        }
    }

    should("buildIndexSearcher should throw Exception when nothing is supplied") {
        shouldThrow<Exception> { buildIndexSearcher { } }
    }

})
