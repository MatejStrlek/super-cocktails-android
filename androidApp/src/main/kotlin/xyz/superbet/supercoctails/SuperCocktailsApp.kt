package xyz.superbet.supercoctails

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SuperCocktailsApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@SuperCocktailsApp)
            modules(dataModule, androidModule)
        }
    }
}