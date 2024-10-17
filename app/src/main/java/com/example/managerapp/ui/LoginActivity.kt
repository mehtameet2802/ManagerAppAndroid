package com.example.managerapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.managerapp.R
import com.example.managerapp.databinding.ActivityLoginBinding
import com.example.managerapp.db.UserDatabase
import com.example.managerapp.repository.AuthRepository
import com.example.managerapp.viewmodel.AuthViewModel
import com.example.managerapp.viewmodel.AuthViewModelFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    lateinit var viewModel: AuthViewModel
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val authRepository = AuthRepository(UserDatabase(this))
        val viewModelFactory = AuthViewModelFactory(application,authRepository)
        viewModel = ViewModelProvider(this,viewModelFactory)[AuthViewModel::class.java]

        navController = supportFragmentManager.findFragmentById(R.id.loginNavHostFragment)
            ?.findNavController()!!

        if(viewModel.getCurrentUser()!=null){
            startActivity(Intent(this@LoginActivity,ManagerActivity::class.java))
            finish()
        }
    }
}