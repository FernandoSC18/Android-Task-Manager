package com.fscsoftware.managetasks.controller

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.fscsoftware.managetasks.R
import com.fscsoftware.managetasks.dao.*


object TaskController : ViewModel() {

    fun getRoomDb  (context : Context) : AppDatabase {
        val db = Room.databaseBuilder(
            context,
            AppDatabase::class.java, AppDatabase.DATABASE_NAME
        )
        .addCallback(
            object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    // do something after database has been created
                    Log.e("TAG", "ONCREATE DATABASE")

                    val queryPriorities = "SELECT id, name FROM priority"
                    val cursorPriorities = db.query(queryPriorities)

                    if (cursorPriorities.count <= 0) {

                        //DATA TO INITIAL PRIORITIES
                        val priority1 = ContentValues()
                        priority1.put("id", 1)
                        priority1.put("name", context.getString(R.string.important))
                        priority1.put("position", 0)
                        val priority2 = ContentValues()
                        priority2.put("id", 2)
                        priority2.put("name", context.getString(R.string.normal))
                        priority2.put("position", 1)

                        db.insert("priority", SQLiteDatabase.CONFLICT_ROLLBACK, priority1)
                        db.insert("priority", SQLiteDatabase.CONFLICT_ROLLBACK, priority2)

                    }

                    val queryTaskTypes = "SELECT id, name FROM task_types"
                    val cursorTaskTypes = db.query(queryTaskTypes)

                    if (cursorTaskTypes.count <= 0) {

                        //DATA TO INITIAL TASK TYPES
                        val dataType1 = ContentValues()
                        dataType1.put("id", 1)
                        dataType1.put("name", "text")
                        val dataType2 = ContentValues()
                        dataType2.put("id", 2)
                        dataType2.put("name", "audio")
                        val dataType3 = ContentValues()
                        dataType3.put("id", 3)
                        dataType3.put("name", "image")
                        val dataType4 = ContentValues()
                        dataType4.put("id", 4)
                        dataType4.put("name", "merge")

                        db.insert("task_types", SQLiteDatabase.CONFLICT_ROLLBACK, dataType1)
                        db.insert("task_types", SQLiteDatabase.CONFLICT_ROLLBACK, dataType2)
                        db.insert("task_types", SQLiteDatabase.CONFLICT_ROLLBACK, dataType3)
                        db.insert("task_types", SQLiteDatabase.CONFLICT_ROLLBACK, dataType4)

                    }

                    cursorPriorities.close()
                    cursorTaskTypes.close()

                }

                override fun onOpen(db: SupportSQLiteDatabase) {
                    // do something every time database is open
                }
            }
        )
        .build()
        return db
    }

    fun getRoomTaskDao (context : Context) : TaskDao {
        return getRoomDb(context).taskDao()
    }

    fun getRoomTaskFinishDao (context : Context) : TaskFinishDao {
        return getRoomDb(context).taskFinishDao()
    }

    fun getRoomFilesDao (context : Context) : FilesDao {
        return getRoomDb(context).fileDao()
    }

    fun getRoomPrioritiesDao (context : Context) : PriorityDao {
        return getRoomDb(context).priorityDao()
    }

    fun getRoomTaskTypesDao (context : Context) : TaskTypesDao {
        return getRoomDb(context).taskTypesDao()
    }

    fun getViewModelScope () = viewModelScope


}
