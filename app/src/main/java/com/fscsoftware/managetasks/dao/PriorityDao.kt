package com.fscsoftware.managetasks.dao

import androidx.room.*
import com.fscsoftware.managetasks.model.Priority

@Dao
interface PriorityDao {

    @Insert
    fun insert(priority: Priority) : Long

    @Query("UPDATE priority SET position = position + 1")
    fun insertUpdate ()

    @Transaction
    fun insertTransaction (priority: Priority) : Long{
        insertUpdate()
        return insert(priority)
    }

    @Update
    fun update(priorities: List<Priority>)

    @Update
    fun update(vararg priority: Priority)

    @Delete
    fun delete(priority: Priority)

    @Query("UPDATE priority SET position = position - 1 " +
            "WHERE position > :position")
    fun deleteUpdate ( position : Int)

    @Transaction
    fun deleteTransaction (priority: Priority, position : Int ){
        deleteUpdate(position)
        delete(priority)
    }

    @Query("SELECT * FROM priority ORDER BY position ASC")
    fun getAll(): List<Priority>


    /*
    * updatePosition follow the next cycle when priority is changed their place
    * on UI through recyclerView, the correct call is using @Transaction
    * */
    // assign new position to item moved
    @Query("UPDATE priority SET position = :toPos WHERE id = :id")
    fun updatePosition1 ( id: Int, toPos : Int)

    // Check positions bigger than old position and short or equal than new position, rest 1
    @Query("UPDATE priority SET position = position - 1 " +
            "WHERE id != :id AND position > :fromPos AND position <= :toPos")
    fun updatePosition2 ( id: Int, toPos : Int, fromPos : Int)

    // Check positions short than old position and bigger or equal than new position, sum 1
    @Query("UPDATE priority SET position = position + 1 " +
            "WHERE id != :id AND position < :fromPos AND position >= :toPos")
    fun updatePosition3 ( id: Int, toPos : Int, fromPos : Int)

    @Transaction
    fun updatePositionTransaction (id: Int, toPos : Int, fromPos : Int){
        updatePosition1(id, toPos)
        updatePosition2(id, toPos, fromPos)
        updatePosition3(id, toPos, fromPos)
    }


    /*
    *
    *

    // Check positions bigger than old position and short or equal than new position, rest 1
    @Query("UPDATE priority SET position = position + 1 " +
            "WHERE position > :fromPos AND position <= :toPos")
    fun updatePosition1 ( toPos : Int, fromPos : Int)

    // Check positions short than old position and bigger or equal than new position, sum 1
    @Query("UPDATE priority SET position = position - 1 " +
            "WHERE position < :fromPos AND position > :toPos")
    fun updatePosition2 ( toPos : Int, fromPos : Int)

    @Transaction
    fun updatePositionTransaction ( toPos : Int, fromPos : Int){
        if (fromPos < toPos) {
            updatePosition1( toPos, fromPos)
        } else {
            updatePosition2( toPos + 1, fromPos)
        }
    }

    *
    * */

}