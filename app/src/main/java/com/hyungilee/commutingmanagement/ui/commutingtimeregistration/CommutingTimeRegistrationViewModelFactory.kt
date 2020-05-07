package com.hyungilee.commutingmanagement.ui.commutingtimeregistration

import android.app.Application
import android.app.ApplicationErrorReport
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hyungilee.commutingmanagement.data.repository.CommutingDatabaseRepository

@Suppress("UNCHECKED_CAST")
class CommutingTimeRegistrationViewModelFactory (private val repository: CommutingDatabaseRepository)
    : ViewModelProvider.NewInstanceFactory(){

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CommutingTimeRegistrationViewModel(repository) as T
    }
}