package app.unduit.a2achatapp

import android.app.Application

import androidx.room.Room
import app.unduit.a2achatapp.room.Database
import com.google.android.libraries.places.api.Places


class Application : Application() {

    // Define your database instance as a singleton
    companion object {
        lateinit var database: Database
            private set
    }

    override fun onCreate() {
        super.onCreate()

        // Initialize your Room database
        database = Room.databaseBuilder(
            applicationContext,
            Database::class.java,
            "a2a_chat"
        ).build()

//        Places.initialize(applicationContext, getString(R.string.google_places_api_key))


        // Other initialization code, if needed
    }
}