package com.hyungilee.commutingmanagement.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "commuting_data_tbl")
data class CommutingData(
    @PrimaryKey(autoGenerate = true)
    var user_id: Long?,
    @ColumnInfo(name = "username")
    var username: String,
    @ColumnInfo(name = "date")
    var date: String,
    @ColumnInfo(name = "category")
    var category: String,
    @ColumnInfo(name = "startTime")
    var startTime: String,
    @ColumnInfo(name = "endTime")
    var endTime: String
)