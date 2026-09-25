package xyz.superbet.supercocktails.di

import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import xyz.superbet.supercocktails.SuperCocktailsApp
import xyz.superbet.supercocktails.presentation.detail.DetailViewModel
import xyz.superbet.supercocktails.presentation.list.ListViewModel
import xyz.superbet.supercocktails.presentation.splash.SplashViewModel

val androidModule = module {
    single(named("appScope")) { (androidContext() as SuperCocktailsApp).applicationScope }
    viewModel { SplashViewModel(get(), get(named("appScope"))) }
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