package xyz.superbet.supercoctails

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import xyz.superbet.supercoctails.di.androidModule
import xyz.superbet.supercoctails.di.dataModule
import xyz.superbet.supercoctails.di.platformDataModule

class SuperCocktailsApp : Application() {
    val applicationScope = CoroutineScope(Dispatchers.Default)

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