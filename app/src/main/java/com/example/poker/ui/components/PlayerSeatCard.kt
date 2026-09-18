package com.example.poker.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.poker.model.Player
import com.example.poker.model.PlayerHandStatus
import com.example.ui.theme.AllInPurple
import com.example.ui.theme.FeltBorder
import com.example.ui.theme.FoldGrey
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.LossRed
import com.example.ui.theme.ProfitGreen
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated

@Composable
fun PlayerSeatCard(
    player: Player,
    isCurrentTurn: Boolean,
    isDealer: Boolean,
    isSmallBlind: Boolean,
    isBigBlind: Boolean,
    onRebuyClick: (Player) -> Unit,
    onAdjustChipsClick: (Player) -> Unit,
    onRemoveClick: (Player) -> Unit,
    modifier: Modifier = Modifier,
    currencySymbol: String = "چیپ"
) {
    var menuExpanded by remember { mutableStateOf(false) }

    val borderColor by animateColorAsState(
        targetValue = when {
            isCurrentTurn -> GoldPrimary
            player.status == PlayerHandStatus.ALL_IN -> AllInPurple
            player.status == PlayerHandStatus.FOLDED -> FoldGrey.copy(alpha = 0.3f)
            else -> FeltBorder
        },
        label = "borderColor"
    )

    val cardBackground = when (player.status) {
        PlayerHandStatus.FOLDED -> Color(0xFF0D1410)
        PlayerHandStatus.ALL_IN -> Color(0xFF1B1425)
        else -> if (isCurrentTurn) SurfaceElevated else SurfaceDark
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(cardBackground)
            .border(if (isCurrentTurn) 2.dp else 1.dp, borderColor, RoundedCornerShape(14.dp))
            .padding(10.dp)
            .testTag("player_card_${player.id}")
    ) {
        Column {
            // Header Row: Avatar, Name, Position Badges, Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Color Avatar
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

                Spacer(modifier = Modifier.width(8.dp))

                // Name & Status
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = player.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (player.status == PlayerHandStatus.FOLDED) FoldGrey else Color.White,
                                fontSize = 14.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Hand Status badge
                    if (player.status != PlayerHandStatus.ACTIVE) {
                        Text(
                            text = player.status.faTitle,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = when (player.status) {
                                    PlayerHandStatus.FOLDED -> FoldGrey
                                    PlayerHandStatus.ALL_IN -> AllInPurple
                                    PlayerHandStatus.SITTING_OUT -> Color(0xFF9E9E9E)
                                    else -> Color.White
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                // Badges: D, SB, BB
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (isDealer) {
                        PositionBadge(text = "D", bgColor = GoldPrimary, textColor = Color.Black)
                    }
                    if (isSmallBlind) {
                        PositionBadge(text = "SB", bgColor = Color(0xFF1E88E5), textColor = Color.White)
                    }
                    if (isBigBlind) {
                        PositionBadge(text = "BB", bgColor = Color(0xFFE53935), textColor = Color.White)
                    }
                }

                // Options Menu
                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.size(28.dp).testTag("player_menu_${player.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Player Options",
                            tint = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier.background(SurfaceElevated)
                    ) {
                        DropdownMenuItem(
                            text = { Text("خرید مجدد چیپ (Re-buy)", color = Color.White) },
                            leadingIcon = { Icon(Icons.Default.Add, contentDescription = null, tint = GoldPrimary) },
                            onClick = {
                                menuExpanded = false
                                onRebuyClick(player)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("تنظیم دستی چیپ (+ / -)", color = Color.White) },
                            leadingIcon = { Icon(Icons.Default.Tune, contentDescription = null, tint = ProfitGreen) },
                            onClick = {
                                menuExpanded = false
                                onAdjustChipsClick(player)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("حذف یا خروج از میز", color = LossRed) },
                            onClick = {
                                menuExpanded = false
                                onRemoveClick(player)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Body Row: Stack Chips & Current Hand Bet
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Stack Chips
                Column {
                    Text(
                        text = "موجودی چیپ",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 10.sp
                        )
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PokerChipIcon(size = 14, baseColor = GoldPrimary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${formatNumber(player.chips)} $currencySymbol",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = if (player.chips > 0) GoldLight else FoldGrey,
                                fontSize = 14.sp
                            )
                        )
                    }
                }

                // Current Bet this hand
                if (player.currentStreetBet > 0 || player.totalHandContributed > 0) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "شرط این دست",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 10.sp
                            )
                        )
                        Text(
                            text = "${formatNumber(player.totalHandContributed)} $currencySymbol",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64B5F6),
                                fontSize = 13.sp
                            )
                        )
                    }
                }

                // Net Profit / Loss
                Column(horizontalAlignment = Alignment.End) {
                    val pnl = player.netProfitLoss
                    val isProfit = pnl >= 0
                    Text(
                        text = "سود / زیان کل",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 10.sp
                        )
                    )
                    Text(
                        text = "${if (pnl > 0) "+" else ""}${formatNumber(pnl)}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (pnl == 0L) Color.White.copy(alpha = 0.7f) else if (isProfit) ProfitGreen else LossRed,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            // Turn indicator banner if it's this player's turn
            if (isCurrentTurn) {
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(GoldDark.copy(alpha = 0.7f), GoldPrimary.copy(alpha = 0.7f))
                            )
                        )
                        .padding(vertical = 3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "نوبت بازی این بازیکن است",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.Black,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun PositionBadge(
    text: String,
    bgColor: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .size(22.dp)
            .clip(CircleShape)
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 10.sp
        )
    }
}
