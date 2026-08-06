package com.example.eduapp.screen

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.eduapp.EduAppApplication
import com.example.eduapp.helper.rememberAssetImage
import com.example.eduapp.viewmodel.AppViewModel
import com.example.eduapp.viewmodel.AppViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    currentContext: Context,
    navController: NavHostController,
    playerName: String,
    level: Int
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sharedPreferences = remember { context.getSharedPreferences("eduapp_prefs", Context.MODE_PRIVATE) }
    
    // ViewModel Setup
    val app = context.applicationContext as EduAppApplication
    val factory = remember { AppViewModelFactory(app.database.appDao()) }
    val viewModel: AppViewModel = viewModel(factory = factory)

    // Load Settings
    val questionsCount = remember { sharedPreferences.getInt("questions_count", 6) }
    val soundEnabled = remember { sharedPreferences.getBoolean("sound_enabled", true) }
    val vibrationEnabled = remember { sharedPreferences.getBoolean("vibration_enabled", true) }
    val maxScore = questionsCount * 10

    // Quiz State from ViewModel
    val quizImages by viewModel.quizImages.collectAsStateWithLifecycle()
    val currentIndex by viewModel.currentIndex.collectAsStateWithLifecycle()
    val score by viewModel.score.collectAsStateWithLifecycle()
    val isSubmitted by viewModel.isSubmitted.collectAsStateWithLifecycle()
    val isCorrect by viewModel.isCorrect.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.startQuiz(context, level, questionsCount)
    }

    // Local UI State
    var userAnswer by rememberSaveable { mutableStateOf("") }
    var showInputError by rememberSaveable { mutableStateOf(false) }

    // Manage ToneGenerator lifecycle
    val toneGenerator = remember {
        try {
            ToneGenerator(AudioManager.STREAM_MUSIC, 70)
        } catch (e: Exception) {
            null
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            toneGenerator?.release()
        }
    }

    // Colors
    val DeepIndigo = Color(0xFF2E3192)
    val Violet = Color(0xFF7E57C2)
    val SoftPlum = Color(0xFF8E24AA)
    val MintGreen = Color(0xFF64FFDA)
    val LavenderGrey = Color(0xFFD1D1E9)

    val levelName = when (level) {
        1 -> "Explorer"
        2 -> "Challenger"
        3 -> "Champion"
        else -> "Quiz"
    }

    val currentImageName = if (quizImages.isNotEmpty()) quizImages[currentIndex] else ""
    val correctAnswer = if (currentImageName.isNotEmpty()) {
        currentImageName.substringBeforeLast(".").substringAfterLast("_")
    } else ""

    val encouragingMessages = listOf(
        "You're getting closer. Keep going!",
        "Don't give up, you've got this!",
        "Every mistake is a lesson. Try the next one!",
        "Keep your head up! You're doing great."
    )
    val randomEncouragement = remember(currentIndex) { encouragingMessages.random() }

    // Feedback Helpers
    val triggerFeedback = { correct: Boolean ->
        val soundOn = sharedPreferences.getBoolean("sound_enabled", true)
        val vibrationOn = sharedPreferences.getBoolean("vibration_enabled", true)

        scope.launch(Dispatchers.Default) {
            if (soundOn && toneGenerator != null) {
                if (correct) {
                    toneGenerator.startTone(ToneGenerator.TONE_DTMF_5, 200)
                    delay(250)
                    toneGenerator.startTone(ToneGenerator.TONE_DTMF_9, 200)
                } else {
                    toneGenerator.startTone(ToneGenerator.TONE_DTMF_3, 200)
                    delay(250)
                    toneGenerator.startTone(ToneGenerator.TONE_DTMF_1, 200)
                }
            }
        }

        if (vibrationOn) {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
            
            if (correct) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(100)
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 150, 100, 150), -1))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(longArrayOf(0, 150, 100, 150), -1)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DeepIndigo, Violet, SoftPlum)))
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("$levelName Quiz", color = Color.White, fontWeight = FontWeight.Bold) },
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
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Info Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(playerName, color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Question ${currentIndex + 1} / $questionsCount", color = LavenderGrey, style = MaterialTheme.typography.bodyMedium)
                    }
                    Surface(color = Color(0x33FFFFFF), shape = RoundedCornerShape(12.dp)) {
                        Text("$score / $maxScore", color = Color.White, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = { (currentIndex + 1) / questionsCount.toFloat() },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = MintGreen,
                    trackColor = Color(0x22FFFFFF),
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Image Card
                Card(
                    modifier = Modifier.fillMaxWidth().height(250.dp).shadow(12.dp, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        if (currentImageName.isNotEmpty()) {
                            val bitmap = rememberAssetImage("$level/$currentImageName")
                            if (bitmap != null) {
                                Image(bitmap = bitmap, contentDescription = "Puzzle", modifier = Modifier.fillMaxSize().padding(16.dp), contentScale = ContentScale.Fit)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Answer Input
                OutlinedTextField(
                    value = userAnswer,
                    onValueChange = { if (!isSubmitted) { userAnswer = it; showInputError = false } },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Your answer", color = LavenderGrey) },
                    shape = RoundedCornerShape(20.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    enabled = !isSubmitted,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                        unfocusedBorderColor = Color(0x66FFFFFF), focusedBorderColor = MintGreen,
                        disabledTextColor = Color.White, disabledBorderColor = Color(0x33FFFFFF)
                    ),
                    isError = showInputError
                )

                if (showInputError) {
                    Text("Please enter an answer", color = Color(0xFFFF8A80), style = MaterialTheme.typography.bodySmall, modifier = Modifier.align(Alignment.Start).padding(top = 4.dp, start = 8.dp))
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Submit Button
                if (!isSubmitted) {
                    Button(
                        onClick = {
                            if (userAnswer.isBlank()) {
                                showInputError = true
                            } else {
                                viewModel.submitAnswer(userAnswer, correctAnswer)
                                triggerFeedback(userAnswer.trim().equals(correctAnswer.trim(), ignoreCase = true))
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MintGreen)
                    ) {
                        Text("Submit", color = Color(0xFF121212), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }

                // Feedback Area
                if (isSubmitted) {
                    val bannerColor = if (isCorrect) Color(0xFFC8E6C9) else Color(0xFFFFCDD2)
                    val contentColor = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828)

                    Surface(modifier = Modifier.fillMaxWidth(), color = bannerColor, shape = RoundedCornerShape(20.dp)) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            if (isCorrect) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = contentColor)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Brilliant! That's right", color = contentColor, fontWeight = FontWeight.Bold)
                                    Text("+10 points", color = contentColor, style = MaterialTheme.typography.bodySmall)
                                }
                            } else {
                                Column {
                                    Text("Not quite — the answer was $correctAnswer", color = contentColor, fontWeight = FontWeight.Bold)
                                    Text(randomEncouragement, color = contentColor, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (currentIndex < quizImages.size - 1) {
                                viewModel.nextQuestion()
                                userAnswer = ""
                                showInputError = false
                            } else {
                                navController.navigate("score/$score/$playerName/$level")
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MintGreen)
                    ) {
                        Text(
                            text = if (currentIndex < quizImages.size - 1) "Next Question" else "See Results",
                            color = Color(0xFF121212),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}
