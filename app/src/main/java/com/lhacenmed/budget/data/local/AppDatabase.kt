package com.lhacenmed.budget.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [PendingSpendingItem::class, GroceryItem::class],
    version  = 2
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pendingItemDao(): PendingItemDao
    abstract fun groceryDao(): GroceryDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE grocery_items (
                        id          INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userId      TEXT    NOT NULL,
                        name        TEXT    NOT NULL,
                        checkedDate TEXT,
                        createdAt   TEXT    NOT NULL
                    )
                """.trimIndent())
            }
        }
    }
}
