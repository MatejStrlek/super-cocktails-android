package xyz.superbet.supercocktails.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import xyz.superbet.supercocktails.domain.model.Cocktail
import xyz.superbet.supercocktails.domain.repository.CocktailRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Tests the CocktailRepository contract using a fake implementation.
 * Covers: offline-first cache reads, flag preservation on upsert, recommended set management.
 */
class CocktailRepositoryTest {

    // ---- Fake implementation --------------------------------------------------

    private class InMemoryCocktailRepository : CocktailRepository {
        private val store = mutableMapOf<String, Cocktail>()

        override fun searchCocktails(query: String): Flow<List<Cocktail>> =
            MutableStateFlow(store.values.filter { it.name.contains(query, ignoreCase = true) })

        override suspend fun searchCocktailsDirect(query: String): List<Cocktail> =
            store.values.filter { it.name.contains(query, ignoreCase = true) }

        override fun observeRecommendedCocktails(): Flow<List<Cocktail>> =
            MutableStateFlow(store.values.filter { it.id.startsWith("r") }.toList())

        override suspend fun saveRecommendedCocktails(cocktails: List<Cocktail>) {
            cocktails.forEach { incoming ->
                val existing = store[incoming.id]
                // Preserve isFavorite — this is the flag-preservation contract
                store[incoming.id] = incoming.copy(isFavorite = existing?.isFavorite ?: incoming.isFavorite)
            }
        }

        override suspend fun hasRecommendedCocktails(): Boolean =
            store.values.any { it.id.startsWith("r") }

        override suspend fun getCocktailById(id: String): Cocktail? = store[id]

        override suspend fun toggleFavorite(id: String) {
            store[id]?.let { store[id] = it.copy(isFavorite = !it.isFavorite) }
        }

        fun seed(vararg cocktails: Cocktail) = cocktails.forEach { store[it.id] = it }
    }

    // ---- Helpers --------------------------------------------------------------

    private fun cocktail(id: String, isFavorite: Boolean = false) = Cocktail(
        id = id, name = "Cocktail $id", category = null, alcoholic = null,
        thumbnail = null, isFavorite = isFavorite
    )

    // ---- Tests ----------------------------------------------------------------

    @Test
    fun `searchCocktails returns matching cached results`() = runTest {
        val repo = InMemoryCocktailRepository()
        repo.seed(cocktail("1"), cocktail("2"))

        val results = repo.searchCocktails("Cocktail").first()

        assertEquals(2, results.size)
    }

    @Test
    fun `searchCocktails returns empty list when nothing matches`() = runTest {
        val repo = InMemoryCocktailRepository()
        repo.seed(cocktail("1"))

        val results = repo.searchCocktails("xyz_no_match").first()

        assertTrue(results.isEmpty())
    }

    @Test
    fun `getCocktailById returns cached cocktail`() = runTest {
        val repo = InMemoryCocktailRepository()
        repo.seed(cocktail("42"))

        val result = repo.getCocktailById("42")

        assertNotNull(result)
        assertEquals("42", result.id)
    }

    @Test
    fun `getCocktailById returns null for unknown id`() = runTest {
        val repo = InMemoryCocktailRepository()

        val result = repo.getCocktailById("unknown")

        assertNull(result)
    }

    @Test
    fun `saveRecommendedCocktails preserves isFavorite flag on existing entry`() = runTest {
        val repo = InMemoryCocktailRepository()
        // Pre-seed a favorited cocktail
        repo.seed(cocktail("r1", isFavorite = true))

        // Network re-fetches it without the favorite flag
        repo.saveRecommendedCocktails(listOf(cocktail("r1", isFavorite = false)))

        val result = repo.getCocktailById("r1")
        assertNotNull(result)
        assertTrue(result.isFavorite, "isFavorite should be preserved after re-save")
    }

    @Test
    fun `saveRecommendedCocktails stores new cocktails`() = runTest {
        val repo = InMemoryCocktailRepository()

        repo.saveRecommendedCocktails(listOf(cocktail("r1"), cocktail("r2")))

        assertNotNull(repo.getCocktailById("r1"))
        assertNotNull(repo.getCocktailById("r2"))
    }

    @Test
    fun `hasRecommendedCocktails returns false when cache is empty`() = runTest {
        val repo = InMemoryCocktailRepository()

        assertEquals(false, repo.hasRecommendedCocktails())
    }

    @Test
    fun `hasRecommendedCocktails returns true after saving recommended cocktails`() = runTest {
        val repo = InMemoryCocktailRepository()
        repo.saveRecommendedCocktails(listOf(cocktail("r1")))

        assertEquals(true, repo.hasRecommendedCocktails())
    }

    @Test
    fun `toggleFavorite flips isFavorite from false to true`() = runTest {
        val repo = InMemoryCocktailRepository()
        repo.seed(cocktail("1", isFavorite = false))

        repo.toggleFavorite("1")

        val result = repo.getCocktailById("1")
        assertNotNull(result)
        assertTrue(result.isFavorite)
    }

    @Test
    fun `toggleFavorite flips isFavorite from true to false`() = runTest {
        val repo = InMemoryCocktailRepository()
        repo.seed(cocktail("1", isFavorite = true))

        repo.toggleFavorite("1")

        val result = repo.getCocktailById("1")
        assertNotNull(result)
        assertEquals(false, result.isFavorite)
    }

    @Test
    fun `observeRecommendedCocktails returns only recommended entries`() = runTest {
        val repo = InMemoryCocktailRepository()
        repo.saveRecommendedCocktails(listOf(cocktail("r1"), cocktail("r2")))
        repo.seed(cocktail("s1"))  // non-recommended

        val results = repo.observeRecommendedCocktails().first()

        assertEquals(setOf("r1", "r2"), results.map { it.id }.toSet())
    }
}