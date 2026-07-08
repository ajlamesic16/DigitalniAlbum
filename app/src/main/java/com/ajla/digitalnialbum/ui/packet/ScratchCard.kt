package com.ajla.digitalnialbum.ui.packet

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage

@Composable
fun ScratchCard(
    imageUrl: String,
    isRevealed: Boolean,
    onRevealed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scratchedPoints = remember { mutableStateListOf<Offset>() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.72f)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        SubcomposeAsyncImage(
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize(),
            loading = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        )

        if (!isRevealed) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                scratchedPoints.add(offset)
                            },
                            onDrag = { change, _ ->
                                scratchedPoints.add(change.position)

                                if (scratchedPoints.size >= 18) {
                                    onRevealed()
                                }
                            }
                        )
                    }
            ) {
                drawRoundRect(
                    color = Color(0xFF0B6B4B)
                )

                scratchedPoints.forEach { point ->
                    drawCircle(
                        color = Color.Black,
                        radius = 48f,
                        center = point,
                        blendMode = BlendMode.Clear
                    )
                }

                scratchedPoints.zipWithNext().forEach { points ->
                    drawLine(
                        color = Color.Black,
                        start = points.first,
                        end = points.second,
                        strokeWidth = 90f,
                        cap = StrokeCap.Round,
                        blendMode = BlendMode.Clear
                    )
                }
            }

            Text(
                text = "?",
                color = Color.White.copy(alpha = 0.45f),
                fontSize = 56.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}