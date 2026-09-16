package xyz.superbet.supercoctails

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import xyz.superbet.supercoctails.di.androidModule
import xyz.superbet.supercoctails.di.dataModule

class SuperCocktailsApp : Application() {
    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@SuperCocktailsApp)
            modules(
                dataModule,
                androidModule
            )
        }
    }
}