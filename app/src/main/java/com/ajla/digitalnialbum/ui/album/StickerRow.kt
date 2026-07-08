package com.ajla.digitalnialbum.ui.album

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.ajla.digitalnialbum.data.model.Sticker

@Composable
fun StickerRow(
    sticker: Sticker,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SubcomposeAsyncImage(
                model = sticker.imageUrl,
                contentDescription = sticker.name,
                modifier = Modifier.size(64.dp),
                loading = {
                    Box(
                        modifier = Modifier.size(64.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "#${sticker.numberInAlbum} ${sticker.name}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = sticker.category,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = sticker.rarity.label,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (sticker.isFavorite) {
                        Icons.Filled.Favorite
                    } else {
                        Icons.Filled.FavoriteBorder
                    },
                    contentDescription = null
                )
            }
        }
    }
}