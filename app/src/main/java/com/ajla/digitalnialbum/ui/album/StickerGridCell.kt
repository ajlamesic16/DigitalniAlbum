package com.ajla.digitalnialbum.ui.album

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.ajla.digitalnialbum.data.model.Rarity
import com.ajla.digitalnialbum.data.model.Sticker
import androidx.compose.ui.draw.alpha

@Composable
fun StickerGridCell(
    sticker: Sticker,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {

    val borderColor = when (sticker.rarity) {
        Rarity.LEGENDARY -> Color(0xFFFFC107)
        Rarity.EPIC -> Color(0xFF1565C0)
        Rarity.RARE -> Color(0xFFE53935)
        Rarity.COMMON -> Color(0xFF43A047)
    }

    val stickerOpacity = if (sticker.isOwned) 1f else 0.35f

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(stickerOpacity),
        ) {

            Box {
                SubcomposeAsyncImage(
                    model = sticker.imageUrl,
                    contentDescription = sticker.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.72f),
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

                Surface(
                    color = borderColor,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(6.dp)
                ) {
                    Text(
                        text = sticker.rarity.label,
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(
                            horizontal = 8.dp,
                            vertical = 4.dp
                        )
                    )
                }
            }

            Text(
                text = "#${sticker.numberInAlbum}",
                style = MaterialTheme.typography.labelMedium,
                color = borderColor,
                modifier = Modifier.padding(
                    start = 8.dp,
                    top = 8.dp,
                    end = 8.dp
                )
            )

            Text(
                text = sticker.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Text(
                text = sticker.category,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                maxLines = 1,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector =
                            if (sticker.isFavorite)
                                Icons.Filled.Favorite
                            else
                                Icons.Filled.FavoriteBorder,
                        contentDescription = null,
                        tint = if (sticker.isFavorite)
                            Color.Red
                        else
                            Color.Gray
                    )
                }
            }
        }
    }
}