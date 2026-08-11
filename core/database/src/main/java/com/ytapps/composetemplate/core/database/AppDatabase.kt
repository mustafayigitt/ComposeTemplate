package com.ytapps.composetemplate.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ytapps.composetemplate.core.database.dao.ExampleDao
import com.ytapps.composetemplate.core.database.entity.ExampleEntity

@Database(
    entities = [ExampleEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun exampleDao(): ExampleDao
}
