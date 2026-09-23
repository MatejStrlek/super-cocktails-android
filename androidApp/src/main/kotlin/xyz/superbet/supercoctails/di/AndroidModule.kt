package xyz.superbet.supercoctails.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import xyz.superbet.supercoctails.SuperCocktailsApp
import xyz.superbet.supercoctails.data.local.AppDatabase
import xyz.superbet.supercoctails.data.preferences.RecentSearchRepositoryImpl
import xyz.superbet.supercoctails.data.preferences.ThemeRepositoryImpl
import xyz.superbet.supercoctails.domain.repository.RecentSearchRepository
import xyz.superbet.supercoctails.domain.repository.ThemeRepository
import xyz.superbet.supercoctails.presentation.list.ListViewModel
import xyz.superbet.supercoctails.presentation.detail.DetailViewModel

val androidModule = module {
    single<RecentSearchRepository> { RecentSearchRepositoryImpl(androidContext()) }
    single<ThemeRepository> { ThemeRepositoryImpl(androidContext()) }
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
    viewModel {
        ListViewModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
    viewModel { DetailViewModel(get(), get()) }
    single { (androidContext() as SuperCocktailsApp).applicationScope }
}