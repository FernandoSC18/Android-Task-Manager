package com.fscsoftware.managetasks.model

open interface TaskFields
{

    var id: Int?
    var title : String
    var priority : String
    var position : Int?
    var detail : String?
    //Type allow add one task by type as image, audio
    var type: String?
    //File reference has the ids from audio files or image files
    var filesReference: String?
    val dateCreated : Long?
    var dateFinish : Long?

    fun toTaskFinish () : TaskFinish

    fun toTask () : Task

}