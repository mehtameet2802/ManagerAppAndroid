package com.example.managerapp.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.managerapp.repository.AuthRepository
import com.example.managerapp.repository.ManagerRepository

class ManagerViewModelFactory(
    val app: Application,
    val authRepository: AuthRepository,
    val managerRepository: ManagerRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ManagerViewModel(app, authRepository, managerRepository) as T
    }
}