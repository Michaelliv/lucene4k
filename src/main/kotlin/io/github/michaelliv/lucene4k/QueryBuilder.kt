package io.github.michaelliv.lucene4k

import org.apache.lucene.index.Term
import org.apache.lucene.search.*

fun BooleanQuery.Builder.shouldOccur(builder: BooleanQuery.Builder.() -> Query) =
    add(builder(), BooleanClause.Occur.SHOULD)

fun BooleanQuery.Builder.mustOccur(builder: BooleanQuery.Builder.() -> Query) =
    add(builder(), BooleanClause.Occur.MUST)

fun BooleanQuery.Builder.mustNotOccur(builder: BooleanQuery.Builder.() -> Query) =
    add(builder(), BooleanClause.Occur.MUST_NOT)

fun BooleanQuery.Builder.filter(builder: BooleanQuery.Builder.() -> Query) =
    add(builder(), BooleanClause.Occur.FILTER)

fun buildBooleanQuery(block: BooleanQuery.Builder.() -> Unit): BooleanQuery =
    BooleanQuery.Builder().also(block).build()

fun termQuery(field: String, value: String) = TermQuery(Term(field, value))
fun phraseQuery(field: String, slop: Int = 0, vararg terms: String) = PhraseQuery(slop, field, *terms)
fun phraseQuery(field: String, vararg terms: String) = PhraseQuery(0, field, *terms)