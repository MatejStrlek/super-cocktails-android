package xyz.superbet.supercoctails

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform