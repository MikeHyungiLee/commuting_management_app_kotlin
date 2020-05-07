package com.hyungilee.commutingmanagement.ui.commutingdatahistory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hyungilee.commutingmanagement.data.repository.CommutingDatabaseRepository

@Suppress("UNCHECKED_CAST")
class CommutingDataHistoryViewModelFactory (private val repository: CommutingDatabaseRepository)
    : ViewModelProvider.NewInstanceFactory(){

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CommutingDataHistoryViewModel(repository) as T
    }
}