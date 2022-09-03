package com.fscsoftware.managetasks.dao

import androidx.room.*
import com.fscsoftware.managetasks.model.TaskTypes

@Dao
interface TaskTypesDao {

    @Insert
    fun insert(vararg taskTypes: TaskTypes)

    @Update
    fun update(taskTypes: TaskTypes)

    @Delete
    fun delete(taskTypes: TaskTypes)

    @Query("SELECT * FROM task_types")
    fun getAll(): List<TaskTypes>

}