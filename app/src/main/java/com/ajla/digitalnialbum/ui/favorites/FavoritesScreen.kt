package com.ajla.digitalnialbum.ui.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ajla.digitalnialbum.di.AppViewModelFactory
import com.ajla.digitalnialbum.ui.album.StickerRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onStickerClick: (Int) -> Unit,
    viewModel: FavoritesViewModel = viewModel(factory = AppViewModelFactory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    var teamMenuExpanded by remember { mutableStateOf(false) }
    var sortMenuExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.selectedTeam, uiState.sortOption) {
        listState.scrollToItem(0)
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Favoriti",
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
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            FavoriteSummaryCard(count = uiState.favorites.size)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = teamMenuExpanded,
                    onExpandedChange = { teamMenuExpanded = !teamMenuExpanded },
                    modifier = Modifier.weight(1.45f)
                ) {
                    OutlinedTextField(
                        value = uiState.selectedTeam ?: "Svi timovi",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tim") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = teamMenuExpanded
                            )
                        },
                        modifier = Modifier
                            .menuAnchor(
                                type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                                enabled = true
                            )
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = teamMenuExpanded,
                        onDismissRequest = { teamMenuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Svi timovi") },
                            onClick = {
                                viewModel.updateSelectedTeam(null)
                                teamMenuExpanded = false
                            }
                        )

                        uiState.teams.forEach { team ->
                            DropdownMenuItem(
                                text = { Text(team) },
                                onClick = {
                                    viewModel.updateSelectedTeam(team)
                                    teamMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = sortMenuExpanded,
                    onExpandedChange = { sortMenuExpanded = !sortMenuExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = uiState.sortOption.label,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Sort") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = sortMenuExpanded
                            )
                        },
                        modifier = Modifier
                            .menuAnchor(
                                type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                                enabled = true
                            )
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = sortMenuExpanded,
                        onDismissRequest = { sortMenuExpanded = false }
                    ) {
                        FavoriteSortOption.entries.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.label) },
                                onClick = {
                                    viewModel.updateSortOption(option)
                                    sortMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            if (uiState.favorites.isEmpty()) {
                EmptyFavoritesCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.favorites, key = { it.id }) { sticker ->
                        StickerRow(
                            sticker = sticker,
                            onClick = { onStickerClick(sticker.id) },
                            onToggleFavorite = { viewModel.toggleFavorite(sticker) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoriteSummaryCard(count: Int) {
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
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(14.dp)
                )
            }

            Column {
                Text(
                    text = "Moji favoriti",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "$count sačuvanih sličica",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun EmptyFavoritesCard(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Nema favorita",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Sličice koje označiš srcem pojavit će se ovdje.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}