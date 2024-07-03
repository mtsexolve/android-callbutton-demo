package com.exolve.callbutton

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.exolve.callbutton.ui.theme.CallButtonTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.platform.LocalContext
import android.Manifest
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType


private const val MAIN_ACTIVITY = "MainActivity"
class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        var granted: Boolean = true
        for (permission in permissions) {
            granted = granted && permission.value
        }

        if (granted) {
            Log.i(MAIN_ACTIVITY, "permission granted")
        } else {
            Log.i(MAIN_ACTIVITY, "permission denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissionLauncher.launch(arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        ))

        setContent {
            CallButtonTheme {
                HomeScreen()
            }
        }
    }
}

data class Screen(val route: String, val icon: ImageVector)

@Composable
fun CallScreen(
    callButtonViewModel: CallButtonViewModel
) {
    val callButtonState by callButtonViewModel.uiState.collectAsState()
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = callButtonViewModel::toggleCall,
            modifier = Modifier.fillMaxWidth(0.8f),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
        )
        {
            Image(
                painter = painterResource(id = if(callButtonState.isCalling) { R.drawable.end_call_icon} else {R.drawable.phone_call_2}),
                modifier = Modifier.fillMaxSize(),
                contentDescription = null
            )
        }
    }
}

@Composable
fun AccountScreen(
    callButtonViewModel: CallButtonViewModel
) {
    val callButtonState by callButtonViewModel.uiState.collectAsState()
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text(text = "SIP account",
            fontSize = 20.sp,
            modifier = Modifier.fillMaxWidth()
                .padding(vertical = 8.dp)
        )
        TextField(
            value = callButtonState.account,
            onValueChange = { callButtonViewModel.updateAccount(account = it) },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next
            )
        )
        Text(text = "SIP password",
            fontSize = 20.sp,
            modifier = Modifier.fillMaxWidth()
                .padding(vertical = 8.dp)
        )
        TextField(
            value = callButtonState.password,
            onValueChange = { callButtonViewModel.updatePassword(password = it) },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            )
        )
        Text(text = "Number to dial",
            fontSize = 20.sp,
            modifier = Modifier.fillMaxWidth()
                .padding(vertical = 8.dp)
        )
        TextField(
            value = callButtonState.number,
            onValueChange = { callButtonViewModel.updateNumber(number = it) },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Done
            )
        )
        Spacer(Modifier.weight(1f).fillMaxHeight())
        Text(text = "${LocalContext.current.packageName} ${callButtonState.versionInfo}",
            fontSize = 16.sp,
            modifier = Modifier.fillMaxWidth()
                .padding(vertical = 8.dp),
            color = Color.Gray.copy(alpha = 0.8f)
        )
    }
}


@Composable
fun HomeScreen(
) {

    val topLevelDestinations = listOf(
        Screen("Call",Icons.Filled.Call),
        Screen("Account",Icons.Filled.AccountBox)
    )

    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
                HomeBottomBar(destinations = topLevelDestinations,
                    currentDestination = navController.currentBackStackEntryAsState().value?.destination,
                    onNavigateToDestination = {
                            navController.navigate(it) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            restoreState = true
                            launchSingleTop = true
                        }
                    })
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            HomeNavHost(navController=navController)
        }
    }
}

@Composable
fun HomeNavHost(
    navController : NavHostController
) {
    val callButtonViewModel: CallButtonViewModel = viewModel()
    NavHost(
        navController = navController,
        startDestination = "Call",
        modifier = Modifier.fillMaxSize()
    ) {
        composable(route = "Call") {
            CallScreen(callButtonViewModel)
        }
        composable(route = "Account") {
            AccountScreen(callButtonViewModel)
        }
    }
}

@Composable
private fun HomeBottomBar(
    destinations: List<Screen>,
    currentDestination: NavDestination?,
    onNavigateToDestination: (route: String) -> Unit
) {

    NavigationBar(
    ) {
        destinations.forEach { destination ->
            val selected =
                currentDestination?.hierarchy?.any { it.route == destination.route } == true
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigateToDestination(destination.route) },
                icon = { Icon(destination.icon, contentDescription = null) },
                label = {
                    Text(
                        text = destination.route
                    )
                })
        }
    }
}