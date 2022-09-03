package com.fscsoftware.managetasks.dao

import androidx.room.Dao
import androidx.room.Insert
import com.fscsoftware.managetasks.model.Files


@Dao
interface FilesDao {

    @Insert
    fun insert(vararg files: Files)

}