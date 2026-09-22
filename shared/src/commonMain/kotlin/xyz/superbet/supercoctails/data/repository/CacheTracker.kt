package xyz.superbet.supercoctails.data.repository

private const val CACHE_TTL_MS = 10 * 60 * 1000L

internal class CacheTracker {
    private val lastFetchedAt = mutableMapOf<String, Long>()

    fun isStale(key: String): Boolean {
        val last = lastFetchedAt[key] ?: return true
        return System.currentTimeMillis() - last > CACHE_TTL_MS
    }

    fun markFetched(key: String) {
        lastFetchedAt[key] = System.currentTimeMillis()
    }
}