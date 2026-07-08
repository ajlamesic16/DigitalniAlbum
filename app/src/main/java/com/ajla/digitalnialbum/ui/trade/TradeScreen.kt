package com.ajla.digitalnialbum.ui.trade

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.ajla.digitalnialbum.data.model.Rarity
import com.ajla.digitalnialbum.data.model.Sticker
import com.ajla.digitalnialbum.di.AppViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TradeScreen(
    viewModel: TradeViewModel = viewModel(factory = AppViewModelFactory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lastUnlocked by viewModel.lastUnlocked.collectAsStateWithLifecycle()
    val unlockError by viewModel.unlockError.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) }
    var teamMenuExpanded by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    LaunchedEffect(selectedTab, uiState.selectedTeam) {
        listState.scrollToItem(0)
    }

    lastUnlocked?.let { sticker ->
        AlertDialog(
            onDismissRequest = { viewModel.clearUnlockedMessage() },
            confirmButton = {
                Button(onClick = { viewModel.clearUnlockedMessage() }) {
                    Text("Super")
                }
            },
            title = { Text("Čestitamo") },
            text = { Text("Dobila si: ${sticker.name} (${sticker.category})") }
        )
    }

    unlockError?.let { message ->
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            confirmButton = {
                Button(onClick = { viewModel.clearError() }) {
                    Text("OK")
                }
            },
            title = { Text("Nije uspjelo") },
            text = { Text(message) }
        )
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Razmjena",
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
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TradeSummaryCard(tokens = uiState.tokens)

            UnlockPanel(
                currentTokens = uiState.tokens,
                costFor = viewModel::costFor,
                onUnlock = viewModel::unlockRarity
            )

            ExposedDropdownMenuBox(
                expanded = teamMenuExpanded,
                onExpandedChange = { teamMenuExpanded = !teamMenuExpanded },
                modifier = Modifier.fillMaxWidth()
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

            PrimaryTabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Duplikati (${uiState.duplicates.size})") }
                )

                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Fale mi (${uiState.missing.size})") }
                )
            }

            val currentList = if (selectedTab == 0) {
                uiState.duplicates
            } else {
                uiState.missing
            }

            if (currentList.isEmpty()) {
                EmptyTradeCard(
                    selectedTab = selectedTab,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(currentList, key = { it.id }) { sticker ->
                        TradeRow(
                            sticker = sticker,
                            showQuantity = selectedTab == 0,
                            onExchange = { viewModel.exchangeDuplicate(sticker) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TradeSummaryCard(tokens: Int) {
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
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
                    )
                ) {
                    Box(
                        modifier = Modifier.size(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "T",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            Column {
                Text(
                    text = tokens.toString(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black
                )

                Text(
                    text = "tokena",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun UnlockPanel(
    currentTokens: Int,
    costFor: (Rarity) -> Int,
    onUnlock: (Rarity) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Otključaj sličicu koja ti fali",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                UnlockButton(
                    rarity = Rarity.COMMON,
                    currentTokens = currentTokens,
                    costFor = costFor,
                    onUnlock = onUnlock,
                    modifier = Modifier.weight(1f)
                )

                UnlockButton(
                    rarity = Rarity.RARE,
                    currentTokens = currentTokens,
                    costFor = costFor,
                    onUnlock = onUnlock,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                UnlockButton(
                    rarity = Rarity.EPIC,
                    currentTokens = currentTokens,
                    costFor = costFor,
                    onUnlock = onUnlock,
                    modifier = Modifier.weight(1f)
                )

                UnlockButton(
                    rarity = Rarity.LEGENDARY,
                    currentTokens = currentTokens,
                    costFor = costFor,
                    onUnlock = onUnlock,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun UnlockButton(
    rarity: Rarity,
    currentTokens: Int,
    costFor: (Rarity) -> Int,
    onUnlock: (Rarity) -> Unit,
    modifier: Modifier = Modifier
) {
    val cost = costFor(rarity)

    Button(
        onClick = { onUnlock(rarity) },
        enabled = currentTokens >= cost,
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
    ) {
        Text(
            text = "${rarity.label} ($cost)",
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
private fun TradeRow(
    sticker: Sticker,
    showQuantity: Boolean,
    onExchange: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                Text(
                    text = "#${sticker.numberInAlbum} ${sticker.name}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = sticker.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Text(
                    text = sticker.rarity.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                if (showQuantity) {
                    Text(
                        text = "Imaš ${sticker.quantity} primjeraka",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            if (showQuantity) {
                Button(onClick = onExchange) {
                    Text("+${sticker.rarity.tokenValue}")
                }
            }
        }
    }
}

@Composable
private fun EmptyTradeCard(
    selectedTab: Int,
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
                    imageVector = Icons.Filled.CardGiftcard,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = if (selectedTab == 0)
                        "Nema duplikata"
                    else
                        "Album je popunjen",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = if (selectedTab == 0)
                        "Duplikati koje dobiješ u paketićima\npojavit će se ovdje."
                    else
                        "Nema sličica koje ti trenutno nedostaju.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}