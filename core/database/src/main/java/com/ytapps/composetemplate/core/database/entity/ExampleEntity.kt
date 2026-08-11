package com.ytapps.composetemplate.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "examples")
data class ExampleEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
)
