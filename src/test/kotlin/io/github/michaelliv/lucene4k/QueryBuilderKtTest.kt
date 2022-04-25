package io.github.michaelliv.lucene4k

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import org.apache.lucene.index.Term
import org.apache.lucene.search.BooleanClause
import org.apache.lucene.search.BooleanQuery
import org.apache.lucene.search.PhraseQuery
import org.apache.lucene.search.TermQuery

class QueryBuilderKtTest : ShouldSpec({
    should("buildBooleanQuery creates scope returns boolean query") {
        val empty = buildBooleanQuery { }
        empty.isEmpty() shouldBe true
        empty.isNotEmpty() shouldBe false
    }

    should("clausesAboveOr creates scope returns boolean query") {
        val notEmpty = buildBooleanQuery {
            shouldOccur { termQuery("field", "test") }
            shouldOccur { termQuery("field", "test") }
            shouldOccur { termQuery("field", "test") }
            shouldOccur { termQuery("field", "test") }
        }
        notEmpty.isNotEmpty() shouldBe true
        notEmpty.clausesAboveOrEquals(2) shouldBe true
    }

    should("shouldOccur creates SHOULD clause") {
        val q = buildBooleanQuery { shouldOccur { TermQuery(Term("test", "tests")) } }
        q.firstOrNull()?.occur shouldBe BooleanClause.Occur.SHOULD
    }

    should("mustOccur creates MUST clause") {
        val q = buildBooleanQuery { mustOccur { TermQuery(Term("test", "tests")) } }
        q.firstOrNull()?.occur shouldBe BooleanClause.Occur.MUST
    }

    should("mustNotOccur creates a MUST_NOT clause") {
        val q = buildBooleanQuery { mustNotOccur { TermQuery(Term("test", "tests")) } }
        q.firstOrNull()?.occur shouldBe BooleanClause.Occur.MUST_NOT
    }

    should("filter creates FILTER clause") {
        val q = buildBooleanQuery { filter { TermQuery(Term("test", "tests")) } }
        q.firstOrNull()?.occur shouldBe BooleanClause.Occur.FILTER
    }

    should("Multiple clauses can be created together") {
        val q = buildBooleanQuery {
            shouldOccur { TermQuery(Term("test", "tests")) }
            mustOccur { TermQuery(Term("test", "tests")) }
        }
        val clauses = q.clauses()
        clauses.first().occur shouldBe BooleanClause.Occur.SHOULD
        clauses.second().occur shouldBe BooleanClause.Occur.MUST
    }

    should("Boolean queries can contain boolean queries") {
        val q = buildBooleanQuery {
            shouldOccur {
                buildBooleanQuery {
                    mustOccur { TermQuery(Term("test", "tests")) }
                }
            }
        }

        q.clauses() shouldHaveSize 1
        q.clauses().first().occur shouldBe BooleanClause.Occur.SHOULD
        q.clauses().first().query.shouldBeTypeOf<BooleanQuery>()
        (q.clauses().first().query as BooleanQuery).clauses() shouldHaveSize 1
        (q.clauses().first().query as BooleanQuery).clauses().first().occur shouldBe BooleanClause.Occur.MUST
    }

    should("phraseQuery should create an object of type PhraseQuery") {
        phraseQuery("field", slop = 0, "asd").shouldBeTypeOf<PhraseQuery>()
        phraseQuery("field", "asd").shouldBeTypeOf<PhraseQuery>()
    }

})
