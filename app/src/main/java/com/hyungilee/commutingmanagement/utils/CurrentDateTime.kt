package com.hyungilee.commutingmanagement.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 現在の日付と時刻の情報を取得するクラス
 */
class CurrentDateTime {

    companion object{

        private val createdAt = LocalDateTime.now()

        fun getCurrentDate(): String{
            return createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE)
        }

        fun getCurrentTime(): String{
            return createdAt.format(DateTimeFormatter.ISO_LOCAL_TIME)
        }

    }

}