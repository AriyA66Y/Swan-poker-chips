package com.example.poker.ui.screens

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.poker.model.Player
import com.example.poker.ui.components.AddPlayerDialog
import com.example.poker.ui.components.AdjustChipsDialog
import com.example.poker.ui.components.PokerChipIcon
import com.example.poker.ui.components.RebuyDialog
import com.example.poker.ui.components.formatNumber
import com.example.poker.viewmodel.PokerViewModel
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.FeltBorder
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.LossRed
import com.example.ui.theme.ProfitGreen
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated

@Composable
fun PlayersScreen(
    viewModel: PokerViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val currency = state.settings.currencyName

    var showAddPlayerDialog by remember { mutableStateOf(false) }
    var playerToRebuy by remember { mutableStateOf<Player?>(null) }
    var playerToAdjust by remember { mutableStateOf<Player?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(GoldDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = null,
                            tint = GoldLight,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "مدیریت بازیکنان و چیپ‌ها",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = GoldLight,
                                fontWeight = FontWeight.ExtraBold
                            )
                        )
                        Text(
                            text = "افزودن، حذف، خرید مجدد و تغییر دستی چیپ",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.65f),
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Button(
                    onClick = { showAddPlayerDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("add_player_top_button")
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("بازیکن جدید", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        // List of Players
        items(state.players, key = { it.id }) { player ->
            PlayerManageCard(
                player = player,
                currencySymbol = currency,
                onRebuy = { playerToRebuy = player },
                onAdjust = { playerToAdjust = player },
                onRemove = { viewModel.removePlayer(player.id) }
            )
        }

        // Buy-in Ledger History
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "تاریخچه ورودی‌ها و خریدهای مجدد (Buy-ins)",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = GoldLight,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        items(state.buyInRecords.reversed(), key = { it.id }) { record ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceDark)
                    .border(1.dp, FeltBorder, RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PokerChipIcon(size = 14, baseColor = GoldPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${record.playerName} (${if (record.isInitial) "ورودی اولیه" else "خرید مجدد"})",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }

                    Text(
                        text = "+${formatNumber(record.amount)} $currency",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = ProfitGreen,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }

    // Dialogs
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

    playerToRebuy?.let { player ->
        RebuyDialog(
            player = player,
            onRebuy = { pId, amount ->
                viewModel.rebuy(pId, amount)
                playerToRebuy = null
            },
            onDismiss = { playerToRebuy = null },
            currencySymbol = currency
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
            currencySymbol = currency
        )
    }
}

@Composable
fun PlayerManageCard(
    player: Player,
    currencySymbol: String,
    onRebuy: () -> Unit,
    onAdjust: () -> Unit,
    onRemove: () -> Unit
) {
    val pnl = player.netProfitLoss

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceDark)
            .border(1.dp, FeltBorder, RoundedCornerShape(14.dp))
            .padding(14.dp)
            .testTag("manage_player_card_${player.id}")
    ) {
        // Player Info Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(player.colorHex)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = player.name.take(1),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = player.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "صندلی شماره ${player.seatIndex + 1}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 10.sp
                        )
                    )
                }
            }

            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(32.dp).testTag("remove_player_${player.id}")
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Remove Player", tint = LossRed.copy(alpha = 0.8f), modifier = Modifier.size(18.dp))
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Numbers row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(SurfaceElevated)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("موجودی چیپ", style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp))
                Text("${formatNumber(player.chips)}", style = MaterialTheme.typography.bodyMedium.copy(color = GoldLight, fontWeight = FontWeight.Bold, fontSize = 13.sp))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("کل ورودی (Buy-in)", style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp))
                Text("${formatNumber(player.totalBuyIn)}", style = MaterialTheme.typography.bodyMedium.copy(color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("سود / زیان", style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp))
                Text(
                    "${if (pnl > 0) "+" else ""}${formatNumber(pnl)}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (pnl > 0) ProfitGreen else if (pnl < 0) LossRed else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Actions row: Rebuy & Adjust
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onRebuy,
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                modifier = Modifier.weight(1f).height(40.dp).testTag("rebuy_button_${player.id}")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("خرید مجدد", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1)
            }

            OutlinedButton(
                onClick = onAdjust,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                modifier = Modifier.weight(1f).height(40.dp).testTag("adjust_chips_button_${player.id}")
            ) {
                Icon(Icons.Default.Tune, contentDescription = null, tint = ProfitGreen, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("تنظیم چیپ", fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1)
            }
        }
    }
}
