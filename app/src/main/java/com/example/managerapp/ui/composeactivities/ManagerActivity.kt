package com.example.managerapp.ui.composeactivities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.managerapp.R
import com.example.managerapp.databinding.ActivityManagerBinding
import com.example.managerapp.ui.composescreens.AddItemScreen
import com.example.managerapp.ui.composescreens.HomeScreen
import com.example.managerapp.ui.composescreens.InventoryStatusScreen
import com.example.managerapp.ui.composescreens.TransactionHistoryScreen
import com.example.managerapp.ui.composescreens.TransactionScreen
import com.example.managerapp.ui.composescreens.UploadFileScreen
//import com.example.managerapp.db.UserDatabase
import com.example.managerapp.viewmodel.ManagerViewModel
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

//@AndroidEntryPoint
//class ManagerActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
//
//    private lateinit var binding: ActivityManagerBinding
//    private lateinit var navController: NavController
//
//    val viewModel: ManagerViewModel by viewModels()
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityManagerBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        navController = supportFragmentManager.findFragmentById(R.id.managerNavHostFragment)
//            ?.findNavController()!!
//
//        binding.drawerIcon.setOnClickListener {
//            openCloseNavDrawer()
//        }
//
//        binding.drawerNavView.setupWithNavController(navController)
//
//        binding.drawerNavView.setNavigationItemSelectedListener(this)
//
//        binding.addIcon.setOnClickListener {
//            navController.navigate(R.id.addItemFragment)
//        }
//
//        setUpNavDrawerHeader(viewModel.getCurrentUser()!!.email!!)
//
//        navController.addOnDestinationChangedListener { _, destination, _ ->
//            when (destination.id) {
//                R.id.homeFragment -> {
//                    binding.toolbarHeading.text = "All Items"
//                    binding.customIcon.setImageResource(R.drawable.ic_download)
//                    binding.addIcon.visibility = View.VISIBLE
//                    binding.customIcon.visibility = View.VISIBLE
//                    binding.drawerIcon.visibility = View.VISIBLE
//                }
//
//                R.id.inventoryStatusFragment -> {
//                    binding.toolbarHeading.text = "Inventory"
//                    binding.customIcon.setImageResource(R.drawable.ic_download)
//                    binding.addIcon.visibility = View.GONE
//                    binding.customIcon.visibility = View.VISIBLE
//                }
//
//                R.id.transactionFragment -> {
//                    binding.toolbarHeading.text = "Transaction"
//                    binding.addIcon.visibility = View.GONE
//                    binding.customIcon.setImageResource(R.drawable.ic_clear)
//                }
//
//                R.id.transactionHistoryFragment -> {
//                    binding.toolbarHeading.text = "History"
//                    binding.addIcon.visibility = View.GONE
//                    binding.customIcon.setImageResource(R.drawable.ic_clear)
//                }
//
//                R.id.uploadFileFragment -> {
//                    binding.toolbarHeading.text = "Upload"
//                    binding.addIcon.visibility = View.GONE
//                    binding.customIcon.setImageResource(R.drawable.ic_clear)
//                }
//
//                R.id.addItemFragment -> {
//                    binding.toolbarHeading.text = "Add Item"
//                    binding.drawerIcon.visibility = View.GONE
//                    binding.addIcon.visibility = View.GONE
//                    binding.customIcon.setImageResource(R.drawable.ic_clear)
//                }
//
//                R.id.logout -> {
//                    openCloseNavDrawer()
//                    Toast.makeText(this@ManagerActivity, "Logging Out", Toast.LENGTH_LONG).show()
//                }
//            }
//
//        }
//    }
//
//    private fun setUpNavDrawerHeader(email:String) {
//        val navHeader = binding.drawerNavView.getHeaderView(0)
//        val profileImage = navHeader.findViewById<ImageView>(R.id.profileImage)
//        val profileEmail = navHeader.findViewById<TextView>(R.id.profileEmail)
//        profileEmail.text = email.trim()
//        profileImage.setImageResource(R.drawable.ic_profile)
//    }
//
//    override fun onNavigationItemSelected(item: MenuItem): Boolean {
//        when(item.itemId){
//            R.id.homeFragment -> {
//                openCloseNavDrawer()
//                navController.navigate(R.id.homeFragment)
//            }
//
//            R.id.inventoryStatusFragment -> {
//                openCloseNavDrawer()
//                navController.navigate(R.id.inventoryStatusFragment)
//            }
//
//            R.id.transactionFragment -> {
//                openCloseNavDrawer()
//                navController.navigate(R.id.transactionFragment)
//            }
//
//            R.id.transactionHistoryFragment -> {
//                openCloseNavDrawer()
//                navController.navigate(R.id.transactionHistoryFragment)
//            }
//
//            R.id.uploadFileFragment -> {
//                openCloseNavDrawer()
//                navController.navigate(R.id.uploadFileFragment)
//            }
//
//            R.id.logout -> {
//                openCloseNavDrawer()
//                Toast.makeText(this@ManagerActivity, "Logging Out", Toast.LENGTH_LONG).show()
//                logOut()
//            }
//        }
//        return true
//    }
//
//    private fun openCloseNavDrawer() {
//        if (binding.drawerLayout.isDrawerOpen(binding.drawerNavView))
//            binding.drawerLayout.closeDrawer(binding.drawerNavView)
//        else
//            binding.drawerLayout.openDrawer(binding.drawerNavView)
//    }
//
//    private fun logOut(){
//        viewModel.logout()
//        startActivity(Intent(this@ManagerActivity, LoginActivity::class.java))
//        finish()
//    }
//}

