package xyz.superbet.supercocktails.presentation.list

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.InternalSerializationApi
import xyz.superbet.supercocktails.domain.model.Cocktail
import xyz.superbet.supercocktails.domain.usecase.cocktail.GetRecommendedCocktailsUseCase
import xyz.superbet.supercocktails.domain.usecase.cocktail.SearchCocktailsUseCase
import xyz.superbet.supercocktails.domain.usecase.cocktail.ToggleFavoriteUseCase
import xyz.superbet.supercocktails.domain.usecase.search.AddRecentSearchUseCase
import xyz.superbet.supercocktails.domain.usecase.search.DeleteRecentSearchUseCase
import xyz.superbet.supercocktails.domain.usecase.search.GetRecentSearchesUseCase
import xyz.superbet.supercocktails.domain.usecase.theme.GetThemePreferenceUseCase
import xyz.superbet.supercocktails.domain.usecase.theme.SetThemePreferenceUseCase
import xyz.superbet.supercocktails.presentation.state.ListUiState
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class ListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var cocktailRepo: FakeCocktailRepository
    private lateinit var recentSearchRepo: FakeRecentSearchRepository
    private lateinit var themeRepo: FakeThemeRepository
    private lateinit var viewModel: ListViewModel

    @OptIn(InternalSerializationApi::class)
    private fun cocktail(id: String) = Cocktail(
        id = id, name = "Cocktail $id", category = null, alcoholic = null, thumbnail = null
    )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        cocktailRepo = FakeCocktailRepository()
        recentSearchRepo = FakeRecentSearchRepository()
        themeRepo = FakeThemeRepository()
        viewModel = ListViewModel(
            searchCocktailsUseCase = SearchCocktailsUseCase(cocktailRepo),
            getRecommendedCocktailsUseCase = GetRecommendedCocktailsUseCase(cocktailRepo),
            toggleFavoriteUseCase = ToggleFavoriteUseCase(cocktailRepo),
            getRecentSearchesUseCase = GetRecentSearchesUseCase(recentSearchRepo),
            addRecentSearchUseCase = AddRecentSearchUseCase(recentSearchRepo),
            deleteRecentSearchUseCase = DeleteRecentSearchUseCase(recentSearchRepo),
            getThemePreferenceUseCase = GetThemePreferenceUseCase(themeRepo),
            setThemePreferenceUseCase = SetThemePreferenceUseCase(themeRepo),
        )
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    // --- Recommended / default state ---

    @Test
    fun `initial state is Loading when recommended cache is empty`() = runTest {
        advanceUntilIdle()
        assertEquals(ListUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `shows Content when recommended cocktails are available`() = runTest {
        val cocktails = listOf(cocktail("1"), cocktail("2"))
        cocktailRepo.setRecommended(cocktails)

        advanceUntilIdle()

        val state = assertIs<ListUiState.Content>(viewModel.uiState.value)
        assertEquals(cocktails, state.cocktails)
    }

    // --- Search ---

    @Test
    fun `search returns Content when results exist`() = runTest {
        val results = listOf(cocktail("a"), cocktail("b"))
        cocktailRepo.setSearchResults(results)

        viewModel.onQueryChanged("mar")
        advanceUntilIdle()

        val state = assertIs<ListUiState.Content>(viewModel.uiState.value)
        assertEquals(results, state.cocktails)
    }

    @Test
    fun `search returns Empty when no results`() = runTest {
        cocktailRepo.setSearchResults(emptyList())

        viewModel.onQueryChanged("xyznothing")
        advanceUntilIdle()

        assertIs<ListUiState.Empty>(viewModel.uiState.value)
    }

    @Test
    fun `search returns Error when repository throws`() = runTest {
        cocktailRepo.searchError = RuntimeException("network error")

        viewModel.onQueryChanged("margarita")
        advanceUntilIdle()

        assertIs<ListUiState.Error>(viewModel.uiState.value)
    }

    @Test
    fun `clearing query returns to recommended state`() = runTest {
        val recommended = listOf(cocktail("r1"), cocktail("r2"))
        cocktailRepo.setRecommended(recommended)
        cocktailRepo.setSearchResults(listOf(cocktail("s1")))

        viewModel.onQueryChanged("mar")
        advanceUntilIdle()
        viewModel.onQueryChanged("")
        advanceUntilIdle()

        val state = assertIs<ListUiState.Content>(viewModel.uiState.value)
        assertEquals(recommended, state.cocktails)
    }

    // --- Search focused ---

    @Test
    fun `search focused with blank query shows SearchFocused with recent searches`() = runTest {
        recentSearchRepo.addRecentSearch("mojito")
        recentSearchRepo.addRecentSearch("daiquiri")

        viewModel.onSearchFocused()
        advanceUntilIdle()

        val state = assertIs<ListUiState.SearchFocused>(viewModel.uiState.value)
        assertEquals(listOf("daiquiri", "mojito"), state.recentSearches)
    }

    @Test
    fun `unfocusing search returns to recommended state`() = runTest {
        val recommended = listOf(cocktail("r1"))
        cocktailRepo.setRecommended(recommended)

        viewModel.onSearchFocused()
        advanceUntilIdle()
        viewModel.onSearchUnfocused()
        advanceUntilIdle()

        assertIs<ListUiState.Content>(viewModel.uiState.value)
    }

    // --- Retry ---

    @Test
    fun `retry re-triggers uiState after error`() = runTest {
        cocktailRepo.searchError = RuntimeException("offline")
        viewModel.onQueryChanged("rum")
        advanceUntilIdle()
        assertIs<ListUiState.Error>(viewModel.uiState.value)

        cocktailRepo.searchError = null
        cocktailRepo.setSearchResults(listOf(cocktail("1")))
        viewModel.retry()
        advanceUntilIdle()

        assertIs<ListUiState.Content>(viewModel.uiState.value)
    }

    // --- Recent searches ---

    @Test
    fun `submitting search saves query to recent searches`() = runTest {
        viewModel.onQueryChanged("negroni")
        viewModel.onSearchSubmitted()
        advanceUntilIdle()

        viewModel.onSearchFocused()
        advanceUntilIdle()

        val state = assertIs<ListUiState.SearchFocused>(viewModel.uiState.value)
        assertEquals(listOf("negroni"), state.recentSearches)
    }

    @Test
    fun `deleting a recent search removes it from suggestions`() = runTest {
        recentSearchRepo.addRecentSearch("mojito")
        recentSearchRepo.addRecentSearch("daiquiri")

        viewModel.deleteRecentSearch("mojito")
        viewModel.onSearchFocused()
        advanceUntilIdle()

        val state = assertIs<ListUiState.SearchFocused>(viewModel.uiState.value)
        assertEquals(listOf("daiquiri"), state.recentSearches)
    }

    // --- One-shot searchErrorEvent via Channel + Turbine ---

    @Test
    fun `searchErrorEvent emits exactly once when search fails`() = runTest {
        cocktailRepo.searchError = RuntimeException("no internet")

        viewModel.searchErrorEvent.test {
            viewModel.onQueryChanged("martini")
            advanceUntilIdle()

            val event = awaitItem()
            assertEquals("no internet", event)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `searchErrorEvent is distinct from uiState Error — both fire on failure`() = runTest {
        cocktailRepo.searchError = RuntimeException("timeout")

        viewModel.searchErrorEvent.test {
            viewModel.onQueryChanged("negroni")
            advanceUntilIdle()

            awaitItem() // channel event fires
            assertIs<ListUiState.Error>(viewModel.uiState.value) // state also updated
            cancelAndIgnoreRemainingEvents()
        }
    }

    // --- flatMapLatest: stale response never overwrites newer query ---

    @Test
    fun `slow stale search response does not overwrite newer query result`() = runTest {
        val staleResults = listOf(cocktail("stale"))
        val freshResults = listOf(cocktail("fresh"))

        // First query — results available immediately
        cocktailRepo.setSearchResults(staleResults)
        viewModel.onQueryChanged("old")
        advanceUntilIdle()

        // Second query — overrides the first; flatMapLatest cancels old subscription
        cocktailRepo.setSearchResults(freshResults)
        viewModel.onQueryChanged("new")
        advanceUntilIdle()

        val state = assertIs<ListUiState.Content>(viewModel.uiState.value)
        assertEquals(freshResults, state.cocktails)
    }
}