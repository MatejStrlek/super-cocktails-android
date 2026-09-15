package xyz.superbet.supercoctails.domain.algorithm

import kotlinx.coroutines.test.runTest
import xyz.superbet.supercoctails.data.model.Cocktail
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RecommendedCocktailsAssemblerTest {
    private fun cocktail(id: String) = Cocktail(id = id, name = "Cocktail $id")

    @Test
    fun deduplicatesRepeatedCocktailsAcrossTerms() = runTest {
        val sameTwo = listOf(cocktail("1"), cocktail("2"))
        val fakeSearch: suspend (String) -> List<Cocktail> = { sameTwo }

        val result = assembleRecommendedCocktails(
            seedPool = listOf("A", "B", "C", "D", "E"),
            search = fakeSearch
        )

        assertEquals(2, result.size)
        assertEquals(setOf("1", "2"), result.map { it.id }.toSet())
    }

    @Test
    fun drawsMoreThanFiveTermsWhenEachTermOnlyContributesOneUniqueCocktail() = runTest {
        var callCount = 0
        val fakeSearch: suspend (String) -> List<Cocktail> = { term ->
            callCount++
            listOf(cocktail(term))
        }
        val pool = (1..15).map { "term$it" }

        val result = assembleRecommendedCocktails(seedPool = pool, search = fakeSearch)

        assertEquals(10, result.size)
        assertTrue(callCount > 5, "Expected more than 5 terms searched, got $callCount")
    }

    @Test
    fun stopsExactlyAtTenEvenWhenMoreResultsAreAvailable() = runTest {
        var termIndex = 0
        val fakeSearch: suspend (String) -> List<Cocktail> = {
            val base = termIndex * 2
            termIndex++
            listOf(cocktail("$base"), cocktail("${base + 1}"))
        }
        val pool = (1..15).map { "term$it" }

        val result = assembleRecommendedCocktails(seedPool = pool, search = fakeSearch)

        assertEquals(10, result.size)
        assertEquals(result.size, result.map { it.id }.toSet().size)
    }
}
