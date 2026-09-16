package xyz.superbet.supercoctails.domain.algorithm

import xyz.superbet.supercoctails.domain.model.Cocktail
private const val TARGET_COUNT = 10
private const val RESULTS_PER_TERM = 2

val SEED_TERM_POOL = listOf(
    "Mojito", "Margarita", "Daiquiri", "Martini", "Negroni",
    "Collins", "Fizz", "Sour", "Punch", "Sling",
    "Cooler", "Toddy", "Flip", "Smash", "Cobbler"
)

suspend fun assembleRecommendedCocktails(
    seedPool: List<String> = SEED_TERM_POOL,
    search: suspend (String) -> List<Cocktail>
): List<Cocktail> {
    val seedTerm = seedPool.shuffled()
    val result = LinkedHashMap<String, Cocktail>()

    for (term in seedTerm) {
        if (result.size >= TARGET_COUNT) break
        val matches = search(term).take(RESULTS_PER_TERM)
        for (cocktail in matches) {
            if (result.size >= TARGET_COUNT) break
            result.putIfAbsent(cocktail.id, cocktail)
        }
    }

    return result.values.toList()
}