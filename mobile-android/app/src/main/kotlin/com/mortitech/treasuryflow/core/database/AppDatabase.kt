package com.mortitech.treasuryflow.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mortitech.treasuryflow.feature.example.data.local.ExampleEntity
import com.mortitech.treasuryflow.feature.example.data.local.ExampleDao

@Database(
    entities = [ExampleEntity::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun exampleDao(): ExampleDao
}
