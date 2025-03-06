package com.example.managerapp.ui.composescreens


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
import com.example.managerapp.utils.Resource
import com.example.managerapp.viewmodel.AuthViewModel


@Composable
fun RegistrationScreen(viewModel: AuthViewModel, navController: NavController) {

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var emailError by rememberSaveable { mutableStateOf<String?>(null) }
    var passwordError by rememberSaveable { mutableStateOf<String?>(null) }
    var confirmPasswordError by rememberSaveable { mutableStateOf<String?>(null) }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current


    LaunchedEffect(viewModel.authResult) {
        viewModel.authResult.collect { resource ->
            when (resource) {
                is Resource.Error -> {
                    isLoading = false
                    Toast.makeText(context, resource.message, Toast.LENGTH_LONG).show()
                }

                is Resource.Loading -> isLoading = true

                is Resource.Success -> {
                    Log.d("Login", resource.data.toString())
                    isLoading = false
                    Toast.makeText(context, "Registered Successful", Toast.LENGTH_LONG).show()
                    navController.navigate(R.id.loginFragment)
                }

                is Resource.StandBy -> isLoading = false
            }
        }
    }

    RegistrationFragmentDesign(
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
                if (passwordValidation(it, confirmPassword)) null else "Invalid password"
        },
        passwordError = passwordError,
        confirmPassword = confirmPassword,
        onConfirmPasswordChange = {
            confirmPassword = it
            confirmPasswordError =
                if (passwordValidation(password, it)) null else "Passwords do not match"
        },
        confirmPasswordError = confirmPasswordError,
        isLoading = isLoading,
        onRegister = {
            if (emailValidation(email) && passwordValidation(password, confirmPassword)) {
                viewModel.signUp(email, password)
            } else {
                emailError = if (!emailValidation(email)) "Invalid email address" else null
                passwordError =
                    if (!passwordValidation(password, confirmPassword)) "Invalid password" else null
                confirmPasswordError = if (!passwordValidation(
                        password,
                        confirmPassword
                    )
                ) "Passwords do not match" else null
            }
        },
        onLogin = {
            navController.navigate(R.id.loginFragment)
        }
    )
}

private fun emailValidation(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
}

private fun passwordValidation(password: String, confirmPassword: String?): Boolean {
    return if (confirmPassword != null) {
        password == confirmPassword && password.length >= 8
    } else {
        password.length >= 8
    }
}

@Composable
fun RegistrationFragmentDesign(
    email: String,
    onEmailChange: (String) -> Unit,
    emailError: String?,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordError: String?,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    confirmPasswordError: String?,
    isLoading: Boolean,
    onRegister: () -> Unit,
    onLogin: () -> Unit
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
                text = "Register",
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

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = onConfirmPasswordChange,
                isError = confirmPasswordError != null,
                label = { Text("Confirm Password") },
                placeholder = {
                    Text("Please re-enter your Password")
                },
                supportingText = {
                    if (confirmPasswordError != null) {
                        Text(confirmPasswordError)
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onRegister,
                modifier = Modifier
                    .width(150.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text("Register")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Already Registered? Login",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onLogin()
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
fun RegistrationPreview() {
    RegistrationFragmentDesign(
        email = "",
        onEmailChange = {},
        emailError = null,
        password = "",
        onPasswordChange = {},
        passwordError = null,
        confirmPassword = "",
        onConfirmPasswordChange = {},
        confirmPasswordError = null,
        isLoading = false,
        onRegister = {},
        onLogin = {}
    )
}