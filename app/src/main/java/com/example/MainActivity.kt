package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.collectAsState
import com.example.ui.screens.CbcPortalScreen
import com.example.ui.screens.CgpaCalculatorScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.ResultDetailScreen
import com.example.ui.screens.SavedResultsScreen
import com.example.ui.screens.SchoolMenuScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.VerificationScreen
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.Navy900
import com.example.ui.theme.ResultHubTheme
import com.example.ui.viewmodel.ResultViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val resultViewModel: ResultViewModel = viewModel()
      val appSettings by resultViewModel.appSettings.collectAsState()

      ResultHubTheme(
        themeMode = appSettings.themeMode,
        colorBlindMode = appSettings.colorBlindMode
      ) {
        MainAppContent(resultViewModel = resultViewModel)
      }
    }
  }
}

sealed class BottomNavScreen(val route: String, val title: String, val icon: ImageVector) {
  object Home : BottomNavScreen("home", "Search", Icons.Default.Search)
  object Cbc : BottomNavScreen("cbc_portal", "Kenya CBC", Icons.Default.School)
  object Menu : BottomNavScreen("school_menu", "School Info", Icons.Default.Info)
  object Saved : BottomNavScreen("saved", "Saved", Icons.Default.Download)
  object Verify : BottomNavScreen("verify", "Verify", Icons.Default.VerifiedUser)
  object Settings : BottomNavScreen("settings", "Settings", Icons.Default.Settings)
}

@Composable
fun MainAppContent(resultViewModel: ResultViewModel = viewModel()) {
  val navController = rememberNavController()

  val navItems = listOf(
    BottomNavScreen.Home,
    BottomNavScreen.Cbc,
    BottomNavScreen.Menu,
    BottomNavScreen.Saved,
    BottomNavScreen.Verify,
    BottomNavScreen.Settings
  )

  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = navBackStackEntry?.destination?.route

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    bottomBar = {
      if (currentRoute != "result_detail" && currentRoute != "login_screen") {
        NavigationBar(
          containerColor = Navy900,
          tonalElevation = 8.dp
        ) {
          navItems.forEach { screen ->
            val isSelected = currentRoute == screen.route
            NavigationBarItem(
              icon = {
                Icon(
                  imageVector = screen.icon,
                  contentDescription = screen.title
                )
              },
              label = {
                Text(
                  text = screen.title,
                  fontSize = 10.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
              },
              selected = isSelected,
              onClick = {
                navController.navigate(screen.route) {
                  popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                  }
                  launchSingleTop = true
                  restoreState = true
                }
              },
              colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Navy900,
                selectedTextColor = BluePrimary,
                indicatorColor = BluePrimary,
                unselectedIconColor = androidx.compose.ui.graphics.Color.LightGray,
                unselectedTextColor = androidx.compose.ui.graphics.Color.LightGray
              ),
              modifier = Modifier.testTag("nav_${screen.route}")
            )
          }
        }
      }
    }
  ) { innerPadding ->
    NavHost(
      navController = navController,
      startDestination = BottomNavScreen.Home.route,
      modifier = Modifier.padding(innerPadding)
    ) {
      composable(BottomNavScreen.Home.route) {
        HomeScreen(
          viewModel = resultViewModel,
          onNavigateToDetail = { result ->
            resultViewModel.setActiveResult(result)
            navController.navigate("result_detail")
          },
          onNavigateToSaved = {
            navController.navigate(BottomNavScreen.Saved.route)
          }
        )
      }

      composable(BottomNavScreen.Cbc.route) {
        CbcPortalScreen(
          viewModel = resultViewModel,
          onNavigateToLogin = {
            navController.navigate("login_screen")
          }
        )
      }

      composable(BottomNavScreen.Menu.route) {
        SchoolMenuScreen(
          viewModel = resultViewModel
        )
      }

      composable("login_screen") {
        LoginScreen(
          viewModel = resultViewModel,
          onLoginSuccess = {
            navController.navigate(BottomNavScreen.Cbc.route) {
              popUpTo(BottomNavScreen.Home.route) { saveState = true }
              launchSingleTop = true
            }
          }
        )
      }

      composable("result_detail") {
        ResultDetailScreen(
          viewModel = resultViewModel,
          onBack = { navController.popBackStack() }
        )
      }

      composable(BottomNavScreen.Saved.route) {
        SavedResultsScreen(
          viewModel = resultViewModel,
          onNavigateToDetail = { result ->
            resultViewModel.setActiveResult(result)
            navController.navigate("result_detail")
          }
        )
      }

      composable(BottomNavScreen.Verify.route) {
        VerificationScreen(viewModel = resultViewModel)
      }

      composable(BottomNavScreen.Settings.route) {
        SettingsScreen(viewModel = resultViewModel)
      }
    }
  }
}

