package xyz.superbet.supercoctails.presentation.list

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import xyz.superbet.supercoctails.domain.model.Cocktail
import xyz.superbet.supercoctails.domain.repository.CocktailRepository

class FakeCocktailRepository : CocktailRepository {
    private val searchResults = MutableStateFlow<List<Cocktail>>(emptyList())
    private val recommendedCocktails = MutableStateFlow<List<Cocktail>>(emptyList())
    var searchError: Exception? = null

    fun setSearchResults(cocktails: List<Cocktail>) { searchResults.value = cocktails }
    fun setRecommended(cocktails: List<Cocktail>) { recommendedCocktails.value = cocktails }

    override fun searchCocktails(query: String): Flow<List<Cocktail>> {
        searchError?.let { throw it }
        return searchResults
    }

    override suspend fun searchCocktailsDirect(query: String): List<Cocktail> = searchResults.value

    override fun observeRecommendedCocktails(): Flow<List<Cocktail>> = recommendedCocktails

    override suspend fun saveRecommendedCocktails(cocktails: List<Cocktail>) {
        recommendedCocktails.value = cocktails
    }

    override suspend fun hasRecommendedCocktails(): Boolean = recommendedCocktails.value.isNotEmpty()

    override suspend fun getCocktailById(id: String): Cocktail? =
        (searchResults.value + recommendedCocktails.value).firstOrNull { it.id == id }

    override suspend fun toggleFavorite(id: String) {}
}