package com.fscsoftware.managetasks.dao

import androidx.room.*
import com.fscsoftware.managetasks.model.TaskFinish


@Dao
interface TaskFinishDao {

    @Insert
    fun insert(taskFinish: TaskFinish) : Long

    @Query("UPDATE tasks_finish SET position = position + 1")
    fun insertUpdate ( )

    @Transaction
    fun insertTransaction ( taskFinish: TaskFinish) : Long{
        insertUpdate( )
        return insert(taskFinish)
    }

    @Insert
    fun insertList (taskFinish: List<TaskFinish>)

    @Update
    fun updateTaskFinis(taskFinish: TaskFinish)

    @Update
    fun updateTaskFinish(vararg taskFinish: TaskFinish)

    @Delete
    fun delete(TaskFinish: TaskFinish)

    @Query("UPDATE tasks_finish SET position = position - 1 " +
            "WHERE position > :position")
    fun deleteUpdate (position : Int)

    @Transaction
    fun deleteTransaction ( taskFinish: TaskFinish, position : Int ){
        delete(taskFinish)
        deleteUpdate(position)
    }

    @Query("SELECT * FROM tasks_finish t WHERE t.id = :id")
    fun getById(id : Int): TaskFinish

    @Query("SELECT * FROM tasks_finish")
    fun getAll(): List<TaskFinish>


}
