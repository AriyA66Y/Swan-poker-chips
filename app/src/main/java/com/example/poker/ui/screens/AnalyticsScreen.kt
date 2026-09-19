package com.example.poker.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.poker.model.HandResult
import com.example.poker.model.Player
import com.example.poker.ui.components.ProfitLossChart
import com.example.poker.ui.components.formatNumber
import com.example.poker.ui.i18n.LocalAppStrings
import com.example.poker.viewmodel.PokerViewModel
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.FeltBorder
import com.example.ui.theme.FeltCard
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.LossRed
import com.example.ui.theme.ProfitGreen
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AnalyticsScreen(
    viewModel: PokerViewModel,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val state by viewModel.uiState.collectAsState()
    val currency = state.settings.currencyName

    val totalBuyIn = state.players.sumOf { it.totalBuyIn }
    val totalChips = state.players.sumOf { it.chips }
    val topWinner = state.players.maxByOrNull { it.netProfitLoss }
    val topLoser = state.players.minByOrNull { it.netProfitLoss }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Screen Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(GoldDark),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Analytics,
                        contentDescription = null,
                        tint = GoldLight,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = strings.analyticsTitle,
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = GoldLight,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                    Text(
                        text = strings.analyticsSubtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.65f),
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }

        // Summary Statistics Cards (2x2 grid)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = strings.totalBuyIns,
                    value = "${formatNumber(totalBuyIn)} $currency",
                    icon = Icons.Default.MonetizationOn,
                    iconColor = GoldPrimary,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = strings.handsPlayed,
                    value = "${state.handNumber} ${if (strings.languageCode == "en") "Hands" else "دست"}",
                    icon = Icons.Default.History,
                    iconColor = Color(0xFF64B5F6),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = strings.topWinner,
                    value = if (topWinner != null && topWinner.netProfitLoss > 0)
                        "${topWinner.name} (+${formatNumber(topWinner.netProfitLoss)})"
                    else strings.noProfit,
                    icon = Icons.Default.EmojiEvents,
                    iconColor = ProfitGreen,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = strings.playerCountLabel,
                    value = "${state.players.size} ${if (strings.languageCode == "en") "Players" else "نفر"}",
                    icon = Icons.Default.People,
                    iconColor = Color(0xFFCE93D8),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Visual Profit & Loss Bar Chart
        item {
            ProfitLossChart(
                players = state.players,
                currencySymbol = currency
            )
        }

        // Hand History Ledger Title
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    tint = GoldPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${strings.handLedgerTitle} (${state.handHistory.size} ${if (strings.languageCode == "en") "Hands" else "دست"})",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = GoldLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                )
            }
        }

        // Hand History Cards
        if (state.handHistory.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceDark)
                        .border(1.dp, FeltBorder, RoundedCornerShape(12.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = strings.noHandsPlayedYet,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 12.sp
                        )
                    )
                }
            }
        } else {
            items(state.handHistory.reversed(), key = { it.id }) { handResult ->
                HandHistoryCard(
                    hand = handResult,
                    players = state.players,
                    currencySymbol = currency
                )
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceDark)
            .border(1.dp, FeltBorder, RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 11.sp
                    )
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                ),
                maxLines = 1
            )
        }
    }
}

@Composable
fun HandHistoryCard(
    hand: HandResult,
    players: List<Player>,
    currencySymbol: String
) {
    val strings = LocalAppStrings.current
    var isExpanded by remember { mutableStateOf(false) }
    val dateFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }
    val timeString = dateFormat.format(Date(hand.timestamp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceDark)
            .border(1.dp, FeltBorder, RoundedCornerShape(14.dp))
            .clickable { isExpanded = !isExpanded }
            .padding(12.dp)
            .testTag("hand_history_${hand.handNumber}")
    ) {
        // Top Row: Hand number & time
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(GoldDark)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "${strings.handNumber} #${hand.handNumber}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = GoldLight,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = timeString,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 11.sp
                    )
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${strings.totalPotLabel}: ${formatNumber(hand.totalPot)} $currencySymbol",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = GoldLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Summary
        Text(
            text = hand.winnersSummary,
            style = MaterialTheme.typography.bodySmall.copy(
                color = ProfitGreen,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
        )

        // Expanded Details: In/Out per player for this hand
        AnimatedVisibility(visible = isExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0A180F))
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = strings.playerChipChanges,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                )

                hand.playerChipDeltas.forEach { (playerId, delta) ->
                    val playerName = players.firstOrNull { it.id == playerId }?.name ?: if (strings.languageCode == "en") "Player" else "بازیکن"
                    val isGain = delta > 0
                    val isLoss = delta < 0

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = playerName,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White,
                                fontSize = 11.sp
                            )
                        )
                        Text(
                            text = "${if (isGain) "+" else ""}${formatNumber(delta)} $currencySymbol",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = when {
                                    isGain -> ProfitGreen
                                    isLoss -> LossRed
                                    else -> Color.White.copy(alpha = 0.5f)
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
