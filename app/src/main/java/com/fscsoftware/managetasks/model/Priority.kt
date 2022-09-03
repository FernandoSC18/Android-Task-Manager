package com.fscsoftware.managetasks.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "priority", indices = [Index(value = ["name"], unique = true)])
data class Priority (
    @PrimaryKey(autoGenerate = true) var id: Int?
    , @ColumnInfo(name = "position") var position: Int?
    , @ColumnInfo(name = "name") var name: String
    , @ColumnInfo(name = "description") val description: String?
)