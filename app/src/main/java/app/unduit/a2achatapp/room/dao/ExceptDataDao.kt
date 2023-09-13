package app.unduit.a2achatapp.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import app.unduit.a2achatapp.models.roomModels.ExceptData
import kotlinx.coroutines.flow.Flow

@Dao
interface ExceptDataDao {
    @Insert
    suspend fun insert(entity: ExceptData)

    @Query("SELECT * FROM except_data")
    suspend fun getAllEntities(): List<ExceptData>

    @Query("SELECT COUNT(*) FROM except_data")
    suspend fun getRowCount(): Int
}