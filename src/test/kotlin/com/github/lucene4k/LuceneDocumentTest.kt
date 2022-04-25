package com.github.lucene4k

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import org.apache.lucene.document.*
import org.apache.lucene.document.IntRange
import org.apache.lucene.document.LongRange
import org.apache.lucene.document.DoubleRange
import org.apache.lucene.document.FloatRange
import java.net.InetAddress

class LuceneDocumentTest : ShouldSpec({
    should("buildDocument should create an empty document") {
        buildDocument { }.fields shouldHaveSize 0
    }

    should("storeFields should activate the store option for relevant fields") {
        val doc = buildDocument {
            storeFields {
                stringField("field", "test")
            }
        }
        doc.getField("field").fieldType().stored() shouldBe true
    }

    should("indexFields should deactivate the store option for relevant fields ") {
        val doc = buildDocument {
            indexFields {
                stringField("field", "test")
            }
        }
        doc.getField("field").fieldType().stored() shouldBe false
    }

    should("stringField should create a StringField in the document") {
        dummyDocument { storeFields { stringField("field", "test") } }.first().shouldBeTypeOf<StringField>()
    }

    should("textField should create a TextField in the document") {
        dummyDocument { storeFields { textField("field", "test") } }.first().shouldBeTypeOf<TextField>()
    }

    should("Document should contain fields and singular values") {
        val doc = buildDocument {
            storeFields {
                stringField("field1", "string")
                stringField("field3", "asd", "dsa")
            }
        }
        doc.getField("field1").stringValue() shouldBe "string"
        doc.getValues("field3") shouldContainAll listOf("asd", "dsa")
    }

    should("point should create point field in the document") {
        dummyDocument { storeFields { point("field", 1) } }.first().shouldBeTypeOf<IntPoint>()
        dummyDocument { storeFields { point("field", 1L) } }.first().shouldBeTypeOf<LongPoint>()
        dummyDocument { storeFields { point("field", 1F) } }.first().shouldBeTypeOf<FloatPoint>()
        dummyDocument { storeFields { point("field", 1.0) } }.first().shouldBeTypeOf<DoublePoint>()
    }

    should("range should create range field in the document") {
        dummyDocument { storeFields { range("field", listOf(1.toInt()), listOf(2.toInt())) } }.first()
            .shouldBeTypeOf<IntRange>()
        dummyDocument { storeFields { range("field", listOf(1L), listOf(2L)) } }.first().shouldBeTypeOf<LongRange>()
        dummyDocument { storeFields { range("field", listOf(1F), listOf(2F)) } }.first().shouldBeTypeOf<FloatRange>()
        dummyDocument { storeFields { range("field", listOf(1.0), listOf(2.0)) } }.first().shouldBeTypeOf<DoubleRange>()
    }

    should("xyPoint should create XYPointField field in the document") {
        val stored = dummyDocument { storeFields { xyPoint("field", 1F, 1F) } }
        stored.first().shouldBeTypeOf<XYPointField>()
        stored.second().shouldBeTypeOf<XYDocValuesField>()

        val notStored = dummyDocument { indexFields { xyPoint("field", 1F, 1F) } }
        notStored.first().shouldBeTypeOf<XYPointField>()
        shouldThrow<NoSuchElementException> { notStored.second() }
    }

    should("feature should create FeatureField field in the document") {
        dummyDocument { storeFields { feature("field", "featureName", 1F) } }.first().shouldBeTypeOf<FeatureField>()
    }

    should("knnVector should create KnnVectorField field in the document") {
        dummyDocument { storeFields { knnVector("field", floatArrayOf(1f, 2F)) } }.first()
            .shouldBeTypeOf<KnnVectorField>()
    }

    should("inetAddressPoint should create InetAddressPoint field in the document") {
        dummyDocument { storeFields { inetAddressPoint("field", InetAddress.getLocalHost()) } }.first()
            .shouldBeTypeOf<InetAddressPoint>()
    }

    should("inetAddressRange should create InetAddressRange field in the document") {
        dummyDocument {
            storeFields {
                inetAddressRange(
                    "field",
                    InetAddress.getLocalHost(),
                    InetAddress.getLocalHost()
                )
            }
        }.first().shouldBeTypeOf<InetAddressRange>()
    }

})
