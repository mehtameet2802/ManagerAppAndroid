package com.example.managerapp.ui.composeactivities

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.managerapp.ui.ManagerActivity
//import com.example.managerapp.db.UserDatabase
import com.example.managerapp.ui.composescreens.ForgotPasswordScreen
import com.example.managerapp.ui.composescreens.LoginScreen
import com.example.managerapp.ui.composescreens.RegistrationScreen
import com.example.managerapp.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

//@AndroidEntryPoint
//class LoginActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityLoginBinding
//    private lateinit var navController: NavController
//
//    val viewModel: AuthViewModel by viewModels()
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityLoginBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        navController = supportFragmentManager.findFragmentById(R.id.loginNavHostFragment)
//            ?.findNavController()!!
//
//        if(viewModel.getCurrentUser()!=null){
//            startActivity(Intent(this@LoginActivity,ManagerActivity::class.java))
//            finish()
//        }
//    }
//}


@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {


    val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()

            LaunchedEffect(viewModel.getCurrentUser()) {
                if (viewModel.getCurrentUser() != null) {
                    val intent = Intent(this@LoginActivity, ManagerActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }

            NavHost(navController = navController, startDestination = "login_screen") {
                composable("login_screen") {
                    LoginScreen(viewModel = viewModel, navController = navController)
                }
                composable("registration_screen") {
                    RegistrationScreen(viewModel = viewModel, navController = navController)
                }
                composable("forgot_password_screen") {
                    ForgotPasswordScreen(viewModel = viewModel, navController = navController)
                }
            }
        }

    }

}