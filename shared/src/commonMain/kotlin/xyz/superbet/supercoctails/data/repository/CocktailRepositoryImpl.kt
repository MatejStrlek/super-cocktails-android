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
import xyz.superbet.supercoctails.domain.algorithm.assembleRecommendedCocktails
import xyz.superbet.supercoctails.domain.repository.CocktailRepository

private const val CACHE_TTL_MS = 10 * 60 * 1000L // 10 minutes

class CocktailRepositoryImpl(
    private val client: HttpClient,
    private val cocktailDao: CocktailDao
) : CocktailRepository {
    private val lastFetchedAt = mutableMapOf<String, Long>()

    override fun searchCocktails(query: String): Flow<List<Cocktail>> = flow {
        val cached = cocktailDao.searchCocktails(query).first()
        if (cached.isEmpty()) {
            val refresh = fetchQueryFromNetwork(query)
            upsertPreservingFlags(refresh)
            markFetched(query)
        } else if (isStale(query)) {
            refreshInBackground(query)
        }
        emitAll(cocktailDao.searchCocktails(query).map { list -> list.map { it.toDomainModel() } })
    }

    override suspend fun getCocktailById(id: String): Cocktail? {
        val cached = cocktailDao.getById(id)
        if (cached != null) {
            if (isStale(id)) revalidateInBackground(id)
            return cached.toDomainModel()
        }
        return fetchCocktailByIdFromNetwork(id)?.also {
            upsertPreservingFlags(listOf(it))
            markFetched(id)
        }
    }

    override fun getRecommendedCocktails(): Flow<List<Cocktail>> = flow {
        val cached = cocktailDao.getRecommendedCocktails().first()
        if (cached.isEmpty()) {
            val assembled =
                assembleRecommendedCocktails(search = { query -> fetchQueryFromNetwork(query) })
            cocktailDao.upsertCocktails(assembled.map {
                it.toEntityModel().copy(isRecommended = true)
            })
        }
        emitAll(
            cocktailDao.getRecommendedCocktails().map { list -> list.map { it.toDomainModel() } })
    }

    override suspend fun toggleFavorite(id: String) {
        cocktailDao.toggleFavorite(id)
    }

    private fun revalidateInBackground(id: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val fresh = fetchCocktailByIdFromNetwork(id) ?: return@launch
                upsertPreservingFlags(listOf(fresh))
                markFetched(id)
            } catch (_: Exception) {
                // network unavailable so cache stays as-is
            }
        }
    }

    private suspend fun fetchQueryFromNetwork(query: String): List<Cocktail> {
        val response: CocktailResponse = client
            .get("https://www.thecocktaildb.com/api/json/v1/1/search.php?s=$query")
            .body()
        return response.drinks?.map { it.toCocktail() } ?: emptyList()
    }

    private suspend fun fetchCocktailByIdFromNetwork(id: String): Cocktail? {
        val response: CocktailResponse = client
            .get("https://www.thecocktaildb.com/api/json/v1/1/lookup.php?i=$id")
            .body()
        return response.drinks?.firstOrNull()?.toCocktail()
    }

    private fun refreshInBackground(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val refresh = fetchQueryFromNetwork(query)
                upsertPreservingFlags(refresh)
                markFetched(query)
            } catch (_: Exception) {
                // network unavailable so cache stays as-is
            }
        }
    }

    private fun isStale(key: String): Boolean {
        val last = lastFetchedAt[key] ?: return true
        return System.currentTimeMillis() - last > CACHE_TTL_MS
    }

    private fun markFetched(key: String) {
        lastFetchedAt[key] = System.currentTimeMillis()
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