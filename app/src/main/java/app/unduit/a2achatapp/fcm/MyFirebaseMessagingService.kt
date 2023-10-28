package app.unduit.a2achatapp.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import app.unduit.a2achatapp.MainActivity
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.helpers.Const
import app.unduit.a2achatapp.helpers.SharedPref
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.Random

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val screenName = "MyFirebaseToken"
    override fun onNewToken(token: String) {
        // This method will be called when a new token is generated.
        // You can save or send the token to your server for later use.
        // In this example, we'll print the token to the log.
        Log.e("fcm_toke22n", "Refreshed token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.e(screenName, "------------Notification Received -----------")
        val data = remoteMessage.notification
        val title = data!!.title
        val body = data.body
        Log.e(screenName, "Notification title : $title")
        Log.e(screenName, "Notification body : $body")

        if(title=="Request Matched"){
          var count=  SharedPref.getInt(this,Const.matchCount,0)
            count += 1
            SharedPref.setInt(this,Const.matchCount,count)


        }

        val intentPlease = Intent(this, MainActivity::class.java)


        intentPlease.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        intentPlease.action = "FirstTimeData"

        generateNotification(this,
            intentPlease,
            body!!,
            title!!,
            "custom",
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        // Handle incoming FCM messages here if needed.
    }


    companion object {

        fun generateNotification(
            context: Context,
            intent: Intent,
            title: String,
            message: String,
            pushType: String,
            sound: Uri?,
        ) {


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


                setupNotificationChannels(pushType, sound!!, context, message, title, intent)

            }
            /**   close temp **/


            /**   end **/


        }


        private fun setupNotificationChannels(
            pushType: String,
            sound: Uri,
            context: Context,
            title: String,
            message: String,
            intent: Intent,
        ) {


            if (pushType == "panic") {
                val ADMIN_CHANNEL_ID = "Panic"

                val adminChannelName = "SOS Alert"
                val adminChannelDescription = "Lagom Parent"

                val adminChannel: NotificationChannel

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    adminChannel = NotificationChannel(
                        ADMIN_CHANNEL_ID,
                        adminChannelName,
                        NotificationManager.IMPORTANCE_HIGH
                    )



                    adminChannel.description = adminChannelDescription
                    adminChannel.enableLights(true)
                    Log.e("ringtune", "panic--------------")
                    val audioAttributes = AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build()
                    adminChannel.setSound(sound, audioAttributes)

                    adminChannel.lightColor = Color.RED
                    adminChannel.enableVibration(true)


                    val uniqueInt = (System.currentTimeMillis() and 0xfffffff).toInt()
                    val contentIntent = PendingIntent.getActivity(
                        context,
                        uniqueInt,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )

                    val notificationId = Random().nextInt(60000)


//            val defaultSoundUri =
//                    RingtoneManager.getDefaultUri(RingtoneManager.getDefaultType(sound))

                    val notificationBuilder = NotificationCompat.Builder(context, ADMIN_CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher_round)  //a resource for your custom small icon
                        .setContentTitle(title) //the "title" value you sent in your notification
                        .setContentText(message) //ditto
                        .setAutoCancel(true)  //dismisses the notification on click
                        .setContentIntent(contentIntent)


                    val notificationManager =
                        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                    if (notificationManager != null)
                        notificationManager.createNotificationChannel(adminChannel)

                    notificationManager.notify(
                        notificationId /* ID of notification */,

                        notificationBuilder.build())


                }


            } else {
                Log.e("ringtune", "other--------------")
                val ADMIN_CHANNEL_ID = "other notifications"

                val adminChannelName = "Other Alerts"
                val adminChannelDescription = "A2A Chat App"

                val adminChannel: NotificationChannel
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    adminChannel = NotificationChannel(
                        ADMIN_CHANNEL_ID,
                        adminChannelName,
                        NotificationManager.IMPORTANCE_HIGH
                    )



                    adminChannel.description = adminChannelDescription
                    adminChannel.enableLights(true)

                    val audioAttributes = AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build()
                    adminChannel.setSound(
                        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                        audioAttributes)

                    adminChannel.lightColor = Color.RED
                    adminChannel.enableVibration(true)


                    val uniqueInt = (System.currentTimeMillis() and 0xfffffff).toInt()
                    val contentIntent = PendingIntent.getActivity(context, uniqueInt, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

                    val notificationId = Random().nextInt(60000)


//            val defaultSoundUri =
//                    RingtoneManager.getDefaultUri(RingtoneManager.getDefaultType(sound))

                    val notificationBuilder = NotificationCompat.Builder(context, ADMIN_CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher_round)  //a resource for your custom small icon
                        .setContentTitle(title) //the "title" value you sent in your notification
                        .setContentText(message) //ditto
                        .setAutoCancel(true)  //dismisses the notification on click
                        .setContentIntent(contentIntent)


                    val notificationManager =
                        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                    if (notificationManager != null)
                        notificationManager.createNotificationChannel(adminChannel)

                    notificationManager.notify(
                        notificationId /* ID of notification */,

                        notificationBuilder.build())


                }


            }


        }


    }
}