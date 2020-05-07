package com.hyungilee.commutingmanagement.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.hyungilee.commutingmanagement.data.db.CommutingManagementDao
import com.hyungilee.commutingmanagement.data.db.CommutingManagementDatabase
import com.hyungilee.commutingmanagement.data.entity.CommutingData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class CommutingDatabaseRepository(private val db: CommutingManagementDatabase) {
    private var commutingDatabaseDao: CommutingManagementDao = db.commutingManagementDao()
    private var allCommutingData: LiveData<List<CommutingData>>

    init {
        allCommutingData = commutingDatabaseDao.getAllCommutingData()
    }

    fun saveCommutingData(commutingData: CommutingData) = runBlocking {
        this.launch(Dispatchers.IO) {
            commutingDatabaseDao.saveCommutingData(commutingData)
        }
    }

    fun getAllCommutingData(): LiveData<List<CommutingData>> {
        return allCommutingData
    }

}