@AndroidEntryPoint
class ManagerActivity : ComponentActivity() {

    private val viewModel: ManagerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ManagerApp(viewModel, onLogout = {
                viewModel.logout()
                startActivity(Intent(this@ManagerActivity, LoginActivity::class.java))
                finish()
            })
        }
    }
}

@Composable
fun ManagerApp(
    viewModel: ManagerViewModel,
    onLogout: () -> Unit
) {

    val navController = rememberNavController()
    val user by viewModel.currentUser.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(250.dp)
            ) {
                DrawerContent(
                    navController = navController,
                    userEmail = user!!.email!!,
                    closeDrawer = {
                        coroutineScope.launch { drawerState.close() }
                    },
                    onLogout = onLogout
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                ManagerTopBar(navController) {
                    coroutineScope.launch { drawerState.open() }
                }
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                ManagerNavHost(navController, viewModel)
            }
        }
    }
}

@Composable
fun DrawerContent(
    navController: NavHostController,
    userEmail: String,
    closeDrawer: () -> Unit,
    onLogout: () -> Unit
) {

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Image(
            painterResource(R.drawable.ic_profile),
            contentDescription = "Profile Image",
            modifier = Modifier
                .size(64.dp)
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = userEmail,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))

        val menuItems = listOf(
            "Home" to "home",
            "Inventory" to "inventory",
            "Transactions" to "transaction",
            "History" to "history",
            "Upload File" to "upload"
        )
        Column(modifier = Modifier.weight(1f)) {
            menuItems.forEach { (title, route) ->
                NavigationDrawerItem(
                    label = { Text(title) },
                    selected = currentRoute == route,
                    onClick = {
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                        closeDrawer()
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onLogout() }, modifier = Modifier.fillMaxWidth()) {
            Text("Logout")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagerTopBar(
    navController: NavHostController,
    onMenuClick: () -> Unit
) {

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination?.route

    TopAppBar(
        title = { Text(getTitleForScreen(destination = currentDestination)) },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(painterResource(id = R.drawable.ic_drawer), contentDescription = "Menu")
            }
        },
        actions = {
            GetActionsForScreen(currentDestination, navController)
        }
    )
}

@Composable
fun ManagerNavHost(navController: NavHostController, viewModel: ManagerViewModel) {
    NavHost(navController, startDestination = "home") {
        composable("home") { HomeScreen(viewModel = viewModel) }
        composable("inventory") { InventoryStatusScreen(viewModel) }
        composable("transaction") { TransactionScreen(viewModel) }
        composable("history") { TransactionHistoryScreen(viewModel) }
        composable("upload") { UploadFileScreen(viewModel) }
        composable("addItem") { AddItemScreen(viewModel) }
    }
}

@Composable
fun getTitleForScreen(destination: String?): String {
    return when (destination) {
        "home" -> "All Items"
        "inventory" -> "Inventory Status"
        "transaction" -> "Transactions"
        "history" -> "Transaction History"
        "upload" -> "Upload File"
        "addItem" -> "Add Item"
        else -> "Manager App"
    }
}

@Composable
fun GetActionsForScreen(destination: String?, navController: NavHostController) {
    when (destination) {
        "home" -> {
            IconButton(onClick = { navController.navigate("addItem") }) {
                Icon(painterResource(id = R.drawable.ic_add), contentDescription = "Add Item")
            }
            IconButton(onClick = { /* Download functionality */ }) {
                Icon(painterResource(id = R.drawable.ic_download), contentDescription = "Download")
            }
        }

//        "inventory" -> {
//            IconButton(onClick = { /* Refresh Inventory */ }) {
//                Icon(painterResource(id = R.drawable.ic_refresh), contentDescription = "Refresh")
//            }
//        }

        "transaction" -> {
            IconButton(onClick = { /* Clear transactions */ }) {
                Icon(painterResource(id = R.drawable.ic_clear), contentDescription = "Clear")
            }
        }

        "history" -> {
            IconButton(onClick = { /* Export history */ }) {
                Icon(painterResource(id = R.drawable.ic_download), contentDescription = "Export")
            }
        }

        "upload" -> {
            IconButton(onClick = { /* Upload file */ }) {
                Icon(painterResource(id = R.drawable.ic_clear), contentDescription = "Upload")
            }
        }

        "addItem" -> {
            IconButton(onClick = { }) {
                Icon(painterResource(id = R.drawable.ic_clear), contentDescription = "Close")
            }
        }
    }
}