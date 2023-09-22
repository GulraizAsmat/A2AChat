package app.unduit.a2achatapp.helpers

import org.ocpsoft.prettytime.PrettyTime
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateHelper {

    fun getCurrentDateTime():String{
        val calendar = Calendar.getInstance()
        val currentDateAndTime = calendar.time

        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentDateAndTime)
    }

    fun getDateTimeFromTimestamp(timeStamp: String): String {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.ENGLISH)
            val netDate = Date(timeStamp.toLong())
            sdf.format(netDate)
        } catch (e: Exception) {
            ""
        }
    }

    fun convertTimestampToTimeAgo(timestampInMillis: Long): String {
        val prettyTime = PrettyTime()

        // Convert milliseconds to Date object
        val date = Date(timestampInMillis)

        // Get the time-ago string
        return prettyTime.format(date)
    }
}