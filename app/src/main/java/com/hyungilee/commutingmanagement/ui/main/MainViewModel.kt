package com.hyungilee.commutingmanagement.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hyungilee.commutingmanagement.data.entity.CommutingData
import com.hyungilee.commutingmanagement.data.repository.CommutingDatabaseRepository

class MainViewModel(application: Application): AndroidViewModel(application) {

    private val commutingDatabaseRepository = CommutingDatabaseRepository(application)
    private val allCommutingData: LiveData<List<CommutingData>> =
                                     commutingDatabaseRepository.getAllCommutingData()

    fun saveCommutingData(commutingData: CommutingData){
        commutingDatabaseRepository.saveCommutingData(commutingData)
    }

    fun getAllCommutingData(): LiveData<List<CommutingData>>{
        return allCommutingData
    }
}