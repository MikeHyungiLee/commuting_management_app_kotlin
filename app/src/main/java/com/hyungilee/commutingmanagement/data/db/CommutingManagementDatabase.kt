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

        // Volatile : This variable is immediately visible to the all the other tracks.
        @Volatile
        private var INSTANCE: CommutingManagementDatabase? = null
        // LOCK : 두 개 이상의 database 인스턴스 생성을 방지하기 위한 설정
        private val LOCK = Any()

        // 데이터베이스 객체를 만들때, invoke 를 통해 context 를 넘겨서 만들어 줄 것이다.
        // Instance 가 NULL 인 경우, synchronized block 을 통해서 초기화시켜준다.
        operator fun invoke(context: Context) = INSTANCE?: synchronized(LOCK){
            INSTANCE?: buildDatabase(context).also{
                INSTANCE = it
            }
        }
        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                CommutingManagementDatabase::class.java,
                "CommutingManagementDatabase.db"
            ).build()

    }

    // 아래와 같은 방식으로 database 의 instance 취득할 수도 있다.
//    fun getInstance(context: Context) : CommutingManagementDatabase? {
//        if(INSTANCE == null){
//            synchronized(CommutingManagementDatabase::class){
//                INSTANCE = Room.databaseBuilder(
//                    context,
//                    CommutingManagementDatabase::class.java,
//                    "management_db"
//                ).build()
//            }
//        }
//        return INSTANCE
//    }

}