package xyz.superbet.supercoctails

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import xyz.superbet.supercoctails.di.dataModule

class SuperCocktailsApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@SuperCocktailsApp)
            modules(
                dataModule,
                androidModule)
        }
    }
}