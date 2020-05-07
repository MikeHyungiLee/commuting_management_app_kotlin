package com.hyungilee.commutingmanagement.ui.commutingdatahistory

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hyungilee.commutingmanagement.data.db.CommutingManagementDatabase
import com.hyungilee.commutingmanagement.data.entity.CommutingData
import com.hyungilee.commutingmanagement.data.repository.CommutingDatabaseRepository

class CommutingDataHistoryViewModel(private val repository: CommutingDatabaseRepository) : ViewModel() {

    private val allCommutingData: LiveData<List<CommutingData>> =
        repository.getAllCommutingData()

    fun saveCommutingData(commutingData: CommutingData){
        repository.saveCommutingData(commutingData)
    }

    fun getAllCommutingData(): LiveData<List<CommutingData>> {
        return allCommutingData
    }

}
