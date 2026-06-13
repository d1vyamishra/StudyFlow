package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.StudyRecord
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    val records by viewModel.allRecords.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    val totalDays = records.size
    val totalSecondsAllTime = records.sumOf { it.totalSeconds }
    val totalHoursAllTime = totalSecondsAllTime / 3600f

    val averageDailyHours = if (totalDays > 0) {
        totalHoursAllTime / totalDays
    } else {
        0f
    }

    val averageWeeklyHours = averageDailyHours * 7f

    val totalLectureSec = records.sumOf { it.lectureSeconds }
    val totalSelfStudySec = records.sumOf { it.selfStudySeconds }
    val totalQuestionSec = records.sumOf { it.questionPracticeSeconds }
    val sumCategories = (totalLectureSec + totalSelfStudySec + totalQuestionSec).toFloat()

    val lecturePercent = if (sumCategories > 0) (totalLectureSec / sumCategories) * 100 else 0f
    val selfStudyPercent = if (sumCategories > 0) (totalSelfStudySec / sumCategories) * 100 else 0f
    val questionPercent = if (sumCategories > 0) (totalQuestionSec / sumCategories) * 100 else 0f

    val currentStreak = remember(records) {
        calculateStreak(records)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Performance Analytics", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Avg Weekly Hours Stat Card
                StatCard(
                    title = "Avg Weekly Hours",
                    value = String.format(Locale.getDefault(), "%.1f h", averageWeeklyHours),
                    icon = Icons.Default.DateRange,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )

                // Daily Average Stat Card
                StatCard(
                    title = "Daily Average",
                    value = String.format(Locale.getDefault(), "%.1f h", averageDailyHours),
                    icon = Icons.Default.QueryStats,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Streak Card
                StatCard(
                    title = "Daily Streak",
                    value = "$currentStreak Days",
                    icon = Icons.Default.LocalFireDepartment,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.weight(1f)
                )

                // Total Study Time Card
                StatCard(
                    title = "Total Compiled",
                    value = String.format(Locale.getDefault(), "%.1f h", totalHoursAllTime),
                    icon = Icons.Default.AutoAwesome,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("weekly_trends_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Weekly Trends (Last 7 Days)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Visualizing daily stacked distributions",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (records.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Start studying to plot performance charts!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        StackedBarChart(records = records)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ChartLegendItem(label = "Lectures", color = MaterialTheme.colorScheme.primary)
                        ChartLegendItem(label = "Self-Study", color = MaterialTheme.colorScheme.secondary)
                        ChartLegendItem(label = "Practice", color = MaterialTheme.colorScheme.tertiary)
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Study Distribution Breakdown",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Percentage share of total effort spent",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    if (sumCategories == 0f) {
                        Text(
                            text = "No recorded categories yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(16.dp)
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            if (lecturePercent > 0) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .weight(lecturePercent)
                                        .background(MaterialTheme.colorScheme.primary)
                                )
                            }
                            if (selfStudyPercent > 0) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .weight(selfStudyPercent)
                                        .background(MaterialTheme.colorScheme.secondary)
                                )
                            }
                            if (questionPercent > 0) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .weight(questionPercent)
                                        .background(MaterialTheme.colorScheme.tertiary)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        DistributionLegendRow(
                            label = "Video Lectures",
                            percent = lecturePercent,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Divider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        DistributionLegendRow(
                            label = "Self-Study",
                            percent = selfStudyPercent,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Divider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        DistributionLegendRow(
                            label = "Question Practice",
                            percent = questionPercent,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Premium Author/Creator Sign-off Branding
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Made with love",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Made By: ",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Text(
                            text = "@v1shal.irl",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.testTag("stat_card_${title.replace(" ", "_").lowercase()}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun StackedBarChart(
    records: List<StudyRecord>,
    modifier: Modifier = Modifier
) {
    val chartRecords = remember(records) {
        records.take(7).reversed()
    }

    val lectureColor = MaterialTheme.colorScheme.primary
    val selfStudyColor = MaterialTheme.colorScheme.secondary
    val questionColor = MaterialTheme.colorScheme.tertiary

    val maxHours = remember(chartRecords) {
        val maxTotalSeconds = chartRecords.maxOfOrNull { it.totalSeconds } ?: 3600L
        val maxCalculatedHours = maxTotalSeconds / 3600f
        maxOf(maxCalculatedHours, 4.0f)
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .testTag("canvas_weekly_chart")
    ) {
        val width = size.width
        val height = size.height

        val paddingBottom = 24.dp.toPx()
        val graphHeight = height - paddingBottom
        val barSpacingPercentage = 0.35f

        val barCount = chartRecords.size
        val blockWidth = width / barCount
        val barWidth = blockWidth * (1f - barSpacingPercentage)
        val spaceWidth = blockWidth * barSpacingPercentage

        chartRecords.forEachIndexed { index, record ->
            val lectureHours = record.lectureSeconds / 3600f
            val selfStudyHours = record.selfStudySeconds / 3600f
            val questionHours = record.questionPracticeSeconds / 3600f

            val lectureHeightPx = (lectureHours / maxHours) * graphHeight
            val selfStudyHeightPx = (selfStudyHours / maxHours) * graphHeight
            val questionHeightPx = (questionHours / maxHours) * graphHeight

            val xOffset = index * blockWidth + spaceWidth / 2f

            var currentY = graphHeight
            if (lectureHeightPx > 0) {
                drawRoundRect(
                    color = lectureColor,
                    topLeft = Offset(xOffset, currentY - lectureHeightPx),
                    size = Size(barWidth, lectureHeightPx),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
                currentY -= lectureHeightPx
            }

            if (selfStudyHeightPx > 0) {
                drawRoundRect(
                    color = selfStudyColor,
                    topLeft = Offset(xOffset, currentY - selfStudyHeightPx),
                    size = Size(barWidth, selfStudyHeightPx),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
                currentY -= selfStudyHeightPx
            }

            if (questionHeightPx > 0) {
                drawRoundRect(
                    color = questionColor,
                    topLeft = Offset(xOffset, currentY - questionHeightPx),
                    size = Size(barWidth, questionHeightPx),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
            }

            val labelText = formatChartDateLabel(record.date)
            drawIntoCanvas { canvas ->
                val textPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.GRAY
                    textSize = 10.dp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                }
                canvas.nativeCanvas.drawText(
                    labelText,
                    xOffset + barWidth / 2f,
                    height - 4.dp.toPx(),
                    textPaint
                )
            }
        }
    }
}

@Composable
fun ChartLegendItem(
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, RoundedCornerShape(2.dp))
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun DistributionLegendRow(
    label: String,
    percent: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(color, CircleShape)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = String.format(Locale.getDefault(), "%.1f %%", percent),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Black,
            color = color
        )
    }
}

// Helper algorithms
fun calculateStreak(records: List<StudyRecord>): Int {
    if (records.isEmpty()) return 0
    
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    // Sort chronological: from oldest to newest
    val sortedDates = records.map { it.date }.distinct().sortedDescending()
    
    var streak = 0
    val cal = java.util.Calendar.getInstance()
    
    // Check if they studied today or yesterday to evaluate active streak
    val todayStr = sdf.format(cal.time)
    cal.add(java.util.Calendar.DAY_OF_YEAR, -1)
    val yesterdayStr = sdf.format(cal.time)
    
    if (sortedDates.first() != todayStr && sortedDates.first() != yesterdayStr) {
        return 0 // Streak died
    }

    var expectedDate = sortedDates.first()
    streak = 1
    
    for (i in 1 until sortedDates.size) {
        try {
            val currentD = sdf.parse(sortedDates[i - 1])!!
            val tempCal = java.util.Calendar.getInstance().apply { time = currentD }
            tempCal.add(java.util.Calendar.DAY_OF_YEAR, -1)
            val expectedStr = sdf.format(tempCal.time)
            
            if (sortedDates[i] == expectedStr) {
                streak++
            } else {
                break
            }
        } catch (e: Exception) {
            break
        }
    }
    
    return streak
}

fun formatChartDateLabel(dateStr: String): String {
    return try {
        val parsed = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateStr)
        SimpleDateFormat("dd/MM", Locale.getDefault()).format(parsed!!)
    } catch (e: Exception) {
        if (dateStr.length >= 5) dateStr.substring(dateStr.length - 5) else dateStr
    }
}
