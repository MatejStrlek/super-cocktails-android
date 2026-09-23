package xyz.superbet.supercoctails.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import xyz.superbet.supercoctails.data.local.CocktailDao
import xyz.superbet.supercoctails.data.mapper.toCocktail
import xyz.superbet.supercoctails.data.mapper.toDomainModel
import xyz.superbet.supercoctails.data.mapper.toEntityModel
import xyz.superbet.supercoctails.domain.model.Cocktail
import xyz.superbet.supercoctails.data.model.CocktailResponse
import xyz.superbet.supercoctails.domain.repository.CocktailRepository

private const val BASE_URL = "https://www.thecocktaildb.com/api/json/v1/1"

class CocktailRepositoryImpl(
    private val client: HttpClient,
    private val cocktailDao: CocktailDao
) : CocktailRepository {
    private val cacheTracker = CacheTracker()

    override fun searchCocktails(query: String): Flow<List<Cocktail>> = flow {
        val cached = cocktailDao.searchCocktails(query).first()
        if (cached.isEmpty()) {
            upsertPreservingFlags(fetchQueryFromNetwork(query))
            cacheTracker.markFetched(query)
        } else if (cacheTracker.isStale(query)) {
            refreshInBackground(query) { fetchQueryFromNetwork(query) }
        }
        emitAll(cocktailDao.searchCocktails(query).map { list -> list.map { it.toDomainModel() } })
    }

    override suspend fun getCocktailById(id: String): Cocktail? {
        val cached = cocktailDao.getById(id)
        if (cached != null) {
            if (cacheTracker.isStale(id)) refreshInBackground(id) {
                fetchCocktailByIdFromNetwork(id)?.let {
                    listOf(
                        it
                    )
                } ?: emptyList()
            }
            return cached.toDomainModel()
        }
        return fetchCocktailByIdFromNetwork(id)?.also {
            upsertPreservingFlags(listOf(it))
            cacheTracker.markFetched(id)
        }
    }

    override suspend fun searchCocktailsDirect(query: String): List<Cocktail> =
        fetchQueryFromNetwork(query)

    override fun observeRecommendedCocktails(): Flow<List<Cocktail>> =
        cocktailDao.getRecommendedCocktails().map { list -> list.map { it.toDomainModel() } }

    override suspend fun saveRecommendedCocktails(cocktails: List<Cocktail>) {
        cocktailDao.upsertCocktails(cocktails.map { it.toEntityModel().copy(isRecommended = true) })
    }

    override suspend fun hasRecommendedCocktails(): Boolean =
        cocktailDao.getRecommendedCocktails().first().isNotEmpty()

    override suspend fun toggleFavorite(id: String) {
        cocktailDao.toggleFavorite(id)
    }

    private fun refreshInBackground(key: String, fetch: suspend () -> List<Cocktail>) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                upsertPreservingFlags(fetch())
                cacheTracker.markFetched(key)
            } catch (_: Exception) {
                // network unavailable, cache stays as-is
            }
        }
    }

    private suspend fun fetchQueryFromNetwork(query: String): List<Cocktail> {
        val response: CocktailResponse = client
            .get("$BASE_URL/search.php?s=$query")
            .body()
        return response.drinks?.map { it.toCocktail() } ?: emptyList()
    }

    private suspend fun fetchCocktailByIdFromNetwork(id: String): Cocktail? {
        val response: CocktailResponse = client
            .get("$BASE_URL/lookup.php?i=$id")
            .body()
        return response.drinks?.firstOrNull()?.toCocktail()
    }

    private suspend fun upsertPreservingFlags(cocktails: List<Cocktail>) {
        val incoming = cocktails.map { it.toEntityModel() }
        val existing = cocktailDao.getByIds(incoming.map { it.id }).associateBy { it.id }
        val merged = incoming.map { new ->
            existing[new.id]?.copy(
                name = new.name,
                category = new.category,
                alcoholic = new.alcoholic,
                thumbnail = new.thumbnail,
                glass = new.glass,
                instructions = new.instructions,
                dateModified = new.dateModified,
                ingredients = new.ingredients,
            ) ?: new
        }
        cocktailDao.upsertCocktails(merged)
    }
}