package com.hyungilee.commutingmanagement.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "commuting_data_tbl")
data class CommutingData(
    @PrimaryKey
    var user_id: String,
    @ColumnInfo(name = "username")
    var user_name: String,
    @ColumnInfo(name = "date")
    var date: String,
    @ColumnInfo(name = "category")
    var category: String,
    @ColumnInfo(name = "startTime")
    var start_time: String,
    @ColumnInfo(name = "leaveTime")
    var leave_time: String
){
    override fun toString(): String {
        return "user_id: $user_id, user_name: $user_name, date: $date, category: $category," +
                "start_time: $start_time, leave_time: $leave_time"
    }
}