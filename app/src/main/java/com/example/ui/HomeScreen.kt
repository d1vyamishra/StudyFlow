package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.data.StudyRecord
import java.text.SimpleDateFormat
import java.util.Locale
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    val activeTimer by viewModel.activeTimer.collectAsStateWithLifecycle()
    val lectureSeconds by viewModel.lectureSeconds.collectAsStateWithLifecycle()
    val selfStudySeconds by viewModel.selfStudySeconds.collectAsStateWithLifecycle()
    val questionSeconds by viewModel.questionPracticeSeconds.collectAsStateWithLifecycle()
    val records by viewModel.allRecords.collectAsStateWithLifecycle()
    val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()

    val totalSeconds = lectureSeconds + selfStudySeconds + questionSeconds

    var isAppLoading by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        delay(1200)
        isAppLoading = false
    }

    var showResetDialog by remember { mutableStateOf(false) }
    var dailyGoalMinutes by remember { mutableStateOf(360f) } // Default 6 hours target (360 minutes)

    val todayStr = viewModel.getTodayDateString()
    val displayDate = remember(todayStr) {
        try {
            val parsed = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(todayStr)
            SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(parsed!!)
        } catch (e: Exception) {
            todayStr
        }
    }

    if (isAppLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(32.dp)
            ) {
                val infiniteTransition = rememberInfiniteTransition(label = "pulse_logo_splash")
                val logoScale by infiniteTransition.animateFloat(
                    initialValue = 0.88f,
                    targetValue = 1.12f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1100, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "scale"
                )

                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .scale(logoScale)
                        .background(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                            CircleShape
                        )
                        .border(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.8f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.app_logo),
                        contentDescription = "Pulsing App Logo",
                        modifier = Modifier.size(76.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 3.dp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Loading Your Study Flow...",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Secure Local Storage Active",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    } else {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            modifier = modifier
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = displayDate.uppercase(Locale.getDefault()),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    letterSpacing = 1.2.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.app_logo),
                                    contentDescription = "Study Flow N Logo",
                                    modifier = Modifier.size(32.dp)
                                )
                                Text(
                                    text = "Study Flow",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { viewModel.toggleTheme() },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(MaterialTheme.colorScheme.surface, CircleShape)
                                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                                    .testTag("theme_toggle_button")
                            ) {
                                Icon(
                                    imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                    contentDescription = "Switch Theme Mode",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            IconButton(
                                onClick = { showResetDialog = true },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(MaterialTheme.colorScheme.surface, CircleShape)
                                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                                    .testTag("reset_today_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Reset Today's Timers",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.app_logo),
                                    contentDescription = "User N Avatar logo",
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                )
                            }
                        }
                    }
                }

                // 2. Bento highlighted TotalAggregatorCard
                item {
                    TotalAggregatorCard(
                    totalSeconds = totalSeconds,
                    goalMinutes = dailyGoalMinutes
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        HalfWidthBentoCard(
                            title = "Video Lectures",
                            seconds = lectureSeconds,
                            icon = Icons.Default.PlayArrow,
                            emoji = "📺",
                            color = MaterialTheme.colorScheme.primary,
                            isActive = activeTimer == TimerType.LECTURE,
                            onToggleActive = {
                                if (activeTimer == TimerType.LECTURE) {
                                    viewModel.pauseTimer()
                                } else {
                                    viewModel.startTimer(TimerType.LECTURE)
                                }
                            },
                            onAdjustTime = { deltaSeconds ->
                                viewModel.adjustTimer(TimerType.LECTURE, deltaSeconds)
                            },
                            tagPrefix = "lecture"
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        HalfWidthBentoCard(
                            title = "Self-Study",
                            seconds = selfStudySeconds,
                            icon = Icons.Default.MenuBook,
                            emoji = "📖",
                            color = MaterialTheme.colorScheme.secondary,
                            isActive = activeTimer == TimerType.SELF_STUDY,
                            onToggleActive = {
                                if (activeTimer == TimerType.SELF_STUDY) {
                                    viewModel.pauseTimer()
                                } else {
                                    viewModel.startTimer(TimerType.SELF_STUDY)
                                }
                            },
                            onAdjustTime = { deltaSeconds ->
                                viewModel.adjustTimer(TimerType.SELF_STUDY, deltaSeconds)
                            },
                            tagPrefix = "self_study"
                        )
                    }
                }
            }

            item {
                QuestionPracticeBentoCard(
                    seconds = questionSeconds,
                    isActive = activeTimer == TimerType.QUESTION,
                    onToggleActive = {
                        if (activeTimer == TimerType.QUESTION) {
                            viewModel.pauseTimer()
                        } else {
                            viewModel.startTimer(TimerType.QUESTION)
                        }
                    },
                    onAdjustTime = { deltaSeconds ->
                        viewModel.adjustTimer(TimerType.QUESTION, deltaSeconds)
                    }
                )
            }

            item {
                WeeklyAnalyticsBentoCard(
                    records = records
                )
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Today's Session?", fontWeight = FontWeight.SemiBold) },
            text = { Text("This will clear out the timing values for your Lectures, Self-Study, and Practice for today. This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetTodayTimers()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("confirm_reset_today")
                ) {
                    Text("Clear Daily Counters")
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
}

@Composable
fun TotalAggregatorCard(
    totalSeconds: Long,
    goalMinutes: Float,
    modifier: Modifier = Modifier
) {
    val hrs = totalSeconds / 3600
    val mins = (totalSeconds % 3600) / 60
    val secs = totalSeconds % 60

    val totalMinutes = totalSeconds / 60f
    val progress = (totalMinutes / goalMinutes).coerceIn(0f, 1f)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("total_aggregator_card"),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        )
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 16.dp, y = 16.dp)
                    .size(96.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        shape = CircleShape
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total Study Time",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                    )

                    // Database Status Heartbeat Pulse
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val infinitePulse = rememberInfiniteTransition(label = "db_heartbeat")
                        val heartbeatAlpha by infinitePulse.animateFloat(
                            initialValue = 0.4f,
                            targetValue = 1.0f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1000, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "hbAlpha"
                        )
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    color = Color(0xFF2ECC71).copy(alpha = heartbeatAlpha),
                                    shape = CircleShape
                                )
                        )
                        Text(
                            text = "Auto-Logging Live",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                                letterSpacing = 0.3.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Time ticker formatted beautifully: e.g. "06h 45m"
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = String.format("%02dh %02dm", hrs, mins),
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 44.sp
                        ),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = String.format("%02ds", secs),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Modern status badge with progress percent +12% from yesterday or custom completeness
                val percent = (progress * 100).toInt()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.12f),
                                shape = CircleShape
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (percent > 0) "+$percent% today's goal" else "Goal: ${(goalMinutes / 60).toInt()} hours",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Text(
                        text = "$percent% Complete",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Sleek slider
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f)
                )
            }
        }
    }
}

