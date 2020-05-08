package com.hyungilee.commutingmanagement.ui.commutingdatahistory.adapter
import com.hyungilee.commutingmanagement.ui.commutingdatahistory.models.RandomDataCell
import java.util.*

class RandomDataFactory(
        numColumns: Int,
        numRows: Int
) {

    var randomCellsList = mutableListOf<Any>()
        private set

    var randomColumnHeadersList = mutableListOf<Any>()
        private set

    var randomRowHeadersList = mutableListOf<Any>()
        private set

    init {
        (1 until 32).forEach {
            randomRowHeadersList.add(RandomDataCell("""$it　日"""))
        }

        val column = arrayListOf<String>("区分","出社","退社","休憩","就業時間","残業時間","深夜残業","法定休日")
        (0 until column.size).forEach {
            randomColumnHeadersList.add(RandomDataCell(column[it]))
        }

        val columnTitle = arrayListOf("category","start","end","rest","work","extra","midnight","public")
        (1 until 32).forEach { row ->
            val cellList = mutableListOf<Any>()
            (0 until column.size).forEach { column ->
                val data: Any = '-'

                cellList.add(RandomDataCell(data, "$column,$row"))
                // 여기에서 Room 데이터베이스에서 데이터를 검색하면 될 것같다.
                // 아래 Query 문에 맞게 Dao 서비스 메소드 작성하기.
                // SELECT [columnTitle[column]] FROM [table] WHERE id='[row]'
            }

            randomCellsList.add(cellList)
        }
    }
}