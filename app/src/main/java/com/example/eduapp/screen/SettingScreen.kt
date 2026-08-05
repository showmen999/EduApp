package com.example.eduapp.screen

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.room.Room
import com.example.eduapp.database.AppDatabase
import com.example.eduapp.viewmodel.AppViewModel
import com.example.eduapp.viewmodel.AppViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(navController: NavHostController, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("eduapp_prefs", Context.MODE_PRIVATE) }

    // Settings States
    var questionsPerRound by remember { mutableIntStateOf(sharedPreferences.getInt("questions_count", 6)) }
    var soundEnabled by remember { mutableStateOf(sharedPreferences.getBoolean("sound_enabled", true)) }
    var vibrationEnabled by remember { mutableStateOf(sharedPreferences.getBoolean("vibration_enabled", true)) }
    
    // Database for Reset
    val db = remember { Room.databaseBuilder(context, AppDatabase::class.java, "app_db").build() }
    val factory = remember { AppViewModelFactory(db.appDao()) }
    val viewModel: AppViewModel = viewModel(factory = factory)

    var showResetDialog by remember { mutableStateOf(false) }

    // Colors
    val DeepIndigo = Color(0xFF2E3192)
    val Violet = Color(0xFF7E57C2)
    val SoftPlum = Color(0xFF8E24AA)
    val MintGreen = Color(0xFF64FFDA)
    val LavenderGrey = Color(0xFFD1D1E9)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DeepIndigo, Violet, SoftPlum)))
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Settings", color = Color.White, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Gameplay Section
                SettingsCard(title = "Gameplay", icon = Icons.Default.Settings) {
                    Text("Questions per round", color = Color.White, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        listOf(6, 10, 12).forEach { count ->
                            val isSelected = questionsPerRound == count
                            Button(
                                onClick = { 
                                    questionsPerRound = count
                                    sharedPreferences.edit().putInt("questions_count", count).apply()
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) MintGreen else Color(0x22FFFFFF),
                                    contentColor = if (isSelected) Color.Black else Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(count.toString(), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Text(
                        "Each correct answer is worth 10 points",
                        color = LavenderGrey,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // Feedback Section
                SettingsCard(title = "Feedback", icon = Icons.Default.Star) {
                    SettingsSwitchRow(
                        label = "Sound effects",
                        description = "Plays a short tone on answers",
                        checked = soundEnabled,
                        onCheckedChange = { 
                            soundEnabled = it
                            sharedPreferences.edit().putBoolean("sound_enabled", it).apply()
                        },
                        mintGreen = MintGreen,
                        lavenderGrey = LavenderGrey
                    )
                    Divider(color = Color(0x11FFFFFF), modifier = Modifier.padding(vertical = 12.dp))
                    SettingsSwitchRow(
                        label = "Vibration",
                        description = "Gives a small haptic buzz on submit",
                        checked = vibrationEnabled,
                        onCheckedChange = { 
                            vibrationEnabled = it
                            sharedPreferences.edit().putBoolean("vibration_enabled", it).apply()
                        },
                        mintGreen = MintGreen,
                        lavenderGrey = LavenderGrey
                    )
                }

                // Data Section
                SettingsCard(title = "Data", icon = Icons.Default.Warning) {
                    Button(
                        onClick = { showResetDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8A80)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Reset Leaderboard", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        "Permanently deletes all saved scores",
                        color = LavenderGrey,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // About Section
                SettingsCard(title = "About", icon = Icons.Default.Info) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("EduApp Quiz", color = Color.White, fontWeight = FontWeight.Bold)
                        Text("Version 1.0.0", color = LavenderGrey, style = MaterialTheme.typography.bodySmall)
                        Text("Student: [Your Name/ID]", color = LavenderGrey, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Leaderboard?") },
            text = { Text("This will permanently erase all high scores. This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearUsers()
                        showResetDialog = false
                        Toast.makeText(context, "Leaderboard cleared", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Reset", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SettingsCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x22FFFFFF)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x44FFFFFF))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = Color(0xFF64FFDA), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
fun SettingsSwitchRow(
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    mintGreen: Color,
    lavenderGrey: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, color = Color.White, fontWeight = FontWeight.Medium)
            Text(description, color = lavenderGrey, style = MaterialTheme.typography.bodySmall)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = mintGreen,
                uncheckedThumbColor = Color.LightGray,
                uncheckedTrackColor = Color(0x44FFFFFF)
            )
        )
    }
}
