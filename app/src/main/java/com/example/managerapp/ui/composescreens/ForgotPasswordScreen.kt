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
fun ForgotPasswordScreen(viewModel: AuthViewModel, navController: NavController) {
    var email by rememberSaveable { mutableStateOf("") }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var emailError by rememberSaveable { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    LaunchedEffect(viewModel.forgotPasswordResult) {

        viewModel.forgotPasswordResult.collect { resource ->

            when (resource) {
                is Resource.Loading -> isLoading = true
                is Resource.Success -> {
                    Log.d("Login", resource.data.toString())
                    isLoading = false
                    Toast.makeText(context, "Reset Password Mail Sent", Toast.LENGTH_SHORT).show()
                    navController.navigate("login_screen")
                }

                is Resource.Error -> {
                    isLoading = false
                    Toast.makeText(
                        context,
                        resource.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is Resource.StandBy -> isLoading = false
            }
        }
    }


    // Use the extracted UI
    ForgotPasswordScreenDesign(
        email = email,
        onEmailChange = {
            email = it
            emailError = if (emailValidation(it)) null else "Invalid email address"
        },
        emailError = emailError,
        isLoading = isLoading,
        onForgotPassword = {
            isLoading = true
            if (!emailValidation(email)) {
                emailError = "Invalid email address"
            } else {
                emailError = null
                viewModel.forgotPassword(email)
            }
        },
        onLogin = {
            navController.navigate("login_screen") {
                popUpTo("login_screen") { inclusive = true }
            }
        }
    )

}

private fun emailValidation(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
}

@Composable
fun ForgotPasswordScreenDesign(
    email: String,
    onEmailChange: (String) -> Unit,
    emailError: String?,
    isLoading: Boolean,
    onForgotPassword: () -> Unit,
    onLogin: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 50.dp),
        contentAlignment = Alignment.Center
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
                text = "Reset Password",
                fontSize = 30.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(25.dp))

            Image(
                painter = painterResource(R.drawable.ic_manager),
                contentDescription = "Forgot Password Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

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
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onForgotPassword,
                modifier = Modifier
                    .width(150.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text("Send Link", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Login",
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
fun ForgotPasswordPreview() {
    ForgotPasswordScreenDesign(
        email = "",
        onEmailChange = {},
        emailError = null,
        isLoading = false,
        onForgotPassword = {},
        onLogin = {}
    )
}
