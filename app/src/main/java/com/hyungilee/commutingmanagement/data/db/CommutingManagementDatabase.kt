package com.hyungilee.commutingmanagement.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hyungilee.commutingmanagement.data.entity.CommutingData

@Database(entities = [CommutingData::class], version = 1, exportSchema = false)
abstract class CommutingManagementDatabase :RoomDatabase() {

    abstract fun commutingManagementDao(): CommutingManagementDao

    companion object{
        private  var INSTANCE: CommutingManagementDatabase? = null

        fun getInstance(context: Context) : CommutingManagementDatabase? {
            if(INSTANCE == null){
                synchronized(CommutingManagementDatabase::class){
                    INSTANCE = Room.databaseBuilder(
                        context,
                        CommutingManagementDatabase::class.java,
                        "management_db"
                    ).build()
                }
            }
            return INSTANCE
        }
    }
}