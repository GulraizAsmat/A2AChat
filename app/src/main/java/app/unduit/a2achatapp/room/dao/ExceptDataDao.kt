package app.unduit.a2achatapp.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import app.unduit.a2achatapp.models.roomModels.ExceptData
import kotlinx.coroutines.flow.Flow

@Dao
interface ExceptDataDao {
    @Insert
    suspend fun insert(entity: ExceptData)

    @Insert
    suspend fun insertAll(entities: List<ExceptData>)

    @Query("SELECT * FROM except_data")
    suspend fun getAllEntities(): List<ExceptData>

    @Query("SELECT EXISTS (SELECT 1 FROM except_data WHERE post_id = :post_id)")
    suspend fun isUserIdAvailable(post_id: String): Boolean
    @Query("SELECT COUNT(*) FROM except_data")
    suspend fun getRowCount(): Int

    @Query("DELETE FROM except_data ")
    suspend fun deleteAllData()
}