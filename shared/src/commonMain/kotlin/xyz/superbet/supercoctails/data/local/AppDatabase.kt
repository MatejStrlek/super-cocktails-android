package xyz.superbet.supercoctails.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CocktailEntity::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun cocktailDao(): CocktailDao
}