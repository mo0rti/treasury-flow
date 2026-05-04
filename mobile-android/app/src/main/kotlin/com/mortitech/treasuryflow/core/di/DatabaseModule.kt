package com.mortitech.treasuryflow.core.di

import android.content.Context
import androidx.room.Room
import com.mortitech.treasuryflow.core.database.AppDatabase
import com.mortitech.treasuryflow.feature.example.data.local.ExampleDao
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
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "treasuryflow-db")
            .fallbackToDestructiveMigration(true)
            .build()

    @Provides
    fun provideExampleDao(database: AppDatabase): ExampleDao =
        database.exampleDao()
}
