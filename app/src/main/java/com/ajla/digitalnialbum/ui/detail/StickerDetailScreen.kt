package com.ajla.digitalnialbum.ui.detail

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import com.ajla.digitalnialbum.data.model.Rarity
import com.ajla.digitalnialbum.data.model.Sticker
import com.ajla.digitalnialbum.di.AppViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StickerDetailScreen(
    onBackClick: () -> Unit,
    viewModel: StickerDetailViewModel = viewModel(factory = AppViewModelFactory)
) {
    val sticker by viewModel.sticker.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalji sličice",
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
                },
                actions = {
                    sticker?.let { current ->
                        IconButton(onClick = { viewModel.toggleFavorite() }) {
                            Icon(
                                imageVector =
                                    if (current.isFavorite)
                                        Icons.Filled.Favorite
                                    else
                                        Icons.Filled.FavoriteBorder,
                                contentDescription = null
                            )
                        }

                        IconButton(
                            onClick = {
                                val shareText = buildStickerShareText(current)

                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                }

                                context.startActivity(
                                    Intent.createChooser(
                                        intent,
                                        "Podijeli sličicu"
                                    )
                                )
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Share,
                                contentDescription = null
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        val current = sticker

        if (current != null) {
            val rarityColor = when (current.rarity) {
                Rarity.LEGENDARY -> Color(0xFFFFC107)
                Rarity.EPIC -> Color(0xFF8E24AA)
                Rarity.RARE -> Color(0xFFE53935)
                Rarity.COMMON -> Color(0xFF43A047)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .navigationBarsPadding()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black)
                        .padding(horizontal = 16.dp, vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    SubcomposeAsyncImage(
                        model = current.imageUrl,
                        contentDescription = current.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(0.72f)
                            .clip(RoundedCornerShape(14.dp)),
                        loading = {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(0.72f),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "#${current.numberInAlbum} ${current.name}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Surface(
                        color = rarityColor.copy(alpha = 0.18f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = current.rarity.label,
                            color = rarityColor,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 8.dp
                            )
                        )
                    }

                    DetailInfoRow(
                        label = "Tim",
                        value = current.category
                    )

                    current.position?.let { position ->
                        DetailInfoRow(
                            label = "Pozicija",
                            value = translatePosition(position)
                        )
                    }

                    current.jerseyNumber?.let { number ->
                        DetailInfoRow(
                            label = "Broj dresa",
                            value = number.toString()
                        )
                    }

                    DetailInfoRow(
                        label = "Status",
                        value = if (current.isOwned)
                            "U kolekciji (broj primjeraka: ${current.quantity})"
                        else
                            "Nije u kolekciji"
                    )

                    current.dateObtained?.let { date ->
                        DetailInfoRow(
                            label = "Dobijena",
                            value = formatStickerDate(date)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailInfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.size(42.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = label.take(1),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray
            )

            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private fun formatStickerDate(dateObtained: Long): String {
    val formatter = SimpleDateFormat("dd.MM.yyyy.", Locale.getDefault())
    return formatter.format(Date(dateObtained))
}

private fun translatePosition(position: String): String {
    return when (position.uppercase()) {
        "GOALKEEPER" -> "Golman"
        "DEFENDER" -> "Odbrana"
        "MIDFIELDER" -> "Vezni"
        "FORWARD" -> "Napadač"
        else -> position
    }
}

private fun buildStickerShareText(sticker: Sticker): String {
    return buildString {
        appendLine("Digitalni album")
        appendLine("#${sticker.numberInAlbum} ${sticker.name}")
        appendLine("Tim: ${sticker.category}")
        appendLine("Rijetkost: ${sticker.rarity.label}")

        sticker.position?.let {
            appendLine("Pozicija: ${translatePosition(it)}")
        }

        sticker.jerseyNumber?.let {
            appendLine("Broj dresa: $it")
        }

        appendLine(
            if (sticker.isOwned)
                "Status: U kolekciji (${sticker.quantity} primjerak/a)"
            else
                "Status: Nije u kolekciji"
        )
    }
}