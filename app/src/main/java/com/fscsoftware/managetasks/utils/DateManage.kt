package com.fscsoftware.managetasks.utils

import java.text.SimpleDateFormat
import java.util.*

object DateManage {

    const val FORMAT_SIMPLE = "dd/MM/yyyy";
    const val FORMAT_FULL = "dd-MM-yyyy HH:mm:ss";

    fun longToStringDate ( date : Long?, format : String ) : String? {
        if (date == null ) return null
        val formatter = SimpleDateFormat(format)
        return formatter.format(date)
    }

    fun dateToStringDate (date : Date?, format : String ) : String? {
        if (date == null ) return null
        val formatter = SimpleDateFormat(format)
        return formatter.format(date)
    }

}