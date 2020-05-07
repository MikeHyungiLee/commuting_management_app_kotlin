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

    @Query("UPDATE commuting_data_tbl SET leaveTime =:end_time WHERE date =:currentDate")
    fun updateLeaveTime(end_time: String, currentDate: String)

    @Query("SELECT startTime FROM commuting_data_tbl WHERE date =:currentDate")
    fun searchStartTime(currentDate: String): String

}