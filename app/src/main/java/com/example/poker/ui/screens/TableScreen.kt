package com.example.poker.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.poker.model.Player
import com.example.poker.model.Street
import com.example.poker.ui.components.AddPlayerDialog
import com.example.poker.ui.components.AdjustChipsDialog
import com.example.poker.ui.components.BettingControls
import com.example.poker.ui.components.PlayerSeatCard
import com.example.poker.ui.components.PotsDisplay
import com.example.poker.ui.components.RebuyDialog
import com.example.poker.ui.components.SettingsDialog
import com.example.poker.ui.components.ShowdownDialog
import com.example.poker.viewmodel.PokerViewModel
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.FeltBorder
import com.example.ui.theme.FeltCard
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.ProfitGreen
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated

@Composable
fun TableScreen(
    viewModel: PokerViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    var showAddPlayerDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var playerToRebuy by remember { mutableStateOf<Player?>(null) }
    var playerToAdjust by remember { mutableStateOf<Player?>(null) }

    val currentTurnPlayer = state.players.firstOrNull { it.id == state.currentTurnPlayerId }
    val isHandActive = (state.currentStreet != Street.ENDED)

    // Calculate SB & BB indices for badges
    val eligibleIndices = state.players.indices.filter { state.players[it].chips > 0 || state.players[it].totalHandContributed > 0 }
    val dealerPos = if (eligibleIndices.isNotEmpty()) eligibleIndices.indexOf(state.dealerIndex).let { if (it == -1) 0 else it } else 0
    val sbIndex = if (eligibleIndices.size == 2) {
        eligibleIndices.getOrNull(dealerPos) ?: -1
    } else if (eligibleIndices.isNotEmpty()) {
        eligibleIndices[(dealerPos + 1) % eligibleIndices.size]
    } else -1

    val bbIndex = if (eligibleIndices.size == 2) {
        eligibleIndices.getOrNull((dealerPos + 1) % eligibleIndices.size) ?: -1
    } else if (eligibleIndices.isNotEmpty()) {
        eligibleIndices[(dealerPos + 2) % eligibleIndices.size]
    } else -1

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D2818), // Casino Felt
                        Color(0xFF06140D)
                    )
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar: Hand #, Street badge, Settings
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Hand number badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceElevated)
                            .border(1.dp, GoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "دست #${state.handNumber}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = GoldLight,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Street status badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isHandActive) FeltCard else Color(0xFF2E1C0C))
                            .border(
                                1.dp,
                                if (isHandActive) ProfitGreen.copy(alpha = 0.6f) else GoldDark,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = state.currentStreet.faName,
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = if (isHandActive) ProfitGreen else GoldLight,
                                fontWeight = FontWeight.ExtraBold
                            )
                        )
                    }
                }

                // Header actions: Add player & Settings
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { showAddPlayerDialog = true },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SurfaceElevated)
                            .testTag("add_player_icon_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Player", tint = GoldPrimary, modifier = Modifier.size(20.dp))
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = { showSettingsDialog = true },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SurfaceElevated)
                            .testTag("settings_icon_button")
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }

            // Blinds indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "بلایندها: ${state.settings.smallBlind} / ${state.settings.bigBlind} ${state.settings.currencyName}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 11.sp
                    )
                )
                Text(
                    text = "بازیکنان با چیپ: ${state.players.count { it.chips > 0 }}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 11.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Main Pot & Side Pots Display
            PotsDisplay(
                pots = state.pots,
                players = state.players,
                modifier = Modifier.padding(horizontal = 16.dp),
                currencySymbol = state.settings.currencyName
            )

            // Winner Summary Banner if hand ended
            if (!isHandActive && state.lastHandSummary != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF0F2E1E))
                        .border(1.dp, ProfitGreen.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.lastHandSummary ?: "",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = ProfitGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            // Start New Hand Button when hand is ended
            if (!isHandActive) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Button(
                        onClick = { viewModel.startNewHand() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("start_new_hand_button"),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (state.handNumber == 0) "شروع اولین دست" else "شروع دست جدید",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Player Seats List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(top = 6.dp, bottom = 100.dp)
            ) {
                items(state.players, key = { it.id }) { player ->
                    val isPlayerTurn = (player.id == state.currentTurnPlayerId)
                    val isDealer = (state.players.indexOf(player) == state.dealerIndex)
                    val isSb = (state.players.indexOf(player) == sbIndex)
                    val isBb = (state.players.indexOf(player) == bbIndex)

                    PlayerSeatCard(
                        player = player,
                        isCurrentTurn = isPlayerTurn,
                        isDealer = isDealer,
                        isSmallBlind = isSb,
                        isBigBlind = isBb,
                        onRebuyClick = { playerToRebuy = it },
                        onAdjustChipsClick = { playerToAdjust = it },
                        onRemoveClick = { viewModel.removePlayer(it.id) },
                        currencySymbol = state.settings.currencyName
                    )
                }
            }
        }

        // Active Turn Betting Controls pinned at bottom when hand is in progress
        if (isHandActive && currentTurnPlayer != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                BettingControls(
                    player = currentTurnPlayer,
                    currentHighestBet = state.currentHighestBet,
                    minRaise = state.minRaise,
                    pots = state.pots,
                    onFold = { viewModel.onFold() },
                    onCheck = { viewModel.onCheck() },
                    onCall = { viewModel.onCall() },
                    onBetOrRaise = { viewModel.onBetOrRaise(it) },
                    onAllIn = { viewModel.onAllIn() },
                    currencySymbol = state.settings.currencyName
                )
            }
        }
    }

    // Dialogs
    if (state.isShowdownModalOpen && state.pots.isNotEmpty()) {
        ShowdownDialog(
            pots = state.pots,
            players = state.players,
            onAwardPots = { viewModel.awardPots(it) },
            onDismiss = { viewModel.dismissShowdownModal() },
            currencySymbol = state.settings.currencyName
        )
    }

    if (showAddPlayerDialog) {
        AddPlayerDialog(
            defaultBuyIn = 1000L,
            onAddPlayer = { name, buyIn, colorHex ->
                viewModel.addPlayer(name, buyIn, colorHex)
                showAddPlayerDialog = false
            },
            onDismiss = { showAddPlayerDialog = false }
        )
    }

    if (showSettingsDialog) {
        SettingsDialog(
            settings = state.settings,
            onSave = {
                viewModel.updateSettings(it)
                showSettingsDialog = false
            },
            onDismiss = { showSettingsDialog = false }
        )
    }

    playerToRebuy?.let { player ->
        RebuyDialog(
            player = player,
            onRebuy = { pId, amount ->
                viewModel.rebuy(pId, amount)
                playerToRebuy = null
            },
            onDismiss = { playerToRebuy = null },
            currencySymbol = state.settings.currencyName
        )
    }

    playerToAdjust?.let { player ->
        AdjustChipsDialog(
            player = player,
            onAdjust = { pId, delta, reason ->
                viewModel.adjustChipsManually(pId, delta, reason)
                playerToAdjust = null
            },
            onDismiss = { playerToAdjust = null },
            currencySymbol = state.settings.currencyName
        )
    }
}
