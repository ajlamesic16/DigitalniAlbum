package com.ajla.digitalnialbum.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ajla.digitalnialbum.di.AppViewModelFactory
import com.ajla.digitalnialbum.ui.theme.DigitalniAlbumTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(factory = AppViewModelFactory)
) {
    val displayMode by viewModel.displayMode.collectAsStateWithLifecycle()

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Postavke",
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
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SettingsHeaderCard(displayMode = displayMode)

            Text(
                text = "Način prikaza albuma",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            DisplayModeCard(
                title = "Lista",
                description = "Sličice prikazane jedna ispod druge.",
                symbol = "L",
                selected = displayMode == "LIST",
                onClick = { viewModel.setDisplayMode("LIST") }
            )

            DisplayModeCard(
                title = "Mreža",
                description = "Sličice prikazane kao kartice u redovima.",
                symbol = "M",
                selected = displayMode == "GRID",
                onClick = { viewModel.setDisplayMode("GRID") }
            )
        }
    }
}

@Composable
private fun SettingsHeaderCard(displayMode: String) {
    val modeText = if (displayMode == "GRID") "Mreža" else "Lista"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                modifier = Modifier.size(58.dp),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "⚙",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Column {
                Text(
                    text = "Postavke albuma",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Trenutni prikaz: $modeText",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun DisplayModeCard(
    title: String,
    description: String,
    symbol: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        border = if (selected) {
            BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
        } else {
            null
        },
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
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = if (selected) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
                } else {
                    MaterialTheme.colorScheme.surface
                }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = symbol,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = if (selected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.size(4.dp))

            RadioButton(
                selected = selected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsHeaderCardPreview() {
    DigitalniAlbumTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            SettingsHeaderCard(displayMode = "GRID")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DisplayModeCardPreview() {
    DigitalniAlbumTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            DisplayModeCard(
                title = "Mreža",
                description = "Sličice prikazane kao kartice u redovima.",
                symbol = "M",
                selected = true,
                onClick = {}
            )
        }
    }
}