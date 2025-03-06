package com.example.managerapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.fragment.findNavController
import com.example.managerapp.ui.composeactivities.LoginActivity
import com.example.managerapp.ui.composescreens.ForgotPasswordScreen
import com.example.managerapp.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

//@AndroidEntryPoint
//class ForgotPasswordFragment : Fragment() {
//
//    lateinit var binding: FragmentForgotPasswordBinding
//    lateinit var viewModel: AuthViewModel
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        // Inflate the layout for this fragment
//        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        viewModel = (activity as LoginActivity).viewModel
//
//        binding.loginText.setOnClickListener {
//            findNavController().navigateUp()
//        }
//
//        binding.forgotPasswordImage.setImageResource(R.drawable.ic_manager)
//
//        binding.forgotPasswordEmail.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                if(!emailValidation(s.toString())){
//                    binding.forgotPasswordEmail.error = "Invalid email address"
//                }
//            }
//            override fun afterTextChanged(s: Editable?) {}
//        })
//
//        binding.forgotPasswordBtn.setOnClickListener {
//            handleForgotPassword()
//        }
//        observeForgotPasswordResult()
//    }
//
//
//    private fun emailValidation(email: String): Boolean {
//        return android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
//    }
//
//    private fun handleForgotPassword(){
//        val email = binding.forgotPasswordEmail.text.toString().trim()
//        if(!emailValidation(email)){
//            binding.forgotPasswordEmail.error = "Invalid email address"
//        }
//        else{
//            viewModel.forgotPassword(email)
//        }
//    }
//
//    private fun observeForgotPasswordResult() {
//        viewLifecycleOwner.lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                viewModel.forgotPasswordResult.collect{ resource ->
//                    when(resource){
//                        is Resource.Error -> {
//                            binding.progressBar.visibility = View.GONE
//                            Toast.makeText(context,resource.message, Toast.LENGTH_LONG).show()
//                        }
//                        is Resource.Loading -> {
//                            binding.progressBar.visibility = View.VISIBLE
//                        }
//                        is Resource.Success -> {
//                            Log.d("Login",resource.data.toString())
//                            binding.progressBar.visibility = View.GONE
//                            Toast.makeText(context,"Reset Password Mail Sent Successful", Toast.LENGTH_LONG).show()
//                            findNavController().navigate(R.id.loginFragment)
//                        }
//
//                        is Resource.StandBy -> {
//                            binding.progressBar.visibility = View.INVISIBLE
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//}


@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {

    lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = (activity as LoginActivity).viewModel

        return ComposeView(requireContext()).apply {
            setContent {
                ForgotPasswordScreen(viewModel, findNavController())
            }
        }

    }
}
