package com.hyungilee.commutingmanagement.ui.commutingtimeregistration

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class CommutingTimeRegistrationViewModelFactory (private val application: Application)
    : ViewModelProvider.NewInstanceFactory(){

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CommutingTimeRegistrationViewModel(application) as T
    }
}