package com.example.eduapp.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

// Color Palette
val DeepIndigo = Color(0xFF2E3192)
val Violet = Color(0xFF7E57C2)
val SoftPlum = Color(0xFF8E24AA)
val MintGreen = Color(0xFF64FFDA)
val LavenderGrey = Color(0xFFD1D1E9)
val TranslucentWhite = Color(0x22FFFFFF)
val FrostedWhite = Color(0x33FFFFFF)

@Composable
fun LandingScreen(navController: NavHostController, modifier: Modifier = Modifier) {
    var username by remember { mutableStateOf("") }
    var selectedLevel by remember { mutableIntStateOf(1) }
    var showError by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(DeepIndigo, Violet, SoftPlum)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Settings Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                contentAlignment = Alignment.TopEnd
            ) {
                IconButton(
                    onClick = { navController.navigate("setting") },
                    modifier = Modifier
                        .size(48.dp)
                        .background(TranslucentWhite, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // App Title & Tagline
            Text(
                text = "EduApp Quiz",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp
            )
            Text(
                text = "Master your knowledge in minutes",
                style = MaterialTheme.typography.bodyMedium,
                color = LavenderGrey,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Frosted Glass Card for Name Input
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(10.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = FrostedWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x44FFFFFF))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Your Name",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = username,
                        onValueChange = {
                            username = it
                            if (it.isNotBlank()) showError = false
                        },
                        placeholder = { Text("Type your name to begin", color = LavenderGrey) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = LavenderGrey) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            unfocusedBorderColor = Color(0x66FFFFFF),
                            focusedBorderColor = Color.White,
                            cursorColor = Color.White
                        ),
                        isError = showError
                    )
                    if (showError) {
                        Text(
                            text = "Please enter your name",
                            color = Color(0xFFFF8A80),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Level Selection
            Text(
                text = "Choose Your Level",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            LevelCard(
                title = "Explorer",
                description = "Gentle start, build confidence",
                icon = Icons.Default.Face,
                iconColor = Color(0xFFFFB300),
                isSelected = selectedLevel == 1,
                onClick = { selectedLevel = 1 }
            )

            Spacer(modifier = Modifier.height(12.dp))

            LevelCard(
                title = "Challenger",
                description = "Step it up a notch",
                icon = Icons.Default.ThumbUp,
                iconColor = Color(0xFF039BE5),
                isSelected = selectedLevel == 2,
                onClick = { selectedLevel = 2 }
            )

            Spacer(modifier = Modifier.height(12.dp))

            LevelCard(
                title = "Champion",
                description = "Only for the fearless",
                icon = Icons.Default.Star,
                iconColor = Color(0xFFD81B60),
                isSelected = selectedLevel == 3,
                onClick = { selectedLevel = 3 }
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Start Quiz Button
            Button(
                onClick = {
                    if (username.isBlank()) {
                        showError = true
                    } else {
                        navController.navigate("game/$username/$selectedLevel")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .shadow(8.dp, RoundedCornerShape(30.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = MintGreen),
                shape = RoundedCornerShape(30.dp)
            ) {
                Text(
                    text = "Start Quiz",
                    color = Color(0xFF121212),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun LevelCard(
    title: String,
    description: String,
    icon: ImageVector,
    iconColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Color(0x44FFFFFF) else Color(0x11FFFFFF)
    val borderColor = if (isSelected) Color.White else Color.Transparent

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(84.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .border(2.dp, borderColor, RoundedCornerShape(20.dp)),
        color = backgroundColor,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Badge
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(iconColor.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Title & Description
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = description,
                    color = LavenderGrey,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2
                )
            }

            // Chevron
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = LavenderGrey,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
