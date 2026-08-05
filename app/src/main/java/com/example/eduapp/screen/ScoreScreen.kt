package com.example.eduapp.screen

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.room.Room
import com.example.eduapp.database.AppDatabase
import com.example.eduapp.database.User
import com.example.eduapp.viewmodel.AppViewModel
import com.example.eduapp.viewmodel.AppViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoreScreen(
    navController: NavHostController,
    score: Int,
    playerName: String,
    level: Int,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("eduapp_prefs", Context.MODE_PRIVATE) }
    val questionsCount = remember { sharedPreferences.getInt("questions_count", 6) }
    val maxPossibleScore = questionsCount * 10
    
    // DB & ViewModel Setup
    val db = remember {
        Room.databaseBuilder(context, AppDatabase::class.java, "app_db").build()
    }
    val factory = remember { AppViewModelFactory(db.appDao()) }
    val viewModel: AppViewModel = viewModel(factory = factory)
    
    // Save score exactly once
    LaunchedEffect(Unit) {
        val levelName = when(level) {
            1 -> "Explorer"
            2 -> "Challenger"
            3 -> "Champion"
            else -> "Lvl $level"
        }
        viewModel.addUser(playerName, levelName, score)
    }

    val topScores by viewModel.topScores.collectAsStateWithLifecycle(initialValue = emptyList())
    val lastInsertedId by viewModel.lastInsertedId.collectAsStateWithLifecycle()

    // Colors
    val DeepIndigo = Color(0xFF2E3192)
    val Violet = Color(0xFF7E57C2)
    val SoftPlum = Color(0xFF8E24AA)
    val MintGreen = Color(0xFF64FFDA)
    val LavenderGrey = Color(0xFFD1D1E9)

    val percentage = (score.toFloat() / maxPossibleScore.toFloat()) * 100
    val (encouragingLine, performanceTitle) = when {
        percentage >= 80 -> "Outstanding! You're a quiz champion!" to "Incredible!"
        percentage >= 50 -> "Great job! You really know your stuff." to "Well Done!"
        else -> "Good effort! Keep playing to improve your score." to "Nice Try!"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(DeepIndigo, Violet, SoftPlum)
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { 
                        Text(
                            text = "Quiz Summary", 
                            color = Color.White, 
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        ) 
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Score Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(12.dp, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0x22FFFFFF)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x44FFFFFF))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$performanceTitle $playerName!",
                            color = LavenderGrey,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "$score / $maxPossibleScore",
                            color = Color.White,
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            fontSize = 64.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = encouragingLine,
                            color = LavenderGrey,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Leaderboard Heading
                Text(
                    text = "Leaderboard",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Leaderboard List
                Box(modifier = Modifier.weight(1f)) {
                    if (topScores.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "No scores yet. Be the first to top the charts!",
                                color = LavenderGrey,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(bottom = 24.dp)
                        ) {
                            itemsIndexed(topScores) { index, user ->
                                LeaderboardRow(
                                    rank = index + 1,
                                    user = user,
                                    isCurrent = user.id == lastInsertedId.toInt(), // Highlighting the record we just saved
                                    mintGreen = MintGreen,
                                    lavenderGrey = LavenderGrey
                                )
                            }
                        }
                    }
                }

                // Play Again Button (fixed at bottom of Column)
                Button(
                    onClick = { 
                        navController.navigate("landing") {
                            popUpTo("landing") { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MintGreen)
                ) {
                    Text(
                        text = "Play Again", 
                        color = Color(0xFF121212), 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun LeaderboardRow(
    rank: Int,
    user: User,
    isCurrent: Boolean,
    mintGreen: Color,
    lavenderGrey: Color
) {
    val backgroundColor = if (isCurrent) Color(0x44FFFFFF) else Color(0x11FFFFFF)
    val borderColor = if (isCurrent) Color.White else Color.Transparent

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.5.dp, borderColor, RoundedCornerShape(20.dp)),
        color = backgroundColor,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "#$rank",
                color = if (isCurrent) Color.White else lavenderGrey,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.width(44.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.username, 
                    color = Color.White, 
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = user.level, 
                    color = lavenderGrey, 
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                text = "${user.score} pts",
                color = mintGreen,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp
            )
        }
    }
}
