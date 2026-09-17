package xyz.superbet.supercoctails.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import xyz.superbet.supercoctails.data.local.CocktailDao
import xyz.superbet.supercoctails.data.mapper.toCocktail
import xyz.superbet.supercoctails.data.mapper.toDomainModel
import xyz.superbet.supercoctails.data.mapper.toEntityModel
import xyz.superbet.supercoctails.domain.model.Cocktail
import xyz.superbet.supercoctails.data.model.CocktailResponse
import xyz.superbet.supercoctails.domain.algorithm.assembleRecommendedCocktails
import xyz.superbet.supercoctails.domain.repository.CocktailRepository

class CocktailRepositoryImpl(
    private val client: HttpClient,
    private val cocktailDao: CocktailDao
) : CocktailRepository {
    override suspend fun searchCocktails(query: String): List<Cocktail> {
        val cached = cocktailDao.searchCocktails(query)
        if (cached.isNotEmpty()) {
            refreshInBackground(query)
            return cached.map { it.toDomainModel() }
        }

        val refresh = fetchQueryFromNetwork(query)
        upsertPreservingFlags(refresh)
        return refresh
    }

    override suspend fun getCocktailById(id: String): Cocktail? {
        val cached = cocktailDao.getById(id)
        if (cached != null) {
            revalidateInBackground(id)
            return cached.toDomainModel()
        }
        return fetchCocktailByIdFromNetwork(id)?.also { upsertPreservingFlags(listOf(it)) }
    }

    override suspend fun getRecommendedCocktails(): List<Cocktail> {
        val cached = cocktailDao.getRecommendedCocktails()
        if (cached.isNotEmpty()) {
            return cached.map { it.toDomainModel() }
        }

        val assembled = assembleRecommendedCocktails(search = { query -> searchCocktails(query) } )
        cocktailDao.upsertCocktails(assembled.map { it.toEntityModel().copy(isRecommended = true) })
        return assembled
    }

    private fun revalidateInBackground(id: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val fresh = fetchCocktailByIdFromNetwork(id) ?: return@launch
                upsertPreservingFlags(listOf(fresh))
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
            } catch (_: Exception) {
                // network unavailable so cache stays as-is
            }
        }
    }

    override suspend fun toggleFavorite(id: String) {
        cocktailDao.toggleFavorite(id)
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