package com.fscsoftware.managetasks.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity (tableName = "tasks_finish")
data class TaskFinish  (
    @PrimaryKey(autoGenerate = true) override var id: Int?
    , @ColumnInfo(name = "title") override var title : String
    , @ColumnInfo(name = "priority") override var priority : String
    , @ColumnInfo(name = "position") override var position : Int?
    , @ColumnInfo(name = "detail") override var detail : String?
    , @ColumnInfo(name = "type") override var type: String?
    , @ColumnInfo(name = "files_reference") override var filesReference: String?
    , @ColumnInfo(name = "date_created") override val dateCreated : Long?
    , @ColumnInfo(name = "date_finish") override var dateFinish: Long?
) : TaskFields {

    override fun toTaskFinish() : TaskFinish {
        return this
    }

    override fun toTask() : Task {
        return Task(
            id
            , title
            , priority
            , position
            , detail
            , type
            , filesReference
            , dateCreated
            , dateFinish
        )
    }

}