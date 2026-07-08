package com.ajla.digitalnialbum.ui.album

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ajla.digitalnialbum.di.AppViewModelFactory
import com.ajla.digitalnialbum.ui.settings.SettingsViewModel
import com.ajla.digitalnialbum.ui.statistics.StatisticsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumScreen(
    onStickerClick: (Int) -> Unit,
    onOpenPacketClick: () -> Unit,
    viewModel: AlbumViewModel = viewModel(factory = AppViewModelFactory),
    settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelFactory),
    statisticsViewModel: StatisticsViewModel = viewModel(factory = AppViewModelFactory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val displayMode by settingsViewModel.displayMode.collectAsStateWithLifecycle()
    val stats by statisticsViewModel.uiState.collectAsStateWithLifecycle()

    var teamMenuExpanded by remember { mutableStateOf(false) }
    var sortMenuExpanded by remember { mutableStateOf(false) }

    val gridState = rememberLazyGridState()
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.selectedTeam, uiState.sortOption) {
        gridState.scrollToItem(0)
        listState.scrollToItem(0)
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {},
        floatingActionButton = {
            FloatingActionButton(onClick = onOpenPacketClick) {
                Icon(Icons.Filled.Add, contentDescription = null)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ALBUM",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp
                )

                Text(
                    text = "Tvoja kolekcija sličica",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatItem("Sakupljeno", "${stats.ownedCount}")
                StatItem("Popunjeno", "${(stats.percentage * 100).toInt()}%")
                StatItem("Ukupno", "${stats.totalCount}")
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = teamMenuExpanded,
                    onExpandedChange = { teamMenuExpanded = !teamMenuExpanded },
                    modifier = Modifier.weight(1.45f)
                ) {
                    OutlinedTextField(
                        value = uiState.selectedTeam?.let { selected ->
                            if (selected == uiState.favoriteTeam)
                                "$selected (omiljena)"
                            else
                                selected
                        } ?: "Svi timovi",
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
                            val isFavoriteTeam = team == uiState.favoriteTeam

                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(team)

                                        if (isFavoriteTeam) {
                                            Icon(
                                                imageVector = Icons.Filled.Star,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                },
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
                        SortOption.entries.forEach { option ->
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

            uiState.favoriteTeam?.let { favoriteTeam ->
                FavoriteTeamChip(
                    team = favoriteTeam,
                    selected = uiState.selectedTeam == favoriteTeam,
                    onClick = { viewModel.updateSelectedTeam(favoriteTeam) }
                )
            }

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.stickers.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Nema sličica za prikaz")
                    }
                }

                displayMode == "GRID" -> {
                    LazyVerticalGrid(
                        state = gridState,
                        modifier = Modifier.weight(1f),
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(
                            start = 12.dp,
                            top = 12.dp,
                            end = 12.dp,
                            bottom = 0.dp
                        ),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.stickers, key = { it.id }) { sticker ->
                            StickerGridCell(
                                sticker = sticker,
                                onClick = { onStickerClick(sticker.id) },
                                onToggleFavorite = { viewModel.toggleFavorite(sticker) }
                            )
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(
                            start = 12.dp,
                            top = 12.dp,
                            end = 12.dp,
                            bottom = 0.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.stickers, key = { it.id }) { sticker ->
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
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = Color.White
        )

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White
        )
    }
}

@Composable
private fun FavoriteTeamChip(
    team: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        color = if (selected)
            MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)
        else
            MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            Column {
                Text(
                    text = "Omiljena reprezentacija",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )

                Text(
                    text = team,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}