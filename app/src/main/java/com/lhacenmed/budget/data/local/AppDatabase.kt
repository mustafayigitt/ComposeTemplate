package com.lhacenmed.budget.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PendingSpendingItem::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pendingItemDao(): PendingItemDao
}
