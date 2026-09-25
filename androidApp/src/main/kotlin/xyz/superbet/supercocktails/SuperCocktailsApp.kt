package xyz.superbet.supercocktails

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import xyz.superbet.supercocktails.di.androidModule
import xyz.superbet.supercocktails.di.dataModule
import xyz.superbet.supercocktails.di.platformDataModule

class SuperCocktailsApp : Application() {
    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@SuperCocktailsApp)
            modules(
                dataModule,
                platformDataModule,
                androidModule
            )
        }
    }
}