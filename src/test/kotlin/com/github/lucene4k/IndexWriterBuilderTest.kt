package com.github.lucene4k

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.store.ByteBuffersDirectory

class IndexWriterBuilderTest : ShouldSpec({
    should("buildIndexWriter should create an index writer") {
        buildIndexWriter {
            withDirectory { ByteBuffersDirectory() }
            withIndexWriterConfig { IndexWriterConfig(StandardAnalyzer()) }
        }.use { iw ->
            iw.isOpen shouldBe true
            iw.analyzer.shouldBeTypeOf<StandardAnalyzer>()
            iw.addDocument { storeFields { textField("field", "this is a test") } }
            iw.docStats.numDocs shouldBe 1
        }
    }
})
