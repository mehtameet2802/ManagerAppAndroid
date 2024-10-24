package com.example.managerapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.managerapp.R
import com.example.managerapp.databinding.ActivityManagerBinding
//import com.example.managerapp.db.UserDatabase
import com.example.managerapp.repository.AuthRepository
import com.example.managerapp.repository.ManagerRepository
import com.example.managerapp.viewmodel.AuthViewModelFactory
import com.example.managerapp.viewmodel.ManagerViewModel
import com.example.managerapp.viewmodel.ManagerViewModelFactory
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ManagerActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityManagerBinding
    private lateinit var navController: NavController

    val viewModel: ManagerViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = supportFragmentManager.findFragmentById(R.id.managerNavHostFragment)
            ?.findNavController()!!

        binding.drawerIcon.setOnClickListener {
            openCloseNavDrawer()
        }

        binding.drawerNavView.setupWithNavController(navController)

        binding.drawerNavView.setNavigationItemSelectedListener(this)

        binding.addIcon.setOnClickListener {
            navController.navigate(R.id.addItemFragment)
        }

        setUpNavDrawerHeader(viewModel.getCurrentUser()!!.email!!)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> {
                    binding.toolbarHeading.text = "All Items"
                    binding.customIcon.setImageResource(R.drawable.ic_download)
                    binding.addIcon.visibility = View.VISIBLE
                    binding.customIcon.visibility = View.VISIBLE
                    binding.drawerIcon.visibility = View.VISIBLE
                }

                R.id.inventoryStatusFragment -> {
                    binding.toolbarHeading.text = "Inventory"
                    binding.customIcon.setImageResource(R.drawable.ic_download)
                    binding.addIcon.visibility = View.GONE
                    binding.customIcon.visibility = View.VISIBLE
                }

                R.id.transactionFragment -> {
                    binding.toolbarHeading.text = "Transaction"
                    binding.addIcon.visibility = View.GONE
                    binding.customIcon.setImageResource(R.drawable.ic_clear)
                }

                R.id.transactionHistoryFragment -> {
                    binding.toolbarHeading.text = "History"
                    binding.addIcon.visibility = View.GONE
                    binding.customIcon.setImageResource(R.drawable.ic_clear)
                }

                R.id.uploadFileFragment -> {
                    binding.toolbarHeading.text = "Upload"
                    binding.addIcon.visibility = View.GONE
                    binding.customIcon.setImageResource(R.drawable.ic_clear)
                }

                R.id.addItemFragment -> {
                    binding.toolbarHeading.text = "Add Item"
                    binding.drawerIcon.visibility = View.GONE
                    binding.addIcon.visibility = View.GONE
                    binding.customIcon.setImageResource(R.drawable.ic_clear)
                }

                R.id.logout -> {
                    openCloseNavDrawer()
                    Toast.makeText(this@ManagerActivity, "Logging Out", Toast.LENGTH_LONG).show()
                }
            }

        }
    }

    private fun setUpNavDrawerHeader(email:String) {
        val navHeader = binding.drawerNavView.getHeaderView(0)
        val profileImage = navHeader.findViewById<ImageView>(R.id.profileImage)
        val profileEmail = navHeader.findViewById<TextView>(R.id.profileEmail)
        profileEmail.text = email.trim()
        profileImage.setImageResource(R.drawable.ic_profile)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.homeFragment -> {
                openCloseNavDrawer()
                navController.navigate(R.id.homeFragment)
            }

            R.id.inventoryStatusFragment -> {
                openCloseNavDrawer()
                navController.navigate(R.id.inventoryStatusFragment)
            }

            R.id.transactionFragment -> {
                openCloseNavDrawer()
                navController.navigate(R.id.transactionFragment)
            }

            R.id.transactionHistoryFragment -> {
                openCloseNavDrawer()
                navController.navigate(R.id.transactionHistoryFragment)
            }

            R.id.uploadFileFragment -> {
                openCloseNavDrawer()
                navController.navigate(R.id.uploadFileFragment)
            }

            R.id.logout -> {
                openCloseNavDrawer()
                Toast.makeText(this@ManagerActivity, "Logging Out", Toast.LENGTH_LONG).show()
                logOut()
            }
        }
        return true
    }

    private fun openCloseNavDrawer() {
        if (binding.drawerLayout.isDrawerOpen(binding.drawerNavView))
            binding.drawerLayout.closeDrawer(binding.drawerNavView)
        else
            binding.drawerLayout.openDrawer(binding.drawerNavView)
    }

    private fun logOut(){
        viewModel.logout()
        startActivity(Intent(this@ManagerActivity,LoginActivity::class.java))
        finish()
    }
}