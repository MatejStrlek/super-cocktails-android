package xyz.superbet.supercoctails.presentation.detail

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.InternalSerializationApi
import xyz.superbet.supercoctails.domain.model.Cocktail
import xyz.superbet.supercoctails.domain.repository.CocktailRepository
import xyz.superbet.supercoctails.domain.usecase.cocktail.GetCocktailByIdUseCase
import xyz.superbet.supercoctails.domain.usecase.cocktail.ToggleFavoriteUseCase
import xyz.superbet.supercoctails.presentation.state.DetailUiState
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repo: FakeDetailCocktailRepository
    private lateinit var viewModel: DetailViewModel

    @OptIn(InternalSerializationApi::class)
    private fun cocktail(id: String) = Cocktail(
        id = id, name = "Cocktail $id", category = null, alcoholic = null, thumbnail = null
    )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repo = FakeDetailCocktailRepository()
        viewModel = DetailViewModel(
            getCocktailByIdUseCase = GetCocktailByIdUseCase(repo),
            toggleFavoriteUseCase = ToggleFavoriteUseCase(repo),
        )
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() {
        assertIs<DetailUiState.Loading>(viewModel.uiState.value)
    }

    @Test
    fun `load emits Content when cocktail is found`() = runTest {
        val cocktail = cocktail("1")
        repo.cocktails["1"] = cocktail

        viewModel.load("1")
        advanceUntilIdle()

        val state = assertIs<DetailUiState.Content>(viewModel.uiState.value)
        assertEquals(cocktail, state.cocktail)
    }

    @Test
    fun `load emits Error when cocktail is not found`() = runTest {
        viewModel.load("missing")
        advanceUntilIdle()

        assertIs<DetailUiState.Error>(viewModel.uiState.value)
    }

    @Test
    fun `load emits Error when repository throws`() = runTest {
        repo.throwError = true

        viewModel.load("1")
        advanceUntilIdle()

        assertIs<DetailUiState.Error>(viewModel.uiState.value)
    }

    @OptIn(InternalSerializationApi::class)
    @Test
    fun `toggleFavorite reloads cocktail after toggling`() = runTest {
        repo.cocktails["1"] = cocktail("1")

        viewModel.load("1")
        advanceUntilIdle()
        assertIs<DetailUiState.Content>(viewModel.uiState.value)

        repo.cocktails["1"] = cocktail("1").copy(isFavorite = true)
        viewModel.toggleFavorite("1")
        advanceUntilIdle()

        val state = assertIs<DetailUiState.Content>(viewModel.uiState.value)
        assertEquals(true, state.cocktail.isFavorite)
    }
}

private class FakeDetailCocktailRepository : CocktailRepository {
    val cocktails = mutableMapOf<String, Cocktail>()
    var throwError = false
    var toggledIds = mutableListOf<String>()

    override suspend fun getCocktailById(id: String): Cocktail? {
        if (throwError) throw RuntimeException("network error")
        return cocktails[id]
    }

    override suspend fun toggleFavorite(id: String) {
        toggledIds.add(id)
    }

    override fun searchCocktails(query: String): Flow<List<Cocktail>> =
        MutableStateFlow(emptyList())

    override suspend fun searchCocktailsDirect(query: String): List<Cocktail> = emptyList()
    override fun observeRecommendedCocktails(): Flow<List<Cocktail>> = MutableStateFlow(emptyList())
    override suspend fun saveRecommendedCocktails(cocktails: List<Cocktail>) {}
    override suspend fun hasRecommendedCocktails(): Boolean = false
}