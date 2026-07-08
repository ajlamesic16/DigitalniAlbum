package com.ajla.digitalnialbum.ui.statistics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ajla.digitalnialbum.di.AppViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = viewModel(factory = AppViewModelFactory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val percent = (state.percentage * 100).toInt()

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Statistika",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            ProgressDonutCard(
                percent = percent,
                ownedCount = state.ownedCount,
                missingCount = state.missingCount,
                totalCount = state.totalCount
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Moje sličice",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatTile(
                        modifier = Modifier.weight(1f),
                        title = "Legendarne",
                        value = state.legendaryCount.toString(),
                        accentColor = Color(0xFFFFC107),
                        symbol = "L"
                    )

                    StatTile(
                        modifier = Modifier.weight(1f),
                        title = "Epske",
                        value = state.epicCount.toString(),
                        accentColor = Color(0xFF8E24AA),
                        symbol = "E"
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatTile(
                        modifier = Modifier.weight(1f),
                        title = "Rijetke",
                        value = state.rareCount.toString(),
                        accentColor = Color(0xFFE53935),
                        symbol = "R"
                    )

                    StatTile(
                        modifier = Modifier.weight(1f),
                        title = "Obične",
                        value = state.commonCount.toString(),
                        accentColor = Color(0xFF43A047),
                        symbol = "O"
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatTile(
                        modifier = Modifier.weight(1f),
                        title = "Nedostaju",
                        value = state.missingCount.toString(),
                        accentColor = Color(0xFF90A4AE),
                        symbol = "N"
                    )

                    StatTile(
                        modifier = Modifier.weight(1f),
                        title = "Duplikati",
                        value = state.duplicateCount.toString(),
                        accentColor = Color(0xFF5E35B1),
                        symbol = "D"
                    )
                }
            }
        }
    }
}

@Composable
private fun ProgressDonutCard(
    percent: Int,
    ownedCount: Int,
    missingCount: Int,
    totalCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Box(
                modifier = Modifier.size(132.dp),
                contentAlignment = Alignment.Center
            ) {
                DonutChart(
                    percentage = if (totalCount == 0)
                        0f
                    else
                        ownedCount.toFloat() / totalCount.toFloat(),
                    modifier = Modifier.size(132.dp)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$percent%",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black
                    )

                    Text(
                        text = "popunjeno",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Napredak albuma",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                ChartLegendRow(
                    color = MaterialTheme.colorScheme.primary,
                    label = "Sakupljeno",
                    value = ownedCount.toString()
                )

                ChartLegendRow(
                    color = Color(0xFF90A4AE),
                    label = "Nedostaju",
                    value = missingCount.toString()
                )

                Text(
                    text = "$ownedCount / $totalCount sličica",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun DonutChart(
    percentage: Float,
    modifier: Modifier = Modifier
) {
    val progressColor = MaterialTheme.colorScheme.primary
    val missingColor = Color(0xFF90A4AE).copy(alpha = 0.45f)

    Canvas(modifier = modifier) {
        val strokeWidth = 22.dp.toPx()
        val diameter = size.minDimension - strokeWidth
        val topLeft = Offset(
            x = (size.width - diameter) / 2f,
            y = (size.height - diameter) / 2f
        )
        val arcSize = Size(diameter, diameter)

        drawArc(
            color = missingColor,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round
            )
        )

        drawArc(
            color = progressColor,
            startAngle = -90f,
            sweepAngle = 360f * percentage.coerceIn(0f, 1f),
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round
            )
        )
    }
}

@Composable
private fun ChartLegendRow(
    color: Color,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            color = color,
            shape = RoundedCornerShape(50),
            modifier = Modifier.size(12.dp)
        ) {}

        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun StatTile(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    accentColor: Color,
    symbol: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                color = accentColor.copy(alpha = 0.18f),
                shape = RoundedCornerShape(50),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = symbol,
                        color = accentColor,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black
            )

            LinearProgressIndicator(
                progress = { 1f },
                modifier = Modifier.fillMaxWidth(),
                color = accentColor,
                trackColor = accentColor.copy(alpha = 0.18f)
            )
        }
    }
}