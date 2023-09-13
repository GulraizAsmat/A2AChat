package app.unduit.a2achatapp.helpers

import java.text.SimpleDateFormat
import java.util.Calendar

object DateHelper {

    fun getCurrentDateTime():String{
        val calendar = Calendar.getInstance()
        val currentDateAndTime = calendar.time

        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentDateAndTime)
    }
}