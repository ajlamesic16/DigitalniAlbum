package com.ajla.digitalnialbum.ui.packet

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ajla.digitalnialbum.data.model.Sticker
import com.ajla.digitalnialbum.di.AppViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PacketScreen(
    onBackClick: () -> Unit,
    viewModel: PacketViewModel = viewModel(factory = AppViewModelFactory)
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val remainingPackets by viewModel.remainingPacketsToday.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val revealedStickerIds = remember { mutableStateListOf<Int>() }

    val shakeDetector = remember { ShakeDetector(context) { viewModel.openPacket() } }

    DisposableEffect(Unit) {
        shakeDetector.start()
        onDispose { shakeDetector.stop() }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Otvori paketić",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RemainingPacketBar(remainingPackets = remainingPackets)

            when (val current = state) {
                is PacketState.Waiting -> {
                    WaitingPacketContent(
                        shakeAvailable = shakeDetector.isAvailable,
                        onOpenClick = {
                            revealedStickerIds.clear()
                            viewModel.openPacket()
                        }
                    )
                }

                is PacketState.Opening -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is PacketState.Revealed -> {
                    Text(
                        text = "Prevuci prstom da otkriješ sličicu!",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )

                    CompactStickerGrid(
                        stickers = current.stickers,
                        revealedStickerIds = revealedStickerIds,
                        modifier = Modifier.weight(1f)
                    )

                    Button(
                        onClick = {
                            revealedStickerIds.clear()
                            viewModel.reset()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Otvori još jedan")
                    }
                }

                is PacketState.Error -> {
                    UsedAllPacketsContent(
                        message = current.message,
                        onBackClick = onBackClick
                    )
                }
            }
        }
    }
}

@Composable
private fun CompactStickerGrid(
    stickers: List<Sticker>,
    revealedStickerIds: MutableList<Int>,
    modifier: Modifier = Modifier
) {
    val firstRow = stickers.take(3)
    val secondRow = stickers.drop(3).take(2)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            firstRow.forEach { sticker ->
                RevealedStickerCell(
                    sticker = sticker,
                    isRevealed = sticker.id in revealedStickerIds,
                    onRevealed = {
                        if (sticker.id !in revealedStickerIds) {
                            revealedStickerIds.add(sticker.id)
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            secondRow.forEach { sticker ->
                RevealedStickerCell(
                    sticker = sticker,
                    isRevealed = sticker.id in revealedStickerIds,
                    onRevealed = {
                        if (sticker.id !in revealedStickerIds) {
                            revealedStickerIds.add(sticker.id)
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            if (secondRow.size == 1) {
                Box(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun RevealedStickerCell(
    sticker: Sticker,
    isRevealed: Boolean,
    onRevealed: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ScratchCard(
            imageUrl = sticker.imageUrl,
            isRevealed = isRevealed,
            onRevealed = onRevealed,
            modifier = Modifier.fillMaxWidth()
        )

        if (isRevealed) {
            Text(
                text = sticker.name,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun RemainingPacketBar(remainingPackets: Int) {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 7.dp, end = 7.dp, bottom = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Preostalo paketića danas:",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )

            Surface(
                shape = RoundedCornerShape(50),
                color = Color(0xFF0B8F62)
            ) {
                Text(
                    text = "$remainingPackets / 5",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(horizontal = 13.dp, vertical = 5.dp)
                )
            }
        }
    }
}

@Composable
private fun WaitingPacketContent(
    shakeAvailable: Boolean,
    onOpenClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Paketić je spreman",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = if (shakeAvailable)
                        "Protresi telefon ili pritisni dugme da otvoriš paketić."
                    else
                        "Senzor nije dostupan, koristi dugme ispod.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )

                Button(onClick = onOpenClick) {
                    Text("Otvori paketić")
                }
            }
        }
    }
}

@Composable
private fun UsedAllPacketsContent(
    message: String,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Nema više paketića",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )

                Button(onClick = onBackClick) {
                    Text("Nazad na album")
                }
            }
        }
    }
}