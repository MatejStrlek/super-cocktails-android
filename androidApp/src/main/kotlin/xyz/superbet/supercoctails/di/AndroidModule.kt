package xyz.superbet.supercoctails.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import xyz.superbet.supercoctails.presentation.list.ListViewModel

val androidModule = module {
    viewModel { ListViewModel(get()) }
}