@Composable
fun HalfWidthBentoCard(
    title: String,
    seconds: Long,
    icon: ImageVector,
    emoji: String,
    color: Color,
    isActive: Boolean,
    onToggleActive: () -> Unit,
    onAdjustTime: (Long) -> Unit,
    tagPrefix: String,
    modifier: Modifier = Modifier
) {
    val hrs = seconds / 3600
    val mins = (seconds % 3600) / 60
    val secs = seconds % 60

    // Animating card colors to represent selection state
    val cardBgColor by animateColorAsState(
        targetValue = if (isActive) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface,
        animationSpec = tween(durationMillis = 300),
        label = "cardBgColor"
    )
    val borderStrokeColor by animateColorAsState(
        targetValue = if (isActive) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outlineVariant,
        animationSpec = tween(durationMillis = 300),
        label = "borderColor"
    )

    // Breathing pulse scale transition for active card
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by if (isActive) {
        infiniteTransition.animateFloat(
            initialValue = 0.98f,
            targetValue = 1.02f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulseScale"
        )
    } else {
        remember { mutableStateOf(1f) }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(pulseScale)
            .clickable { onToggleActive() }
            .testTag("${tagPrefix}_timer_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        border = BorderStroke(1.dp, borderStrokeColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Emoji Icon bubble
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(color.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = emoji, fontSize = 18.sp)
                }

                // Status Indicator
                if (isActive) {
                    Box(
                        modifier = Modifier
                            .background(color.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "LIVE",
                            style = MaterialTheme.typography.labelSmall,
                            color = color,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant, CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Timer monospace format
            Text(
                text = String.format("%02d:%02d:%02d", hrs, mins, secs),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = if (isActive) color else MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Robust and beautifully tight micro adjustments inside the 1f layout box
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { onAdjustTime(-300L) },
                    modifier = Modifier
                        .weight(1f)
                        .height(26.dp)
                        .testTag("${tagPrefix}_sub_5m"),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        "-5m",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                TextButton(
                    onClick = { onAdjustTime(300L) },
                    modifier = Modifier
                        .weight(1f)
                        .height(26.dp)
                        .testTag("${tagPrefix}_add_5m"),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        "+5m",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = color
                    )
                }

                TextButton(
                    onClick = { onAdjustTime(900L) },
                    modifier = Modifier
                        .weight(1f)
                        .height(26.dp)
                        .testTag("${tagPrefix}_add_15m"),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        "+15m",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = color
                    )
                }
            }
        }
    }
}

