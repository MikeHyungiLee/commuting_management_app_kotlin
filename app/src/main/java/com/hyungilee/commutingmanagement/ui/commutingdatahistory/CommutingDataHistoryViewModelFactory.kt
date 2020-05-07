package com.hyungilee.commutingmanagement.ui.commutingdatahistory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class CommutingDataHistoryViewModelFactory (private val application: Application)
    : ViewModelProvider.NewInstanceFactory(){

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CommutingDataHistoryViewModel(application) as T
    }
}