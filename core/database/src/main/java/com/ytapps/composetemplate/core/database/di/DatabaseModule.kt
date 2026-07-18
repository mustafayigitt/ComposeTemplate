package com.ytapps.composetemplate.core.database.di

import android.content.Context
import androidx.room.Room
import com.ytapps.composetemplate.core.database.AppDatabase
import com.ytapps.composetemplate.core.database.dao.ExampleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase =
        Room
            .databaseBuilder(
                context,
                AppDatabase::class.java,
                "app_database",
            ).build()

    @Provides
    fun provideExampleDao(database: AppDatabase): ExampleDao = database.exampleDao()
}
