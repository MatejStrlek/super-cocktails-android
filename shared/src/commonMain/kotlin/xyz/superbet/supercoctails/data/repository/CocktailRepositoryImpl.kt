package xyz.superbet.supercoctails.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import xyz.superbet.supercoctails.data.local.CocktailDao
import xyz.superbet.supercoctails.data.mapper.toDomainModel
import xyz.superbet.supercoctails.data.mapper.toEntityModel
import xyz.superbet.supercoctails.data.model.Cocktail
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

        val refresh = fetchFromNetwork(query)
        cocktailDao.upsertCocktails(refresh.map { it.toEntityModel() })
        return refresh
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

    private suspend fun fetchFromNetwork(query: String): List<Cocktail> {
        val response: CocktailResponse = client
            .get("https://www.thecocktaildb.com/api/json/v1/1/search.php?s=$query")
            .body()
        return response.drinks ?: emptyList()
    }

    private fun refreshInBackground(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val refresh = fetchFromNetwork(query)
                cocktailDao.upsertCocktails(refresh.map { it.toEntityModel() })
            } catch (_: Exception) {
                // network unavailable so cache stays as-is
            }
        }
    }
}