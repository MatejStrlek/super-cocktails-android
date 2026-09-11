package xyz.superbet.supercoctails

import io.ktor.client.HttpClient
import org.koin.dsl.module

val dataModule = module {
    single { HttpClient() }
}