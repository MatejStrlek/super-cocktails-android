package xyz.superbet.supercocktails.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import xyz.superbet.supercocktails.data.local.AppDatabase
import xyz.superbet.supercocktails.data.preferences.RecentSearchRepositoryImpl
import xyz.superbet.supercocktails.data.preferences.ThemeRepositoryImpl
import xyz.superbet.supercocktails.domain.repository.RecentSearchRepository
import xyz.superbet.supercocktails.domain.repository.ThemeRepository

val platformDataModule = module {
    single<AppDatabase> {
        Room.databaseBuilder<AppDatabase>(
            androidContext(),
            androidContext().getDatabasePath("cocktails.db").absolutePath
        )
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }
    single { get<AppDatabase>().cocktailDao() }
    single<RecentSearchRepository> { RecentSearchRepositoryImpl(androidContext()) }
    single<ThemeRepository> { ThemeRepositoryImpl(androidContext()) }
}
