package xyz.superbet.supercoctails.di

import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import xyz.superbet.supercoctails.SuperCocktailsApp
import xyz.superbet.supercoctails.presentation.detail.DetailViewModel
import xyz.superbet.supercoctails.presentation.list.ListViewModel

val androidModule = module {
    single { (androidContext() as SuperCocktailsApp).applicationScope }
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
}