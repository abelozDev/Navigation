package ru.maplyb.navigation.gui.impl.data.local.database

import android.content.Context
import androidx.room.Room

internal object Database {
    private var dbInstance: NavigationDatabase? = null

    fun provideDatabase(context: Context): NavigationDatabase {
        if (dbInstance == null) {
            synchronized(NavigationDatabase::class) {
                if (dbInstance == null) {
                    dbInstance = Room.databaseBuilder(
                        context.applicationContext,
                        NavigationDatabase::class.java,
                        "navigation_database.db"
                    ).fallbackToDestructiveMigration()
                        .build()
                }
            }
        }
        return dbInstance!!
    }
}