package com.fscsoftware.managetasks.dao

import androidx.room.*
import com.fscsoftware.managetasks.model.Task

@Dao
interface TaskDao {

    @Insert
    fun insert(task: Task) : Long

    @Query("UPDATE tasks SET position = position + 1 WHERE UPPER(priority) = UPPER(:priority)")
    fun insertUpdate (priority : String)

    @Transaction
    fun insertTransaction (priority : String, task: Task) : Long{
        insertUpdate(priority)
        return insert(task)
    }

    @Insert
    fun insertList (tasks: List<Task>)

    @Update
    fun updateTask(task: Task)

    @Query("UPDATE tasks SET priority = :toPriority " +
            "WHERE priority = :fromPriority")
    fun updatePriorityName(fromPriority : String, toPriority : String)

    @Update
    fun updateTasks(vararg task: Task)

    @Delete
    fun delete(task: Task)

    @Query("UPDATE tasks SET position = position - 1 " +
            "WHERE position > :position AND UPPER(priority) = UPPER(:priority)")
    fun deleteUpdate (priority : String, position : Int)

    @Transaction
    fun deleteTransaction ( task: Task, priority : String, position : Int ){
        deleteUpdate(priority, position)
        delete(task)
    }

    @Query("SELECT * FROM tasks t WHERE t.id = :id")
    fun getById(id : Int): Task

    @Query("SELECT * FROM tasks")
    fun getAll(): List<Task>

    @Query("SELECT * FROM tasks t WHERE UPPER(t.priority) = UPPER(:priority) ORDER BY position ASC")
    fun getAllByPriority(priority : String): List<Task>


    /*
    * updatePosition follow the next cycle when task is changed their place
    * on UI through recyclerView, the correct call is using @Transaction
    * */
    // assign new position to item moved
    @Query("UPDATE tasks SET position = :toPos WHERE id = :id " +
            "AND UPPER(priority) = UPPER(:priority)")
    fun updatePosition1 (priority : String, id: Int, toPos : Int)

    // Check positions bigger than old position and short or equal than new position, rest 1
    @Query("UPDATE tasks SET position = position - 1 " +
            "WHERE id != :id AND position > :fromPos AND position <= :toPos " +
            "AND UPPER(priority) = UPPER(:priority)")
    fun updatePosition2 (priority : String, id: Int, toPos : Int, fromPos : Int)

    // Check positions short than old position and bigger or equal than new position, sum 1
    @Query("UPDATE tasks SET position = position + 1 " +
            "WHERE id != :id AND position < :fromPos AND position >= :toPos " +
            "AND UPPER(priority) = UPPER(:priority)")
    fun updatePosition3 (priority : String, id: Int, toPos : Int, fromPos : Int)

    @Transaction
    fun updatePositionTransaction (priority : String, id: Int, toPos : Int, fromPos : Int){

        updatePosition1(priority, id, toPos)
        updatePosition2(priority, id, toPos, fromPos)
        updatePosition3(priority, id, toPos, fromPos)
    }

}
