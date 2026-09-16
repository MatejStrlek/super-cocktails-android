package xyz.superbet.supercoctails.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import xyz.superbet.supercoctails.SuperCocktailsApp
import xyz.superbet.supercoctails.data.local.AppDatabase
import xyz.superbet.supercoctails.presentation.list.ListViewModel

val androidModule = module {
    single<AppDatabase> {
        Room.databaseBuilder<AppDatabase>(
            context = androidContext(),
            name = androidContext().getDatabasePath("cocktails.db").absolutePath
        )
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }
    single { get<AppDatabase>().cocktailDao() }
    viewModel { ListViewModel(get(), get()) }
    single { (androidContext() as SuperCocktailsApp).applicationScope }
}