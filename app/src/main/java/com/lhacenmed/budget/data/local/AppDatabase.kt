package com.lhacenmed.budget.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        PendingSpendingItem::class,
        GroceryItem::class,
        CachedSpendingItem::class,
        CachedContribution::class,
    ],
    version = 3
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pendingItemDao(): PendingItemDao
    abstract fun groceryDao(): GroceryDao
    abstract fun cachedSpendingDao(): CachedSpendingDao
    abstract fun cachedContributionDao(): CachedContributionDao

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

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE cached_spending_items (
                        id          TEXT    PRIMARY KEY NOT NULL,
                        date        TEXT    NOT NULL,
                        shopper     TEXT    NOT NULL,
                        name        TEXT    NOT NULL,
                        quantity    TEXT    NOT NULL,
                        price       REAL    NOT NULL,
                        description TEXT,
                        createdAt   TEXT    NOT NULL DEFAULT ''
                    )
                """.trimIndent())
                db.execSQL("""
                    CREATE TABLE cached_contributions (
                        id          TEXT    PRIMARY KEY NOT NULL,
                        contributor TEXT    NOT NULL,
                        amount      REAL    NOT NULL,
                        createdAt   TEXT    NOT NULL DEFAULT ''
                    )
                """.trimIndent())
            }
        }
    }
}
