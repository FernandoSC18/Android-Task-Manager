package com.fscsoftware.managetasks.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fscsoftware.managetasks.model.*

/*
* INITIALIZER DATA IS EXECUTED BY TaskController Class
* INITIALIZER DATA IS EXECUTED BY TaskController Class
* INITIALIZER DATA IS EXECUTED BY TaskController Class
* */

@Database(entities = [Task::class, TaskFinish::class, Files::class, Priority::class, TaskTypes::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        const val DATABASE_NAME = "tasks_db"
    }

    abstract fun taskDao(): TaskDao

    abstract fun taskFinishDao(): TaskFinishDao

    abstract fun fileDao(): FilesDao

    abstract fun priorityDao(): PriorityDao

    abstract fun taskTypesDao(): TaskTypesDao

}