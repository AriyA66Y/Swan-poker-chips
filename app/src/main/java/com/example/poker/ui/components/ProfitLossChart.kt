package com.example.poker.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.poker.model.Player
import com.example.poker.ui.i18n.LocalAppStrings
import com.example.ui.theme.FeltBorder
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.LossRed
import com.example.ui.theme.ProfitGreen
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import kotlin.math.abs
import kotlin.math.max

@Composable
fun ProfitLossChart(
    players: List<Player>,
    modifier: Modifier = Modifier,
    currencySymbol: String = "چیپ"
) {
    if (players.isEmpty()) return
    val strings = LocalAppStrings.current

    // Sort players by net profit descending (top winner first)
    val sortedPlayers = players.sortedByDescending { it.netProfitLoss }
    val maxAbsolutePnl = max(1L, sortedPlayers.maxOf { abs(it.netProfitLoss) })

    val totalBuyIn = players.sumOf { it.totalBuyIn }
    val totalChips = players.sumOf { it.chips }
    val isBalanced = (totalBuyIn == totalChips)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark)
            .border(1.dp, FeltBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
            .testTag("profit_loss_chart_container")
    ) {
        // Chart Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(GoldDark),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = "Chart",
                        tint = GoldLight,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = strings.chartTitle,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = GoldLight,
                            fontSize = 15.sp
                        )
                    )
                    Text(
                        text = strings.chartSubtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.65f),
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Players Bar Chart Rows
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            sortedPlayers.forEach { player ->
                PlayerProfitLossBarRow(
                    player = player,
                    maxAbsValue = maxAbsolutePnl,
                    currencySymbol = currencySymbol
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Balance Verification Summary
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(SurfaceElevated)
                .border(1.dp, if (isBalanced) ProfitGreen.copy(alpha = 0.4f) else LossRed.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Balance,
                    contentDescription = "Balance Check",
                    tint = if (isBalanced) ProfitGreen else LossRed,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isBalanced) strings.balanceExact else strings.balanceMismatch,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                    )
                )
            }

            Text(
                text = "${strings.chipsLabel}: ${formatNumber(totalChips)} | ${strings.totalBuyIns}: ${formatNumber(totalBuyIn)}",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 10.sp
                )
            )
        }
    }
}

@Composable
fun PlayerProfitLossBarRow(
    player: Player,
    maxAbsValue: Long,
    currencySymbol: String
) {
    val strings = LocalAppStrings.current
    val pnl = player.netProfitLoss
    val isProfit = pnl > 0
    val isLoss = pnl < 0
    val absPnl = abs(pnl)

    val targetRatio = (absPnl.toFloat() / maxAbsValue.toFloat()).coerceIn(0.05f, 1f)
    val animatedRatio by animateFloatAsState(
        targetValue = targetRatio,
        animationSpec = tween(durationMillis = 600),
        label = "barRatio"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        // Player Name & Net P/L Label
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(Color(player.colorHex)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = player.name.take(1),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = player.name,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                )
            }

            // P/L Value with + / -
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isProfit) {
                    Icon(
                        Icons.Default.ArrowUpward,
                        contentDescription = "Profit",
                        tint = ProfitGreen,
                        modifier = Modifier.size(14.dp)
                    )
                } else if (isLoss) {
                    Icon(
                        Icons.Default.ArrowDownward,
                        contentDescription = "Loss",
                        tint = LossRed,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "${if (isProfit) "+" else ""}${formatNumber(pnl)} $currencySymbol",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = when {
                            isProfit -> ProfitGreen
                            isLoss -> LossRed
                            else -> Color.White.copy(alpha = 0.7f)
                        },
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp
                    )
                )
                Spacer(modifier = Modifier.width(6.dp))
                // ROI %
                Text(
                    text = "(${if (isProfit) "+" else ""}${String.format("%.1f", player.roiPercentage)}%)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 10.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Visual bi-directional bar container
        // Left side is Loss, Center line, Right side is Profit
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(18.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF09170E)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Loss Side (Left half)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.CenterEnd
            ) {
                if (isLoss) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedRatio)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(LossRed, LossRed.copy(alpha = 0.7f))
                                )
                            )
                    )
                }
            }

            // Center Axis Divider
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .fillMaxHeight()
                    .background(Color.White.copy(alpha = 0.4f))
            )

            // Profit Side (Right half)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.CenterStart
            ) {
                if (isProfit) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedRatio)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(ProfitGreen.copy(alpha = 0.7f), ProfitGreen)
                                )
                            )
                    )
                }
            }
        }

        // Subtitle: Buy-in vs Stack
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${strings.totalBuyInLabel}: ${formatNumber(player.totalBuyIn)}",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 10.sp
                )
            )
            Text(
                text = "${strings.currentChips}: ${formatNumber(player.chips)}",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 10.sp
                )
            )
        }
    }
}