@Composable
fun QuestionPracticeBentoCard(
    seconds: Long,
    isActive: Boolean,
    onToggleActive: () -> Unit,
    onAdjustTime: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val hrs = seconds / 3600
    val mins = (seconds % 3600) / 60
    val secs = seconds % 60

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onToggleActive() }
            .testTag("question_timer_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(
                                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(14.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "✍️", fontSize = 20.sp)
                    }

                    Column {
                        Text(
                            text = "Question Practice",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                        )
                        Text(
                            text = String.format("%02d:%02d:%02d", hrs, mins, secs),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            ),
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }

                IconButton(
                    onClick = onToggleActive,
                    modifier = Modifier
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.surface, CircleShape)
                        .testTag("question_play_pause_button")
                ) {
                    Icon(
                        imageVector = if (isActive) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Start/Stop Question Practice",
                        tint = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { onAdjustTime(-300L) },
                    modifier = Modifier.testTag("question_sub_5m")
                ) {
                    Text(
                        "-5m",
                        color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                TextButton(
                    onClick = { onAdjustTime(300L) },
                    modifier = Modifier.testTag("question_add_5m")
                ) {
                    Text(
                        "+5m",
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                TextButton(
                    onClick = { onAdjustTime(900L) },
                    modifier = Modifier.testTag("question_add_15m")
                ) {
                    Text(
                        "+15m",
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun WeeklyAnalyticsBentoCard(
    records: List<StudyRecord>,
    modifier: Modifier = Modifier
) {
    val totalDays = records.size
    val totalSecondsAllTime = records.sumOf { it.totalSeconds }
    val totalHoursAllTime = totalSecondsAllTime / 3600f
    val averageDailyHours = if (totalDays > 0) totalHoursAllTime / totalDays else 0f

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("weekly_analytics_bento_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "WEEKLY ANALYTICS",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = String.format(Locale.getDefault(), "AVG: %.1fh/day", averageDailyHours),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    val daysData = remember(records) {
                        val padsCount = 7 - records.size.coerceAtMost(7)
                        val list = mutableListOf<Float>()
                        repeat(padsCount) {
                            list.add(0.15f)
                        }
                        records.take(7).reversed().forEach {
                            val hrs = it.totalSeconds / 3600f
                            val pct = (hrs / 8f).coerceIn(0.15f, 1.0f) // assume 8h is max scale
                            list.add(pct)
                        }
                        list
                    }

                    daysData.forEachIndexed { i, pct ->
                        val isToday = i == daysData.size - 1
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(pct)
                                .background(
                                    color = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(8.dp)
                                )
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                    daysOfWeek.forEachIndexed { i, day ->
                        val isToday = i == daysOfWeek.size - 1
                        Text(
                            text = day,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 10.sp
                            ),
                            color = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}
