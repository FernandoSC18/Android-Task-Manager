package com.fscsoftware.managetasks.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "task_types", indices = [Index(value = ["name"], unique = true)])
data class TaskTypes (
    @PrimaryKey(autoGenerate = true) val id: Int?
    , @ColumnInfo(name = "name") val name: String?
    , @ColumnInfo(name = "description") val description: String?
)