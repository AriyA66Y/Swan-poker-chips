package com.example.poker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.poker.model.Player
import com.example.poker.model.PlayerHandStatus
import com.example.poker.model.Pot
import com.example.poker.ui.i18n.LocalAppStrings
import com.example.ui.theme.FeltBorder
import com.example.ui.theme.FeltCard
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.ProfitGreen
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated

@Composable
fun ShowdownDialog(
    pots: List<Pot>,
    players: List<Player>,
    onAwardPots: (Map<String, List<String>>) -> Unit,
    onDismiss: () -> Unit,
    currencySymbol: String = "چیپ"
) {
    val strings = LocalAppStrings.current
    // Map of potId -> Set<String> of winner playerIds
    val selectedWinners = remember(pots) {
        mutableStateMapOf<String, Set<String>>().apply {
            pots.forEach { pot ->
                // If only 1 player is eligible, auto-preselect them as the winner
                if (pot.eligiblePlayerIds.size == 1) {
                    this[pot.id] = setOf(pot.eligiblePlayerIds.first())
                } else {
                    this[pot.id] = emptySet()
                }
            }
        }
    }

    val allPotsHaveWinners = pots.isNotEmpty() && pots.all { pot ->
        (selectedWinners[pot.id]?.size ?: 0) > 0
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.5.dp, GoldPrimary, RoundedCornerShape(20.dp)),
            color = SurfaceDark
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(GoldDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Showdown",
                            tint = GoldLight,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = strings.showdownTitle,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = GoldLight,
                                fontSize = 16.sp
                            )
                        )
                        Text(
                            text = strings.showdownSubtitle,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // List of Pots to resolve
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(pots) { pot ->
                        val winnersForThisPot = selectedWinners[pot.id] ?: emptySet()
                        PotResolutionCard(
                            pot = pot,
                            players = players,
                            selectedWinnerIds = winnersForThisPot,
                            onToggleWinner = { playerId ->
                                val current = selectedWinners[pot.id] ?: emptySet()
                                val updated = if (current.contains(playerId)) {
                                    current - playerId
                                } else {
                                    current + playerId
                                }
                                selectedWinners[pot.id] = updated
                            },
                            currencySymbol = currencySymbol
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Submit Button
                Button(
                    onClick = {
                        val result = selectedWinners.mapValues { it.value.toList() }
                        onAwardPots(result)
                    },
                    enabled = allPotsHaveWinners,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("award_pots_button"),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        disabledContainerColor = Color(0xFF2C2C2C)
                    )
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = if (allPotsHaveWinners) Color.Black else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (allPotsHaveWinners) strings.confirmAwardPots else strings.selectWinnersWarning,
                        color = if (allPotsHaveWinners) Color.Black else Color.LightGray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun PotResolutionCard(
    pot: Pot,
    players: List<Player>,
    selectedWinnerIds: Set<String>,
    onToggleWinner: (String) -> Unit,
    currencySymbol: String
) {
    val strings = LocalAppStrings.current
    // Show eligible players for this pot (or non-folded players if list empty)
    val eligiblePlayers = if (pot.eligiblePlayerIds.isNotEmpty()) {
        val matched = players.filter { pot.eligiblePlayerIds.contains(it.id) }
        if (matched.isNotEmpty()) matched else players.filter { it.status != PlayerHandStatus.FOLDED }
    } else {
        players.filter { it.status != PlayerHandStatus.FOLDED }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceElevated)
            .border(1.dp, FeltBorder, RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        // Pot Title & Amount
        val displayPotName = if (strings.languageCode == "en") {
            if (pot.name.contains("اصلی") || pot.name.contains("Main")) "Main Pot"
            else pot.name.replace("پات جانبی", "Side Pot").replace("پات اصلی", "Main Pot")
        } else {
            pot.name
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = displayPotName,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = GoldLight,
                    fontSize = 13.sp
                )
            )
            ChipAmountBadge(
                amount = pot.amount,
                currencySymbol = currencySymbol,
                accentColor = GoldPrimary
            )
        }

        // Split pot note if multiple selected
        if (selectedWinnerIds.size > 1) {
            val share = pot.amount / selectedWinnerIds.size
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = strings.evenSplit(share, currencySymbol),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = ProfitGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // List of eligible players as large, tappable selectable rows
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            eligiblePlayers.forEach { player ->
                val isSelected = selectedWinnerIds.contains(player.id)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) FeltCard else Color(0xFF0C1B12))
                        .border(
                            1.5.dp,
                            if (isSelected) ProfitGreen else Color(0xFF1F3827),
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { onToggleWinner(player.id) }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Checkbox with null onCheckedChange so whole row clicks seamlessly
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = null,
                        colors = CheckboxDefaults.colors(
                            checkedColor = ProfitGreen,
                            checkmarkColor = Color.Black,
                            uncheckedColor = Color.Gray
                        ),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = player.name,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "${formatNumber(player.chips)} $currencySymbol",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (isSelected) GoldLight else Color.White.copy(alpha = 0.6f),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }
    }
}
