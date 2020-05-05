package com.hyungilee.commutingmanagement.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.hyungilee.commutingmanagement.data.entity.CommutingData

@Dao
interface CommutingManagementDao {
    @Insert
    suspend fun saveCommutingData(commutingData: CommutingData)

    @Query("SELECT * FROM commuting_data_tbl ORDER BY user_id DESC")
    fun getAllCommutingData(): LiveData<List<CommutingData>>

}