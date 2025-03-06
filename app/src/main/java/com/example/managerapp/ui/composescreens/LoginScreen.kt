package com.example.managerapp.ui.composescreens


import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.managerapp.R
import com.example.managerapp.ui.ManagerActivity
import com.example.managerapp.utils.Resource
import com.example.managerapp.viewmodel.AuthViewModel


@Composable
fun LoginScreen(viewModel: AuthViewModel, navController: NavController) {

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var emailError by rememberSaveable { mutableStateOf<String?>(null) }
    var passwordError by rememberSaveable { mutableStateOf<String?>(null) }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current


    LaunchedEffect(viewModel.authResult) {
        viewModel.authResult.collect { resource ->
            when (resource) {
                is Resource.Error -> {
                    isLoading = false
                    println("logged in failed collecting error ========= " + resource.data)
                    Toast.makeText(context, resource.message, Toast.LENGTH_LONG).show()
                }

                is Resource.Loading -> isLoading = true

                is Resource.Success -> {
                    println("logged in successfully ========= " + resource.data)
                    Log.d("LoginFragment", resource.data.toString())
                    isLoading = false
                    Toast.makeText(context, "Login Successful", Toast.LENGTH_LONG).show()


                    val intent = Intent(context, ManagerActivity::class.java)
                    // Add flags to clear the activity stack
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    // Start the activity
                    context.startActivity(intent)
                    (context as? android.app.Activity)?.finish()
                }

                is Resource.StandBy -> isLoading = false
            }
        }
    }

    LoginFragmentDesign(
        email = email,
        onEmailChange = {
            email = it
            emailError = if (emailValidation(it)) null else "Invalid email address"
        },
        emailError = emailError,
        password = password,
        onPasswordChange = {
            password = it
            passwordError =
                if (passwordValidation(it)) null else "Invalid password"
        },
        passwordError = passwordError,
        isLoading = isLoading,
        onLogin = {
            if (emailValidation(email) && passwordValidation(password)) {
                viewModel.logIn(email, password)
            } else {
                emailError = if (!emailValidation(email)) "Invalid email address" else null
                passwordError = if (!passwordValidation(password)) "Invalid password" else null
            }

        },
        onRegister = {
            navController.navigate(R.id.registrationFragment)
        },
        onForgotPassword = {
            navController.navigate(R.id.forgotPasswordFragment)
        }


    )
}

private fun emailValidation(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
}

private fun passwordValidation(password: String): Boolean {
    return password.trim().length >= 8
}

@Composable
fun LoginFragmentDesign(
    email: String,
    onEmailChange: (String) -> Unit,
    emailError: String?,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordError: String?,
    isLoading: Boolean,
    onLogin: () -> Unit,
    onRegister: () -> Unit,
    onForgotPassword: () -> Unit

) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 50.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 20.dp)
                .verticalScroll(rememberScrollState())
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Login",
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(18.dp))

            Image(
                painter = painterResource(R.drawable.ic_manager),
                contentDescription = "Register Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                isError = emailError != null,
                label = { Text("Email") },
                placeholder = {
                    Text("Please enter Email Address")
                },
                supportingText = {
                    if (emailError != null) {
                        Text(emailError)
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                isError = passwordError != null,
                label = { Text("Password") },
                placeholder = {
                    Text("Please enter Password")
                },
                supportingText = {
                    if (passwordError != null) {
                        Text(passwordError)
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onLogin,
                modifier = Modifier
                    .width(150.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text("Login")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "New User? Register",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onRegister()
                    }
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Forgot Password?",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onForgotPassword()
                    }
            )

        }

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    LoginFragmentDesign(
        email = "",
        onEmailChange = {},
        emailError = null,
        password = "",
        onPasswordChange = {},
        passwordError = null,
        isLoading = false,
        onLogin = {},
        onRegister = {},
        onForgotPassword = {}

    )
}