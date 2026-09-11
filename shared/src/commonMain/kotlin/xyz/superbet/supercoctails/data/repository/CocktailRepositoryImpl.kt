package xyz.superbet.supercoctails.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import xyz.superbet.supercoctails.domain.repository.CocktailRepository
import xyz.superbet.supercoctails.data.model.Cocktail
import xyz.superbet.supercoctails.data.model.CocktailResponse

class CocktailRepositoryImpl(private val client: HttpClient) : CocktailRepository {
    override suspend fun searchCocktails(query: String): List<Cocktail> {
        val response: CocktailResponse = client
            .get("https://www.thecocktaildb.com/api/json/v1/1/search.php?s=$query")
            .body()
        return response.drinks ?: emptyList()
    }
}