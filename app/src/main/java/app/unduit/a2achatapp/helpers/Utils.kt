package app.unduit.a2achatapp.helpers

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import java.text.DecimalFormat
import java.text.SimpleDateFormat

import java.util.*
import java.util.concurrent.TimeUnit

object Utils {

    fun toast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }




    fun getTime(timestamp: Double): String? {
        try {

            val sdf = SimpleDateFormat("hh:mm a")
            val date = Date(timestamp.toLong() * 1000)
            sdf.format(date)
            return sdf.format(date)
        } catch (e: Exception) {
        }
        return ""
    }


    fun formatPrice(price: Double): String {
        val df = DecimalFormat("#.##")

        return when {
            price >= 1_000_000_000 -> "${df.format(price / 1_000_000_000)}B"
            price >= 1_000_000 -> "${df.format(price / 1_000_000)}M"
            price >= 1_000 -> "${df.format(price / 1_000)}K"
            else -> df.format(price)
        }
    }


    fun getTimeAgo(timestamp: Double): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp.toLong()*1000
        val minuteInMillis = 60 * 1000
        val hourInMillis = 60 * minuteInMillis
        val dayInMillis = 24 * hourInMillis
        val monthInMillis = 30 * dayInMillis

        return when {
            diff < minuteInMillis -> "0m ago"
            diff < 2 * minuteInMillis -> "1m ago"
            diff < hourInMillis -> "${diff / minuteInMillis}m ago"
            diff < 2 * hourInMillis -> "1h ago"
            diff < dayInMillis -> "${diff / hourInMillis}h ago"
            diff < 2 * dayInMillis -> "1d ago"
            diff < 3 * dayInMillis -> "${diff / dayInMillis}d ago"
            diff < 4 * dayInMillis -> "${diff / dayInMillis}d ago"
            diff < 5 * dayInMillis -> "${diff / dayInMillis}d ago"
            diff < 6 * dayInMillis -> "${diff / dayInMillis}d ago"
            diff < 7 * dayInMillis -> "${diff / dayInMillis}d ago"
            diff < 8 * dayInMillis -> "${diff / dayInMillis}d ago"
            diff < 9 * dayInMillis -> "${diff / dayInMillis}d ago"
            diff < 10 * dayInMillis -> "${diff / dayInMillis}d ago"
            diff < 11 * dayInMillis -> "${diff / dayInMillis}d ago"
            diff < 12 * dayInMillis -> "${diff / dayInMillis}d ago"
            diff < 13 * dayInMillis -> "${diff / dayInMillis}d ago"
            diff < 14 * dayInMillis -> "${diff / dayInMillis}d ago"
            diff < 15 * dayInMillis -> "${diff / dayInMillis}d ago"
            diff < 16 * dayInMillis -> "${diff / dayInMillis}d ago"
            diff < 17 * dayInMillis -> "${diff / dayInMillis}d ago"
            diff < 18  * dayInMillis  -> "${diff / dayInMillis}d ago"
            diff < 19  * dayInMillis  -> "${diff / dayInMillis}d ago"
            diff < 20  * dayInMillis  -> "${diff / dayInMillis}d ago"
            diff < 21  * dayInMillis  -> "${diff / dayInMillis}d ago"
            diff < 22  * dayInMillis  -> "${diff / dayInMillis}d ago"
            diff < 23  * dayInMillis  -> "${diff / dayInMillis}d ago"
            diff < 24  * dayInMillis  -> "${diff / dayInMillis}d ago"
            diff < 25  * dayInMillis  -> "${diff / dayInMillis}d ago"
            diff < 26  * dayInMillis  -> "${diff / dayInMillis}d ago"
            diff < 27  * dayInMillis  -> "${diff / dayInMillis}d ago"
            diff < 28  * dayInMillis  -> "${diff / dayInMillis}d ago"
            diff < 29  * dayInMillis  -> "${diff / dayInMillis}d ago"
            diff < 30  * dayInMillis  -> "${diff / dayInMillis}d ago"
            diff < 31  * dayInMillis  -> "${diff / dayInMillis}d ago"

            diff < monthInMillis -> "1mo ago"
            else -> "${diff / monthInMillis}mo ago"
        }
    }
    fun getDate(timestamp: Double): String? {
        try {
//            val sdf = SimpleDateFormat("yyyy-MM-dd")
//            val netDate = Date(timestamp.toLong())
//            val date =sdf.format(netDate)

//            Log.e("Tag","Formatted Date"+date)
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val date = Date(timestamp.toLong() * 1000)
            sdf.format(date)
            return sdf.format(date)
        } catch (e: Exception) {
        }
        return ""
    }
    fun getDay(timestamp: Double): String? {
        try {

            val sdf = SimpleDateFormat("EEEE")
            val date = Date(timestamp.toLong() * 1000)
            sdf.format(date)
            return sdf.format(date)
        } catch (e: Exception) {
        }
        return ""
    }


}