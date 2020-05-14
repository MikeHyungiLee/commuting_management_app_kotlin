package com.hyungilee.commutingmanagement.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "commuting_data_tbl")
data class CommutingData(
    @PrimaryKey(autoGenerate = true)
    var id: Int?,
    @ColumnInfo(name = "username")
    var user_name: String,
    @ColumnInfo(name = "category")
    var category: String,
    @ColumnInfo(name = "location_lat")
    var location_lat: String,
    @ColumnInfo(name = "location_lon")
    var location_lon: String,
    @ColumnInfo(name = "date")
    var date: String,
    @ColumnInfo(name = "startTime")
    var start_time: String,
    @ColumnInfo(name = "leaveTime")
    var leave_time: String,
    @ColumnInfo(name = "restTime")
    var rest_time: String,
    @ColumnInfo(name = "workHours")
    var work_hours: String,
    @ColumnInfo(name = "overTimeHours")
    var over_time: String,
    @ColumnInfo(name = "midnightWorkHour")
    var midnight_work_hours: String,
    @ColumnInfo(name = "holidayWorkHour")
    var holiday_work_hours: String
){
    override fun toString(): String {
        return "id: $id, user_name: $user_name, date: $date, category: $category," +
                "start_time: $start_time, leave_time: $leave_time"
    }
}