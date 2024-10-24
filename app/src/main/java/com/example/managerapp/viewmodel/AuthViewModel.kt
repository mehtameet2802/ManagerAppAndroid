package com.example.managerapp.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_ETHERNET
import android.net.ConnectivityManager.TYPE_MOBILE
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_ETHERNET
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.managerapp.ManagerApplication
import com.example.managerapp.models.User
import com.example.managerapp.utils.Resource
import com.example.managerapp.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    @ApplicationContext app: Context,
    private val authRepository: AuthRepository
) : AndroidViewModel(app as Application) {

    private val _authResult = MutableStateFlow<Resource<FirebaseUser>>(Resource.StandBy())
    val authResult = _authResult.asStateFlow()

    private val _forgotPasswordResult = MutableStateFlow<Resource<Unit>>(Resource.StandBy())
    val forgotPasswordResult = _forgotPasswordResult.asStateFlow()

    fun signUp(email: String, password: String){
        viewModelScope.launch(Dispatchers.IO) {
            if(hasInternetConnection()){
                _authResult.value = Resource.Loading()
                authRepository.signUp(email, password)
                    .addOnSuccessListener { result ->
                        authRepository.addUserData(User(result.user!!.email!!,result.user!!.uid))
                            .addOnSuccessListener {
                                _authResult.value = Resource.Success(result.user!!)
                                resetAuthResult()
                            }
                            .addOnFailureListener { e ->
                                _authResult.value =
                                    Resource.Error(e.message ?: "Contact Customer Care")
                                resetAuthResult()
                            }
                    }
                    .addOnFailureListener { e ->
                        _authResult.value =
                            Resource.Error(e.message ?: "An error occurred during signup")
                        resetAuthResult()
                    }
            } else{
                _authResult.value =Resource.Error("No Internet Connection")
                resetAuthResult()
            }
        }
    }

    fun logIn(email: String, password: String){
        if(hasInternetConnection()){
            viewModelScope.launch(Dispatchers.IO) {
                _authResult.value = Resource.Loading()
                authRepository.logIn(email, password)
                    .addOnSuccessListener { result ->
                        _authResult.value = Resource.Success(result.user!!)
//                        storeUserLocally(User(result.user!!.email!!,result.user!!.uid))
                        resetAuthResult()
                    }
                    .addOnFailureListener { e ->
                        _authResult.value = Resource.Error(e.message ?: "An error occurred during login")
                        resetAuthResult()
                    }
            }
        } else{
            _authResult.value =Resource.Error("No Internet Connection")
            resetAuthResult()
        }
    }

    fun forgotPassword(email: String) {
        if(hasInternetConnection()){
            viewModelScope.launch(Dispatchers.IO) {
                _forgotPasswordResult.value = Resource.Loading()
                authRepository.forgotPassword(email)
                    .addOnSuccessListener {
                        _forgotPasswordResult.value = Resource.Success(Unit)
                        resetForgetPasswordResult()
                    }
                    .addOnFailureListener { e ->
                        _forgotPasswordResult.value =
                            Resource.Error(
                                e.message ?: "Error occurred while sending password reset mail"
                            )
                        resetForgetPasswordResult()
                    }
            }
        } else{
            _authResult.value = Resource.Error("No Internet Connection")
            resetAuthResult()
        }

    }


    fun getCurrentUser(): FirebaseUser? {
        return authRepository.getCurrentUser()
    }

//    private fun storeUserLocally(user: User) {
//        viewModelScope.launch(Dispatchers.IO) {
//            authRepository.storeUserLocally(user)
//        }
//    }

//    fun getCurrentUser(): User? {
//        if(hasInternetConnection()){
//            val currentUser = authRepository.getCurrentUser()
//            val user = User(currentUser!!.email!!, currentUser.uid)
//            storeUserLocally(user)
//            return user
//        }
//        else{
//
//        }
//        return null
//    }
//
//    private fun storeUserLocally(user: User) {
//        viewModelScope.launch(Dispatchers.IO) {
//            authRepository.storeUserLocally(user)
//        }
//    }
//
//    private fun getLocalUser() {
//        authRepository.getLocalUser()
//    }

    private fun resetAuthResult() {
        viewModelScope.launch {
            delay(500) // Delay to allow handling the message
            _authResult.value = Resource.StandBy() // or another initial state
        }
    }

    private fun resetForgetPasswordResult() {
        viewModelScope.launch {
            delay(400) // Delay to allow handling the message
            _forgotPasswordResult.value = Resource.StandBy() // or another initial state
        }
    }

    private fun hasInternetConnection():Boolean{
        val connectivityManager = getApplication<ManagerApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val activeNetwork = connectivityManager.activeNetwork?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }
        else{
            connectivityManager.activeNetworkInfo?.run {
                return when(type){
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }


}