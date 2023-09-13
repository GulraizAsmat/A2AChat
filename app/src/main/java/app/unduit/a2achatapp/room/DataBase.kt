package app.unduit.a2achatapp.room

import androidx.room.Database
import androidx.room.RoomDatabase
import app.unduit.a2achatapp.models.roomModels.ExceptData
import app.unduit.a2achatapp.room.dao.ExceptDataDao


@Database(entities = [ExceptData::class], version = 1)
abstract class Database : RoomDatabase() {
    abstract fun exceptDataDao(): ExceptDataDao
}