package app.unduit.a2achatapp.models.roomModels

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey


@Entity(tableName = "except_data")
data class ExceptData(


    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int=0,

    @ColumnInfo(name = "post_id")
    var post_id:String=""




)