@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.eduapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.eduapp.screen.GameScreen
import com.example.eduapp.screen.LandingScreen
import com.example.eduapp.screen.ScoreScreen
import com.example.eduapp.screen.SettingScreen
import com.example.eduapp.screen.TestDBScreen
import com.example.eduapp.ui.theme.EduAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val currentContext = applicationContext
        setContent {
            EduAppTheme {
                AppNav(currentContext)
            }
        }
    }
}
@Composable
fun AppNav(currentContext: Context){
    //obtain navController
    val navController = rememberNavController()
    //set navHost and the routes
    NavHost(navController = navController, startDestination = "landing") {
        composable("landing") { LandingScreen(navController) }
        composable("setting") { SettingScreen(navController) }
        composable(
            route = "game/{playerName}/{level}",
            arguments = listOf(
                navArgument("playerName") { type = NavType.StringType },
                navArgument("level") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val playerName = backStackEntry.arguments?.getString("playerName") ?: ""
            val level = backStackEntry.arguments?.getInt("level") ?: 1
            GameScreen(currentContext, navController, playerName, level)
        }
        composable(
            route = "score/{score}/{playerName}/{level}",
            arguments = listOf(
                navArgument("score") { type = NavType.IntType },
                navArgument("playerName") { type = NavType.StringType },
                navArgument("level") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val score = backStackEntry.arguments?.getInt("score") ?: 0
            val playerName = backStackEntry.arguments?.getString("playerName") ?: ""
            val level = backStackEntry.arguments?.getInt("level") ?: 1
            ScoreScreen(navController) // You might want to update ScoreScreen to take these
        }
        composable("testDB") { TestDBScreen(currentContext) }
    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    EduAppTheme {

    }
}